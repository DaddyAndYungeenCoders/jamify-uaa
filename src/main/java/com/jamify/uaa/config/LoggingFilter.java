package com.jamify.uaa.config;

import jakarta.servlet.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Component
public class LoggingFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(LoggingFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        logger.debug("LoggingFilter triggered for request URL: {}", ((HttpServletRequest) request).getRequestURL());
        if (request instanceof HttpServletRequest httpRequest) {
            Cookie[] cookies = httpRequest.getCookies();
            if (cookies != null) {
                Arrays.stream(cookies).forEach(cookie ->
                        logger.debug("Received cookie: Name={}, Value={}", cookie.getName(), cookie.getValue())
                );
            } else {
                logger.debug("No cookies received");
            }
            Enumeration<String> headerNames = httpRequest.getHeaderNames();
            Map<String, String> headers = new HashMap<>();

            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                headers.put(headerName, httpRequest.getHeader(headerName));
            }

            // Log the headers
            headers.forEach((key, value) -> logger.debug("{}: {}", key, value));
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // Cleanup code if needed
    }
}