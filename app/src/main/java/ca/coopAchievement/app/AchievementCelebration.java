package ca.coopAchievement.app;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import ca.coopAchievement.app.model.Achievements;
import ca.coopAchievement.app.model.ConfigurationsManager;
import ca.coopAchievement.app.model.Game;

/*
*  AchievementCelebration class displays an animation, plays a sound, when user achieves a level
*  It shows how many points away the user is from the next level, or will tell user they are already at highest level
*  User can change the theme using the dropdown menu and can replay the animation
 */
public class AchievementCelebration extends AppCompatActivity {
    private final String FRUITS = "Fruits";
    private final String FANTASY = "Fantasy";
    private final String STAR_WARS = "Star Wars";
    private ConfigurationsManager manager = ConfigurationsManager.getInstance();
    private String gameTheme;
    private int currentConfigPosition;
    private int indexOfGame;
    private int selectedTheme;
    private Achievements achievements;
    private String levelAchieved;
    private boolean isNewGame;
    private MediaPlayer mediaPlayer;
    private Animation fadeOut;
    private TextView userLevelAchieved;
    private int indexLevelAchieved;
    private boolean isHighestLevel; //Is true if user received highest level
    private TextView nextLevel; //Text View to display the next level after the one user achieved
    private String nextAchievementLevel; //String for next level after the one user achieved
    private int pointsAwayFromNextLevel; //Stores how points away user is from achieving next level

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        gameTheme = getAchievementTheme(this);
        achievements = new Achievements(getAchievementTheme(this));
        currentConfigPosition = manager.getIndexOfCurrentConfiguration();

        Bundle bundle = getIntent().getExtras(); // from game history
        if (bundle != null){ //If game is being edited
            isNewGame = false;
            indexOfGame = bundle.getInt("selected game"); //index of game is game being edited
        } else { //If new game is being added
            isNewGame = true;
            indexOfGame = manager.getItemAtIndex(currentConfigPosition).getSizeOfListOfGamePlays() - 1; //index of game is last position
        }

        //Get the level user achieved when editing or adding new game
        levelAchieved = getGame().getLevelAchieved();
        //Get the index position of level achieved
        indexLevelAchieved = getGame().getIndexGameLevelAchieved();
        //Check if user achieved highest level
        isHighestLevel = getGame().isHighestLevelAchieved();
        //Get the name of the next higher level
        nextAchievementLevel = getGame().getNextAchievementLevel();
        displayAchievementThemeLayout();
        storeSelectedAchievementTheme();
    }

    //Get the game that is being added or edited
    private Game getGame() {
        return manager.getItemAtIndex(currentConfigPosition).getGame(indexOfGame);
    }

    //Display the matching theme layout for achievement celebration
    private void displayAchievementThemeLayout(){
        if (gameTheme.equals(FRUITS)){
            setContentView(R.layout.fruitsalertdialog);
            showFruitsResult();
        } else if (gameTheme.equals(FANTASY)){
            setContentView(R.layout.fantasyalertdialog);
            showFantasyResult();
        } else if (gameTheme.equals(STAR_WARS)){
            setContentView(R.layout.starwarsalertdialog);
            showStarWarsResult();
        }
    }

    //Change the theme if user changes theme using the dropdown
    private void storeSelectedAchievementTheme(){
        Spinner dropdown = findViewById(R.id.dropdownTheme);
        String[] themesArray = getResources().getStringArray(R.array.achievementThemes);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(AchievementCelebration.this, R.array.achievementThemes, android.R.layout.simple_spinner_dropdown_item);
        dropdown.setAdapter(adapter);
        //Display Set a Theme prompt
        dropdown.setPrompt(getResources().getString(R.string.select_theme_prompt));

        for (int i = 0; i < themesArray.length; i++) {
            if (themesArray[i].equals(gameTheme)) {
                dropdown.setSelection(i);
            }
        }
        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //When user selects a new theme, recreate activity to display theme levels and achievements
                selectedTheme = dropdown.getSelectedItemPosition();
                String[] themesArray = getResources().getStringArray(R.array.achievementThemes);
                for (int i = 0; i < themesArray.length; i++){
                    if (i == selectedTheme && !achievements.getAchievementTheme().equals(themesArray[i])) {
                        final String achievementTheme = themesArray[i];
                        saveAchievementTheme(achievementTheme);
                        achievements.setAchievementTheme(achievementTheme);
                        //Set the level achieved for the new theme selected at the same index position of previously selected level
                        getGame().setLevelAchieved(achievements.getAchievementLevel(indexLevelAchieved));
                        //Get the next higher level for the new theme if the level user achieved is not the highest
                        if (!isHighestLevel) {
                            getGame().setNextAchievementLevel(achievements.getAchievementLevel(indexLevelAchieved + 1));
                        }
                        //Change theme for game
                        manager.getItemAtIndex(currentConfigPosition).getGame(indexOfGame).setTheme(achievements.getAchievementTheme());
                        AchievementCelebration.this.recreate();
                    }
                }
            }

            @Override
            //Do nothing
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void playSound(){
        if (gameTheme.equals(FRUITS)){
            mediaPlayer = MediaPlayer.create(this, R.raw.fruitslice);
        }
        if (gameTheme.equals(FANTASY)){
            mediaPlayer = MediaPlayer.create(this, R.raw.fairysound);
        }
        if (gameTheme.equals(STAR_WARS)){
            mediaPlayer = MediaPlayer.create(this, R.raw.lightsaber);
        }
        mediaPlayer.start();
    }

    private void displayLevelAchievedAndNextLevel() {
        //Display the level user achieved
        userLevelAchieved = findViewById(R.id.levelAchievedTxt);
        userLevelAchieved.setText(levelAchieved);

        nextLevel = findViewById(R.id.nextLevelTxt);
        pointsAwayFromNextLevel = getGame().getPointsAwayFromNextLevel();
        if (isHighestLevel){ //If user got highest level, display highest level msg
            nextLevel.setText(getResources().getString(R.string.got_highest_level_msg));
        } else { //Else, display how many points away user's from next level and name of next level
            nextLevel.setText(getResources().getString(R.string.you_were_msg) + " " + pointsAwayFromNextLevel + " " + getResources().getString(R.string.points_away_msg) + " " +  nextAchievementLevel);
        }
    }

    //Show achievement celebration for fruits theme
    private void showFruitsResult(){
        //Play sound
        playSound();
        //Display the level user achieved and the next level
        displayLevelAchievedAndNextLevel();

        //Display the animation
        fadeOut = AnimationUtils.loadAnimation(this,R.anim.fadeout);
        ImageView foxCelebrationAnimation = findViewById(R.id.celebrationAlertsImage);
        foxCelebrationAnimation.startAnimation(fadeOut);
        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            //Don't have animation code here, animation will not end properly
            public void onAnimationStart(Animation animation) {}
            @Override
            public void onAnimationEnd(Animation animation) {
                //End the animation
                foxCelebrationAnimation.setVisibility(View.GONE);
            }
            @Override
            //Do nothing
            public void onAnimationRepeat(Animation animation) {}
        });
        //Set up button to leave achievement celebration page
        Button okBtn = findViewById(R.id.appleOkBtn);
        okBtn.setOnClickListener(v -> {
            //If adding a new game, go to game config
            Intent refresh;
            if (isNewGame){
                refresh = new Intent(AchievementCelebration.this, ViewConfiguration.class);
            } else { //If editing a game, go to history
                refresh = new Intent(AchievementCelebration.this, GameHistory.class);
            }
            startActivity(refresh);
        });
        //Set up button to replay animation and sound
        Button replayBtn = findViewById(R.id.appleReplayBtn);
        replayBtn.setOnClickListener(v -> {
            foxCelebrationAnimation.startAnimation(fadeOut);
            playSound();
        });
    }

    //Show achievement celebration for fantasy theme
    private void showFantasyResult(){
        //Play sound
        playSound();
        //Display the level user achieved and the next level
        displayLevelAchievedAndNextLevel();

        //Display the animation
        fadeOut = AnimationUtils.loadAnimation(this,R.anim.fadeout);
        ImageView foxCelebrationAnimation = findViewById(R.id.celebrationAlertsImage);
        foxCelebrationAnimation.startAnimation(fadeOut);
        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            //Don't have animation code here, animation will not end properly
            public void onAnimationStart(Animation animation) {}
            @Override
            public void onAnimationEnd(Animation animation) {
                //End the animation
                foxCelebrationAnimation.setVisibility(View.GONE);
            }
            @Override
            //Do nothing
            public void onAnimationRepeat(Animation animation) {}
        });
        //Set up button to leave achievement celebration page
        Button okBtn = findViewById(R.id.starOkBtn);
        okBtn.setOnClickListener(v -> {
            //If adding a new game, go to game config
            Intent refresh;
            if (isNewGame){
                refresh = new Intent(AchievementCelebration.this, ViewConfiguration.class);
            } else { //If editing a game, go to history
                refresh = new Intent(AchievementCelebration.this, GameHistory.class);
            }
            startActivity(refresh);
        });
        //Set up button to replay animation and sound
        Button replayBtn = findViewById(R.id.starReplayBtn);
        replayBtn.setOnClickListener(v -> {
            foxCelebrationAnimation.startAnimation(fadeOut);
            playSound();
        });
    }

    //Show achievement celebration for starwars theme
    private void showStarWarsResult(){
        //Play sound
        playSound();
        //Display the level user achieved and the next level
        displayLevelAchievedAndNextLevel();

        //Display the animation
        fadeOut = AnimationUtils.loadAnimation(this,R.anim.fadeout);
        ImageView foxCelebrationAnimation = findViewById(R.id.celebrationAlertsImage);
        foxCelebrationAnimation.startAnimation(fadeOut);
        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            //Don't have animation code here, animation will not end properly
            public void onAnimationStart(Animation animation) {}
            @Override
            public void onAnimationEnd(Animation animation) {
                //End the animation
                foxCelebrationAnimation.setVisibility(View.GONE);
            }
            @Override
            //Do nothing
            public void onAnimationRepeat(Animation animation) {}
        });
        //Set up button to leave achievement celebration page
        Button okBtn = findViewById(R.id.yodaOkBtn);
        okBtn.setOnClickListener(v -> {
            //If adding a new game, go to game config
            Intent refresh;
            if (isNewGame){
                refresh = new Intent(AchievementCelebration.this, ViewConfiguration.class);
            } else { //If editing a game, go to history
                refresh = new Intent(AchievementCelebration.this, GameHistory.class);
            }
            startActivity(refresh);
        });
        //Set up button to replay animation and sound
        Button replayBtn = findViewById(R.id.yodaReplayBtn);
        replayBtn.setOnClickListener(v -> {
            foxCelebrationAnimation.startAnimation(fadeOut);
            playSound();
        });
    }

    private void saveAchievementTheme(String theme){
        SharedPreferences preferences = this.getSharedPreferences("Theme Preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("Achievement Theme", theme);
        editor.apply();
    }

    static public String getAchievementTheme(Context context){
        SharedPreferences preferences = context.getSharedPreferences("Theme Preferences", MODE_PRIVATE);
        String defaultTheme = context.getResources().getString(R.string.defaultTheme);
        return preferences.getString("Achievement Theme",defaultTheme);
    }
}