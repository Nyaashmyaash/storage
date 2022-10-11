package com.cdek.storage.config;

import com.cdek.abac.pep.ICdekAbac;
import com.cdek.abac.pep.config.PepConfig;
import com.cdek.storage.infrastructure.security.CdekAbacUserDetailsService;
import com.cdek.storage.infrastructure.security.RestAuthenticationEntryPoint;
import com.cdek.storage.infrastructure.security.provider.SystemUserProvider;
import com.cdek.storage.infrastructure.security.provider.UserProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsByNameServiceWrapper;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.security.web.authentication.preauth.RequestHeaderAuthenticationFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.util.Collections;

@Configuration
@Import({ PepConfig.class, RestConfig.class })
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Configuration
    @Order(1)
    public static class ActuatorConfigurerAdapter extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.requestMatcher(EndpointRequest.toAnyEndpoint()).anonymous();
        }
    }

    @Configuration
    @Order(2)
    @RequiredArgsConstructor
    public static class ApiConfigurerAdapter extends WebSecurityConfigurerAdapter {
        private static final String TOKEN_HEADER_NAME = "X-Auth-Token";

        @Override
        public void configure(WebSecurity web) {
            web.ignoring().mvcMatchers(
                    "/swagger-ui.html",
                    "/v3/**",
                    "/webjars/**",
                    "/swagger-resources/**",
                    "/configuration/ui");

        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.authorizeRequests()
                    .antMatchers("/api/**")
                    .authenticated()
                    .and()
                    .addFilterAfter(tokenHeaderFilter(), RequestHeaderAuthenticationFilter.class)
                    .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                    .csrf()
                    .disable()
                    .exceptionHandling()
                    .authenticationEntryPoint(restAuthenticationEntryPoint(null));
        }

        @Bean
        public RestAuthenticationEntryPoint restAuthenticationEntryPoint(
                @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver) {
            return new RestAuthenticationEntryPoint(resolver);
        }

        @Bean
        protected UserDetailsService userDetailsService(ICdekAbac iCdekAbac) {
            return new CdekAbacUserDetailsService(iCdekAbac);
        }

        @Bean
        public RequestHeaderAuthenticationFilter tokenHeaderFilter() {
            var requestHeaderAuthenticationFilter = new RequestHeaderAuthenticationFilter();
            requestHeaderAuthenticationFilter.setExceptionIfHeaderMissing(false);
            requestHeaderAuthenticationFilter.setPrincipalRequestHeader(TOKEN_HEADER_NAME);
            requestHeaderAuthenticationFilter.setCredentialsRequestHeader(TOKEN_HEADER_NAME);
            requestHeaderAuthenticationFilter.setAuthenticationManager(authenticationManager());
            return requestHeaderAuthenticationFilter;
        }

        @Bean
        @Override
        public AuthenticationManager authenticationManager() {
            return new ProviderManager(Collections.singletonList(preAuthProvider()));
        }

        @Bean
        public PreAuthenticatedAuthenticationProvider preAuthProvider() {
            var preAuthenticatedAuthenticationProvider = new PreAuthenticatedAuthenticationProvider();
            preAuthenticatedAuthenticationProvider
                    .setPreAuthenticatedUserDetailsService(userDetailsServiceWrapper());
            return preAuthenticatedAuthenticationProvider;
        }

        @Bean
        public UserDetailsByNameServiceWrapper<PreAuthenticatedAuthenticationToken> userDetailsServiceWrapper() {
            return new UserDetailsByNameServiceWrapper<>(userDetailsService(null));
        }
    }

    @Bean
    @ConfigurationProperties(prefix = "cdek.system-user")
    public UserProvider systemUserProvider(ICdekAbac iCdekAbac) {
        return new SystemUserProvider(iCdekAbac);
    }
}
