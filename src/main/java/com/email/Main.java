package com.email;

import java.io.*;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        Mail mail = new Mail();
        mail.setupServerProperties();

        String line;

        try (InputStream inputStream = Main.class.getClassLoader().getResourceAsStream("Emails.csv")) {
            assert inputStream != null;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

                while ((line = reader.readLine()) != null) {
                    List<String> row = List.of(line.split(","));
                    String company = row.get(0);
                    List<String> recipients = row.stream().filter(cell -> cell.contains("@")).toList();
                    mail.sendEmail(recipients, company);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
