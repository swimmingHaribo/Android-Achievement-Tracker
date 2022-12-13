package ca.coopAchievement.app.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AchievementsTest {

    @Test
    void getNumOfBoundedLevels() {
        Achievements newAchievements = new Achievements("");
        //check standard
        assertEquals(8, newAchievements.getNumOfBoundedLevels());
    }

    @Test
    void getAchievementLevel() {
        Achievements newAchievements = new Achievements("");
        assertEquals("Beautiful Bananas",newAchievements.getAchievementLevel(0));
        assertEquals("Wonderful Watermelons",newAchievements.getAchievementLevel(1));
        assertEquals("Outstanding Oranges",newAchievements.getAchievementLevel(2));
        assertEquals("Admirable Apricots",newAchievements.getAchievementLevel(3));
        assertEquals("Good Grapefruits",newAchievements.getAchievementLevel(4));
        assertEquals("Amazing Apples",newAchievements.getAchievementLevel(5));
        assertEquals("Great Grapes",newAchievements.getAchievementLevel(6));
        assertEquals("Better Blueberries",newAchievements.getAchievementLevel(7));
        assertEquals("Super Strawberries",newAchievements.getAchievementLevel(8));
        assertEquals("Perfect Peaches",newAchievements.getAchievementLevel(9));
    }



    @Test
    void calculateMinMaxScore() {
        Achievements newAchievements = new Achievements("");
        //check with set number of score and players
        int scoreTest = 100;
        int playersTest = 100;
        assertEquals(scoreTest * playersTest,newAchievements.calculateMinMaxScore(scoreTest, playersTest));
    }

    @Test
    void calculateLevelRange() {
        Achievements newAchievements = new Achievements("");
        int minTest = 10;
        int maxTest = 20;
        assertEquals((maxTest - minTest)/8, newAchievements.calculateLevelRange(minTest, maxTest));
        int finalMinTest = 20;
        int finalMaxTest = 10;
        assertThrows(IllegalArgumentException.class, () -> newAchievements.calculateLevelRange(finalMinTest, finalMaxTest));
    }

    @Test
    void setAchievementsBoundsANDcalculateLevelAchieved() {
        Achievements newAchievements = new Achievements("");
        int finalMinTest = -1;
        int finalMaxTest = -2;
        int finalPlayerTest = -1;
        assertThrows(IllegalArgumentException.class,
                ()-> newAchievements.setAchievementsBounds(finalMinTest, finalMaxTest, finalPlayerTest));
        int minTest = 1;
        int maxTest = 2;
        int playerTest = 1;
        newAchievements.setAchievementsBounds(minTest, maxTest, playerTest);
        //combinedScore < intAchievements[0]
        int combinedScoreTest = -10;
        newAchievements.calculateLevelAchieved(combinedScoreTest);
        assertEquals("Beautiful Bananas", newAchievements.getLevelAchieved());
        //combinedScore < intAchievements[0]
        combinedScoreTest = 10;
        newAchievements.calculateLevelAchieved(combinedScoreTest);
        assertEquals("Perfect Peaches", newAchievements.getLevelAchieved());
        //combinedScore >= intAchievements[9]
        combinedScoreTest = 1;
        newAchievements.calculateLevelAchieved(combinedScoreTest);
        assertEquals("Wonderful Watermelons", newAchievements.getLevelAchieved());
        //else
    }

    @Test
    void setAchievementsScoresANDcalculateScoreAchieved() {
        Achievements newAchievements = new Achievements("");
        int finalMinTest = -1;
        int finalMaxTest = -2;
        int finalPlayerTest = -1;
        assertThrows(IllegalArgumentException.class,
                ()-> newAchievements.setAchievementsScores(finalMinTest, finalMaxTest, finalPlayerTest));
        int minTest = 1;
        int maxTest = 2;
        int playerTest = 1;
        newAchievements.setAchievementsScores(minTest, maxTest, playerTest);
        int combinedScoreTest = 10;
        newAchievements.calculateScoreAchieved(combinedScoreTest);
        assertEquals("Perfect Peaches", newAchievements.getLevelAchieved());
        combinedScoreTest = -10;
        newAchievements.calculateScoreAchieved(combinedScoreTest);
        assertEquals("Beautiful Bananas", newAchievements.getLevelAchieved());
        combinedScoreTest = 1;
        newAchievements.calculateScoreAchieved(combinedScoreTest);
        assertEquals("Wonderful Watermelons", newAchievements.getLevelAchieved());
    }
}