# Expense Manager – Java Web Application  

A full-stack Java web application to help users track, analyze, and manage personal expenses.  
The system supports secure user accounts, expense categorization, visual reports, and monthly summaries.  

---

## Features  
- Secure login and registration with PBKDF2 password hashing  
- Add, view, filter, and categorize expenses  
- Visualize spending with interactive Pie and Bar charts (Chart.js)  
- Generate monthly reports in HTML or CSV formats  
- Session management and input validation for security  

---

## Tech Stack  
- **Frontend:** HTML5, CSS3, Chart.js, Material Design  
- **Backend:** Java Servlets (Jakarta EE)  
- **Database:** MySQL 8.0+ with JDBC  
- **Server:** Apache Tomcat 11.0  

---

## Project Structure  
├── src/ # Java source files (Servlets, Models, JDBC handlers)
├── web/ # HTML, CSS, and web.xml configuration
├── lib/ # Dependencies (e.g., mysql-connector-j)
└── README.md # Project documentation


---

## Setup Instructions  

### 1. Prerequisites  
- Java JDK 17 or later  
- Apache Tomcat 11.0  
- MySQL 8.0+  
- MySQL Connector/J  

### 2. Database Setup  
Run the following SQL commands:  

```sql
CREATE DATABASE expense_tracker;
USE expense_tracker;

CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE expenses (
    id INT AUTO_INCREMENT PRIMARY KEY,
    date DATE NOT NULL,
    category VARCHAR(100) NOT NULL,
    amount DOUBLE NOT NULL,
    description VARCHAR(255),
    user_id INT,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

```
### 3. Configure Database Connection

- Update the following in both ExpenseStorageJDBC.java and UserStorageJDBC.java:

```java
private static final String JDBC_URL = "jdbc:mysql://localhost:3306/expense_tracker";
private static final String JDBC_USER = "root";
private static final String JDBC_PASSWORD = "yourpassword";
```
### 4. Deploy the Application

- 1.Build the project into a .war file.

- 2.Place the file in Tomcat’s webapps/ directory.

- 3.Start Tomcat and open:
  ```
  http://localhost:8080/expense-manager

<img width="940" height="499" alt="image" src="https://github.com/user-attachments/assets/9cd0675d-b41d-44e6-985e-76d4ec6048b3" />
- Home Page
  
<img width="940" height="347" alt="image" src="https://github.com/user-attachments/assets/35cd76a8-99a9-49c9-a5c2-72dfc3f05a29" />

- Registration Page

<img width="493" height="682" alt="image" src="https://github.com/user-attachments/assets/30c50ea5-7c33-4ae2-b916-23ca2e0dd929" />

- Login Page

<img width="940" height="352" alt="image" src="https://github.com/user-attachments/assets/281955d6-339a-4360-9bd9-bb08bdd2f8e0" />

- Secure Passwords

<img width="940" height="505" alt="image" src="https://github.com/user-attachments/assets/9cefc6e8-5272-4fe9-96f2-3e02cd12d1d7" />
<img width="940" height="501" alt="image" src="https://github.com/user-attachments/assets/7cb1bcd5-5bf9-4eae-9fce-1c6b4276080a" />
<img width="940" height="509" alt="image" src="https://github.com/user-attachments/assets/5f53103e-e7c2-4dc9-bd5f-0e0a66e74179" />
<img width="940" height="689" alt="image" src="https://github.com/user-attachments/assets/a078ca81-4dfa-44f2-a348-145e20db27ee" />
<img width="940" height="576" alt="image" src="https://github.com/user-attachments/assets/f665bf0a-46b7-425d-aef8-a977f3842b34" />
<img width="940" height="502" alt="image" src="https://github.com/user-attachments/assets/da3bfb66-fb29-4d35-ba2e-fd1b5d4e65dc" />


### Future Improvements

- More advanced filtering and search options

- Category dropdowns with predefined values

- PDF report export

- Database connection pooling for performance

- Improved responsive design for mobile devices

## Author

Developed by Tanishq Agrawal
