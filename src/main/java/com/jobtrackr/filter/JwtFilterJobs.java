package com.jobtrackr.filter;

import com.jobtrackr.errorHandling.ErrorResponseHandler;
import com.jobtrackr.service.AuthService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.util.List;

@WebFilter("/api/jobs/*")
public class JwtFilterJobs implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(JwtFilterJobs.class);
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException{
        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;
        logger.info("JWT Filter triggered for method: {}", httpRequest.getMethod());
        String authHeader = httpRequest.getHeader("authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            httpResponse.setContentType("application/json");
            httpResponse.setCharacterEncoding("UTF-8");
            new ErrorResponseHandler().sendErrorResponse(httpResponse,HttpServletResponse.SC_UNAUTHORIZED,"Missing or invalid JWT token");
            return;
        }
        logger.info("Token is present in header");
        String token = authHeader.substring(7);
        logger.info("Token has been generated");
        try {
            AuthService authUtil = new AuthService();
            logger.info("Validating token.......................");
            Claims claims = authUtil.validateToken(token);
            httpRequest.setAttribute("email", claims.getSubject());
            httpRequest.setAttribute("user_id", claims.get("user_id", String.class));
            httpRequest.setAttribute("roles", claims.get("roles", List.class));
            try {
                logger.info("Claims: {}", claims);
                logger.info("Email: {}", claims.getSubject());
                logger.info("User ID: {}", claims.get("user_id", String.class));
                logger.info("Roles: {}", claims.get("roles", List.class));

                logger.info("Before calling filterChain.doFilter");
                filterChain.doFilter(servletRequest, servletResponse);
                logger.info("After calling filterChain.doFilter");
            } catch (Exception e) {
                logger.error("Exception in Chain: {}", e.getMessage());
                ((HttpServletResponse) servletResponse).sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT validation failed");
            }
        } catch (SignatureException | IllegalArgumentException e) {
            httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT token");
        } catch (Exception e) {
            httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT validation failed");
        }

    }
}


