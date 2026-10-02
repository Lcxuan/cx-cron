package com.cxcron.config;

import com.cxcron.common.utils.EmailPasswordEncryptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EmailPasswordConfig {
    @Bean
    public EmailPasswordEncryptor emailPasswordEncryptor(@Value("${cx-cron.email.encryption-key:}") String key) {
        return new EmailPasswordEncryptor(key);
    }
}
