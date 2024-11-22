package com.modulos.tokenBucket.config;

import com.modulos.sanitizacion.interceptor.SanitizationInterceptor;
import com.modulos.tokenBucket.interceptor.RateLimitInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    private final RateLimitInterceptor rateLimitInterceptor;
    private final SanitizationInterceptor sanitizationInterceptor;

    public WebMvcConfig(RateLimitInterceptor rateLimitInterceptor, SanitizationInterceptor sanitizationInterceptor) {
        this.rateLimitInterceptor = rateLimitInterceptor;
        this.sanitizationInterceptor = sanitizationInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(sanitizationInterceptor).addPathPatterns("/**");
        registry.addInterceptor(rateLimitInterceptor)
                .addPathPatterns("/api/**"); // Aplica el rate limit a todas las rutas /api/

    }
}
