Resume Builder

A full-stack web application that lets users create, edit, preview and download professional resumes as PDF files.

Live Demo: your-app.onrender.com  |  Click "Try Demo - No Registration Required" on the login page to explore without signing up.

Features
User accounts: registration and login with Spring Security. Passwords are stored as BCrypt hashes.
Multiple resumes per user: create, edit, view and delete your own resumes. Each user only sees their own data.
Resume sections: Professional Summary, Education, Technical Skills, Projects and Certifications.
Projects with links: each project has a tech stack, a clickable GitHub link and a clickable Live Demo link. Empty links are hidden automatically.
Drag-and-drop section ordering: arrange sections in any order you like.
Custom section titles: rename any section (for example "Technical Skills" to "Skills").
Formatting options: choose the font family and font size, and use bold, italic and underline in descriptions.
Live preview: review the resume in the browser before downloading.
PDF download: generates a clean, ATS-friendly PDF with clickable links.
Demo login: one-click access to a demo account.
Tech Stack
Layer	Technology
Language	Java 17
Framework	Spring Boot 4.1 (Spring MVC)
Security	Spring Security, BCrypt
Database access	Spring Data JPA, Hibernate
Database	MySQL
Templates (frontend)	Thymeleaf, HTML, CSS, JavaScript
PDF generation	OpenHTMLToPDF, Apache PDFBox
Build tool	Maven
Deployment	Docker, Render, Aiven (MySQL)
How It Works
Browser (Thymeleaf pages)
        |
        v
Controllers  -->  Services  -->  Repositories (JPA)  -->  MySQL
        |
        v
PDF generation (HTML -> PDF)

Registration: signup.html -> AuthController -> validation (SignupForm) -> UserService (BCrypt) -> UserRepository -> users table.

Login: login.html -> Spring Security -> UserService.loadUserByUsername() -> password check -> session created.

Creating a resume: resume-form.html -> ResumeController (/resumes/save) -> ResumeService -> repositories -> database. Empty entries are removed before saving.

PDF download: /resumes/{id}/pdf -> PdfController builds HTML for each section in the saved order -> converted to a PDF file.

Project Structure
Resume_Builder/
├── Dockerfile
├── pom.xml
└── src/main/
    ├── java/com/resume/resume_builder/
    │   ├── config/        SecurityConfig
    │   ├── controller/    Auth, Home, Resume, Pdf, Demo controllers
    │   ├── dto/           SignupForm
    │   ├── entity/        User, Resume, Education, Skill, Project, Certification
    │   ├── repository/    JPA repositories
    │   └── service/       UserService, ResumeService
    └── resources/
        ├── templates/     index, login, signup, resume-form, resume-preview
        ├── fonts/         fonts used in PDF generation
        └── application.properties
Run Locally
Prerequisites
Java 17
MySQL 8 (running locally)
Git
Steps
Clone the repository
bash
   git clone https://github.com/<your-username>/Resume_Builder.git
   cd Resume_Builder
Create the database
sql
   CREATE DATABASE resume_builder;
Set environment variables The app reads its database settings from environment variables.
Variable	Example
DB_URL	jdbc:mysql://localhost:3306/resume_builder?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Kolkata
DB_USERNAME	root
DB_PASSWORD	your MySQL password
Linux / macOS:
bash
   export DB_URL="jdbc:mysql://localhost:3306/resume_builder?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Kolkata"
   export DB_USERNAME="root"
   export DB_PASSWORD="your_password"

Windows (PowerShell):

powershell
   $env:DB_URL="jdbc:mysql://localhost:3306/resume_builder?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Kolkata"
   $env:DB_USERNAME="root"
   $env:DB_PASSWORD="your_password"
Run the application
bash
   ./mvnw spring-boot:run

On Windows use mvnw.cmd spring-boot:run.

Open the app at http://localhost:8080

The tables are created automatically on first run (spring.jpa.hibernate.ddl-auto=update).

Demo login (optional)

The "Try Demo" button logs in as demo@resumebuilder.com. To use it locally, first sign up with that email, or change demoEmail in DemoController.java to an account you have created.

Run with Docker
bash
docker build -t resume-builder .

docker run -p 8080:8080 \
  -e DB_URL="jdbc:mysql://<host>:3306/resume_builder" \
  -e DB_USERNAME="<user>" \
  -e DB_PASSWORD="<password>" \
  resume-builder
Deployment

The live version is deployed with:

Render: hosts the Docker container (the app reads the PORT variable set by Render).
Aiven: hosts the MySQL database. Use a JDBC URL with ?ssl-mode=REQUIRED.

Set DB_URL, DB_USERNAME and DB_PASSWORD as environment variables on the hosting platform. Do not commit real credentials to the repository.

Main Routes
Route	Method	Description	Access
/login	GET	Login page	Public
/signup	GET, POST	Registration page and submit	Public
/demo-login	GET	One-click demo account login	Public
/	GET	Home page (your resumes)	Logged in
/resumes/new	GET	New resume form	Logged in
/resumes/save	POST	Save a resume	Logged in
/resumes/{id}	GET	Resume preview	Logged in
/resumes/edit/{id}	GET	Edit a resume	Logged in
/resumes/delete/{id}	GET	Delete a resume	Logged in
/resumes/{id}/pdf	GET	Download the resume as PDF	Logged in
/logout	POST	Log out	Logged in
Future Improvements
Add a Coding Profiles section (HackerRank, LeetCode and similar)
Multiple resume templates
Email verification and password reset
Mobile-friendly layout for the resume form
Author

Your Name

GitHub: github.com/your-VishalRevaje
LinkedIn: linkedin.com/in/vishal-revaje
