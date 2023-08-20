package com.fortress.errorhandler;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ErrorResponse {
    private String message;
    private int status;
}
