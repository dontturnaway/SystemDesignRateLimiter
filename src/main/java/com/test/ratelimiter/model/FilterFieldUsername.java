package com.test.ratelimiter.model;

public class FilterFieldUsername extends FilterField<String> {

    public FilterFieldUsername(String fieldValue) {
        super(fieldValue);
    }

    @Override
    protected String parse(String fieldValue) {
        return fieldValue;
    }
}
