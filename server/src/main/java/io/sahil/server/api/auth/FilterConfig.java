package io.sahil.server.api.auth;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Sahil Saini
 */
@Configuration
public class FilterConfig {
    @Bean
    public FilterRegistrationBean<ApiKeyFilter> filterRegistrationBean(ApiKeyFilter apiKeyFilter) {
        FilterRegistrationBean<ApiKeyFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(apiKeyFilter);
        registrationBean.addUrlPatterns("/*");
        return registrationBean;
    }
}
