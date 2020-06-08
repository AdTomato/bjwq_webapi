package com.authine.cloudpivot.web.api.config;

import com.authine.cloudpivot.web.api.filter.PermitAuthenticationFilter;
import com.authine.cloudpivot.web.api.handler.AccessDeniedHandlerImpl;
import com.authine.cloudpivot.web.api.security.CustomAccessTokenConverter;
import com.authine.cloudpivot.web.api.security.CustomOAuth2AuthExceptionEntryPoint;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.web.header.HeaderWriterFilter;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * @author longhai
 */
@Slf4j
@Configuration
@EnableResourceServer
@SuppressWarnings("unused")
@Order(6)
public class ResourceConfig extends ResourceServerConfigurerAdapter {

    @Value("${cloudpivot.api.oauth.enabled:true}")
    private boolean oauthEnabled;

    @Autowired
    private AccessDeniedHandlerImpl accessDeniedHandler;

    @Autowired
    private CustomOAuth2AuthExceptionEntryPoint point;

    @Autowired
    private PermitAuthenticationFilter permitAuthenticationFilter;

    @Bean
    public TokenStore tokenStore() {
        return new JwtTokenStore(accessTokenConverter());
    }

    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        final JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        final Resource resource = new ClassPathResource("public.txt");
        String publicKey;
        try {
            publicKey = IOUtils.toString(resource.getInputStream(), Charset.defaultCharset());
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
        converter.setVerifierKey(publicKey);
        converter.setAccessTokenConverter(new CustomAccessTokenConverter());
        return converter;
    }

    @Bean
    public ResourceServerTokenServices tokenServices() {
        final DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
        defaultTokenServices.setTokenStore(tokenStore());
        defaultTokenServices.setTokenEnhancer(accessTokenConverter());
        defaultTokenServices.setSupportRefreshToken(true);
        return defaultTokenServices;
    }

    @Override
    public void configure(final HttpSecurity http) throws Exception {
        //开启权限校验
        if (oauthEnabled) {
            http.authorizeRequests()
                    // swagger start
                    .antMatchers("/swagger-ui.html").permitAll()
                    .antMatchers("/doc.html").permitAll()
                    .antMatchers("/swagger-resources/**").permitAll()
                    .antMatchers("/images/**").permitAll()
                    .antMatchers("/webjars/**").permitAll()
                    .antMatchers("/v2/api-docs").permitAll()
                    .antMatchers("/configuration/ui").permitAll()
                    .antMatchers("/configuration/security").permitAll()
                    .antMatchers("/public/**").permitAll()
                    .antMatchers("/externalLink/**").permitAll()
                    .antMatchers("/login/**").permitAll()
                    .antMatchers("/actuator/**").permitAll()
                    //openapi
                    .antMatchers("/openapi/**").hasAuthority("openapi")
                    //生成二维码
                    .antMatchers("/api/qrcode/**").permitAll()
                    .antMatchers("/api/runtime/convert/download").permitAll()
                    // swagger end
                    .antMatchers("/actuator/**", "/monitor/**", "/login/dingtalk", "login/mobile", "login/mobile/ajax", "login/password").permitAll()
                    .antMatchers("/oauth/**").permitAll()
                    .antMatchers("/login/**").permitAll()
                    .antMatchers("/login").permitAll()
                    .antMatchers("/oauth").permitAll()
                    .antMatchers("/logout").permitAll()
                    .antMatchers("/css/**").permitAll()
                    .antMatchers("/js/**").permitAll()
                    .antMatchers("/images/**").permitAll()
                    .antMatchers("/fonts/**").permitAll()
                    .antMatchers("/favicon.*").permitAll()
                    .antMatchers("/api/dingtalk/**").permitAll()

                    // 新增修改客户用户
                    .antMatchers("/controller/clientUser/**").permitAll()
                    // 社保办卡导入
                    .antMatchers("/controller/socialSecurityCard/**").permitAll()
                    // 发送邮件
                    .antMatchers("/controller/sendMailController/**").permitAll()
                    // 数据采集
                    .antMatchers("/controller/collectController/**").permitAll()
                    // 征缴政策
                    .antMatchers("/controller/product/updateProduct").permitAll()
                    // 增减员
                    .antMatchers("/controller/employeeMaintain/**").permitAll()
                    // 上海增员
                    .antMatchers("/controller/shAddEmployee/**").permitAll()
                    // 上海减员
                    .antMatchers("/controller/shDeleteEmployee/**").permitAll()
                    // 全国增减或减员
                    .antMatchers("/controller/nationalDelivery/**").permitAll()
                    // 商保
                    .antMatchers("/controller/businessInsurance/**").permitAll()
                    .antMatchers("/controller/clientController/**").permitAll()
                    .antMatchers("/controller/systemManage/**").permitAll()
                    .antMatchers("/controller/employeeFiles/**").permitAll()
                    // 数据修改
                    .antMatchers("/controller/dataModify/**").permitAll()
                    //政策平台
                    .antMatchers("/controller/policyPlatform/**").permitAll()
                    // 基数采集
                    .antMatchers("/controller/baseCollection/**").permitAll()
                    // 支付管理
                    .antMatchers("/controller/paymentApplication/**").permitAll()
                    // 公告
                    .antMatchers("/controller/announcementController/**").permitAll()
                    // 账单查询导出
                    .antMatchers("/controller/getEnquiryExcel/**").permitAll()
                    // 检查excel表格
                    .antMatchers("/controller/checkExcel/**").permitAll()
                    // 合同变更
                    .antMatchers("/controller/contractChange/**").permitAll()
                    // 公积金申报停缴
                    .antMatchers("/controller/provident/**").permitAll()
                    // 社保申报控制类
                    .antMatchers("/controller/socialSecurityDeclare/**").permitAll()
                    // 批量预派撤离控制层
                    .antMatchers("/controller/preSentLeave/**").permitAll()
                    // 测试类
                    .antMatchers("/controller/test/**").permitAll()
                    // 提交校验
                    .antMatchers("/controller/submitCheckout/**").permitAll()
                    // 下拉关联的时候查询接口
                    .antMatchers("/api/runtime/query/**").permitAll()
                    // 组织机构
                    .antMatchers("/controller/unit/**").permitAll()
                    // 客户个性化设置
                    .antMatchers("/controller/nccps/**").permitAll()
                    // 调基调比
                    .antMatchers("/controller/adjustBaseAndRatio/**").permitAll()
                    //test
                    .antMatchers("/api/licenseExt/**").permitAll()
                    .antMatchers("/v1/dashboard/**").permitAll()
                    .antMatchers("/api/aliyun/download").permitAll()
                    //客户端接口测试
                    .antMatchers("/api/client/**").hasAuthority("AUTH_SYSTEM_MANAGE")
                    //test
                    .antMatchers(HttpMethod.OPTIONS, "/api/**").permitAll()
                    .antMatchers("/api/**").hasAuthority("AUTH_SYSTEM_MANAGE")
                    .anyRequest().authenticated()
                    .and().exceptionHandling().authenticationEntryPoint(point).accessDeniedHandler(accessDeniedHandler);
        } else {
            http.authorizeRequests()
                    .antMatchers("/public/**").permitAll()
                    .antMatchers("/actuator/**").permitAll()
                    .antMatchers("/externalLink/**").permitAll()
                    .antMatchers(HttpMethod.OPTIONS, "/api/**").permitAll()
                    .antMatchers("/api/**").permitAll()
                    .and().exceptionHandling().accessDeniedHandler(accessDeniedHandler);
        }

        http.addFilterBefore(permitAuthenticationFilter, HeaderWriterFilter.class);
    }

    @Override
    public void configure(final ResourceServerSecurityConfigurer config) {
        config.resourceId("api").tokenServices(tokenServices()).tokenStore(tokenStore())
                .authenticationEntryPoint(point).accessDeniedHandler(accessDeniedHandler);
    }

}
