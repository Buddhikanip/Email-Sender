package com.email;

import lombok.extern.slf4j.Slf4j;

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

    String username = ""; // paste your email
    String password = ""; // paste email app password;
    String host = "smtp.gmail.com";

    public void setupServerProperties() {
        log.info("Setting up mail server properties...");

        Properties properties = System.getProperties();
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");

        newSession = Session.getDefaultInstance(properties, null);
        log.info("Mail server properties set up successfully.");
    }

    public void draftEmail(String recipient, String company) throws MessagingException, IOException {
//        log.info("Drafting email for recipient: {}", recipient);

        String subject = "Application for Software Engineering Internship at " + company;
        String body = String.format("""
                Dear Sir/Madam,
                                     
                I hope this message finds you well. My name is Buddhika Senanayake, and I am currently a third-year undergraduate studying Computer Science at the University of Colombo School of Computing. I am writing to express my keen interest in securing a Software Engineering internship position at %s.
                                     
                Throughout my academic journey, I have built a strong foundation in software development. By actively participating in hackathons and engaging in a variety of external projects, I have greatly enhanced my practical skills. I am excited to apply this knowledge in a professional environment where I can make meaningful contributions to impactful projects.
                                     
                I am particularly attracted to %s because of its esteemed reputation in the software development industry, and I would welcome the opportunity to learn from your experienced team.
                                     
                I have attached my resume for your review and am available for an interview at your earliest convenience.
                                     
                Thank you for considering my application. I look forward to the possibility of joining %s as a Software Engineering intern and contributing to your team's success.
                                     
                Best regards,
                Buddhika Senanayake.
                +94 71 327 0510
                bnsbuddhika@gmail.com
                https://www.linkedin.com/in/buddhikanip/
                """, company, company, company);
        mimeMessage = new MimeMessage(newSession);

        mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
        mimeMessage.setSubject(subject);

        MimeBodyPart bodyPart = new MimeBodyPart();
        bodyPart.setText(body, "utf-8");

        MimeBodyPart attachmentPart = new MimeBodyPart();
        String attachmentPath = "src/main/resources/Buddhika_resume.pdf"; // Local path
        File resumeFile = new File(attachmentPath);
        if (!resumeFile.exists()) {
            attachmentPath = "/app/resources/Buddhika_resume.pdf"; // Docker path
        }
        attachmentPart.attachFile(attachmentPath);

        MimeMultipart multipart = new MimeMultipart();
        multipart.addBodyPart(bodyPart);
        multipart.addBodyPart(attachmentPart);
        mimeMessage.setContent(multipart);
//        log.info("Email drafted successfully for recipient: {}", recipient);
    }

    public void sendEmail(List<String> recipients, String company) throws MessagingException, IOException {
        try (Transport transport = newSession.getTransport("smtp")) {
            System.out.println();
//            log.info("Connecting to email server...");
            transport.connect(host, username, password);
            log.info("Connected to email server.");

            System.out.println();
            log.info("Sending emails to {}", company);

            for (String recipient : recipients) {
                try {
                    draftEmail(recipient, company);
//                    transport.sendMessage(mimeMessage, mimeMessage.getAllRecipients());
                    log.info("Email sent successfully to {}", recipient);
                } catch (MessagingException e) {
                    log.error("Failed to send email to {}", recipient, e);
                }
            }

            log.info("Emails sent successfully to {}.", company);
        } catch (MessagingException e) {
            log.error("Failed to connect to email server.", e);
        }
    }
}
