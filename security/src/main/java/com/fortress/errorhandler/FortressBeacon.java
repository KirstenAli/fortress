package com.fortress.errorhandler;

public class FortressBeacon extends RuntimeException{
    public FortressBeacon(String message) {
        super(message);
    }
}
