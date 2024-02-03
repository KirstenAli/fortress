package com.fortress.utilities;

import java.util.regex.Pattern;

@Deprecated
public class PasswordValidator implements Validator {

    public static final String passwordPolicy ="Must contain at least one lowercase letter, " +
            "uppercase letter, one number, one special character, " +
            "no white spaces, and be at least 8 characters long";
    private static final String PASSWORD_REGEX = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(PASSWORD_REGEX);

    public boolean validate(String password) {
        return PASSWORD_PATTERN.matcher(password).matches();
    }
}