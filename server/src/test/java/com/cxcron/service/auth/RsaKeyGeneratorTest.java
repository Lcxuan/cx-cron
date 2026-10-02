package com.cxcron.service.auth;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RsaKeyGeneratorTest {

    @Test
    void generatePrivateKey() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        KeyPair keyPair = generator.generateKeyPair();

        String privateKey = toPem("PRIVATE KEY", keyPair.getPrivate().getEncoded());
        String publicKey = toPem("PUBLIC KEY", keyPair.getPublic().getEncoded());

        assertEquals("RSA", keyPair.getPrivate().getAlgorithm());
        assertTrue(privateKey.startsWith("-----BEGIN PRIVATE KEY-----"));
        assertTrue(publicKey.startsWith("-----BEGIN PUBLIC KEY-----"));

        System.out.println("AUTH_RSA_PRIVATE_KEY = " + privateKey.replace("\n", "\\n"));
        System.out.println(publicKey);
    }

    private static String toPem(String type, byte[] encoded) {
        String body = Base64.getMimeEncoder(64, "\n".getBytes(StandardCharsets.US_ASCII)).encodeToString(encoded);
        return "-----BEGIN " + type + "-----\n" + body + "\n-----END " + type + "-----";
    }
}
