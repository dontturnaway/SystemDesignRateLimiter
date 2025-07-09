package com.test.ratelimiter.model;

public abstract class FilterField {

    public String value;

    public FilterField(String fieldValue) {
        if (fieldValue == null) {
            throw new NullPointerException("fieldValue must not be null");
        }
        this.value = fieldValue;
    }
}
