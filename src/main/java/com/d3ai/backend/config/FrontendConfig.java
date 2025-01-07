package com.d3ai.backend.config;

import java.io.IOException;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

@Configuration
public class FrontendConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        // By setting this, you instruct Spring to prioritize this handler above the
        // default one (which is order 0), obviously don't do this. But it's good to
        // understand.
        // -- registry.setOrder(-1);

    	 registry
         // Serve static files from the /static directory
         .addResourceHandler("/**")
         .addResourceLocations("classpath:/static/")
         .resourceChain(true)
         .addResolver(new PathResourceResolver() {
             @Override
             protected Resource getResource(String resourcePath, Resource location) throws IOException {
                 Resource requestedResource = location.createRelative(resourcePath);

                 // Serve the requested file if it exists and is readable
                 if (requestedResource.exists() && requestedResource.isReadable()) {
                     return requestedResource;
                 }

                 // Fallback to index.html for React routes
                 return new ClassPathResource("/static/index.html");
             }
         });
}
}
