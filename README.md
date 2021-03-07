# Minesweeper-API challenge

This project is an api implementation of the classic game of [Minesweeper](https://en.wikipedia.org/wiki/Minesweeper_(video_game))

# Platform considerations
I'm using Java/Spring Boot because this is the language I'm currently working on, and the one we talked about in the interviews.

I picked heroku platform to deploy the application for its ease of use, simplicity and its price (free). This decision limited my options on databases to use. I wanted to use a NoSQL database because of their speed and data simplicity. They don't have one easily available, so I decided to use Postgresql.

# Running locally
You will need to install PosgreSQL and create a "minesweeper" db.
You will need to define following environment variables:
- JDBC_DATABASE_URL=jdbc:postgresql://localhost:5432/minesweeper

    The URL is just an example, db name and port can be different
- JDBC_DATABASE_USERNAME={{your DB user name}}
- JDBC_DATABASE_PASSWORD={{your DB password}}

**WARNING**: The project uses lombok library. It reduces the amount of code because it autogenerate getters and setters. lombok plugin needs to be installed in the IDE of your choice, or the IDE will show errors. More details: [Project lombok](https://projectlombok.org/setup/overview)


# Deployment
Deployment will be done connecting heroku to the github repository, and configuring the credentials and other parameters using environment variables.

# Design
The service requires user management to remember previous games and to persist per user information. Some token based security/authentication should also be added to avoid users accessing other peoples games.
The game board would be generated using a basic algorithm, the design would allow for it to be improved/modified.

It is obvious some operations could be performed on the client side (eg: timing), but the aim of this API is to make the client as dumb as possible, meaning every single operation would be performed on server side.
The benefit of this approach in a real/commercial game wold be to have spectators, or even allow two users to play on the same board.

# Board considerations
The board is a grid of Rows (R) and Columns (C) containing a number of mines (M). R and C can be different (a rectangle). For playability reasons, the amount of mines will cover 20% of the board at most. (M <= R*C*.20)  

To reduce size the board data, and improve the game performance, the elements would be stored as follows: 
   
- Layout. Represents the board numbers, mines and spaces. The grid will be flattened to a long string. Each character in the string will represent a cell.
    
    On a 4x4 board Mines={1,6,15} would mean mines are placed in following coordinates {[0,1],[1,2],[3,3]} 
    - 0->8 = number of mines around that cell. (0=empty)
    - M = mine.
    
- State. Represents the flags, visible and hidden cells. This is also a string. Each character will represent the status of each cell.
    - H = hidden
    - F = flag
    - X = explosion
    - Space = visible

Wining state would be where Fs matches Layout Bs, and the rest of the cells are spaces.

#Game change
The initial idea was to create a board as a template, so we could create multiple games from it. 
I changed the idea (renamed board to game) because the board details is just a string that, if required, can be copied from game to game. 