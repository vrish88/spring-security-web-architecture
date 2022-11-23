package com.example.demo.helpers;

import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.function.Consumer;

import static org.assertj.core.api.Fail.fail;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.web.servlet.function.RouterFunctions.route;

public class SpringAppMockMvcHelper {
    public static DefaultMockMvcBuilder setupMockMvc(Consumer<GenericApplicationContext> setup) {
        AnnotationConfigServletWebApplicationContext context = new AnnotationConfigServletWebApplicationContext();
        context.setServletContext(new MockServletContext());
        setup.accept(context);
        context.refresh();
        return MockMvcBuilders.webAppContextSetup(context);
    }

    public static <T> ResultMatcher whatDoYouExpectThisToBe(T status) {
        return result -> fail("Fill in your expectation for the " + status.toString());
    }
}
