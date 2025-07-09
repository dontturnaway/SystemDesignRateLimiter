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
        return this.value.compareTo(o.toString()) == 0;
    }
}
