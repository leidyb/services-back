package com.bernate.services_back.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Value("${app.upload.dir:./uploads_default}")
    private String uploadDirRootProperty;


    public static final String PRODUCT_IMAGE_SUBDIRECTORY = "product-images"; 
    public static final String SERVICE_IMAGE_SUBDIRECTORY = "service-images"; 


    public static final String BASE_URL_UPLOAD_PATH = "/uploads"; 


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        String productResourceHandlerPath = BASE_URL_UPLOAD_PATH + "/" + PRODUCT_IMAGE_SUBDIRECTORY + "/**";
        Path absoluteProductDiskPath = Paths.get(uploadDirRootProperty, PRODUCT_IMAGE_SUBDIRECTORY).toAbsolutePath().normalize();
        String productResourceLocations = "file:" + absoluteProductDiskPath.toString() + "/";

        registry.addResourceHandler(productResourceHandlerPath)
                .addResourceLocations(productResourceLocations); 

        System.out.println("MvcConfig: Sirviendo imágenes de PRODUCTOS desde URL -> " + productResourceHandlerPath);
        System.out.println("MvcConfig: Mapeado a ruta física -> " + productResourceLocations);


        String serviceResourceHandlerPath = BASE_URL_UPLOAD_PATH + "/" + SERVICE_IMAGE_SUBDIRECTORY + "/**";
        Path absoluteServiceDiskPath = Paths.get(uploadDirRootProperty, SERVICE_IMAGE_SUBDIRECTORY).toAbsolutePath().normalize();
        String serviceResourceLocations = "file:" + absoluteServiceDiskPath.toString() + "/";

        registry.addResourceHandler(serviceResourceHandlerPath)
                .addResourceLocations(serviceResourceLocations);

        System.out.println("MvcConfig: Sirviendo imágenes de SERVICIOS desde URL -> " + serviceResourceHandlerPath);
        System.out.println("MvcConfig: Mapeado a ruta física -> " + serviceResourceLocations);
    }
}