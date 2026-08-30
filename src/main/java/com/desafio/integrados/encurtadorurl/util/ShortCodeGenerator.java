package com.desafio.integrados.encurtadorurl.util;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Random;

@Component
public class ShortCodeGenerator {

    public static final int MIN_LENGTH = 5;
    public static final int MAX_LENGTH = 10;
    private static final String ALPHANUMERIC = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final Random RANDOM = new SecureRandom();

    public String generateCode(int length) {
        if (length < MIN_LENGTH || length > MAX_LENGTH) {
            throw new IllegalArgumentException("O tamanho do código deve estar entre " + MIN_LENGTH + " e " + MAX_LENGTH + " caracteres.");
        }

        StringBuilder builder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = RANDOM.nextInt(ALPHANUMERIC.length());
            builder.append(ALPHANUMERIC.charAt(index));
        }
        return builder.toString();
    }

    public boolean isValidCode(String code) {
        if (code == null) {
            return false;
        }
        int len = code.length();
        if (len < MIN_LENGTH || len > MAX_LENGTH) {
            return false;
        }
        for (int i = 0; i < len; i++) {
            char ch = code.charAt(i);
            if (!isAlphaNumeric(ch)) {
                return false;
            }
        }
        return true;
    }

    private boolean isAlphaNumeric(char ch) {
        return (ch >= 'a' && ch <= 'z') ||
               (ch >= 'A' && ch <= 'Z') ||
               (ch >= '0' && ch <= '9');
    }
}
