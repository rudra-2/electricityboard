# Electricity Board Management System

A comprehensive JavaFX-based desktop application for managing electricity billing operations, designed to streamline the workflow of electricity boards with role-based access for consumers, meter readers, cashiers, and company administrators.

## 🔌 Overview

This project is a complete electricity billing management system built with JavaFX and MySQL database. It provides a user-friendly interface for managing electricity consumption, billing, payments, and administrative tasks.

## ✨ Features

### Consumer Portal (`ElectricityBillApp.java`)
- View electricity bills and due amounts
- Check payment history
- Make online payments (UPI/Card)
- Update consumer details
- View due dates and payment status

### Meter Reader Portal (`MeterReader.java`)
- Secure login system with credentials
- Monthly meter reading management
- Track assigned houses/consumers
- Record electricity consumption readings
- Monitor pending readings

### Cashier Portal (`CashierApp.java`)
- Secure authentication system
- Process bill payments
- Import payment data from files
- Update payment records
- Manage consumer billing information

### Company Admin Portal (`CompanyApp.java`)
- View all due bills across consumers
- Manage meter readers (add/remove)
- Manage cashiers
- Administrative oversight of the entire system

## 🛠️ Technologies Used

- **Frontend:** JavaFX
- **Backend:** Java
- **Database:** MySQL
- **JDBC:** MySQL Connector/J (com.mysql.cj.jdbc.Driver)

## 📁 Project Structure

```
electricityboard/
├── src/
│   ├── ElectricityBillApp.java    # Consumer application
│   ├── MeterReader.java            # Meter reader application
│   ├── CashierApp.java             # Cashier application
│   └── CompanyApp.java             # Admin/Company application
├── lib/                            # External libraries and dependencies
├── .vscode/                        # VS Code configuration
└── README.md
```

## 🚀 Getting Started

### Prerequisites

- Java Development Kit (JDK) 8 or higher
- JavaFX SDK
- MySQL Server
- MySQL JDBC Driver

### Database Setup

1. Install MySQL Server
2. Create a database named `electricity`:
```sql
CREATE DATABASE electricity;
```

3. Configure the database connection:
   - **Host:** localhost
   - **Port:** 3306
   - **Database:** electricity
   - **Username:** root
   - **Password:** (empty by default)

4. The application expects the following tables (you'll need to create them based on your schema):
   - `consumer_detail`
   - `reader_detail`
   - `cashier`
   - `meter_reader`
   - `history`
   - `prev_reads2024`

### Running the Application

1. Clone the repository:
```bash
git clone https://github.com/rudra-2/electricityboard.git
cd electricityboard
```

2. Compile the Java files:
```bash
javac --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml src/*.java
```

3. Run the desired application:

**Consumer Portal:**
```bash
java --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml -cp src ElectricityBillApp
```

**Meter Reader Portal:**
```bash
java --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml -cp src MeterReader
```

**Cashier Portal:**
```bash
java --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml -cp src CashierApp
```

**Company Admin Portal:**
```bash
java --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml -cp src CompanyApp
```

## 💡 Usage

### For Consumers
1. Launch the `ElectricityBillApp`
2. Enter your consumer number
3. View your bills, due amounts, and payment history
4. Make payments using UPI or Card

### For Meter Readers
1. Launch the `MeterReader` application
2. Login with your reader ID and password
3. Select the month for readings
4. Record meter readings for assigned houses

### For Cashiers
1. Launch the `CashierApp`
2. Login with cashier ID and password
3. Process payments and manage billing records

### For Administrators
1. Launch the `CompanyApp`
2. View all due bills
3. Manage meter readers and cashiers
4. Oversee system operations

## 🔐 Security

- Password-protected login for all user roles
- Secure database connections
- Role-based access control

## 📝 License

This project is available for educational purposes.

## 👤 Author

**rudra-2**

## 🤝 Contributing

Contributions, issues, and feature requests are welcome!

---

**Note:** Make sure to update the database credentials in the source files before running the application.