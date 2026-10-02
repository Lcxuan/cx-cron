package com.cxcron.service.email.impl;

import com.cxcron.common.utils.EmailPasswordEncryptor;
import com.cxcron.convert.email.EmailConfigConvert;
import com.cxcron.controller.system.dto.EmailConfigReq;
import com.cxcron.controller.system.vo.EmailConfigResp;
import com.cxcron.entity.SystemEmailConfigDO;
import com.cxcron.enums.exception.BusinessErrorCodeConstants;
import com.cxcron.enums.exception.BusinessException;
import com.cxcron.mapper.SystemEmailConfigMapper;
import com.cxcron.service.email.SystemEmailConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class SystemEmailConfigServiceImpl implements SystemEmailConfigService {

    private final SystemEmailConfigMapper mapper;

    private final EmailPasswordEncryptor encryptor;

    @Override
    @Transactional
    public void save(EmailConfigReq request) {
        // 查询邮件配置
        SystemEmailConfigDO config = mapper.selectConfig();
        // 校验是否配置邮件密码加密密钥
        if (!encryptor.isAvailable()) throw new BusinessException(BusinessErrorCodeConstants.EMAIL_ENCRYPTION_KEY_MISSING);
        // 获取新增或更新的邮件配置
        SystemEmailConfigDO updateConfig = EmailConfigConvert.INSTANCE.toConfig(request);
        // 加密邮件密码
        updateConfig.setSmtpPasswordCiphertext(encryptor.encrypt(request.getSmtpPassword()));
        // 新增或更新邮件配置
        if (config == null || config.getId() == null) {
            mapper.insert(updateConfig);
            return;
        }
        updateConfig.setId(config.getId());
        mapper.updateById(updateConfig);
    }

    @Override
    public EmailConfigResp get() {
        // 查询邮件配置
        SystemEmailConfigDO config = mapper.selectConfig();
        
        EmailConfigResp response = new EmailConfigResp();
        if (config != null) {
            BeanUtils.copyProperties(config, response);
            response.setPasswordConfigured(StringUtils.hasText(config.getSmtpPasswordCiphertext()));
        } else {
            response.setEnabled(false);
            response.setPasswordConfigured(false);
        }
        return response;
    }

    @Override
    public SystemEmailConfigDO requireEnabled() {
        SystemEmailConfigDO config = mapper.selectConfig();
        if (config == null || !Boolean.TRUE.equals(config.getEnabled())) throw new BusinessException(BusinessErrorCodeConstants.EMAIL_CONFIG_DISABLED);
        return config;
    }
}
