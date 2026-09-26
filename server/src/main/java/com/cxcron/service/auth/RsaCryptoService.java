package com.cxcron.service.auth;

import com.cxcron.enums.exception.BusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;

import static com.cxcron.enums.exception.GlobalErrorCodeConstants.BAD_REQUEST;

@Service
public class RsaCryptoService {

    private static final OAEPParameterSpec OAEP_SHA_256 = new OAEPParameterSpec("SHA-256", "MGF1", MGF1ParameterSpec.SHA256, PSource.PSpecified.DEFAULT);

    private static final String RSA_OAEP = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";

    private final PrivateKey privateKey;
    
    private final String publicKey;

    public RsaCryptoService(@Value("${cxcron.auth.rsa.private-key}") String privateKeyPem) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            byte[] privateKeyBytes = Base64.getMimeDecoder().decode(stripPem(privateKeyPem));
            privateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes));
            if (!(privateKey instanceof RSAPrivateCrtKey rsaPrivateKey)) {
                throw new IllegalArgumentException("RSA 私钥必须包含 CRT 参数");
            }
            byte[] publicKeyBytes = keyFactory.generatePublic(new RSAPublicKeySpec(
                    rsaPrivateKey.getModulus(), rsaPrivateKey.getPublicExponent())).getEncoded();
            publicKey = toPem("PUBLIC KEY", publicKeyBytes);
        } catch (Exception exception) {
            throw new IllegalStateException("AUTH_RSA_PRIVATE_KEY 配置无效", exception);
        }
    }

    public String decrypt(String ciphertext) {
        try {
            Cipher cipher = Cipher.getInstance(RSA_OAEP);
            cipher.init(Cipher.DECRYPT_MODE, privateKey, OAEP_SHA_256);
            byte[] plaintext = cipher.doFinal(Base64.getDecoder().decode(ciphertext));
            return new String(plaintext, StandardCharsets.UTF_8);
        } catch (Exception exception) {
            throw new BusinessException(BAD_REQUEST);
        }
    }

    public String getPublicKey() {
        return publicKey;
    }

    private static String stripPem(String pem) {
        return pem.replace("\\n", "\n")
                .replaceAll("-----BEGIN [A-Z ]+-----", "")
                .replaceAll("-----END [A-Z ]+-----", "")
                .replaceAll("\\s", "");
    }

    private static String toPem(String type, byte[] encoded) {
        String body = Base64.getMimeEncoder(64, "\n".getBytes(StandardCharsets.US_ASCII)).encodeToString(encoded);
        return "-----BEGIN " + type + "-----\n" + body + "\n-----END " + type + "-----";
    }
}
