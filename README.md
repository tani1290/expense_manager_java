# Expense Manager

A web-based expense tracking application built with Java Servlets, JSP, and H2 Database.

## Features

- **User Authentication**: Secure registration and login with BCrypt password hashing
- **Create Expenses**: Add expenses with title, amount, category, date, and optional description
- **View Expenses**: Browse all expenses with total spending summary
- **Filter Expenses**: Filter by date, category, or month
- **Edit Expenses**: Modify existing expense details including description
- **Delete Expenses**: Remove expenses with confirmation dialog
- **Monthly Reports**: Visual reports with pie charts (category breakdown) and bar charts (daily spending)
- **CSV Export**: Download monthly expense reports as CSV files
- **Category Tracking**: Organize expenses into categories (Food, Transport, Utilities, Entertainment, Health, Other)
- **Responsive UI**: Material Design-inspired interface with mobile-friendly layout

## Tech Stack

- **Backend**: Java 17, Jakarta Servlets 5.0
- **Frontend**: HTML5, CSS3, JavaScript, JSP with JSTL, Chart.js
- **Database**: H2 Database (file-based, persists locally)
- **Security**: BCrypt password hashing
- **Build Tool**: Maven
- **Server**: Jetty (via Maven plugin)

## Prerequisites

- Java 17 or higher
- Maven 3.6 or higher

## Getting Started

### Build the Project

```bash
mvn clean package
```

### Run the Application

```bash
mvn jetty:run
```

The application will be available at: `http://localhost:8080`

## Project Structure

```
src/main/java/com/expense/
├── model/
│   ├── Expense.java          # Expense data model (with LocalDate, description)
│   └── User.java             # User data model
├── servlet/
│   ├── AddExpenseServlet.java       # Create expense (POST)
│   ├── DeleteExpenseServlet.java    # Delete expense (POST)
│   ├── EditExpenseServlet.java      # Load edit form (GET)
│   ├── LoginServlet.java            # User login (GET/POST)
│   ├── LogoutServlet.java           # User logout (GET)
│   ├── RegisterServlet.java         # User registration (GET/POST)
│   ├── ReportServlet.java           # Monthly reports with charts + CSV (GET)
│   ├── UpdateExpenseServlet.java    # Update expense (POST)
│   └── ViewExpensesServlet.java     # View expenses with filtering (GET)
└── storage/
    ├── DBUtil.java                  # Database connection & initialization
    ├── ExpenseStorageJDBC.java      # Expense CRUD operations
    └── UserStorageJDBC.java         # User authentication operations

web/
├── css/styles.css                   # Material Design styles
├── addExpense.html                  # Add expense form
├── editExpense.jsp                  # Edit expense form
├── index.html                       # Landing page (auto-redirect to login)
├── login.html                       # Login page
├── register.html                    # Registration page
└── viewExpenses.jsp                 # Expense list with filters
```

## API Endpoints

| Method | Path | Description |
|--------|------|-------------|
| GET | `/` | Landing page (redirects to login after 5s) |
| GET | `/login` | Login page |
| POST | `/login` | Authenticate user |
| GET | `/register` | Registration page |
| POST | `/register` | Create new user |
| GET | `/logout` | Logout user |
| POST | `/addExpense` | Create new expense |
| GET | `/viewExpenses` | View all expenses |
| GET | `/viewExpenses?filterType=category&filterValue=Food` | Filter expenses |
| GET | `/viewExpenses?filterType=month&filterValue=2026-05` | Filter by month |
| GET | `/viewExpenses?filterType=date&filterValue=2026-05-01` | Filter by date |
| GET | `/editExpense?id=X` | Load edit form for expense |
| POST | `/updateExpense` | Update an expense |
| POST | `/deleteExpense` | Delete an expense |
| GET | `/report` | Monthly expense report with charts |
| GET | `/report?month=2026-05` | Report for specific month |
| GET | `/report?month=2026-05&format=csv` | Download CSV report |

## Database

The application uses H2 file-based database (`./expense_db`) which automatically creates the following tables on startup:

- **users**: Stores user accounts with hashed passwords
- **expenses**: Stores expense records linked to users (title, amount, category, date, description)

To switch to MySQL, update the connection string in `DBUtil.java` and add the MySQL connector dependency (already included in `pom.xml`).

## Reports

The Reports page (`/report`) provides:
- **Pie Chart**: Category breakdown showing spending distribution
- **Bar Chart**: Daily spending trends across the selected month
- **Summary Cards**: Total spending, number of categories, transaction count
- **Category Table**: Detailed breakdown with percentages
- **CSV Export**: Download report data as CSV file
- **Month Selector**: Navigate between different months

## Security Features

- Passwords are hashed using BCrypt before storage
- Session-based authentication for protected routes
- User isolation (users can only view/edit/delete their own expenses)
- Input validation on both client and server side
- Duplicate username prevention during registration
