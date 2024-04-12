package com.indigententerprises.components;

public class Person {
    public final String firstName;
    public final String lastName;

    public Person(
            final String firstName,
            final String lastName
    ) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof Person)) {
            return false;
        } else {
            final Person other = (Person) o;
            return this.firstName.equals(other.firstName) &&
                    this.lastName.equals(other.lastName);
        }
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + (this.firstName != null ? this.firstName.hashCode() : 0);
        result = 31 * result + (this.lastName != null ? this.lastName.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return this.firstName + ":" + this.lastName;
    }
}
