package com.jobtrackr.servlet;

import com.jobtrackr.errorHandling.ErrorResponseHandler;
import com.jobtrackr.util.HeaderUtil;
import com.jobtrackr.service.AuthService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/api/auth/*")
public class AuthServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        HeaderUtil headerUtil = new HeaderUtil();
        headerUtil.setSecurityHeaders(response);
        String path = request.getPathInfo();
            if (path!=null){
                AuthService authUtil = new AuthService();
                switch (path) {
                    case "/register" -> {
                        try {
                            authUtil.registerUser(request, response);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                    case "/login" -> {
                        try {
                            authUtil.loginUser(request, response);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                    case "/verify" -> {
                        try {
                            authUtil.verifyToken(request, response);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                    case "/resend-token" ->
                        authUtil.resendVerificationMail(request, response);
                    default ->
                        new ErrorResponseHandler().sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid url pattern");
                }
            }else {
                new ErrorResponseHandler().sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST,"Invalid url pattern");
            }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        HeaderUtil headerUtil = new HeaderUtil();
        headerUtil.setSecurityHeaders(response);
        AuthService authUtil = new AuthService();
        String path = request.getPathInfo();
        if (path.equals("/me")){
                authUtil.getMyProfile(request,response);
        }else {
            new ErrorResponseHandler().sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST,"Invalid url pattern");
        }
    }
}
