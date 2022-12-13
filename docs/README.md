# Android-Coop-Achievement
[![forthebadge](http://forthebadge.com/images/badges/made-with-java.svg)](http://forthebadge.com)
[![forthebadge](http://forthebadge.com/images/badges/built-with-love.svg)](http://forthebadge.com)
- [Demo video](https://youtu.be/c3wQJ9Rmlvk)
- Works on 5", 6", 10" sized screens

## Installation

### Option 1: 
``` git clone https://github.com/swimmingHaribo/Android-Coop-Achievement.git ```

### Option 2:
1. [Download Android Studio](https://developer.android.com/studio)
2. Download the zip file
3. Select a device (eg. Pixel 2)
4. Time to run it!


## Feature

### Configurations Application
- This is an application to store game configurations and game plays for saved games.
- User can create games with expected min and max scores and add new games for multiple players with individual scores scores and set difficulty to that games.
- New game may have one of the 3 themes of achievements.
- Game Editing is now available. It allows to change individual scores, theme and difficulty of the game.
- Users can view achievements for a given number of players with different game difficulty and theme, view history, edit and delete game configuration.
- Game configurations and game plays are saved between launches of the application.
- When viewing the achievement screen, users are allowed to view a list of achievement levels for an existing game configuration.

### Game Played Difficulty
Each time user plays a game, users are allowed to select a difficulty level for that specific game play.
- If user selects "normal", the scores required to earn an achievement level be 100% of the usual amount.
- If user selects "easy", the scores required to earn an achievement level be 75% of the usual amount.
- If user selects "hard", the scores required to earn an achievement level be 125% of the usual amount.
- When viewing the achievement screen, user can see the difficulty level listed on the screen.
- When viewing the list of games played in game history screen, user can see the difficulty level listed for each game play.

### Photos for Configurations and GamePlays
- User can open ViewImage activity from clicking on standard image in ViewConfiguration page.
- It can also start from the AddNewGame activity after saving a game in Add new gamePlay and Edit gamePlay activities. In addition Edit gamePlay has a button to open ViewImage activity as well for a better user convenience.

### Add New Game && Edit Game
When using the score calculator to add up player scores while recording a game play:
- Work for at least 100 players. Default to 2 players so that it works for the average case. 
- While entering or editing a game play's scores, if the user enters scores and then decreases the number of players (say from 10 to 5), this will cause some of the entered scores to seem to be "lost". If the user then increases the number of players, the relevant previous scores that were "lost" (which user had entered when there were more users) to come back as user increases the player count.
- If the user enters scores for some number of players, then decreases the number of players and save, then the user can go back and edit the game play later and increase the number of players, and each new player's score defaults to 0. 
- User can change the number of players and edit scores of existing game plays.

### Achievement Statistics
- Statistics are shown in a graph. When user edits a game play and they get a different achievement level from the previous one, the old achievement level earned will be deleted from statistics (the graph) and the new one will be added. If the level of the edited game is the same as the previous one, the earned level statistics will not change.
- There is a button added called 'Levels' for convenience. When user clicks the button they will see a pop-up window with three buttons for the themes that when clicked, will display the number associated with each achievement theme. The 'X' button will close the pop-up. There is also a message that displays that statistics will not be displayed when user clicks configuration that was made from previous iterations, but user should delete the configs from previous iterations.

### Achievement Celebration Page
- When user reaches highest level, it tells the user they got the highest level instead of displaying how many points away they are from the next level.
- When viewing the achievement celebration page, users are able to replay the animation and change the theme.

