package io.github.cmmplb.activiti.configuration;

import io.github.cmmplb.activiti.constants.SecurityConstant;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

import javax.naming.ConfigurationException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
        Docket doc = new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(new ApiInfoBuilder().title("spring-boot-activiti 接口文档").build());
        // 添加请求头
        doc.securityContexts(securityContext()).securitySchemes(paramSecuritySchemes());
        doc.select()
                // 扫描控制层接口
                .apis(RequestHandlerSelectors.basePackage("io.github.cmmplb.activiti.controller"))
                // 扫描的接口路径
                .paths(PathSelectors.any())
                .build();
        return doc;
    }

    /**
     * 配置默认的全局鉴权策略的开关，通过正则表达式进行匹配；默认匹配所有URL
     * @return SecurityContext
     */
    private static List<SecurityContext> securityContext() {
        return Collections
                .singletonList(SecurityContext.builder()
                        .securityReferences(defaultAuth())
                        .build());
    }

    /**
     * 默认的全局鉴权策略
     * @return List<SecurityReference>
     */
    private static List<SecurityReference> defaultAuth() {
        ArrayList<AuthorizationScope> authorizationScopeList = new ArrayList<>();
        authorizationScopeList.add(new AuthorizationScope(SecurityConstant.AUTHORIZATION, "认证请求头"));
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[authorizationScopeList.size()];
        return Collections
                .singletonList(SecurityReference.builder().reference(SecurityConstant.AUTHORIZATION)
                        .scopes(authorizationScopeList.toArray(authorizationScopes)).build());
    }

    /**
     * param模式权限参数配置
     */
    private List<ApiKey> paramSecuritySchemes() {
        return Collections.singletonList(new ApiKey(SecurityConstant.AUTHORIZATION, SecurityConstant.AUTHORIZATION, "header"));
    }

}
