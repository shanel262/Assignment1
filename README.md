# Assignment 1
Assignment 1 for Distributed Systems. A GUI to access an SQL DB using the JDBC library. Allows the user to browse the db entries using the next and previous buttons, as well as adding new and deleting entries. Also features an exit button to shut down the program.

# Settings
The java file contains three settings for connecting to the database:
- the url of the database
- the username (called user)
- and the password (called pass)

# How to use it
## IMPORTANT INFO
The database assumes a unique Ssn is applied to each record and uses it to scroll trough the users db. I have included a text file with 10 insert queries in case you do not have unique Ssn numbers.

## Scrolling (Next and previous)
The user can scroll through the db using the next and previous buttons.

## Adding a user (static record)
The user can add a static record by pressing the add button. To enable the feature to add a custom record, uncomment the feature and comment out the static entry code.

### Adding a user (commented out code)
The user can add a new user by pressing the 'Add' button. When this happens the 'Add' button changes to 'Confirm' so the user knows to click there when they are done inserting the new users details. 
#### Some rules
- __All fields must be filled in.__
- __The Salary field must contain a number__
- __The date field must be in the format YYYY-MM-DD or YYYY/MM/DD__

## Delete a user
When a user is being viewed, the delete button can be pressed to delete the record.

## Exiting
Click the exit button to close the connection and exit the program.

## Why I use the boolean "direction"
In the event of the program requesting an id that doesn't exist, I needed a way for it to determine what to do then. My solution was to create a boolean that would tell the parseAndInsert function which id to try next. If the boolean is false then decrement the id, if it is true then increment the id.


Developed by Shane Lacey.
