package ca.coopAchievement.app.model;

/*
* Class Achievements calculates the achievement level, expected min score and max score,
* and the range and bounds of the scores in each level.
* Keeps/populates the array of achievements names and allows to retrieve achievement names
 */

public class Achievements {

    private final String FANTASY = "Fantasy";
    private final String STAR_WARS = "Star Wars";
    private int numOfBoundedLevels;
    private String achievementsFruits[];
    private String achievementsFantasy[];
    private String achievementsStarWars[];
    private int intAchievements[] = new int[10];
    private String levelAchieved;
    private int minScore;
    private int maxScore;
    //difLevels: 0 - Easy; 1 - Normal; 2 - Hard
    private int currentDifLevel = 1;
    private String theme;
    private int indexLevelAchieved; //Index position of level achieved
    private boolean isHighestLevel; //True if user has reached the highest level
    private String nextAchievementLevel; //Stores the achievement level that is after the one user achieved
    private int pointsAwayFromNextLevel; //Stores how points away user is from achieving next level

    public Achievements(String theme){
        //Three different arrays for three themes
        this.achievementsFruits = new String[]{"Beautiful Bananas", "Wonderful Watermelons",
                "Outstanding Oranges", "Admirable Apricots", "Good Grapefruits", "Amazing Apples",
                "Great Grapes", "Better Blueberries", "Super Strawberries", "Perfect Peaches"};
        this.achievementsFantasy = new String[]{"Stinky Orc", "Friendly Troll", "Grumpy Dwarf",
                "High Elf", "Powerful Lich", "Golden Pegasus", "Ferocious Minotaur", "Majestic Unicorn",
                "Shadow Wyvern", "Azure Dragon"};
        this.achievementsStarWars = new String[]{"Grogu", "Chewbacca", "Anakin", "Cara Dune", "Ahsoka Tano",
                "Luke Skywalker", "Yoda", "Darth Vader", "Asajj Ventress", "Leia Organa"};
        this.numOfBoundedLevels = 8;
        this.theme = theme;
    }

    public int getNumOfBoundedLevels(){
        return numOfBoundedLevels;
    }
    public String getLevelAchieved() {
        return levelAchieved;
    }
    public void setAchievementTheme(String theme){
        this.theme = theme;
    }
    public String getAchievementTheme(){
        return theme;
    }

    public String getAchievementLevel(int index){
        //Get the achievement level for the theme selected in radio button
        if (this.theme.equals(FANTASY)){
            return achievementsFantasy[index];
        }
        if (this.theme.equals(STAR_WARS)){
            return achievementsStarWars[index];
        }
        return achievementsFruits[index];
    }

    public int calculateMinMaxScore(int score, int numPlayers){
        if(numPlayers <= 0){ throw new IllegalArgumentException("number of players should not be negative and cannot be zero");}
        return score * numPlayers;
    }

    public int calculateLevelRange(int min, int max){
        if(min > max){
            throw new IllegalArgumentException("min is greater than max");
        }
        int range = ((max - min)/8);
        return range;
    }

    //used in: ViewAchievements, AddNewGame
    //Gets the lower and upper bound for each level
    public void setAchievementsBounds(int min, int max, int players) {
        if(players <= 0){ throw new IllegalArgumentException("players should not be negative and cannot be zero");}
        minScore = calculateMinMaxScore(min, players);
        maxScore = calculateMinMaxScore(max, players);
        this.intAchievements[0] = minScore;
        this.intAchievements[1] = minScore;
        int range = calculateLevelRange(minScore, maxScore);
        for (int i = 2; i < intAchievements.length - 1; i++){
            this.intAchievements[i] = (this.intAchievements[i-1] + range + 1);
        }
        this.intAchievements[9] = maxScore + 1;
    }

    public void calculateLevelAchieved(int combinedScore){
        boolean calculatingLevel = true;
        //If the combined score is less than the poor score, user has lowest level
        if (combinedScore < intAchievements[0]){
            this.levelAchieved = getAchievementLevel(0);
            this.indexLevelAchieved = 0;
            this.isHighestLevel = false;
            this.nextAchievementLevel = getAchievementLevel(1);
            this.pointsAwayFromNextLevel = intAchievements[1] - combinedScore;
        } else if (combinedScore >= intAchievements[9]){ //If combined score is greater than great score, user has highest level
            this.levelAchieved = getAchievementLevel(9);
            this.indexLevelAchieved = 9;
            this.isHighestLevel = true;
        } else { //Else go through the array that stores each upper bound
            while (calculatingLevel) {
                for (int i = 1; i < intAchievements.length - 1; i++) {
                    if (i == (intAchievements.length - 1)) { //If array is at last position before greatest level, achievement level is before greatest level
                        if (combinedScore >= intAchievements[i] && combinedScore <= maxScore) {
                            this.levelAchieved = getAchievementLevel(i);
                            this.indexLevelAchieved = i;
                            this.isHighestLevel = false;
                            this.nextAchievementLevel = getAchievementLevel(i+1);
                            this.pointsAwayFromNextLevel = maxScore - combinedScore;
                            calculatingLevel = false;
                        }
                    } else { //If array is not at last position and value is greater than current level bound but less than i+1 level bound
                        if (combinedScore >= intAchievements[i] && combinedScore < intAchievements[i + 1]) {
                            this.levelAchieved = getAchievementLevel(i);
                            this.indexLevelAchieved = i;
                            this.isHighestLevel = false;
                            this.nextAchievementLevel = getAchievementLevel(i+1);
                            this.pointsAwayFromNextLevel = intAchievements[i+1] - combinedScore;
                            calculatingLevel = false;
                        }
                    }
                }
            }
        }
    }

    public void setAchievementsScores(int min, int max, int players){
        if(players <= 0){ throw new IllegalArgumentException("players should not be negative and cannot be zero");}
        minScore = calculateMinMaxScore(min, players);
        maxScore = calculateMinMaxScore(max, players);
        this.intAchievements[0] = minScore;
        this.intAchievements[1] = minScore;
        for (int i = 2; i <= (maxScore - minScore + 1); i++){
            this.intAchievements[i] = minScore + i - 1;
        }
        if ((max - min + 1) != 8) {
            this.intAchievements[maxScore - minScore + 1] = maxScore;
        }
    }

    public void calculateScoreAchieved(int combinedScore){
        if (combinedScore < intAchievements[0]){
            this.levelAchieved = getAchievementLevel(0);
            this.indexLevelAchieved = 0;
            this.isHighestLevel = false;
            this.nextAchievementLevel = getAchievementLevel(1);
            this.pointsAwayFromNextLevel = intAchievements[1] - combinedScore;
        }
        else if (combinedScore >= intAchievements[maxScore - minScore + 1]){
            this.levelAchieved = getAchievementLevel(9);
            this.indexLevelAchieved = 9;
            this.isHighestLevel = true;
        }
        else{
            for (int i = 1; i < (maxScore - minScore + 2); i++){
                if (combinedScore == intAchievements[i]){
                    this.levelAchieved = getAchievementLevel(i);
                    this.indexLevelAchieved = i;
                    this.isHighestLevel = false;
                    this.nextAchievementLevel = getAchievementLevel(i+1);
                    this.pointsAwayFromNextLevel = intAchievements[i+1] - combinedScore;
                }
            }
        }
    }

    public void setDifficultyLevel(int newLevel){
        this.currentDifLevel = newLevel;
    }
    public int getDifficultyLevel(){
        return this.currentDifLevel;
    }
    public int getIndexLevelAchieved(){return this.indexLevelAchieved;}
    public boolean isHighestLevelAchieved(){return isHighestLevel;}
    public String getNextAchievementLevel(){return nextAchievementLevel;}
    public void setNextAchievementLevel(String nextLevel){this.nextAchievementLevel = nextLevel;}
    public int getPointsAwayFromNextLevel(){return pointsAwayFromNextLevel;}
}