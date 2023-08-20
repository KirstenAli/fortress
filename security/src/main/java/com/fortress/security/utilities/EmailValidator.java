package com.fortress.security.utilities;

import java.util.regex.Pattern;

public class EmailValidator implements Validator {
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[_A-Za-z0-9-+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"
    );

    public boolean validate(String email) {
        return EMAIL_PATTERN.matcher(email).matches();
    }
}

