# Minesweeper-API challenge

This project is an api implementation of the classic game of [Minesweeper](https://en.wikipedia.org/wiki/Minesweeper_(video_game))

# Platform considerations
I'm using Java/Spring Boot because this is the language I'm currently working on, and the one we talked about in the interviews.

I picked heroku platform to deploy the application for its ease of use, simplicity and its price (free). This decision limited my options on databases to use. I wanted to use a NoSQL database because of their speed and data simplicity. They don't have one easily available, so I decided to use Postgresql.

# Deployment
Deployment will be done connecting heroku to the github repository, and configuring the credentials and other parameters using environment variables.

# Design
The service requires user management to remember previous games and to persist per user information. Some token based security/authentication should also be added to avoid users accessing other peoples games.
The game board would be generated using a basic algorithm, the design would allow for it to be improved/modified.

It is obvious some operations could be performed on the client side (eg: timing), but the aim of this API is to make the client as dumb as possible, meaning every single operation would be performed on server side.
The benefit of this approach in a real/commercial game wold be to have spectators, or even allow two users to play on the same board.

    
