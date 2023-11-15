package com.interceptor.mcamp;
//Done
import java.util.regex.Pattern;

public class EmailValidator {
    private final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    private final Pattern pattern = Pattern.compile(EMAIL_PATTERN);

    public boolean isValidEmail(String email) {
        return pattern.matcher(email).matches();
    }
}
