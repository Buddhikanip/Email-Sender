package com.email;

import javax.mail.MessagingException;
import java.io.*;

import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException, MessagingException {

        Mail mail = new Mail();
        mail.setupServerProperties();

        String file = "src/main/resources/Emails.csv";
        BufferedReader reader = null;
        String line = null;

        try {
            reader = new BufferedReader(new FileReader(file));
            while ((line = reader.readLine()) != null) {
                List<String> row = List.of(line.split(","));

                String company = row.get(0);
                List<String> recipients = row.stream().filter(cell->cell.contains("@")).toList();

                mail.sendEmail(recipients,company);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            assert reader != null;
            reader.close();
        }
    }
}