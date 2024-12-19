# Mass Email Sender

This Java-based application is for sending emails to multiple recipients across various companies. It leverages
environment variables, a configurable cover letter template, and CSV-based recipient management. Additionally, it is
containerized using Docker for ease of deployment.

## Project Structure

```bash
.
├── Dockerfile
├── docker-compose.yml
├── pom.xml
└── src
    ├── main
    │   ├── java
    │   │   └── com
    │   │       └── email
    │   │           ├── EmailServer.java
    │   │           ├── Mail.java
    │   │           ├── Main.java
    │   │           └── User.java
    │   └── resources
    │       ├── Emails.csv
    │       ├── cover_letter.txt
    │       └── resume.pdf
    └── test
        └── java
```

### Key Files

- **[Emails.csv](src/main/resources/Emails.csv)**: Contains the recipient details and sending status.
    
    | Status  | Company | Recipient 1                  | Recipient 2                 | Recipient 3             |
    |---------|---------|------------------------------|-----------------------------|-------------------------|
    | Sent    | Google  | sundar.pichai@gmail.com      | walter.white@gmail.com      | jesse.pinkman@gmail.com |
    | Failed  | Netflix | michael.scofield@netflix.com | lincoln.burrows@netflix.com |
    | Pending | Apple   | steve.jobs@apple.com         |

- **[cover\_letter.txt](src/main/resources/cover_letter.txt)**: A customizable cover letter template.

  ```text
  Dear Sir/Madam,

  I hope this message finds you well. My name is {name}, and I am currently a third-year undergraduate studying 
  Computer Science at the University of Colombo School of Computing. I am writing to express my keen interest in 
  securing a Software Engineering internship position at {company}.

  Best regards,
  {name}.
  {phone}
  {email}
  {linkedIn}
  ```

- **[resume.pdf](src/main/resources/resume.pdf)**: Your resume file.

## Environment Variables

Set the following environment variables to configure the application:

- Root directory create a `.env` file

```env
# Email server settings
EMAIL_USERNAME=example@gmail.com
EMAIL_PASSWORD=your-email-app-password
EMAIL_HOST=smtp.gmail.com

# Email content
EMAIL_SUBJECT=Application for Software Engineering Internship at {company}
COVER_LETTER=cover_letter.txt

# Personal details for the cover letter
NAME=John Doe
PHONE=+94 71 234 5678
EMAIL=example@email.com
LINKEDIN=https://linkedin.com

# Resume file
RESUME=resume.pdf
```

## How to Run

### Prerequisites

- Java 17+
- Docker and Docker Compose (Optional)

### Steps

1. **Clone the Repository**

   ```bash
   git clone https://github.com/Buddhikanip/Email-Sender.git
   cd Email-Sender
   ```

2. **Build and Run with Docker**

    - Build and Run Docker image:
      ```bash
      docker-compose up --build
      ```

3. **Run Without Docker**

    - Compile the project:
      ```bash
      mvn clean install
      ```
    - Execute the application:
      ```bash
      java -jar target/<your-jar-file>.jar
      ```

4. **Check Logs**
   Monitor the logs to ensure emails are being sent:

   ```bash
   docker logs <container-id>
   ```

## Customization

- **[Emails.csv](src/main/resources/Emails.csv)**: Add your recipient details.
  > - First column has a `status` initially it can be empty or Pending like word, after program once run it append right
    status
  > - Second column: `company name`
  > - Since third column you can put that company email recipients (one cell only contain one email address)
- **cover\_letter.txt**: Modify the cover letter template.
- **Environment Variables**: Update personal and email server information.

## Features

- **Dynamic Email Generation**: Personalizes the cover letter and subject line for each recipient.
- **CSV-based Management**: Tracks sending status and recipient details.
- **Resume Attachment**: Automatically attaches the specified resume file.
- **Dockerized Deployment**: Simplifies setup and ensures consistent environment.

## Example

A generated email might look like this:

**Subject:** Application for Software Engineering Internship at Google

**Body:**

```text
Dear Sir/Madam,

I hope this message finds you well. My name is Sundar Pichai, and I am currently a third-year undergraduate studying 
Computer Science at the University of Colombo School of Computing. I am writing to express my keen interest in securing a 
Software Engineering internship position at Google.

Best regards,
John Doe.
+94 71 234 5678
example@email.com
https://linkedin.com
```

**Attachment:** resume.pdf

## License

This project is licensed under the MIT License.

## Contributions

Feel free to submit issues or pull requests for improvements
