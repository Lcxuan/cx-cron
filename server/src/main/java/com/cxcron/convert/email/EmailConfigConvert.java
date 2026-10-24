package com.cxcron.convert.email;

import com.cxcron.controller.system.dto.EmailConfigReq;
import com.cxcron.entity.SystemEmailConfigDO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface EmailConfigConvert {
    EmailConfigConvert INSTANCE = Mappers.getMapper(EmailConfigConvert.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "createBy", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "smtpPasswordCiphertext", ignore = true)
    SystemEmailConfigDO toConfig(EmailConfigReq request);
}
