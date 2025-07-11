package com.test.ratelimiter.model;

public abstract class FilterField<T> {

    public T value;

    public FilterField(T fieldValue) {
        if (fieldValue == null) {
            throw new NullPointerException("fieldValue must not be null");
        }
        this.value = fieldValue;
    }

    @Override
    public String toString() {
        return this.value.toString();
    }

}
