package com.indigententerprises.components;

import com.indigententerprises.services.Callback;
import com.indigententerprises.services.ElementAlreadyFoundException;
import com.indigententerprises.services.TimedListAdminService;
import com.indigententerprises.services.TimedListService;
import com.indigententerprises.domain.Node;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * @author jonnie savell
 * @author the chosen one, should he choose to join
 */
public class TimedListComponent<T> implements TimedListService<T>, TimedListAdminService<T>, Runnable {
    private final Callback<T> callback;
    private final Lock lock;
    private final Condition waiting;
    private final Condition empty;
    private final LinkedList<Node<T>> linkedList;
    private final ExecutorService executorService;

    // mutable state
    private final SleepRecorder sleepBegan;
    private Thread thread;

    public TimedListComponent(final Callback<T> callback) {
        this.callback = callback;
        this.lock = new ReentrantLock();
        this.waiting = lock.newCondition();
        this.empty = lock.newCondition();
        this.linkedList = new LinkedList<>();
        this.sleepBegan = new SleepRecorder();
        this.executorService = Executors.newSingleThreadExecutor();
    }

    public void init() {
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public List<Node<T>> retrieveCopy() {

        lock.lock();

        try {
            return linkedList.stream().collect(Collectors.toList());
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void remove(T t) {

        lock.lock();

        try {
            ListIterator<Node<T>> listIterator = linkedList.listIterator();

            while (listIterator.hasNext()) {
                final Node<T> node = listIterator.next();

                if (node.getT().equals(t)) {
                    listIterator.remove();

                    final long currentTime = System.currentTimeMillis();

                    if (listIterator.hasNext()) {
                        final Node<T> nextNode = listIterator.next();
                        try {
                            final long sleepBegan = this.sleepBegan.getSleepBegan();
                            final long sleepTime = currentTime - sleepBegan;
                            final long amountToBeAdded = Math.max(0, node.getTimeoutInMilliseconds() - sleepTime);
                            final long newTimeoutInMilliseconds = nextNode.getTimeoutInMilliseconds() + amountToBeAdded;
                            nextNode.setTimeoutInMilliseconds(newTimeoutInMilliseconds);
                        } catch (SleepInitializationException e) {
                            // sleepBegan NEED NOT have been initialized. run is initially blocked on empty.
                            // if run had never passed this point, then sleepBegan would not have been set.
                            final long amountToBeAdded = node.getTimeoutInMilliseconds();
                            final long newTimeoutInMilliseconds = nextNode.getTimeoutInMilliseconds() + amountToBeAdded;
                            nextNode.setTimeoutInMilliseconds(newTimeoutInMilliseconds);
                        }
                    }

                    // we might have removed the element upon which run() was waiting.
                    // reset mutable state and signal (even if the signal gets lost).
                    sleepBegan.setSleepBegan(currentTime);
                    waiting.signal();

                    break;
                }
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void add(T t, long milliseconds) throws ElementAlreadyFoundException {

        assert t != null;
        assert milliseconds >= 0;

        lock.lock();

        try {
            for (final Node<T> node : linkedList) {
                if (node.getT().equals(t)) {
                    throw new ElementAlreadyFoundException();
                }
            }

            final Node<T> newNode = new Node<>(t);
            final ListIterator<Node<T>> listIterator = linkedList.listIterator();
            final boolean wasEmpty = linkedList.isEmpty();
            long accumulator = milliseconds;

            while (listIterator.hasNext() && accumulator >= 0) {
                final Node<T> node = listIterator.next();
                accumulator -= node.getTimeoutInMilliseconds();
            }

            if (accumulator < 0) {
                assert listIterator.hasPrevious();

                final Node<T> node = listIterator.previous();
                accumulator += node.getTimeoutInMilliseconds();
            }

            newNode.setTimeoutInMilliseconds(accumulator);
            listIterator.add(newNode);

            if (listIterator.hasNext() && accumulator > 0) {
                final Node<T> node = listIterator.next();
                // BUG: difference is negative!
                final long difference = accumulator - node.getTimeoutInMilliseconds();
                final long difference2 = node.getTimeoutInMilliseconds() - accumulator;
                node.setTimeoutInMilliseconds(difference2);
            }

            if (wasEmpty) {
                empty.signal();
            } else {
                waiting.signal();
            }
        } finally {
            lock.unlock();
        }
    }

    public void run() {

        lock.lock();

        try {
            while (true) {
                while (linkedList.isEmpty()) {
                    empty.await();
                }

                while (!linkedList.isEmpty()) {
                    final ListIterator<Node<T>> listIterator = linkedList.listIterator();
                    assert listIterator.hasNext();
                    Node<T> node = listIterator.next();
                    sleepBegan.setSleepBegan(System.currentTimeMillis());

                    waiting.await(node.getTimeoutInMilliseconds(), TimeUnit.MILLISECONDS);

                    if (linkedList.isEmpty()) {
                        // node was removed while we slept. no problem: cycle through and await on empty.
                    } else {
                        // please note that in this case, node could still have been removed; we could
                        // awaken to find an entirely new node at the head.
                        final ListIterator<Node<T>> newListIterator = linkedList.listIterator();
                        Node<T> newNode = newListIterator.next();
                        long postSleepTimeInMilliseconds = System.currentTimeMillis();
                        long sleepBegan = 0L;
                        try {
                            sleepBegan = this.sleepBegan.getSleepBegan();
                        } catch (SleepInitializationException e) {
                            // cannot happen: this is a BUG!
                            assert false;
                        }

                        long howLongInSleep = postSleepTimeInMilliseconds - sleepBegan;

                        if (howLongInSleep >= newNode.getTimeoutInMilliseconds()) {
                            final RunnableCallbackExecution<T> runnableCallbackExecution =
                                    new RunnableCallbackExecution<>(
                                            callback,
                                            newNode.getT()
                                    );
                            executorService.submit(runnableCallbackExecution);
                            newListIterator.remove();
                        } else {
                            long howMuchSleepRemains = newNode.getTimeoutInMilliseconds() - howLongInSleep;
                            newNode.setTimeoutInMilliseconds(howMuchSleepRemains);
                        }
                    }
                }
            }
        } catch (InterruptedException e) {
            // go down
            executorService.shutdown();
        } finally {
            lock.unlock();
        }
    }
}
