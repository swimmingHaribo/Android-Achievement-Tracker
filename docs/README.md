# Android-Coop-Achievement

### Configurations Application
This is an application to store game configurations and game plays for saved games. <br>
User can create games with expected min and max scores and add new games for multiple players with individual scores scores and set difficulty to that games. <br>
New game may have one of the 3 themes of achievements. <br>
Game Editing is now available. It allows to change individual scores, theme and difficulty of the game.<br>
Users can view achievements for a given number of players with different game difficulty and theme, view history, edit and delete game configuration.<br>
Game configurations and game plays are saved between launches of the application.<br>

### Photos for Configurations and GamePlays
User can open ViewImage activity from clicking on standard image in ViewConfiguration page. <br>
It can also start from the AddNewGame activity after saving a game in Add new gamePlay and Edit gamePlay activities. In addition Edit gamePlay has a button to open ViewImage activity as well for a better user convenience.

### Score Calculator
When using the score calculator to add up player scores while recording a game play: <br>
Work for at least 100 players. Default to 2 players so that it works for the average case. <br>
While entering or editing a game play's scores, if the user enters scores and then decreases the number of players (say from 10 to 5), this will cause some of the entered scores to seem to be "lost". If the user then increases the number of players, the relevant previous scores that were "lost" (which user had entered when there were more users) to come back as user increases the player count.<br>
If the user enters scores for some number of players, then decreases the number of players and save, then the user can go back and edit the game play later and increase the number of players, and each new player's score defaults to 0. <br>
User can change the number of players and edit scores of existing game plays.

### Achievement Statistics
Statistics are shown in a graph. When user edits a game play and they get a different achievement level from the previous one, the old achievement level earned will be deleted from statistics (the graph) and the new one will be added. If the level of the edited game is the same as the previous one, the earned level statistics will not change. <br>
There is a button added called 'Levels' for convenience. When user clicks the button they will see a pop-up window with three buttons for the themes that when clicked, will display the number associated with each achievement theme. The 'X' button will close the pop-up. There is also a message that displays that statistics will not be displayed when user clicks configuration that was made from previous iterations, but user should delete the configs from previous iterations.

### Achievement Celebration Page
When user reaches highest level, it tells the user they got the highest level instead of displaying how many points away they are from the next level.
