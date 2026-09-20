package com.cxcron.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.parameters.Parameter;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

/**
 * Swagger 接口文档配置。
 */
@Configuration
public class SwaggerConfig {

    /**
     * 创建 API 接口文档。
     *
     * @param properties 文档信息配置
     * @return OpenAPI 配置
     */
    @Bean
    public OpenAPI openApi(SwaggerInfoProperties properties) {
        return new OpenAPI()
                .info(new Info()
                        .title(properties.getTitle())
                        .description(properties.getDescription())
                        .version(properties.getVersion())
                        .contact(new Contact().name(properties.getAuthor()).url(properties.getUrl()))
                        .license(new License().name("许可协议").url(properties.getLicenseUrl())));
    }

    /**
     * 管理后台接口文档分组。
     *
     * @return 管理后台接口分组
     */
    @Bean
    public GroupedOpenApi adminGroupedOpenApi() {
        return buildGroupedOpenApi("管理后台", "/admin/**");
    }

    /**
     * 学校端接口文档分组。
     *
     * @return 学校端接口分组
     */
    @Bean
    public GroupedOpenApi clientGroupedOpenApi() {
        return buildGroupedOpenApi("学校端", "/client/**");
    }

    /**
     * 构建接口文档分组。
     *
     * @param group 分组名称
     * @param path 路径匹配规则
     * @return 接口文档分组
     */
    private static GroupedOpenApi buildGroupedOpenApi(String group, String path) {
        return GroupedOpenApi.builder()
                .group(group)
                .pathsToMatch(path)
                .addOperationCustomizer((operation, handlerMethod) -> operation
                        .addParametersItem(buildSecurityHeaderParameter()))
                .build();
    }

    /**
     * 构建 Authorization 认证请求参数。
     *
     * @return Authorization 请求头参数
     */
    private static Parameter buildSecurityHeaderParameter() {
        return new Parameter()
                .name(HttpHeaders.AUTHORIZATION)
                .description("认证 Token")
                .in("header");
    }
}
