package com.email;

import lombok.extern.slf4j.Slf4j;

import java.nio.file.Files;
import java.nio.file.Paths;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

@Slf4j
public class Mail {
    Session newSession = null;
    MimeMessage mimeMessage = null;

    public void setupServerProperties() {
        log.info("Setting up mail server properties...");

        Properties properties = System.getProperties();
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");

        newSession = Session.getDefaultInstance(properties, null);
        log.info("Mail server properties set up successfully.");
    }

    public void draftEmail(String recipient, String company, User user) throws MessagingException, IOException {
        String subject = user.emailSubject().replace("{company}", company);

        // Read the cover letter
        String templatePath = String.format("src/main/resources/%s", user.coverLetter()); // Local path
        if (!new File(templatePath).exists()) {
            templatePath = String.format("/app/resources/%s", user.coverLetter()); // Docker path
        }
        String bodyTemplate = Files.readString(Paths.get(templatePath));

        // Replace placeholders with actual values
        String body = bodyTemplate.replace("{name}", user.name())
                .replace("{company}", company)
                .replace("{phone}", user.phone())
                .replace("{email}", user.email())
                .replace("{linkedIn}", user.linkedIn());

        mimeMessage = new MimeMessage(newSession);
        mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
        mimeMessage.setSubject(subject);

        MimeBodyPart bodyPart = new MimeBodyPart();
        bodyPart.setText(body, "utf-8");

        MimeBodyPart attachmentPart = new MimeBodyPart();
        String attachmentPath = String.format("src/main/resources/%s", user.resume()); // Local path
        File resumeFile = new File(attachmentPath);
        if (!resumeFile.exists()) {
            attachmentPath = String.format("/app/resources/%s", user.resume()); // Docker path
        }
        attachmentPart.attachFile(attachmentPath);

        MimeMultipart multipart = new MimeMultipart();
        multipart.addBodyPart(bodyPart);
        multipart.addBodyPart(attachmentPart);
        mimeMessage.setContent(multipart);
    }

    public void sendEmail(List<String> recipients, String company, EmailServer emailServer, User user) throws MessagingException, IOException {
        try (Transport transport = newSession.getTransport("smtp")) {
            System.out.println();
            transport.connect(emailServer.host(), emailServer.username(), emailServer.password());

            log.info("Sending emails to {}", company);
            for (String recipient : recipients) {
                try {
                    draftEmail(recipient, company, user);
                    transport.sendMessage(mimeMessage, mimeMessage.getAllRecipients());
                    log.info("Email sent successfully to {}", recipient);
                } catch (MessagingException e) {
                    log.error("Failed to send email to {}", recipient, e);
                    throw e;
                }
            }

            log.info("Emails sent successfully to {}.", company);
        } catch (MessagingException e) {
            log.error("Failed to connect to email server.", e);
            throw e;
        }
    }
}
