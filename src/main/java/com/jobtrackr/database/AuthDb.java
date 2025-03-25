package com.jobtrackr.database;

import com.jobtrackr.dto.authDto.RegistrationDto;
import com.jobtrackr.errorHandling.ErrorResponseHandler;
import com.jobtrackr.model.User;
import com.jobtrackr.util.DatabaseUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AuthDb {
    private static final Logger logger = LoggerFactory.getLogger(AuthDb.class);
    private static final String SQLErrorMessage = "A SQL error has occurred ";

    public void saveUser(RegistrationDto registrationDto, UUID id, String hashedPassword){
        logger.info("Saving user to database.................................");
        String sql = "INSERT INTO users (id, name, email, job_title_target, created_at, password, isActivated) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, id);
            stmt.setString(2, registrationDto.getLastname() + " " + registrationDto.getFirstname());
            stmt.setString(3, registrationDto.getEmail());
            stmt.setString(4, registrationDto.getJob_title_target());
            stmt.setObject(5, LocalDateTime.now());
            stmt.setString(6, hashedPassword);
            stmt.setBoolean(7,false);
            stmt.executeUpdate();

            logger.info("User has been created :{}", registrationDto);
        } catch (SQLException e) {
            logger.error(SQLErrorMessage+"creating user");
            throw new RuntimeException("Failed to create user: " + e.getMessage(), e);
        }
    }

    public void setUserRoles(String role, UUID user_id, HttpServletResponse response)  {
        logger.info("Before query.........................");
        String sql = "INSERT INTO roles (user_id, roletype) VALUES (?, ?::user_role)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            System.out.println("Role insertion.......................");
            stmt.setObject(1, user_id);
            stmt.setObject(2, role);
            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                logger.info("User role {} successfully created", role);
            } else {
                logger.error("Role insertion failed....................................");
                new ErrorResponseHandler().sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Role not set");
            }

        } catch (SQLException e) {
            logger.error(SQLErrorMessage+"setting user roles");
            throw new RuntimeException("Failed to set user roles: " + e.getMessage(), e);
        }
    }

    public User fetchUserByEmail(String email)  {
        logger.info("Fetching user details............................................");
        User user = new User();
        String userSql = "SELECT * FROM users WHERE email = ?";
        String rolesSql = "SELECT roletype FROM roles WHERE user_id = (Select id from users where email = ?)";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement userStmt = conn.prepareStatement(userSql);
             PreparedStatement rolesStmt = conn.prepareStatement(rolesSql)) {

            userStmt.setString(1, email);
            try (ResultSet rs = userStmt.executeQuery()) {
                if (rs.next()) {
                    user.setId((UUID) rs.getObject("id"));
                    user.setEmail(rs.getString("email"));
                    String[] names = rs.getString("name").split(" ");
                    user.setFirstname(names[0]);
                    user.setLastname(names[1]);
                    user.setJob_title_target(rs.getString("job_title_target"));
                    user.setActivated(rs.getBoolean("isActivated"));
                    user.setPassword(rs.getString("password"));

                    List<String> roles = new ArrayList<>();
                    rolesStmt.setObject(1, email);
                    try (ResultSet rolesRs = rolesStmt.executeQuery()) {
                        while (rolesRs.next()) {
                            roles.add(rolesRs.getString("roletype"));
                        }
                    }
                    user.setRoletype(roles);
                    return user;
                }
            }
        }catch (SQLException e){
            logger.error(SQLErrorMessage+"retrieving user by email ");
            throw new RuntimeException("Failed to fetch user by email : " + e.getMessage(), e);
        }
        return null;
    }

    public User fetchUserById(UUID id){
        logger.info("Fetching user by Id...............................");
        User user = new User();
        String userSql = "SELECT * FROM users WHERE id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement userStmt = conn.prepareStatement(userSql)) {
            userStmt.setObject(1, id);
            try (ResultSet rs = userStmt.executeQuery()) {
                if (rs.next()) {
                    user.setEmail(rs.getString("email"));
                    String[] names = rs.getString("name").split(" ");
                    user.setFirstname(names[0]);
                    user.setLastname(names[1]);
                    user.setJob_title_target(rs.getString("job_title_target"));
                    return user;
                }
            }catch (SQLException e){
                logger.error(SQLErrorMessage+"fetching user by Id");
                throw new RuntimeException("Failed to fetch user by Id: " + e.getMessage(), e);
            }
        }catch (SQLException e){
            logger.error(SQLErrorMessage+"retrieving user using id");
            throw new RuntimeException("Failed to fetch user using id : " + e.getMessage(), e);
        }
        return null;
    }

    public void activateUser(String email, HttpServletResponse response) {
        logger.info("Activating user...............................");
        String sql = "UPDATE users SET isActivated = ? WHERE email = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBoolean(1, true);
            stmt.setString(2, email);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                new ErrorResponseHandler().sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, "User not found");
            }
        } catch (SQLException e) {
            logger.error(SQLErrorMessage+"activating user");
            throw new RuntimeException("Failed to activate: " + e.getMessage(), e);
        }
    }

    public boolean userExists(String email)  {
        logger.info("Checking if user exists......................................");
        String sql = "SELECT * FROM users WHERE email = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return true;
                }
            }
        }catch (SQLException e){
            logger.error(SQLErrorMessage+"activating user.............");
            throw new RuntimeException("Failed to check if user exists: " + e.getMessage(), e);
        }
        return false;
    }
}
