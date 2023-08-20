package com.fortress.security.errorhandler;

public class FortressBeacon extends RuntimeException{
    public FortressBeacon(String message) {
        super(message);
    }
}
