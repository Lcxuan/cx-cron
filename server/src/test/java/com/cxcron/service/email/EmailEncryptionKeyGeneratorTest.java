package com.cxcron.service.email;

import org.junit.jupiter.api.Test;

import java.security.SecureRandom;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EmailEncryptionKeyGeneratorTest {

    @Test
    void generateEncryptionKey() {
        byte[] key = new byte[32];
        new SecureRandom().nextBytes(key);
        String encodedKey = Base64.getEncoder().encodeToString(key);

        assertEquals(32, Base64.getDecoder().decode(encodedKey).length);
        System.out.println("CX_CRON_EMAIL_ENCRYPTIONKEY = " + encodedKey);
    }
}
