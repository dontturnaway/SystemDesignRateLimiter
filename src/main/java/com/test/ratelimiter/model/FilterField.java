package com.test.ratelimiter.model;

public abstract class FilterField<T> {
    public final T value;

    public FilterField(String fieldValue) {
        if (fieldValue == null) {
            throw new NullPointerException("fieldValue must not be null");
        }
        this.value = parse(fieldValue);
    }

    protected abstract T parse(String fieldValue);

    @Override
    public String toString() {
        return value.toString();
    }
}