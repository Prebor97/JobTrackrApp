package com.jobtrackr.util;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;

public class EmailUtil {
    private static final Logger logger = LoggerFactory.getLogger(EmailUtil.class);
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "465";
    private static final String SMTP_USER = "your-mail.com";
    private static final String SMTP_PASSWORD = "your-app-password";

    public String generateToken() {
        Random random = new Random();
        int token = 100000 + random.nextInt(900000);
        logger.info("Token generated");
        return String.valueOf(token);
    }
    public String loadReminderMailTemplate(String company, String title,String email, String name, String description, String priority, UUID task_id)  {
        logger.info("Getting email template for reminder mail.............................................");
        String templatePath = "templates/TaskReminderMail.html"; // Path relative to the classpath
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(templatePath)) {
            if (inputStream == null) {
                throw new IOException("Email template not found at path: " + templatePath);
            }

            StringBuilder templateContent = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    templateContent.append(line).append("\n");
                }
            }

            String temp = templateContent.toString();
            temp = temp.replace("{name}", name);
            temp = temp.replace("{description}", description);
            temp = temp.replace("{priority}", priority);
            temp = temp.replace("{task_id}", task_id.toString());
            temp = temp.replace("{email}", email);
            temp = temp.replace("{company}", company);
            temp = temp.replace("{title}", title);
            logger.info("Mail template for reminder successfully retrieved");
            return temp;
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }
    public String loadEmailTemplate(HttpServletRequest request, String token)  {
        logger.info("Getting email template for activation.............................................");
        String templatePath = "/WEB-INF/templates/ActivationMailTemplate.html";
        try (InputStream inputStream = request.getServletContext().getResourceAsStream(templatePath)) {
            if (inputStream == null) {
                throw new IOException("Email template not found");
            }
            StringBuilder templateContent = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    templateContent.append(line).append("\n");
                }
            }
            logger.info("Mail template for activation successfully retrieved");
            return templateContent.toString().replace("{token}", token);
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    public void sendEmail(String to, String subject, String htmlContent) {
        logger.info("Preparing to send email to: {}", to);
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SMTP_USER, SMTP_PASSWORD);
            }
        });
      try {
          Message message = new MimeMessage(session);
          message.setFrom(new InternetAddress(SMTP_USER));
          message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
          message.setSubject(subject);
          message.setContent(htmlContent, "text/html; charset=utf-8");

          Transport.send(message);
          logger.info("Email sent successfully to: {}", to);
      }catch (MessagingException e){
          logger.error("Failed to send email to: {}", to);
          throw new RuntimeException(e);
      }
    }
}
