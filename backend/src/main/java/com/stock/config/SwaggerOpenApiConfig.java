package com.stock.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.HashMap;

/** 这是一个普通的Swagger配置文档，其中不包含API接口的配置（API接口的配置推荐使用注解方式）
 **/
@SpringBootConfiguration
public class SwaggerOpenApiConfig {

    /***
     * 构建Swagger3.0文档说明
     * @return 返回 OpenAPI
     */
    @Bean
    public OpenAPI customOpenAPI() {

        Info info = new Info()
                .title("股票预测系统接口文档")
                .description("股票预测系统接口文档");
        return new OpenAPI()
                .openapi("1.0")
                .info(info);
    }
}