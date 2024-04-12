package com.indigententerprises.components;

import com.indigententerprises.services.Callback;
import com.indigententerprises.services.ElementAlreadyFoundException;
import com.indigententerprises.domain.Node;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import org.hamcrest.collection.IsIterableContainingInOrder;

import java.util.LinkedList;
import java.util.List;

public class TimedListComponentStaticTest implements Callback<Person> {

    private LinkedList<Person> collection = new LinkedList<>();

    @Override
    public void timedOut(final Person person) {
        collection.add(person);
    }

    @Test
    public void test1() throws ElementAlreadyFoundException, InterruptedException {

        final TimedListComponent<Person> systemUnderTest = new TimedListComponent<>(this);
        systemUnderTest.init();

        final TimeoutReportingComponent<Person> reporter = new TimeoutReportingComponent<>(systemUnderTest);
        final Person a = new Person("a", "a");
        final Person b = new Person("b", "b");
        final Person c = new Person("c", "c");
        final Person d = new Person("d", "d");

        systemUnderTest.add(a, 2000L);
        systemUnderTest.add(b, 2000L);
        systemUnderTest.add(c, 2000L);
        systemUnderTest.add(d, 2500L);

        final List<Node<String>> nodeList = reporter.retrieveCopy();

        // visual inspection sucks! fix this!
        for (final Node<String> node : nodeList) {
            System.out.println("{ t: '" + node.getT() + "', timeout: " + node.getTimeoutInMilliseconds() + "}");
        }

        Thread.sleep(3000L);

        assertThat(collection, IsIterableContainingInOrder.contains(a, b, c, d));
    }

    @Test
    public void test2() throws ElementAlreadyFoundException, InterruptedException {

        final TimedListComponent<Person> systemUnderTest = new TimedListComponent<>(this);
        systemUnderTest.init();

        final TimeoutReportingComponent<Person> reporter = new TimeoutReportingComponent<>(systemUnderTest);
        final Person a = new Person("a", "a");
        final Person b = new Person("b", "b");
        final Person c = new Person("c", "c");
        final Person d = new Person("d", "d");

        systemUnderTest.add(a, 2000L);
        systemUnderTest.add(b, 2500L);
        systemUnderTest.add(c, 2000L);
        systemUnderTest.add(d, 1900L);

        final List<Node<String>> nodeList = reporter.retrieveCopy();

        // visual inspection sucks! fix this!
        for (final Node<String> node : nodeList) {
            System.out.println("{ t: '" + node.getT() + "', timeout: " + node.getTimeoutInMilliseconds() + "}");
        }

        Thread.sleep(3000L);

        assertThat(collection, IsIterableContainingInOrder.contains(d, a, c, b));
    }

    @Test
    public void test3() throws ElementAlreadyFoundException, InterruptedException {

        final TimedListComponent<Person> systemUnderTest = new TimedListComponent<>(this);
        systemUnderTest.init();

        final TimeoutReportingComponent<Person> reporter = new TimeoutReportingComponent<>(systemUnderTest);
        final Person a = new Person("a", "a");
        final Person b = new Person("b", "b");
        final Person c = new Person("c", "c");
        final Person d = new Person("d", "d");

        systemUnderTest.add(a, 2000L);
        systemUnderTest.add(b, 2500L);
        systemUnderTest.add(c, 2000L);
        systemUnderTest.add(d, 1900L);

        systemUnderTest.remove(d);

        final List<Node<String>> nodeList = reporter.retrieveCopy();

        // visual inspection sucks! fix this!
        for (final Node<String> node : nodeList) {
            System.out.println("{ t: '" + node.getT() + "', timeout: " + node.getTimeoutInMilliseconds() + "}");
        }

        Thread.sleep(3000L);

        assertThat(collection, IsIterableContainingInOrder.contains(a, c, b));
    }
}
