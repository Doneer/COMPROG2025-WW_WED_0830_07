# Sudoku Game

A JavaFX-based Sudoku puzzle application featuring multiple difficulty levels (Easy, Medium, Hard) and game persistence. Players can generate puzzles, validate solutions, and save/load games.

## Features
- Three difficulty levels
- Interactive game board
- Solution validation
- Save and load functionality
- Clean user interface

## Technical Details
- Built with JavaFX
- MVC architecture
- Implementation of Cloneable and Comparable interfaces
- Prototype design pattern
- Persistent game storage with custom DAO

## How to Run
1. Clone the repository
2. Build with Maven: `mvn clean install`
3. Run the View module: `mvn javafx:run -pl View`

## Project Structure
- Model: Core game logic and data structures
- View: JavaFX user interface
- DAO: Data Access Objects for game persistence

## Requirements
- Java 23
- JavaFX 25
- Maven 3.6+

Developed as part of the Component Programming course at Lodz University of Technology.
