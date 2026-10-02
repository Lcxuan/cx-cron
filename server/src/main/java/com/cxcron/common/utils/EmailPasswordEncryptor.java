package com.cxcron.common.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

public class EmailPasswordEncryptor {
    private static final int NONCE_LENGTH = 12;
    private static final int TAG_LENGTH = 128;
    private final byte[] key;

    public EmailPasswordEncryptor(String key) {
        this.key = key == null || key.isBlank() ? null : Base64.getDecoder().decode(key.trim());
        if (this.key != null && this.key.length != 32) throw new IllegalArgumentException("邮件密码加密密钥必须是 32 字节 Base64 编码值");
    }

    public boolean isAvailable() {
        return key != null;
    }

    public String encrypt(String password) {
        if (key == null) throw new IllegalStateException("未配置邮件密码加密密钥");
        try {
            byte[] nonce = new byte[NONCE_LENGTH];
            new SecureRandom().nextBytes(nonce);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, "AES"), new GCMParameterSpec(TAG_LENGTH, nonce));
            byte[] encrypted = cipher.doFinal(password.getBytes(StandardCharsets.UTF_8));
            byte[] result = new byte[nonce.length + encrypted.length];
            System.arraycopy(nonce, 0, result, 0, nonce.length);
            System.arraycopy(encrypted, 0, result, nonce.length, encrypted.length);
            return Base64.getEncoder().encodeToString(result);
        } catch (Exception e) {
            throw new IllegalStateException("邮件密码加密失败");
        }
    }

    public String decrypt(String ciphertext) {
        if (key == null) throw new IllegalStateException("未配置邮件密码加密密钥");
        try {
            byte[] bytes = Base64.getDecoder().decode(ciphertext);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, "AES"), new GCMParameterSpec(TAG_LENGTH, bytes, 0, NONCE_LENGTH));
            return new String(cipher.doFinal(bytes, NONCE_LENGTH, bytes.length - NONCE_LENGTH), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new IllegalStateException("邮件密码无法解密，请重新配置密码");
        }
    }
}
