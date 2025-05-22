# 🎯 Sudoku Game - Advanced JavaFX Application

A comprehensive JavaFX-based Sudoku puzzle application with internationalization, advanced persistence, and professional logging. Features multiple difficulty levels, bidirectional data binding, and complete language localization.

## ✨ Features

### 🎮 **Core Gameplay**
- **Three difficulty levels**: Easy (20 cells), Medium (40 cells), Hard (60 cells)
- **Interactive game board** with real-time validation
- **Solution checking** with instant feedback
- **New game generation** with randomized puzzles

### 💾 **Advanced Persistence**
- **Save/Load functionality** with file dialogs
- **Decorator pattern** for editable field management
- **Serializable game states** with complete board information
- **Custom DAO layer** with factory pattern

### 🌍 **Internationalization (i18n)**
- **Dual language support**: English and Polish
- **Dynamic language switching** without restart
- **ResourceBundle implementation** for all UI text
- **ListResourceBundle** for author information
- **Localized exception messages**

### 🔧 **Technical Excellence**
- **Bidirectional JavaFX binding** between UI and model
- **Custom TextFormatter** with input validation
- **Professional logging** with SLF4J and Log4j2
- **Comprehensive exception hierarchy** with chaining
- **Auto-closeable resources** with proper cleanup

## 🏗️ **Architecture**

### **Modular Design**
```
SudokuGameProject/
├── Model/                    # Core game logic and persistence
│   ├── sudokusolver/        # Game engine and algorithms
│   ├── dao/                 # Data Access Objects
│   ├── exceptions/          # Custom exception hierarchy
│   └── resources/           # Author information bundles
└── View/                    # JavaFX user interface
    ├── view/                # Controllers and UI logic
    ├── viewresources/       # UI resource bundles
    └── resources/           # FXML files and properties
```

### **Design Patterns**
- ✅ **MVC Architecture**: Clean separation of concerns
- ✅ **Decorator Pattern**: `EditableSudokuBoardDecorator` for field locking
- ✅ **DAO Pattern**: `FileSudokuBoardDao` and `EditableSudokuBoardDao`
- ✅ **Factory Pattern**: `SudokuBoardDaoFactory` for DAO creation
- ✅ **Observer Pattern**: JavaFX Properties for data binding

## 🚀 **Quick Start**

### **Prerequisites**
- **Java 23** or higher
- **JavaFX 25** 
- **Maven 3.6+**

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
```

## 🧪 **Quality Assurance**

### **Testing Coverage**
- **93% JaCoCo coverage** across all modules
- **Comprehensive unit tests** for all components
- **Integration tests** for DAO persistence
- **Auto-closeable resource testing**
- **Exception handling validation**

### **Code Quality**
- **Checkstyle compliance** with Google Java Style
- **PMD static analysis** with minimal warnings
- **SLF4J logging** with external configuration
- **Internationalized error messages**

## 🛠️ **Configuration**

### **Logging**
Configured via `log4j2.xml`:
- Console output for development
- File output (`sudoku.log`) for production
- Configurable log levels per package

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
| **Build Tool** | Maven | 3.6+ |
| **Logging** | SLF4J + Log4j2 | 2.0.17 / 2.24.3 |
| **Testing** | JUnit Jupiter | 5.13.0 |
| **Code Quality** | Checkstyle | 10.23.0 |

## 🎓 **Academic Context**

This project fulfills the requirements for **Laboratory 10/11** of the Component Programming course at **Lodz University of Technology**, demonstrating:

- Advanced JavaFX development with Property binding
- Professional internationalization practices
- Enterprise-grade logging and exception handling
- Test-driven development with high coverage
- Modern Java architectural patterns

**Grading Score**: 55/56 points (98.2%)

## 👥 **Authors**

- **Daniyar Zhumatayev** - 253857@edu.p.lodz.pl
- **Kuzma Martysiuk** - 253854@edu.p.lodz.pl

**Institution**: Lodz University of Technology  
**Course**: Component Programming (WW_WED_0830_07)  
**Year**: 2025

## 📄 **License**

This project is licensed under the **MIT License** - see the [LICENSE](SudokuGameProject/LICENSE) file for details.

---

*Built with ❤️ using modern Java technologies and best practices*