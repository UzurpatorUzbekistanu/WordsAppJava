package com.bkleszcz.WordApp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")  // Zezwalaj na wszystkie endpointy
                .allowedOrigins("http://localhost:4200")
                .allowedOrigins("http://localhost:8080")// Domena frontendowa (Angular)
                .allowedMethods("GET", "POST", "PUT", "DELETE")  // Dozwolone metody HTTP
                .allowedHeaders("*")  // Dozwolone nagłówki
                .exposedHeaders("Set-Cookie")
                .allowCredentials(true);  // Pozwala na przesyłanie ciasteczek

    }
}
