package com.email;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class Main {
    private static final Dotenv dotenv = Dotenv.load();

    private static final String emailUsername = getEnv("EMAIL_USERNAME");
    private static final String emailPassword = getEnv("EMAIL_PASSWORD");
    private static final String emailHost = getEnv("EMAIL_HOST");
    private static final String emailPort = getEnv("EMAIL_PORT");

    private static final String emailSubject = getEnv("EMAIL_SUBJECT");
    private static final String emailBody = getEnv("EMAIL_BODY");

    private static final String name = getEnv("NAME");
    private static final String phone = getEnv("PHONE");
    private static final String email = getEnv("EMAIL");
    private static final String linkedIn = getEnv("LINKEDIN");

    private static final String resume = getEnv("RESUME");

    public static void main(String[] args) {
        try {
            EmailServer emailServer = new EmailServer(emailHost, emailUsername, emailPassword);
            User user = new User(name, phone, email, linkedIn, emailSubject, emailBody, resume);

            Mail mail = new Mail();
            mail.setupServerProperties(emailPort);

            List<String[]> emailData = readCsv();
            List<String[]> updatedData = new ArrayList<>();

            for (String[] row : emailData) {
                if (row.length < 2) {
                    log.warn("Skipping invalid row: {}", Arrays.toString(row));
                    updatedData.add(updateRowStatus(row, "Invalid row"));
                    continue;
                }

                String status = row[0];
                if ("Sent".equalsIgnoreCase(status)) {
                    log.info("Skipping already processed row: {}", Arrays.toString(row));
                    updatedData.add(row);
                    continue;
                }

                String company = row[1];
                List<String> recipients = Arrays.stream(row, 2, row.length)
                        .filter(cell -> cell.contains("@"))
                        .collect(Collectors.toList());

                if (recipients.isEmpty()) {
                    log.warn("No valid email recipients for company: {}", company);
                    updatedData.add(updateRowStatus(row, "No recipients"));
                    continue;
                }

                try {
                    mail.sendEmail(recipients, company, emailServer, user);
                    updatedData.add(updateRowStatus(row, "Sent"));
                } catch (Exception e) {
                    updatedData.add(updateRowStatus(row, "Failed"));
                }
            }
            writeResultCsv(updatedData);
        } catch (Exception e) {
            log.error("An error occurred during execution: {}", e.getMessage());
            e.printStackTrace();
        }
    }

    private static String getEnv(String key) {
        String value = dotenv.get(key);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Environment variable " + key + " is missing or empty.");
        }
        return value.trim();
    }

    private static List<String[]> readCsv() throws IOException {
        try (InputStream inputStream = Main.class.getClassLoader().getResourceAsStream("Emails.csv")) {
            assert inputStream != null;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

                return reader.lines()
                        .map(line -> line.split(","))
                        .collect(Collectors.toList());
            }
        }
    }

    private static void writeResultCsv(List<String[]> data) throws IOException {
        String resourcePath = "src/main/resources/Emails.csv"; // local path
        File resourceFile = new File(resourcePath);
        if (!resourceFile.exists()) {
            resourcePath = "app/resources/Emails.csv"; // docker path
            resourceFile = new File(resourcePath);
        }

        if (!resourceFile.exists()) {
            throw new FileNotFoundException("Resource file Emails.csv not found at " + resourceFile.getAbsolutePath());
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(resourceFile))) {
            for (String[] row : data) {
                writer.write(String.join(",", row));
                writer.newLine();
            }
        }
    }

    private static String[] updateRowStatus(String[] row, String status) {
        String[] updatedRow = Arrays.copyOf(row, row.length);
        updatedRow[0] = status;
        return updatedRow;
    }
}