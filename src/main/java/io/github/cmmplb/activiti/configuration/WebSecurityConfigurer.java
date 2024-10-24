package io.github.cmmplb.activiti.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;

/**
 * @author penglibo
 * @date 2024-10-19 22:36:23
 * @since jdk 1.8
 */
public class WebSecurityConfigurer {

    /*
     * 配置权限相关的配置，在spring security6.x版本之后，原先经常用的and()方法被废除了，现在spring官方推荐使用Lambda表达式的写法。
     * */
    // @Bean
    // public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    //
    //     // 配置请求的权限
    //     http.authorizeHttpRequests(registry -> {
    //                 if (!securityProperties.getEnabled()) {
    //                     log.info("关闭资源验证");
    //                     registry.anyRequest().permitAll();
    //                 } else {
    //                     // 配置不需要安全拦截url
    //                     securityProperties.getWhiteList().values().forEach(url -> registry.requestMatchers(url.split(",")).permitAll());
    //                     // 其他路径都要进行拦截
    //                     registry.anyRequest().authenticated();
    //                 }
    //             }
    //     );
    //
    //     // 设置登录页面，默认的登录页面导入的两个css需要翻墙才能访问，这里下载下来单独引用
    //     http.formLogin(loginConfigurer -> {
    //                 // security默认相关登录页面和请求
    //                 loginConfigurer
    //                         // 登录页面路径
    //                         .loginPage("/login")
    //                         // 登录处理路径
    //                         .loginProcessingUrl("/login")
    //                         // 登录成功后跳转的路径, 第二个参数为登录成功是否跳转到默认页面, 为false则登录成功后回调到认证之前的页面
    //                         .defaultSuccessUrl("/", false)
    //                         // 登录成功后的处理器，注意使用这个之后会覆盖上面的登录成功后跳转的路径，避免冲突
    //                         .successHandler(new AuthenticationSuccessHandler())
    //                         // 登录失败转发路径
    //                         .failureForwardUrl("/basic/failure")
    //                         // 登陆失败后的处理器，注意使用这个之后会覆盖上面的登录失败转发路径，避免冲突
    //                         .failureHandler(new AuthenticationFailureHandler());
    //             }
    //     );
    //     // 设置退出登录
    //     http.logout(logoutConfigurer -> logoutConfigurer
    //             // 退出登录的url
    //             .logoutUrl("/logout")
    //             // 退出登录后跳转页面
    //             .logoutSuccessUrl("/login")
    //             // 退出登录后的处理器，注意使用这个之后会覆盖上面的退出登录后跳转页面，避免冲突
    //             .logoutSuccessHandler(new LogoutSuccessHandler())
    //             // 退出登录时删除cookie
    //             .deleteCookies("JSESSIONID", "remember-me")
    //     );
    //
    //     // 设置记住我功能，将Token存储浏览器Cookie中，当关闭浏览器后再次访问，该Token会被读取并用于验证用户的身份，从而无需重新登录。
    //     http.rememberMe(rememberMeConfigurer -> rememberMeConfigurer
    //             // 即使未设置remember-me参数，也设置为记住我或者false勾选也不记住
    //             // .alwaysRemember(true)
    //             // 数据库存储token，实现免登录，默认是TokenBasedRememberMeServices存储在cookie中，
    //             /** {@link RememberMeConfigurer#createRememberMeServices(HttpSecurityBuilder, String)} */
    //             .tokenRepository(persistentTokenRepository())
    //             // 值可以是on|yew|1|true, 就会记住token到cookie，默认remember-me
    //             .rememberMeParameter("remember-me")
    //             // 记住我token有效期，默认：TWO_WEEKS_S = 1209600
    //             .tokenValiditySeconds(AbstractRememberMeServices.TWO_WEEKS_S)
    //     );
    //
    //     // 设置session管理
    //     http.sessionManagement(sessionManagementConfigurer -> sessionManagementConfigurer
    //             // Session失效后跳转到这个链接
    //             // .invalidSessionUrl("/basic/session/invalid")
    //             .invalidSessionUrl("/login")
    //             // 并发登录限制1，配置这个失效，需要重写User对象的equals方法，来对比两个用户的用户名
    //             .maximumSessions(1)
    //             // 只允许一个用户登录，需要配置HttpSessionEventPublisher，默认是SessionRegistryImpl
    //             // .maxSessionsPreventsLogin(true)
    //             // session失效处理器
    //             .expiredSessionStrategy(new SessionInformationExpiredStrategyImpl())
    //     );
    //
    //     // 设置权限不足的异常处理器
    //     http.exceptionHandling(handlingConfigurer -> handlingConfigurer
    //             .accessDeniedHandler(new AccessDeniedHandler())
    //             .authenticationEntryPoint(new ResourceAuthenticationEntryPoint())
    //     );
    //
    //     // todo:有问题, 配置自定义登录过滤器, 执行顺序放在UsernamePasswordAuthenticationFilter之前
    //     // httpSecurity.addFilterBefore(new AuthenticationLoginFilter(authenticationManager()), UsernamePasswordAuthenticationFilter.class);
    //     // http.with(new AuthenticationLoginConfiguration(passwordEncoder, userService), new Customizer<AuthenticationLoginConfiguration>() {
    //     //     @Override
    //     //     public void customize(AuthenticationLoginConfiguration authenticationLoginConfiguration) {
    //     //         System.out.println("123");
    //     //     }
    //     // });
    //     // 将短信验证码认证配置加到 Spring Security 中
    //     // http.addFilterBefore(new AuthenticationLoginFilter(), UsernamePasswordAuthenticationFilter.class);
    //     // http.with(new AuthenticationMobileLoginConfiguration(userService), loginConfiguration -> {
    //     // });
    //
    //     // 添加过滤器
    //     // 图形验证码校验，添加UsernamePasswordAuthenticationFilter前面
    //     http.addFilterBefore(new ValidateCodeFilter(), UsernamePasswordAuthenticationFilter.class);
    //
    //     // 配置自定义token认证过滤器, 根据token获取用户信息后放入到SecurityContext上下文中, 执行顺序放在UsernamePasswordAuthenticationFilter之前
    //     http.addFilterBefore(new TokenAuthenticationFilter(authenticationManager(), redisService)
    //             , UsernamePasswordAuthenticationFilter.class);
    //
    //     // 关闭csrf保护
    //     http.csrf(AbstractHttpConfigurer::disable);
    //     return http.build();
    // }
    //
    // @Bean
    // public HttpSessionEventPublisher httpSessionEventPublisher() {
    //     return new HttpSessionEventPublisher();
    // }
    //
    // /**
    //  * 记住我功能
    //  */
    // @Bean
    // public PersistentTokenRepository persistentTokenRepository() {
    //     JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
    //     jdbcTokenRepository.setDataSource(dataSource);
    //     // 是否启动时自动创建表，第一次启动创建就行，后面启动把这个注释掉,不然报错已存在表，或者执行脚本里的sql
    //     // jdbcTokenRepository.setCreateTableOnStartup(true);
    //     return jdbcTokenRepository;
    // }
    //
    // // @Autowired
    // // private AuthenticationConfiguration authenticationConfiguration;
    //
    // /**
    //  * 配置AuthenticationManager，这个对象是spring security中用户认证的核心对象，它负责用户认证
    //  */
    // // @Bean
    // // public AuthenticationManager authenticationManager() throws Exception {
    // //     return authenticationConfiguration.getAuthenticationManager();
    // // }
    //
    // /**
    //  * 配置AuthenticationManager，另一种配置方式，这个对象是spring security中用户认证的核心对象，它负责用户认证
    //  */
    // @Bean
    // public AuthenticationManager authenticationManager() {
    //     // 身份验证器
    //     DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
    //     // 用户详情服务
    //     daoAuthenticationProvider.setUserDetailsService(userService);
    //     // 密码编码器
    //     daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);
    //     return new ProviderManager(daoAuthenticationProvider);
    // }
}
