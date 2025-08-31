package com.lcsk42.starter.web.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Web Auto Configuration class that sets up common web-related beans and utilities.
 */
public class WebAutoConfiguration {

    /**
     * DispatcherServlet initialization endpoint path.
     */
    public static final String INITIALIZE_PATH = "/initialize/dispatcher-servlet";

    /**
     * Global exception handler to intercept all controller-level exceptions.
     */
    @Bean
    @ConditionalOnMissingBean
    public GlobalExceptionHandler globalExceptionHandler() {
        return new GlobalExceptionHandler();
    }

    /**
     * Global result handler to unify API response format.
     */
    @Bean
    @ConditionalOnMissingBean
    public GlobalResultHandler globalResultHandler() {
        return new GlobalResultHandler();
    }

    /**
     * Initializes a lightweight controller used to trigger DispatcherServlet early.
     */
    @Bean
    public InitializeDispatcherServletController initializeDispatcherServletController() {
        return new InitializeDispatcherServletController();
    }

    /**
     * PortHolder bean to hold the web server port.
     * This is used to initialize the DispatcherServlet early.
     */
    @Bean
    public PortHolder portHolder() {
        return new PortHolder();
    }

    /**
     * RestTemplate bean with custom HTTP client factory.
     */
    @Bean
    public RestTemplate simpleRestTemplate(ClientHttpRequestFactory factory) {
        return new RestTemplate(factory);
    }

    /**
     * Basic ClientHttpRequestFactory with timeout settings.
     * Improves fault tolerance and responsiveness.
     */
    @Bean
    public ClientHttpRequestFactory simpleClientHttpRequestFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setReadTimeout(5 * 1_000);
        factory.setConnectTimeout(5 * 1_000);
        return factory;
    }

    /**
     * CommandLineRunner bean to call the DispatcherServlet initialization endpoint
     * immediately after Spring Boot starts, reducing the first-response latency.
     */
    @Bean
    public InitializeDispatcherServletHandler initializeDispatcherServletHandler(
            RestTemplate simpleRestTemplate,
            PortHolder portHolder,
            ConfigurableEnvironment configurableEnvironment
    ) {
        return new InitializeDispatcherServletHandler(simpleRestTemplate, portHolder, configurableEnvironment);
    }

    /**
     * Basic WebMvcConfigurer implementation for future extensibility (e.g., CORS, formatters, interceptors).
     */
    @Bean
    public WebConfiguration webConfig() {
        return new WebConfiguration();
    }
}
