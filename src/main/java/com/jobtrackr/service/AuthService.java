package com.jobtrackr.service;

import com.jobtrackr.dto.emailDto.SendTokenDto;
import com.jobtrackr.dto.emailDto.TokenEntry;
import com.jobtrackr.dto.emailDto.VerificationTokenDetailsDto;
import com.jobtrackr.dto.authDto.LoginDetailsDto;
import com.jobtrackr.dto.userDto.MyProfile;
import com.jobtrackr.dto.authDto.RegistrationDto;
import com.jobtrackr.errorHandling.ErrorResponseHandler;
import com.jobtrackr.errorHandling.SuccessResponseHelper;
import com.jobtrackr.model.User;
import com.jobtrackr.database.AuthDb;
import com.jobtrackr.util.EmailUtil;
import com.jobtrackr.util.JsonMapperUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.validator.routines.EmailValidator;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.SecretKey;
import java.util.*;

public class AuthService {
    private final JsonMapperUtil jsonMapperUtil = new JsonMapperUtil();
    private final AuthDb authDbUtil = new AuthDb();
    private final EmailUtil emailUtil = new EmailUtil();
    private final static String secretKey = "your-secret-key";
    private final static long EXPIRATION_TIME = 1000 * 60 * 60;
    private final static SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes());
    private final EmailValidator emailValidator = EmailValidator.getInstance();
    private static final Map<String, TokenEntry> tokenStore = new HashMap<>();
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    public void registerUser(HttpServletRequest request, HttpServletResponse response) {
        RegistrationDto registrationDto = jsonMapperUtil.getRequestBody(request, RegistrationDto.class);

        if (registrationDto.getFirstname() == null || registrationDto.getFirstname().trim().isEmpty() ||
                registrationDto.getLastname() == null || registrationDto.getLastname().trim().isEmpty() ||
                registrationDto.getEmail() == null || registrationDto.getEmail().trim().isEmpty())  {
            new ErrorResponseHandler().sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "All fields required");
            return;
        }
        if (!Objects.equals(registrationDto.getPassword(), registrationDto.getConfirmPassword())){
            new ErrorResponseHandler().sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Password mismatched");
            return;
        }
        if (!emailValidator.isValid(registrationDto.getEmail())) {
            new ErrorResponseHandler().sendErrorResponse(response,HttpServletResponse.SC_BAD_REQUEST,"Invalid email format");
            return;
        }

        if (authDbUtil.userExists(registrationDto.getEmail())) {
                new ErrorResponseHandler().sendErrorResponse(response,HttpServletResponse.SC_BAD_REQUEST,"User already exists");
        }

        String email = registrationDto.getEmail();
        String hashedPassword = BCrypt.hashpw(registrationDto.getPassword(), BCrypt.gensalt());
        UUID id = UUID.randomUUID();
        authDbUtil.saveUser(registrationDto,id,hashedPassword);
        List<String> roles = new ArrayList<>();
        roles.add("user");
        String jwt = createJwt(id,roles,email);
        logger.info("JWT created for registered user");
        for (String role : roles) {
            authDbUtil.setUserRoles(role, id, response);
        }
        String token = emailUtil.generateToken();
        tokenStore.put(registrationDto.getEmail(), new TokenEntry(token, System.currentTimeMillis()));
        String emailContent = emailUtil.loadEmailTemplate(request, token);
        emailUtil.sendEmail(registrationDto.getEmail(), "Email Verification", emailContent);
        response.setHeader("Authorization", "Bearer " + jwt);
        new SuccessResponseHelper<String>().sendSuccessResponseWithData(response,HttpServletResponse.SC_CREATED,"Registration successful",jwt);
    }

    public void loginUser(HttpServletRequest request, HttpServletResponse response)  {
        LoginDetailsDto loginDetails = jsonMapperUtil.getRequestBody(request, LoginDetailsDto.class);
        String email = loginDetails.getEmail();
        User user = authDbUtil.fetchUserByEmail(email);
        if (user==null){
            new ErrorResponseHandler().sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, "User not found");
            return;
        }
        if (!user.isActivated()) {
            new ErrorResponseHandler().sendErrorResponse(response, HttpServletResponse.SC_FORBIDDEN, "User not activated");
            return;
        }
        logger.info("User ID before JWT creation: {}", user.getId());
        if (BCrypt.checkpw(loginDetails.getPassword(), user.getPassword())) {
            String jwt = createJwt(user.getId(), user.getRoleTypes(), email);
            logger.info("JWT created successfully for logged in user");
            response.setHeader("Authorization", "Bearer " + jwt);
            new SuccessResponseHelper<String>().sendSuccessResponseWithData(response, HttpServletResponse.SC_OK, "Login successful", jwt);
        } else {
            new ErrorResponseHandler().sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid email or password");
        }
    }

    public void verifyToken(HttpServletRequest request, HttpServletResponse response){
        VerificationTokenDetailsDto verifyRequest = jsonMapperUtil.getRequestBody(request, VerificationTokenDetailsDto.class);
        String email = verifyRequest.getEmail();
        String token = verifyRequest.getToken();
        if (email == null || token == null || email.trim().isEmpty() || token.trim().isEmpty()) {
            new ErrorResponseHandler().sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Email and token are required");
            return;
        }
        TokenEntry entry = tokenStore.get(email);
        if (entry == null) {
            new ErrorResponseHandler().sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, "No token found for this email");
            return;
        }

        long currentTime = System.currentTimeMillis();
        if (entry.getToken().equals(token) && (currentTime - entry.getTimestamp() <= EXPIRATION_TIME)) {
            authDbUtil.activateUser(email, response);
            tokenStore.remove(email);
            response.setContentType("application/json");
                    new SuccessResponseHelper<>().sendSuccessResponseWithoutData(response,HttpServletResponse.SC_OK, "Email verified successfully");
        } else if (currentTime - entry.getTimestamp() > EXPIRATION_TIME) {
            tokenStore.remove(email);
            new ErrorResponseHandler().sendErrorResponse(response, HttpServletResponse.SC_FORBIDDEN, "Token has expired");
        } else {
            new ErrorResponseHandler().sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
        }
    }

    public void resendVerificationMail(HttpServletRequest request, HttpServletResponse response) {
        SendTokenDto tokenDto = jsonMapperUtil.getRequestBody(request,SendTokenDto.class);
        String token = emailUtil.generateToken();
        tokenStore.put(tokenDto.getEmail(), new TokenEntry(token, System.currentTimeMillis()));
        String emailContent = emailUtil.loadEmailTemplate(request, token);
        emailUtil.sendEmail(tokenDto.getEmail(), "Email Verification", emailContent);
        new SuccessResponseHelper<>().sendSuccessResponseWithoutData(response,HttpServletResponse.SC_OK, "Email verified successfully");
    }

    public void getMyProfile(HttpServletRequest request, HttpServletResponse response){
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            new ErrorResponseHandler().sendErrorResponse(response, HttpServletResponse.SC_FORBIDDEN, "Error retrieving token");
            return;
        }

        MyProfile myProfile = new MyProfile();
        UUID id = getIdFromToken(authHeader);
        if (id == null) {
            new ErrorResponseHandler().sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired token");
            return;
        }

        logger.info("User about to be created......");
        User user =  authDbUtil.fetchUserById(id);

        if (user == null) {
            new ErrorResponseHandler().sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, "User not found");
            return;
        }

        logger.info("User already mapped to db......");
        if (!user.isActivated()) {
            new ErrorResponseHandler().sendErrorResponse(response, HttpServletResponse.SC_FORBIDDEN, "User not activated");
            return;
        }

        logger.info("My Profile dto about to be created......");
        myProfile.setEmail(user.getEmail());
        myProfile.setLastname(user.getLastname());
        myProfile.setFirstname(user.getFirstname());
        myProfile.setJob_title_target(user.getJob_title_target());
        logger.info("My profile dto created......");
        new SuccessResponseHelper<MyProfile>().sendSuccessResponseWithData(response,HttpServletResponse.SC_OK, "Email verified successfully", myProfile);
    }

    public UUID getIdFromToken(String headerString) {
        String token = headerString.replace("Bearer ", "");

        try {
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            System.out.println("JWT Claims: " + claims);

            Object idObject = claims.get("user_id");
            logger.info("Extracted user_id: {}", idObject);

            if (idObject instanceof String userIdStr) {
                try {
                    return UUID.fromString(userIdStr);
                } catch (IllegalArgumentException e) {
                    logger.error("Invalid UUID format in JWT: {}", userIdStr);
                    throw new IllegalArgumentException("Invalid UUID format in JWT");
                }
            } else {
                throw new IllegalArgumentException("user_id must be a String representing a UUID");
            }
        } catch (JwtException | IllegalArgumentException e) {
            throw new RuntimeException(e);
        }
    }

    private List<String> getRolesFromToken(String token) {
        /**
          This method is meant for authorising user by roles by fetching roles from jwt and
         checking in the filter
         **/

        try {
            Object rolesObj = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .get("roles");

            if (rolesObj instanceof List<?> rawList) {
                List<String> roles = new ArrayList<>();
                for (Object item : rawList) {
                    if (item instanceof String) {
                        roles.add((String) item);
                    } else {
                        throw new IllegalArgumentException("Role list contains non-String elements");
                    }
                }
                return roles;
            } else {
                throw new IllegalArgumentException("Roles claim is not a list");
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse roles from token", e);
        }
    }

    public Claims validateToken(String token){
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            throw new JwtException("JWT token has expired", e);
        } catch (SignatureException e) {
            throw new JwtException("Invalid JWT signature", e);
        } catch (JwtException e) {
            throw new JwtException("JWT validation failed", e);
        }
    }

    private String createJwt(UUID id, List<String> roles, String email){
        logger.info("Generating JWT...................................................");
        return Jwts.builder()
                .subject(email)
                .claim("user_id", id)
                .claim("roles", roles)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + AuthService.EXPIRATION_TIME))
                .signWith(AuthService.key)
                .compact();
    }

}
