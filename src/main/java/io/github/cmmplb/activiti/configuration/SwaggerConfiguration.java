package io.github.cmmplb.activiti.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

/**
 * @author penglibo
 * @date 2024-10-22 10:43:51
 * @since jdk 1.8
 */

@Configuration
@EnableSwagger2WebMvc
public class SwaggerConfiguration {

    @Bean
    public Docket defaultApi2() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(new ApiInfoBuilder().title("spring-boot-activiti 接口文档").build())
                .select()
                // 扫描控制层接口
                .apis(RequestHandlerSelectors.basePackage("io.github.cmmplb.activiti.controller"))
                // 扫描的接口路径
                .paths(PathSelectors.any())
                .build();
    }
}
