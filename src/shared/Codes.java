// src/main/java/shared/Codes.java
package shared;

import java.security.SecureRandom;

public final class Codes {
    private static final String ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"; // sem 0/O/1/I
    private static final SecureRandom RNG = new SecureRandom();

    private Codes() {}

    public static String newGameCode() {
        // 8 chars com hífen ao meio: XXXX-XXXX
        StringBuilder sb = new StringBuilder(9);
        for (int i = 0; i < 8; i++) {
            if (i == 4) sb.append('-');
            sb.append(ALPHABET.charAt(RNG.nextInt(ALPHABET.length())));
        }
        return sb.toString();
    }
}
