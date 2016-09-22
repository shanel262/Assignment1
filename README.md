# Assignment 1
Assignment 1 for Distributed Systems. A GUI to access mySQL DB. Allows the user to browse the db entries using the next and previous buttons, as well as adding new and deleting entries. Also features an exit button to shut down the program.

# Settings
The java file contains three settings for connecting to the database:
- the url of the database
- the username (called user)
- and the password (called pass)

## Why I use the boolean "direction"
In the event of the program requesting an id that doesn't exist, I needed a way for it to determine what to do then. My solution was to create a boolean that would tell the parseAndInsert function which id to try next. If the boolean is false then decrement the id, if it is true then increment the id.

# to-do
- update documentation
- comment code

Developed by Shane Lacey.
