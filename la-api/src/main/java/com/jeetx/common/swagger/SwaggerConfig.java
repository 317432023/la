package com.jeetx.common.swagger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2; 
 
@Configuration
@EnableSwagger2
@EnableWebMvc
@ComponentScan(basePackages = {"com.jeetx.controller.api"})  
public class SwaggerConfig extends WebMvcConfigurationSupport {

    @Bean  
    public Docket customDocket() {  
        return new Docket(DocumentationType.SWAGGER_2)  
                .apiInfo(apiInfo())  
                .select()  
                .apis(RequestHandlerSelectors.basePackage("com.jeetx.controller.api")) //扫描路径下的api文档
                .paths(PathSelectors.any()) //路径判断
                .build();  
    }  
  
    private ApiInfo apiInfo() {  
        return new ApiInfoBuilder()  
                .title("LA项目API接口")  
                .description("提供手机端及后台管理调用服务接口，避免中文参数编码问题，参数值均URLEncoder编码后提交，线上环境后台管理需绑定IP")  
                .license("")  
                .licenseUrl("")  
                .termsOfServiceUrl("")  
                .version("1.1.1")  
                .contact(new Contact("","", ""))  
                .build();  
    }  
}