package com.cxcron.convert.email;

import com.cxcron.controller.system.dto.EmailConfigReq;
import com.cxcron.entity.SystemEmailConfigDO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface EmailConfigConvert {
    EmailConfigConvert INSTANCE = Mappers.getMapper(EmailConfigConvert.class);

    @Mapping(target = "smtpPasswordCiphertext", ignore = true)
    SystemEmailConfigDO toConfig(EmailConfigReq request);
}
