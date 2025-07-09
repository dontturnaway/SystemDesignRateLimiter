package com.test.ratelimiter.model;

public abstract class FilterField {

    public String value;

    public FilterField(String fieldValue) {
        this.value = fieldValue;
    }
}
