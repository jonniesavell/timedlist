package com.indigententerprises.components;

import com.indigententerprises.services.Callback;
import com.indigententerprises.services.ElementAlreadyFoundException;

import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.lessThanOrEqualTo;

import java.util.HashMap;

public class TimedListComponentSecondDynamicTest implements Callback<Person> {

    private long beginning = System.currentTimeMillis();

    // damn, half a second a big delta! this component is not very accurate!
    // additionally, you can expect more delay with additional values stored in the component.
    private long delta = 500L;
    private HashMap<Person, Long> publicRecord = new HashMap<>();

    @Override
    public void timedOut(final Person person) {
        final long now = System.currentTimeMillis();
        final long duration = now - beginning;
        publicRecord.put(person, duration);
    }

    private void assertWithinRange(
            final long target,
            final long delta,
            final long value
    ) {
        assertThat(value, greaterThanOrEqualTo(target - delta));
        assertThat(value, lessThanOrEqualTo(target + delta));
    }

    @Test
    public void test1() throws ElementAlreadyFoundException, InterruptedException {

        final TimedListComponent<Person> systemUnderTest = new TimedListComponent<>(this);
        systemUnderTest.init();

        final Person a = new Person("a", "a");
        final Person b = new Person("b", "b");
        final Person c = new Person("c", "c");
        final Person d = new Person("d", "d");

        systemUnderTest.add(a, 2000L);

        Thread.sleep(100L);

        systemUnderTest.add(b, 2000L);

        Thread.sleep(100L);

        systemUnderTest.add(c, 2000L);

        Thread.sleep(100L);

        systemUnderTest.add(d, 2500L);

        Thread.sleep(3000L);

        {
            Assert.assertNotNull(publicRecord.get(a));
            final long durationOfA = publicRecord.get(a);
            assertWithinRange(2000L, delta, durationOfA);
        }

        {
            Assert.assertNotNull(publicRecord.get(b));
            final long durationOfB = publicRecord.get(b);
            assertWithinRange(2000L, delta, durationOfB);
        }

        {
            Assert.assertNotNull(publicRecord.get(c));
            final long durationOfC = publicRecord.get(c);
            assertWithinRange(2000L, delta, durationOfC);
        }

        {
            Assert.assertNotNull(publicRecord.get(d));
            final long durationOfD = publicRecord.get(d);
            assertWithinRange(2500L, delta, durationOfD);
        }
    }

    @Test
    public void test2() throws ElementAlreadyFoundException, InterruptedException {

        final TimedListComponent<Person> systemUnderTest = new TimedListComponent<>(this);
        systemUnderTest.init();

        final Person a = new Person("a", "a");
        final Person b = new Person("b", "b");
        final Person c = new Person("c", "c");
        final Person d = new Person("d", "d");

        systemUnderTest.add(a, 2000L);

        Thread.sleep(100L);

        systemUnderTest.add(b, 2500L);

        Thread.sleep(100L);

        systemUnderTest.add(c, 2000L);

        Thread.sleep(100L);

        systemUnderTest.add(d, 1900L);

        Thread.sleep(3000L);

        {
            Assert.assertNotNull(publicRecord.get(a));
            final long durationOfA = publicRecord.get(a);
            assertWithinRange(2000L, delta, durationOfA);
        }

        {
            Assert.assertNotNull(publicRecord.get(b));
            final long durationOfB = publicRecord.get(b);
            assertWithinRange(2500L, delta, durationOfB);
        }

        {
            Assert.assertNotNull(publicRecord.get(c));
            final long durationOfC = publicRecord.get(c);
            assertWithinRange(2000L, delta, durationOfC);
        }

        {
            Assert.assertNotNull(publicRecord.get(d));
            final long durationOfD = publicRecord.get(d);
            assertWithinRange(1900L, delta, durationOfD);
        }
    }

    @Test
    public void test3() throws ElementAlreadyFoundException, InterruptedException {

        final TimedListComponent<Person> systemUnderTest = new TimedListComponent<>(this);
        systemUnderTest.init();

        final Person a = new Person("a", "a");
        final Person b = new Person("b", "b");
        final Person c = new Person("c", "c");
        final Person d = new Person("d", "d");

        systemUnderTest.add(a, 2000L);

        Thread.sleep(100L);

        systemUnderTest.add(b, 2500L);

        Thread.sleep(100L);

        systemUnderTest.add(c, 2000L);

        Thread.sleep(100L);

        systemUnderTest.add(d, 1900L);

        Thread.sleep(100L);

        systemUnderTest.remove(d);

        Thread.sleep(3000L);

        {
            Assert.assertNotNull(publicRecord.get(a));
            final long durationOfA = publicRecord.get(a);
            assertWithinRange(2000L, delta, durationOfA);
        }

        {
            Assert.assertNotNull(publicRecord.get(b));
            final long durationOfB = publicRecord.get(b);
            assertWithinRange(2500L, delta, durationOfB);
        }

        {
            Assert.assertNotNull(publicRecord.get(c));
            final long durationOfC = publicRecord.get(c);
            assertWithinRange(2000L, delta, durationOfC);
        }
    }
}
