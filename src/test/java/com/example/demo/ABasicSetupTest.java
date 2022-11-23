package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebApplicationContext;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.function.EntityResponse;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import javax.servlet.Filter;

import static com.example.demo.helpers.SpringAppMockMvcHelper.setupMockMvc;
import static com.example.demo.helpers.SpringAppMockMvcHelper.whatDoYouExpectThisToBe;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.web.servlet.function.RouterFunctions.route;

class ABasicSetupTest {
    @RestController
    public static class MyController {
        public MyController() {
        }

        @GetMapping("/foo")
        public String asdf() {
            return "bar";
        }
    }

    @Test
    void classControllers() throws Exception {
        AnnotationConfigServletWebApplicationContext context = new AnnotationConfigServletWebApplicationContext();
        context.setServletContext(new MockServletContext());
        context.registerBean(MyController.class);
        context.refresh();

        MockMvcBuilders.webAppContextSetup(context)
            .build()
            .perform(get("/foo"))
            .andExpect(whatDoYouExpectThisToBe(content()));
    }

    @Test
    void routerFunctions() throws Exception {
        MockMvc mockMvc = setupMockMvc(context -> context.registerBean(
            RouterFunction.class,
            () -> route()
                .GET("/foo", request -> ServerResponse.ok().body("bar"))
                .build()
        )).build();

        mockMvc
            .perform(get("/foo"))
            .andExpect(whatDoYouExpectThisToBe(content()));
    }

    @Test
    void routerFunctionsWithFilter() throws Exception {
        MockMvc mockMvc = setupMockMvc(context -> {
            context.registerBean(RouterFunction.class, () ->
                route()
                    .GET("/foo", request -> ServerResponse.ok().body("bar"))
                    .filter((request, next) -> EntityResponse.fromObject("BAR!").build())
                    .build());
        }).build();
        mockMvc.perform(get("/foo"))
            .andExpect(whatDoYouExpectThisToBe(content()));
    }

    @Test
    void filterClass() throws Exception {
        MockMvc mockMvc = setupMockMvc(context -> {
            context.registerBean(RouterFunction.class, () ->
                route()
                    .GET("/foo", request -> ServerResponse.ok().body("bar"))
                    .build());
            context.registerBean(Filter.class, () -> (request, response, chain) -> {
                response.getWriter().write("HIII");
                response.getWriter().close();
            });
        }).build();

        mockMvc
            .perform(get("/foo"))
            .andExpect(whatDoYouExpectThisToBe(content()));
    }
}
