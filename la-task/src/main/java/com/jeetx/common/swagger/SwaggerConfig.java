package com.jeetx.common.swagger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2; 
 
@Configuration
@EnableSwagger2
@ComponentScan(basePackages = {"com.jeetx.controller.api"})   
@EnableWebMvc
public class SwaggerConfig extends WebMvcConfigurationSupport {

    @Bean
    public Docket customDocket() {
        return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo());
    }

    private ApiInfo apiInfo() {
        Contact contact = new Contact("什么都是浮云", "", "");
        return new ApiInfo("LA项目API接口",//大标题 title
                "",//小标题
                "1.0.1",//版本
                "",//termsOfServiceUrl
                contact,//作者
                "",//链接显示文字
                ""//网站链接
        );
    }   
}