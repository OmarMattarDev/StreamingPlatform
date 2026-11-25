# 🎬 Movie Streaming Platform

A Java-based Movie Streaming Platform built using **Object-Oriented Programming (OOP), Java Collections, Lambda Expressions, Stream API, MySQL, and JDBC**.

The project simulates the core operations of a movie streaming platform, including user management, movie catalog management, reviews, watchlists, watch history, and subscription management.

---

## ✨ Features

### 👤 User Management
- Register and manage users.
- Retrieve users by ID, username, or email.
- Manage account statuses: `ACTIVE`, `SUSPENDED`, and `CLOSED`.
- Manage user roles: `USER` and `ADMIN`.
- Filter and sort users.

### 🎥 Movie Management
- Add, update, retrieve, search, and delete movies.
- Manage movie availability.
- Search movies by title.
- Filter and sort movies.
- Retrieve long movies based on duration.

### 🏷️ Genre Management
- Create and manage movie genres.
- Assign multiple genres to movies.
- Retrieve movies by genre and genres by movie.

### ⭐ Review Management
- Add, update, and delete reviews.
- Retrieve reviews by movie or user.
- Validate movie ratings.
- Calculate average movie ratings.
- Filter positive reviews and sort reviews by rating.

### 📋 Watchlist Management
- Add movies to users' watchlists.
- Check whether a movie exists in a watchlist.
- Retrieve and remove watchlist entries.

### ⏱️ Watch History
- Track users' movie-watching progress.
- Store watched time in seconds.
- Mark movies as completed.
- Retrieve completed and incomplete movies.
- Sort watch history by progress.

### 💳 Subscription Management
- Create and manage subscription plans.
- Configure plan prices, durations, and maximum devices.
- Subscribe users to plans.
- Manage subscription statuses: `ACTIVE`, `EXPIRED`, and `CANCELLED`.
- Retrieve and sort subscription information.

### 📊 Reports
- User statistics and account summaries.
- Movie statistics and average durations.
- Review statistics and average ratings.
- Watch activity and completion rates.
- Subscription statistics and plan summaries.

---

## 🧱 Project Architecture

The project follows a layered architecture to separate responsibilities and keep the code organized.

| Package | Responsibility |
|---|---|
| `application` | Application entry point and execution |
| `config` | Database connection configuration |
| `model` | Entity classes and enums |
| `dao` | Database access and SQL operations |
| `service` | Business logic and validations |
| `workflow` | Coordination of multi-step operations and transactions |
| `report` | Database reports and statistics |

### 📁 Project Structure

```text
MovieStreamingPlatform/
│
├── MovieStreamingDB/
│   ├── database_setup.sql
│   ├── tables.sql
│   ├── basic_queries.sql
│   └── indexes.sql
│
├── src/
│   ├── config/
│   │   └── database.properties.example
│   │
│   └── com/
│       └── moviestreaming/
│           ├── application/
│           │   └── Main.java
│           │
│           ├── config/
│           │   └── DatabaseConnection.java
│           │
│           ├── model/
│           │   ├── enums/
│           │   └── Entity Classes
│           │
│           ├── dao/
│           │   └── DAO Classes
│           │
│           ├── service/
│           │   └── Service Classes
│           │
│           ├── workflow/
│           │   └── Workflow Classes
│           │
│           └── report/
│               └── Report Classes
│
├── .gitignore
└── README.md
```

---

## 🗄️ Database Design

The project uses **MySQL** with a database named `MovieStreamingDB`.

It contains 9 tables:

| Table | Description |
|---|---|
| `users` | Stores user information, roles, and account statuses |
| `movies` | Stores movie details |
| `genres` | Stores movie genres |
| `movie_genres` | Connects movies with their genres |
| `reviews` | Stores user reviews and ratings |
| `watch_history` | Tracks users' watching progress |
| `watchlist` | Stores movies saved by users |
| `subscription_plans` | Stores available subscription plans |
| `user_subscriptions` | Stores users' subscriptions |

The database scripts are located in the `MovieStreamingDB` directory.

---

## 🛠️ Technologies Used

- **Java** – Core programming language.
- **Object-Oriented Programming (OOP)** – Encapsulation, inheritance, polymorphism, abstraction, and interfaces.
- **Java Collections Framework** – Managing and processing collections of objects.
- **Lambda Expressions** – Functional programming operations.
- **Stream API** – Filtering, sorting, mapping, and processing data.
- **MySQL** – Relational database management system.
- **JDBC** – Connecting Java applications to MySQL.
- **PreparedStatement** – Executing parameterized SQL queries.
- **Git & GitHub** – Version control and project management.
- **IntelliJ IDEA** – Development environment.

---

## ⚙️ Setup and Installation

### 1. Clone the Repository

```bash
git clone YOUR_REPOSITORY_URL
```

### 2. Create the Database

Open MySQL and execute the SQL scripts in this order:

1. `database_setup.sql`
2. `tables.sql`
3. `indexes.sql`

The `basic_queries.sql` file contains example SQL queries.

### 3. Configure Database Connection

Inside `src/config/`, create a local file named:

```text
database.properties
```

Add your own database credentials:

```properties
db.url=jdbc:mysql://localhost:3306/MovieStreamingDB
db.username=YOUR_USERNAME
db.password=YOUR_PASSWORD
```

The repository includes `database.properties.example` as a configuration template.

**Important:** Never upload your actual database credentials to GitHub.

### 4. Add MySQL Connector/J

Download **MySQL Connector/J** and add the JAR file to the project libraries in IntelliJ IDEA.

### 5. Run the Application

Open the project in IntelliJ IDEA and run:

```text
com.moviestreaming.application.Main
```

The `Main` class executes a demonstration of the application's services, workflows, and reports using sample data.

> ⚠️ The demo inserts records with predefined unique values. Run it against a fresh, empty database. Running it again without cleaning the sample data may cause duplicate-entry errors.

---

## 📚 What This Project Demonstrates

This project applies Java programming concepts in a practical application connected to a relational database.

It demonstrates:
- Designing and organizing a multi-layered Java application.
- Applying OOP principles in a real project.
- Working with Java Collections, Lambda Expressions, and Stream API.
- Designing relational database tables and relationships.
- Implementing CRUD operations using JDBC.
- Using `PreparedStatement` and `ResultSet`.
- Separating database operations from business logic.
- Managing multi-step operations and transactions.
- Generating reports and statistics using SQL.

---

## 👨‍💻 Author

**Omar Mattar**

Java Backend Development Learner

