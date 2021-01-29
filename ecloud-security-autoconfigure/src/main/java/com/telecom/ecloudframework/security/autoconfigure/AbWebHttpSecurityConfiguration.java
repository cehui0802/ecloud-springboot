package com.telecom.ecloudframework.security.autoconfigure;


import com.telecom.ecloudframework.base.core.jwt.JWTService;
import com.telecom.ecloudframework.org.api.context.ICurrentContext;
import com.telecom.ecloudframework.security.authentication.AbDaoAuthenticationProvider;
import com.telecom.ecloudframework.security.authentication.AccessDecisionManagerImpl;
import com.telecom.ecloudframework.security.authentication.FilterInvocationSecurityMetadataSourceImpl;
import com.telecom.ecloudframework.security.authentication.JWTAuthenticationFilter;
import com.telecom.ecloudframework.security.authentication.OpenApiAuthenticationFilter;
import com.telecom.ecloudframework.security.authentication.SecurityInterceptor;
import com.telecom.ecloudframework.security.filter.EncodingFilter;
import com.telecom.ecloudframework.security.filter.RefererCsrfFilter;
import com.telecom.ecloudframework.security.filter.RequestThreadFilter;
import com.telecom.ecloudframework.security.filter.SsoFilter;
import com.telecom.ecloudframework.security.filter.XssFilter;
import com.telecom.ecloudframework.security.forbidden.DefaultAccessDeniedHandler;
import com.telecom.ecloudframework.security.forbidden.DefualtAuthenticationEntryPoint;
import com.telecom.ecloudframework.security.login.CustomPwdEncoder;
import com.telecom.ecloudframework.security.login.UserDetailsServiceImpl;
import com.telecom.ecloudframework.security.login.context.LoginContext;
import com.telecom.ecloudframework.security.login.logout.DefualtLogoutSuccessHandler;
import com.telecom.ecloudframework.sys.util.ContextUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DefaultAuthenticationEventPublisher;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.security.SpringSessionBackedSessionRegistry;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;

@EnableConfigurationProperties({AbSecurityProperties.class})
@Configuration
public class AbWebHttpSecurityConfiguration extends WebSecurityConfigurerAdapter {
    @Autowired
    private AbSecurityProperties abSecurityProperties;
    @Autowired
    private ApplicationContext applicationContext;
    CustomPwdEncoder customPwdEncoder = new CustomPwdEncoder();

    public AbWebHttpSecurityConfiguration() {
    }

    @Bean
    public LoginContext loginContext() {
        return new LoginContext();
    }

    @Bean
    public ContextUtil contextUtil(ICurrentContext loginContext) {
        ContextUtil context = new ContextUtil();
        context.setCurrentContext(loginContext);
        return context;
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        if (this.abSecurityProperties.isSessionRegistryBacked()) {
            FindByIndexNameSessionRepository sessionRepository = (FindByIndexNameSessionRepository)this.applicationContext.getBean(FindByIndexNameSessionRepository.class);
            return new SpringSessionBackedSessionRegistry(sessionRepository);
        } else {
            return new SessionRegistryImpl();
        }
    }

    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }

    public XssFilter xssFilter() {
        XssFilter xssFilter = new XssFilter();
        List<String> ingores = new ArrayList();
        String ingroesConfig = this.abSecurityProperties.getXssIngores();
        if (StringUtils.isNotEmpty(ingroesConfig)) {
            ingores = Arrays.asList(ingroesConfig.split(","));
        }

        xssFilter.setIngores((List)ingores);
        return xssFilter;
    }

    public RefererCsrfFilter csrfFilter() {
        RefererCsrfFilter filter = new RefererCsrfFilter();
        List<String> ingores = new ArrayList();
        String ingroesConfig = this.abSecurityProperties.getCsrfIngores();
        if (StringUtils.isNotEmpty(ingroesConfig)) {
            ingores = Arrays.asList(ingroesConfig.split(","));
        }

        filter.setIngores((List)ingores);
        return filter;
    }

    public DefualtLogoutSuccessHandler logoutSuccessHandler() {
        return new DefualtLogoutSuccessHandler();
    }

    public DefaultAccessDeniedHandler accessDeniedHandler() {
        return new DefaultAccessDeniedHandler();
    }

    public DefualtAuthenticationEntryPoint authenticationLoginEntry() {
        return new DefualtAuthenticationEntryPoint();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.exceptionHandling().authenticationEntryPoint(new DefualtAuthenticationEntryPoint());
        http.rememberMe().key("rememberPrivateKey");
        http.logout().logoutUrl("/org/logout").logoutSuccessHandler(new DefualtLogoutSuccessHandler());
        http.addFilterAt(this.csrfFilter(), CsrfFilter.class);
        SecurityInterceptor securityInterceptor = this.abSecurityInterceptor();
        http.addFilterBefore(securityInterceptor, FilterSecurityInterceptor.class);
        http.addFilterBefore(new RequestThreadFilter(), CsrfFilter.class);
        http.addFilterBefore(new EncodingFilter(), CsrfFilter.class);
        http.addFilterBefore(this.jwtAuthenticationFilter(), LogoutFilter.class);
        http.addFilterBefore(this.ssoFilter(), JWTAuthenticationFilter.class);
        http.addFilterBefore(this.openApiAuthenticationFilter(), SsoFilter.class);
        http.exceptionHandling().accessDeniedHandler(this.accessDeniedHandler());
        http.headers().frameOptions().disable();
        http.csrf().disable();
        http.sessionManagement().maximumSessions(this.abSecurityProperties.getMaximumSessions()).sessionRegistry(this.sessionRegistry());
    }

    @Override
    public void configure(WebSecurity web) {
    }

    @Bean({"abJWTAuthenticationFilter"})
    public JWTAuthenticationFilter jwtAuthenticationFilter() {
        return new JWTAuthenticationFilter();
    }

    @Bean({"abJWTService"})
    protected JWTService abJwtService() {
        return new JWTService();
    }

    @Bean
    protected AccessDecisionManager accessDecisionManager() {
        return new AccessDecisionManagerImpl();
    }

    @Bean
    protected FilterInvocationSecurityMetadataSource securityMetadataSource() {
        FilterInvocationSecurityMetadataSourceImpl securityMetadataSource = new FilterInvocationSecurityMetadataSourceImpl();
        List<String> ingores = new ArrayList();
        String ingroesConfig = this.abSecurityProperties.getAuthIngores();
        if (StringUtils.isNotEmpty(ingroesConfig)) {
            ingores = Arrays.asList(ingroesConfig.split(","));
        }

        securityMetadataSource.setIngores((List)ingores);
        return securityMetadataSource;
    }

    @Override
    @Bean({"userDetailsService"})
    public UserDetailsService userDetailsService() {
        return new UserDetailsServiceImpl();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(this.userDetailsService()).passwordEncoder(this.customPwdEncoder);
    }

    @Bean
    public AbDaoAuthenticationProvider abDaoAuthenticationProvider() {
        AbDaoAuthenticationProvider abDaoAuthenticationProvider = new AbDaoAuthenticationProvider();
        abDaoAuthenticationProvider.setUserDetailsService(this.userDetailsService());
        abDaoAuthenticationProvider.setPasswordEncoder(this.customPwdEncoder);
        return abDaoAuthenticationProvider;
    }

    @Override
    @Bean({"authenticationManager"})
    public AuthenticationManager authenticationManagerBean() {
        ProviderManager providerManager = new ProviderManager(Collections.singletonList(this.abDaoAuthenticationProvider()));
        providerManager.setAuthenticationEventPublisher(new DefaultAuthenticationEventPublisher(this.applicationContext));
        return providerManager;
    }

    @Bean({"securityInterceptor"})
    protected SecurityInterceptor abSecurityInterceptor() {
        SecurityInterceptor intercept = new SecurityInterceptor();
        intercept.setAccessDecisionManager(new AccessDecisionManagerImpl());
        intercept.setSecurityMetadataSource(this.securityMetadataSource());
        return intercept;
    }

    @Bean({"localeResolver"})
    public CookieLocaleResolver cookieLocaleResolver() {
        CookieLocaleResolver cookieLocaleResolver = new CookieLocaleResolver();
        cookieLocaleResolver.setDefaultLocale(Locale.CHINA);
        return cookieLocaleResolver;
    }

    public SsoFilter ssoFilter() {
        SsoFilter ssoFilter = new SsoFilter();
        List<String> ingores = new ArrayList();
        String ingroesConfig = this.abSecurityProperties.getSsoIngores();
        if (StringUtils.isNotEmpty(ingroesConfig)) {
            ingores = Arrays.asList(ingroesConfig.split(","));
        }

        ssoFilter.setIngores((List)ingores);
        return ssoFilter;
    }

    @Bean({"openApiAuthenticationFilter"})
    public OpenApiAuthenticationFilter openApiAuthenticationFilter() {
        return new OpenApiAuthenticationFilter();
    }
}

