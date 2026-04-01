package com.campana.said_urban_backend;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Obtenemos la ruta absoluta de la carpeta de tu proyecto
        String userDir = System.getProperty("user.dir");

        // Esto mapea la URL /uploads/** a la carpeta física /uploads/
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + userDir + "/uploads/");
    }
}