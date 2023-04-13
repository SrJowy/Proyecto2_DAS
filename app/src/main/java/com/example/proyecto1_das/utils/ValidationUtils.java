package com.example.proyecto1_das.utils;

import java.util.regex.Pattern;

public class ValidationUtils {
    public static void validateUsr(String[] params) throws Exception {
        String usr = params[0];
        String surname = params[1];
        String mail = params[2];
        String p1 = params[3];
        String p2 = params[4];
        try {
            checkUser(usr);
            checkSurname(surname);
            checkMail(mail);
            checkPassword(p1, p2);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }

    }

    public static void checkUser(String usr) throws Exception {
        if (usr.isBlank() || usr.length() > 255) {
            throw new Exception("usr");
        }
    }

    public static void checkSurname(String surname) throws Exception {
        if (surname.isBlank() || surname.length() > 255) {
            throw new Exception("surname");
        }
    }

    public static void checkMail(String email) throws Exception {
        if (!Pattern.compile("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")
                .matcher(email)
                .matches()) {
            throw new Exception("mail");
        }

    }

    public static void checkPassword(String p1, String p2) throws Exception {
        if (!p1.equals(p2) && p1.length() < 8) {
            throw new Exception("pass");
        }
    }
}
