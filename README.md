## Spring Web Security Guided By Tests!!!

The intent of this repository is to explore the architecture of Spring Security for web based applications. I would highly
recommend the [Spring Security Architecture](https://spring.io/guides/topicals/spring-security-architecture) document
maintained by the Spring Security team.

This repository utilizes the [tests](src/test/java/com/example/demo) to work through the concepts. All the tests are failing.
Some tests can be made to pass by providing the expected implementation. Other tests will be made to pass through filling
in the proper expectation. These tests use the `whatDoYouExpectThisToBe` method in their expectation. For example:

```java
        mockMvc
            .perform(get("/foo"))
            .andExpect(whatDoYouExpectThisToBe(content()));
```

Can be made to pass through removing the wrapping `whatDoYouExpectThisToBe` and adding the expectation:

```java
        mockMvc
            .perform(get("/foo"))
            .andExpect(content().string("bar"));
```
