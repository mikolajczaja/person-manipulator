package com.example.model;

import java.math.BigDecimal;

public enum Classification {
    ADDED,LOW,MEDIUM,HIGH,DELETED,NOT_CALCULATED_YET;

    private static final BigDecimal THRESHOLD_LOW = new BigDecimal("0.4");
    private static final BigDecimal THRESHOLD_HIGH = new BigDecimal("0.9");

    public static Classification fromBigDecimal(BigDecimal input) {
        if(input.compareTo(THRESHOLD_LOW) < 0) {
            return LOW;
        } else if(input.compareTo(THRESHOLD_HIGH) < 0) {
            return MEDIUM;
        } else {
            return HIGH;
        }
    }
}
