package com.hayden.hap.config;

import com.hayden.hap.common.utils.gzip.GZIPFilter;
import org.owasp.esapi.filters.ClickjackFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.util.Collections;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<CharacterEncodingFilter> characterEncodingFilter() {
        CharacterEncodingFilter filter = new CharacterEncodingFilter();
        filter.setEncoding("UTF-8");
        filter.setForceEncoding(true);
        FilterRegistrationBean<CharacterEncodingFilter> registrationBean = new FilterRegistrationBean<>(filter);
        registrationBean.setUrlPatterns(Collections.singletonList("/*"));
        registrationBean.setOrder(0);
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<ClickjackFilter> clickjackFilter() {
        ClickjackFilter filter = new ClickjackFilter();
        FilterRegistrationBean<ClickjackFilter> registrationBean = new FilterRegistrationBean<>(filter);
        registrationBean.setUrlPatterns(Collections.singletonList("/*"));
        registrationBean.addInitParameter("mode", "SAMEORIGIN");
        registrationBean.setOrder(1);
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<GZIPFilter> gzipFilter() {
        FilterRegistrationBean<GZIPFilter> registrationBean = new FilterRegistrationBean<>(new GZIPFilter());
        registrationBean.setUrlPatterns(Collections.singletonList("/*"));
        registrationBean.setOrder(2);
        return registrationBean;
    }
}
