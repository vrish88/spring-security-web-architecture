package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import static com.example.demo.helpers.SpringAppMockMvcHelper.setupMockMvc;
import static com.example.demo.helpers.SpringAppMockMvcHelper.whatDoYouExpectThisToBe;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.function.RouterFunctions.route;


class CMultipleFilterChains {
    @Configuration
    @EnableWebSecurity(debug = true)
    public static class AuthenticatedAndPublicRoutesSecurity {
    }

    @Test
    void authenticatedAndPublicRoutes_makeThisPass() throws Exception {
        MockMvc mockMvc = setupMockMvc((context) -> {
            context.registerBean(
                RouterFunction.class,
                () -> route()
                    .GET("/public/info", r -> ServerResponse.ok().body("Everyone can see this"))
                    .GET("/foo", r -> ServerResponse.ok().body("bar"))
                    .build()
            );
            context.registerBean(AuthenticatedAndPublicRoutesSecurity.class);
        }).apply(springSecurity()).build();

        mockMvc.perform(get("/public/info"))
            .andExpect(status().isOk())
            .andExpect(content().string("Everyone can see this"));

        mockMvc.perform(get("/foo"))
            .andExpect(status().isOk())
            .andExpect(content().string("bar"));
    }

    @Configuration
    @EnableWebSecurity(debug = true)
    public static class AuthenticatedAndPublicRoutesSecurityAlternative {
    }

    @Test
    void authenticatedAndPublicRoutesAlternative_makeThisPass() throws Exception {
        MockMvc mockMvc = setupMockMvc((context) -> {
            context.registerBean(
                RouterFunction.class,
                () -> route()
                    .GET("/public/info", r -> ServerResponse.ok().body("Everyone can see this"))
                    .GET("/foo", r -> ServerResponse.ok().body("bar"))
                    .build()
            );
            context.registerBean(AuthenticatedAndPublicRoutesSecurityAlternative.class);
        }).apply(springSecurity()).build();

        mockMvc.perform(get("/public/info"))
            .andExpect(status().isOk())
            .andExpect(content().string("Everyone can see this"));

        mockMvc.perform(get("/foo"))
            .andExpect(status().isOk())
            .andExpect(content().string("bar"));
    }

    @Configuration
    @EnableWebSecurity
    public static class OverlappingFilterChainsSecurity {
        @Bean
        SecurityFilterChain oneChain(HttpSecurity http) throws Exception {
            return http
                .authorizeRequests().antMatchers("/foo").authenticated().and()
                .build();
        }

        @Bean
        SecurityFilterChain twoChain(HttpSecurity http) throws Exception {
            return http
                .authorizeRequests().antMatchers("/foo").permitAll().and()
                .build();
        }
    }

    @Test
    void overlappingFilterChains() throws Exception {
        MockMvc mockMvc = setupMockMvc((context) -> {
            context.registerBean(
                RouterFunction.class,
                () -> route()
                    .GET("/foo", r -> ServerResponse.ok().body("bar"))
                    .build()
            );
            context.registerBean(OverlappingFilterChainsSecurity.class);
        }).apply(springSecurity()).build();

        mockMvc.perform(get("/foo"))
            .andExpect(whatDoYouExpectThisToBe(status()));
    }
}
