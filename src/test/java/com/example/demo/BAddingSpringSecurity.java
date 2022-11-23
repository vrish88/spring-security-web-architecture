package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import java.util.List;

import static com.example.demo.helpers.SpringAppMockMvcHelper.setupMockMvc;
import static com.example.demo.helpers.SpringAppMockMvcHelper.whatDoYouExpectThisToBe;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.function.RouterFunctions.route;

class BAddingSpringSecurity {
    @Configuration
    @EnableWebSecurity(debug = true)
    public static class EmptySecurityConfig {
    }

    @Test
    void emptySpringSecurity() throws Exception {
        MockMvc mockMvc = setupMockMvc(context -> {
            context.registerBean(
                RouterFunction.class,
                () -> route()
                    .GET("/foo", request -> ServerResponse.ok().body("bar"))
                    .build()
            );
            context.registerBean(EmptySecurityConfig.class);
        }).apply(springSecurity()).build();

        mockMvc
            .perform(get("/foo"))
            .andExpect(whatDoYouExpectThisToBe(status()))
            .andExpect(content().string("????"));
    }

    @Configuration
    @EnableWebSecurity(debug = true)
    public static class BasicAuthSecurity {
        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            return http
                .authorizeRequests().anyRequest().authenticated().and()
                .httpBasic().and()
                .csrf().disable()
                .build();
        }

        @Bean
        public UserDetailsService userDetailsService() {
            return new InMemoryUserDetailsManager(
                User.withDefaultPasswordEncoder()
                    .username("mySharona")
                    .password("doDedodoDe")
                    .authorities(List.of())
                    .build()
            );
        }
    }

    @Test
    void basicAuthAndMockMvcHelpers() throws Exception {
        MockMvc mockMvc = setupMockMvc(context -> {
            context.registerBean(
                RouterFunction.class,
                () -> route().GET(
                    "/foo",
                    request -> ServerResponse.ok().body("bar")
                ).build()
            );
            context.registerBean(BasicAuthSecurity.class);
        }).apply(springSecurity()).build();

        mockMvc
            .perform(get("/foo"))
            .andExpect(whatDoYouExpectThisToBe(status()));

        // What's an alternative way to also get past authentication?
        mockMvc
            .perform(get("/foo"))//.with(????)
            .andExpect(status().isOk());
    }
}
