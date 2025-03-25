package com.jobtrackr.util;

import jakarta.servlet.http.HttpServletResponse;

public class HeaderUtil {
    public void setSecurityHeaders(HttpServletResponse response) {
        response.setHeader("X-Content-Type-Options", "nosniff");
        response.setHeader("X-Frame-Options", "DENY");
        response.setHeader("Content-Security-Policy", "default-src 'self'");
        response.setContentType("application/json");
    }
}
