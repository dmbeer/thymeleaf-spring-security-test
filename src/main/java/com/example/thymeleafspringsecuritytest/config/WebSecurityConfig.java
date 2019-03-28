package com.example.thymeleafspringsecuritytest.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationSuccessHandler;
import org.springframework.security.web.server.authentication.logout.LogoutWebFilter;
import org.springframework.security.web.server.authentication.logout.RedirectServerLogoutSuccessHandler;
import org.springframework.security.web.server.authentication.logout.SecurityContextServerLogoutHandler;
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;

import java.net.URI;

@Configuration
@EnableWebFluxSecurity
public class WebSecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http.csrf().disable().authorizeExchange().pathMatchers(HttpMethod.DELETE).denyAll()
                .pathMatchers("/login", "/logout", "/css/*.css", "/favicon.ico").permitAll()
                .anyExchange().authenticated()
                .and()
                .httpBasic().and()
                .formLogin().loginPage("/login").authenticationSuccessHandler(new RedirectServerAuthenticationSuccessHandler("/page/1"))
                .and()
                .logout().disable()
                .addFilterAt(logoutWebFilter(), SecurityWebFiltersOrder.LOGOUT);
        return http.build();
    }

    private LogoutWebFilter logoutWebFilter() {
        LogoutWebFilter logoutWebFilter = new LogoutWebFilter();

        SecurityContextServerLogoutHandler logoutHandler = new SecurityContextServerLogoutHandler();
//        logoutHandler.setSecurityContextRepository(securityContextRepository());

        RedirectServerLogoutSuccessHandler logoutSuccessHandler = new RedirectServerLogoutSuccessHandler();
        logoutSuccessHandler.setLogoutSuccessUrl(URI.create("/"));

        logoutWebFilter.setLogoutHandler(logoutHandler);
        logoutWebFilter.setLogoutSuccessHandler(logoutSuccessHandler);
        logoutWebFilter.setRequiresLogoutMatcher(
                ServerWebExchangeMatchers.pathMatchers(HttpMethod.GET, "/logout")
        );

        return logoutWebFilter;
    }

    private ServerLogoutSuccessHandler logoutSuccessHandler(String uri) {
        RedirectServerLogoutSuccessHandler successHandler = new RedirectServerLogoutSuccessHandler();
        successHandler.setLogoutSuccessUrl(URI.create(uri));
        return successHandler;
    }

}