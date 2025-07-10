package com.test.ratelimiter.model;

public abstract class FilterField {

    public String value;

    public FilterField(String fieldValue) {
        if (fieldValue == null) {
            throw new NullPointerException("fieldValue must not be null");
        }
        this.value = fieldValue;
    }

    @Override
    public String toString() {
        return this.value.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FilterField other = (FilterField) o;
        return this.value.equals(other.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
