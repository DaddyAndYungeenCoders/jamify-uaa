//package com.jamify.uaa.config;
//
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.Cookie;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
//import org.springframework.stereotype.Component;
//
//import java.io.IOException;
//
//@Component
//public class CookieAuthenticationFilter extends AbstractPreAuthenticatedProcessingFilter {
//
//    private static final Logger logger = LoggerFactory.getLogger(CookieAuthenticationFilter.class);
//
//    @Override
//    protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
//        Cookie[] cookies = request.getCookies();
//        if (cookies != null) {
//            for (Cookie cookie : cookies) {
//                if ("jamify-auth".equals(cookie.getName())) {
//                    logger.debug("Found authentication cookie: {}", cookie.getValue());
//                    return cookie.getValue();
//                }
//            }
//        }
//        logger.debug("No authentication cookie found");
//        return null;
//    }
//
//    @Override
//    protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
//        return "N/A";
//    }
//
//    @Override
//    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, Authentication authResult) throws IOException, ServletException {
//        super.successfulAuthentication(request, response, authResult);
//        SecurityContextHolder.getContext().setAuthentication(authResult);
//    }
//
//    @Override
//    @Autowired
//    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
//        super.setAuthenticationManager(authenticationManager);
//    }
//}