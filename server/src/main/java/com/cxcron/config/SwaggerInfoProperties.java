package com.cxcron.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Swagger 接口文档信息配置。
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "swagger.info")
public class SwaggerInfoProperties {

    /**
     * 文档标题。
     */
    private String title;

    /**
     * 文档描述。
     */
    private String description;

    /**
     * 文档版本。
     */
    private String version;

    /**
     * 联系人名称。
     */
    private String author;

    /**
     * 联系人地址。
     */
    private String url;

    /**
     * 许可证地址。
     */
    private String licenseUrl;
}
