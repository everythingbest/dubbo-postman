/*
 * MIT License
 *
 * Copyright (c) 2019 everythingbest
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.dubbo.postman.config;

import com.dubbo.postman.security.UserDetailsService;
import org.jasig.cas.client.validation.Cas20ServiceTicketValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.cas.ServiceProperties;
import org.springframework.security.cas.authentication.CasAuthenticationProvider;
import org.springframework.security.cas.web.CasAuthenticationEntryPoint;
import org.springframework.security.cas.web.CasAuthenticationFilter;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsByNameServiceWrapper;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

/**
 * @author everythingbest
 * 用户登陆登出授权相关操作
 * 如果启用cas的话把下面的注释去掉即可
 */
/*@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(jsr250Enabled = true)*/
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     * CAS单点登录服务地址
     */
    @Value("${cas.url.prefix}")
    private String SSO_URL;

    @Value("${app.service.home}")
    private String SERVICE_HOME;

    @Autowired
    private UserDetailsService userDetailsService;

    /**
     * Spring Security 基本配置
     * @param httpSecurity
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.exceptionHandling()
                .authenticationEntryPoint(getCasAuthenticationEntryPoint())
                .and().addFilter(casAuthenticationFilter())
                .addFilterBefore(logoutFilter(), LogoutFilter.class)
                .authorizeRequests()
                .antMatchers("/js/**", "/css/**", "/imgs/**","/api/**").permitAll()
                .antMatchers("/external/datasource/**").permitAll()
                .anyRequest().authenticated()
                .and().logout().invalidateHttpSession(true).deleteCookies("SESSION").permitAll()
                .and().csrf().disable();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(casAuthenticationProvider());
    }

    /**
     * 配置CAS登录页面
     */
    public SessionExpireEntryPoint getCasAuthenticationEntryPoint() {
        CasAuthenticationEntryPoint point = new CasAuthenticationEntryPoint();
        point.setLoginUrl(SSO_URL + "/login");
        point.setServiceProperties(serviceProperties());
        SessionExpireEntryPoint entryPoint = new SessionExpireEntryPoint(point);
        return entryPoint;
    }

    /**
     * 认证过滤器
     */
    public CasAuthenticationFilter casAuthenticationFilter() throws Exception {
        CasAuthenticationFilter filter = new CasAuthenticationFilter();
        filter.setAuthenticationManager(authenticationManager());
        filter.setFilterProcessesUrl("/cas_security_check");
        return filter;
    }
    
    public LogoutFilter logoutFilter() {
        LogoutFilter filter = new LogoutFilter(SSO_URL + "/logout"+"?service="+SERVICE_HOME, new SecurityContextLogoutHandler());
        return filter;
    }

//    @Bean
    public CasAuthenticationProvider casAuthenticationProvider() {
        CasAuthenticationProvider provider = new CasAuthenticationProvider();
        provider.setTicketValidator(cas20ServiceTicketValidator());
        provider.setServiceProperties(serviceProperties());
        provider.setKey("an_id_for_this_auth_provider_only");
        provider.setAuthenticationUserDetailsService(userDetailsByNameServiceWrapper());
        return provider;
    }

    private ServiceProperties serviceProperties() {
        ServiceProperties properties = new ServiceProperties();
        properties.setService(SERVICE_HOME+"/cas_security_check");
        properties.setSendRenew(false);
        return properties;
    }

    /**
     * 当CAS认证成功时, Spring Security会自动调用此类对用户进行授权
     */
    private UserDetailsByNameServiceWrapper userDetailsByNameServiceWrapper() {
        UserDetailsByNameServiceWrapper wrapper = new UserDetailsByNameServiceWrapper();
        wrapper.setUserDetailsService(userDetailsService);
        return wrapper;
    }

    private Cas20ServiceTicketValidator cas20ServiceTicketValidator() {
        Cas20ServiceTicketValidator validator = new Cas20ServiceTicketValidator(SSO_URL);
        return validator;
    }
}
