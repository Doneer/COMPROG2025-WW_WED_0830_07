# 🎯 Sudoku Game - Enterprise JavaFX Application

A comprehensive JavaFX-based Sudoku puzzle application with internationalization, advanced persistence (file & database), and professional logging. Features multiple difficulty levels, bidirectional data binding, and complete language localization with enterprise-grade database integration.

## ✨ Features

### 🎮 **Core Gameplay**
- **Three difficulty levels**: Easy (20 cells), Medium (40 cells), Hard (60 cells)
- **Interactive game board** with real-time validation
- **Solution checking** with instant feedback
- **New game generation** with randomized puzzles
- **Field editability management** with visual indicators

### 💾 **Advanced Persistence (Dual Storage)**
- **File-based storage** with custom serialization
- **PostgreSQL database integration** with JDBC
- **Save/Load to files** with file dialogs
- **Save/Load to database** with name management
- **Decorator pattern** for editable field management
- **ACID transaction support** with proper rollback handling
- **Auto-closeable resources** with proper cleanup

### 🗄️ **Database Features**
- **PostgreSQL 17** with Docker containerization
- **Two-table relational design** (1-N relationship)
- **Complete board state persistence** including editability
- **Transaction management** with commit/rollback
- **Connection pooling** and proper resource management
- **Custom JDBC exception wrapping** with internationalization

### 🌍 **Internationalization (i18n)**
- **Dual language support**: English and Polish
- **Dynamic language switching** without restart
- **ResourceBundle implementation** for all UI text
- **ListResourceBundle** for author information
- **Localized exception messages** for all error types
- **Database error localization** with proper formatting

### 🔧 **Technical Excellence**
- **Bidirectional JavaFX binding** between UI and model
- **Custom TextFormatter** with input validation
- **Professional logging** with SLF4J and Log4j2
- **Comprehensive exception hierarchy** with chaining
- **Custom DAO implementations** with factory pattern
- **Enterprise-grade error handling** with proper cleanup

## 🏗️ **Architecture**

### **Modular Design**
```
SudokuGameProject/
├── Model/                    # Core game logic and persistence
│   ├── sudokusolver/        # Game engine and algorithms
│   ├── dao/                 # Data Access Objects (File & JDBC)
│   ├── exceptions/          # Custom exception hierarchy
│   └── resources/           # Author information bundles
├── View/                    # JavaFX user interface
│   ├── view/                # Controllers and UI logic
│   ├── viewresources/       # UI resource bundles
│   └── resources/           # FXML files and properties
└── docker-compose.yml      # PostgreSQL database setup
```

### **Database Schema**
```sql
-- Parent table (1)
sudoku_boards (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) UNIQUE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
)

-- Child table (N) - 81 fields per board
sudoku_fields (
    id SERIAL PRIMARY KEY,
    board_id INTEGER REFERENCES sudoku_boards(id) ON DELETE CASCADE,
    row_index INTEGER NOT NULL,
    col_index INTEGER NOT NULL,
    field_value INTEGER NOT NULL,
    is_editable BOOLEAN DEFAULT TRUE,
    UNIQUE(board_id, row_index, col_index)
)
```

### **Design Patterns**
- ✅ **MVC Architecture**: Clean separation of concerns
- ✅ **Decorator Pattern**: `EditableSudokuBoardDecorator` for field locking
- ✅ **DAO Pattern**: `FileSudokuBoardDao`, `EditableSudokuBoardDao`, `JdbcSudokuBoardDao`
- ✅ **Factory Pattern**: `SudokuBoardDaoFactory` for DAO creation
- ✅ **Observer Pattern**: JavaFX Properties for data binding
- ✅ **Template Method**: Abstract exception handling with localization

## 🚀 **Quick Start**

### **Prerequisites**
- **Java 23** or higher
- **JavaFX 25** 
- **Maven 3.6+**
- **Docker & Docker Compose** (for database)

### **Database Setup**
```bash
# Start PostgreSQL database
docker-compose up -d

# Database will be available at:
# - PostgreSQL: localhost:5432
# - pgAdmin: localhost:80 (admin@sudoku.com / pgadmin)
```

### **Running the Application**
```bash
# Clone the repository
git clone [repository-url]
cd SudokuGameProject

# Build the project
mvn clean install

# Run the application
mvn javafx:run -pl View
```

### **Development Mode**
```bash
# Run tests with coverage
mvn clean test jacoco:report

# Check code style
mvn checkstyle:check

# Generate site documentation
mvn site

# Stop database when done
docker-compose down
```

## 🧪 **Quality Assurance**

### **Testing Coverage**
- **91%+ JaCoCo coverage** across all modules
- **Comprehensive unit tests** for all components
- **Integration tests** for both file and database persistence
- **Transaction testing** with rollback scenarios
- **Auto-closeable resource testing**
- **Exception handling validation** with internationalization
- **Factory pattern testing** with multiple DAO types

### **Code Quality**
- **Checkstyle compliance** with Google Java Style
- **PMD static analysis** with minimal warnings
- **SLF4J logging** with external configuration
- **Internationalized error messages** for all exceptions
- **ACID transaction compliance** with proper isolation

## 🛠️ **Configuration**

### **Database Configuration**
```java
// DatabaseConfig.java
DB_URL = "jdbc:postgresql://localhost:5432/sudokudb"
DB_USER = "sudoku"
DB_PASSWORD = "sudokupassword"
```

### **Logging**
Configured via `log4j2.xml`:
- Console output for development
- File output (`sudoku.log`) for production
- Configurable log levels per package
- Database operation logging

### **Internationalization**
- `messages_en.properties` - English UI text
- `messages_pl.properties` - Polish UI text
- `exception_messages_*.properties` - Localized error messages
- `AuthorsBundle_*.java` - Author information in both languages

## 📊 **Technical Specifications**

| Component | Technology | Version |
|-----------|------------|---------|
| **Language** | Java | 23 |
| **UI Framework** | JavaFX | 25-ea+12 |
| **Database** | PostgreSQL | 17 |
| **Build Tool** | Maven | 3.6+ |
| **Logging** | SLF4J + Log4j2 | 2.0.17 / 2.24.3 |
| **Testing** | JUnit Jupiter | 5.13.0 |
| **Code Quality** | Checkstyle | 10.23.0 |
| **JDBC Driver** | PostgreSQL JDBC | 42.7.4 |
| **Containerization** | Docker | Latest |

## 🎯 **Persistence Options**

### **File Storage**
- **Serialization-based** with custom format
- **Directory-based organization** with file dialogs
- **Editable field state preservation**
- **Cross-platform compatibility**

### **Database Storage**
- **PostgreSQL relational storage** with normalized schema
- **ACID transaction support** with proper isolation
- **Concurrent access handling** with connection management
- **Scalable architecture** for enterprise deployment
- **Complete audit trail** with timestamps

## 🎓 **Academic Context**

This project demonstrates advanced software engineering concepts and modern Java development practices:

- Advanced JavaFX development with Property binding
- Professional internationalization practices
- Enterprise-grade logging and exception handling
- **Database integration with JDBC and transactions**
- **Multi-tier architecture with proper separation**
- **Docker containerization for development**
- Test-driven development with high coverage
- Modern Java architectural patterns

**Institution**: Lodz University of Technology  
**Course**: Component Programming

## 🐳 **Docker Integration**

### **Database Services**
```yaml
# docker-compose.yml
services:
  db:                    # PostgreSQL 17 database
    image: postgres:17
    environment:
      POSTGRES_DB: sudokudb
      POSTGRES_USER: sudoku
      POSTGRES_PASSWORD: sudokupassword
    ports: ["5432:5432"]
  
  pgadmin:              # Database administration
    image: dpage/pgladmin4:8.12
    ports: ["80:80"]
```

## 👥 **Authors**

- **Daniyar Zhumatayev** - 253857@edu.p.lodz.pl
- **Kuzma Martysiuk** - 253854@edu.p.lodz.pl

**Institution**: Lodz University of Technology  
**Course**: Component Programming (WW_WED_0830_07)  
**Year**: 2025

## 📄 **License**

This project is licensed under the **MIT License** - see the [LICENSE](SudokuGameProject/LICENSE) file for details.

---

*Built with ❤️ using modern Java technologies, enterprise patterns, and database best practices*