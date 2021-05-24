package security;

public class PasswordPolicy {

    public static boolean checkPassword(String password) {
        if (password == null) {
            return false;
        }
        if (password.length() < 12) {
            return false;
        }
        boolean containsUpperCase = false;
        boolean containsLowerCase = false;
        boolean containsDigit = false;
        for (char ch : password.toCharArray()) {
            if (Character.isUpperCase(ch)) {
                containsUpperCase = true;
            }
            if (Character.isLowerCase(ch)) {
                containsLowerCase = true;
            }
            if (Character.isDigit(ch)) {
                containsDigit = true;
            }
        }

        if (containsUpperCase == true && containsLowerCase == true && containsDigit == true) {
            return true;
        }
        return false;
    }

   

}
