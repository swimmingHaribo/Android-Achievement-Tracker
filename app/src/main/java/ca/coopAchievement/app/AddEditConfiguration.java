package ca.coopAchievement.app;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import ca.coopAchievement.app.model.Configuration;
import ca.coopAchievement.app.model.ConfigurationsManager;

/*
* activity class addEditConfiguration
* is a UI class for both add config and edit config activities
* contains and manipulates user input in the corresponding activities
* checks user input for validity and saves it into config manager
* does not allow user to input into the fields if they exceed limits
 */

public class AddEditConfiguration extends AppCompatActivity {

    private EditText gameNameFromUser;
    private EditText expPoorScoreFromUser;
    private EditText expGreatScoreFromUser;

    private String gameNameAsStr;
    private String expPoorScoreAsStr;
    private String expGreatScoreAsStr;

    private int expPoorScore;
    private int expGreatScore;

    private final int MAX_NAME_LENGTH = 100;
    private final int MAX_POS_SCORE_INPUT = 100000000;
    private final int MAX_NEG_SCORE_INPUT = -100000000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_configuration);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        getUserInput();
        checkUserInput();

        Bundle b = getIntent().getExtras();
        if(b!= null){
            //Edit Game Config Activity
            int currentConfigPosition = b.getInt(getString(R.string.selected_config_position));
            ConfigurationsManager manager = ConfigurationsManager.getInstance();
            Configuration currentConfig = manager.getItemAtIndex(currentConfigPosition);

            getSupportActionBar().setTitle(getString(R.string.edit_game___configuration, currentConfig.getGameNameFromConfig()));

            //set variables from pre-existing config to the screen
            String gameName = String.valueOf(currentConfig.getGameNameFromConfig());
            String minScoreStr = String.valueOf(currentConfig.getMinPoorScoreFromConfig());
            String maxScoreStr = String.valueOf(currentConfig.getMaxBestScoreFromConfig());
            setVariablesFromExistingConfig(gameName, minScoreStr, maxScoreStr);
            setUpSaveConfigButton();
        }
        else{
            //Add Game Config Activity
            getSupportActionBar().setTitle(R.string.add_new_game_config);
            getUserInput();
            setUpSaveConfigButton();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //makes intent to start an activity
    public static Intent makeIntent(Context context){
        return new Intent(context, AddEditConfiguration.class);
    }

    //check user input for the validity
    private void checkUserInput(){
        gameNameFromUser.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Does not do anything
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                gameNameAsStr = gameNameFromUser.getText().toString();
                if (gameNameAsStr.length() >= MAX_NAME_LENGTH){
                    displayMaxNameLengthMsg();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Does not do anything
            }
        });

        expPoorScoreFromUser.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Does not do anything
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                expPoorScoreAsStr = expPoorScoreFromUser.getText().toString();
                try{
                    expPoorScore = Integer.parseInt(expPoorScoreAsStr);
                     if (expPoorScore >= MAX_POS_SCORE_INPUT || expPoorScore <= MAX_NEG_SCORE_INPUT) {
                        displayMaxScoreMsg(true);
                    }
                }catch (NumberFormatException ex){
                    if (expPoorScoreFromUser.length() == 0) {
                        Toast.makeText(AddEditConfiguration.this, R.string.emptyOrInvalid, Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Does not do anything
            }
        });

        expGreatScoreFromUser.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Does not do anything
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                expGreatScoreAsStr = expGreatScoreFromUser.getText().toString();
                try{
                    expGreatScore = Integer.parseInt(expGreatScoreAsStr);
                    if (expGreatScore >= MAX_POS_SCORE_INPUT || expGreatScore <= MAX_NEG_SCORE_INPUT){
                        displayMaxScoreMsg(false);
                    }
                }catch (NumberFormatException ex){
                    if (expGreatScoreFromUser.length() == 0) {
                        Toast.makeText(AddEditConfiguration.this, R.string.emptyOrInvalid, Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Does not do anything
            }
        });
    }

    //give an alert dialog message if user exceeded limits for string fields input
    private void displayMaxNameLengthMsg(){
        AlertDialog alertDialog = new AlertDialog.Builder(AddEditConfiguration.this).create();
        alertDialog.setTitle(getString(R.string.game_name_is_too_long));
        alertDialog.setMessage(getString(R.string.sorry_game_name_is_too_long_try_shorter));
        alertDialog.setButton(getString(R.string.Ok), (dialog, which) -> {
            //Stay on ViewAchievement activity
        });
        alertDialog.show();
        //set num of player to the minimum
        gameNameFromUser = findViewById(R.id.inputName);
        gameNameFromUser.setText("");
    }

    //give an alert dialog message if user exceeded limits for number fields input
    private void displayMaxScoreMsg(boolean isPoorScore){
        AlertDialog alertDialog = new AlertDialog.Builder(AddEditConfiguration.this).create();
        alertDialog.setTitle(getString(R.string.game_score_is_too_long));
        alertDialog.setMessage(getString(R.string.sorry_score_is_too_long_please_try_shorter));
        alertDialog.setButton(getString(R.string.Ok), (dialog, which) -> {
            //Stay on ViewAchievement activity
        });
        alertDialog.show();
        //set num of player to the minimum
        if (isPoorScore) {
            expPoorScoreFromUser = findViewById(R.id.inputLowScore);
            expPoorScoreFromUser.setText("");
        }
        else{
            expGreatScoreFromUser = findViewById(R.id.inputHighScore);
            expGreatScoreFromUser.setText("");
        }
    }

    private void getUserInput(){
        //Input into TextEdit variables
        gameNameFromUser = findViewById(R.id.inputName);
        expPoorScoreFromUser = findViewById(R.id.inputLowScore);
        expGreatScoreFromUser = findViewById(R.id.inputHighScore);
    }

    private void setUpSaveConfigButton(){
        Button saveConfigBtn = findViewById(R.id.saveConfigBtn);
        saveConfigBtn.setOnClickListener(v ->{
            Bundle b = getIntent().getExtras();
            //get variables checked again for the new input
            getUserInput();
            convertTxtToStr();
            //if it is an add new config activity
            if(b == null){
                convertTxtToStr();
                //Save game if all values are valid
                if (isUserInputValid()) {
                    Configuration newConfig = new Configuration(gameNameAsStr, expPoorScore, expGreatScore);
                    ConfigurationsManager manager = ConfigurationsManager.getInstance();
                    manager.add(newConfig);
                    finish();
                }
            }
            //if it an edit config activity
            else{
                if (isUserInputValid()) {
                    int currentConfigPosition = b.getInt(getString(R.string.selected_config_position));
                    ConfigurationsManager manager = ConfigurationsManager.getInstance();
                    Configuration currentConfig = manager.getItemAtIndex(currentConfigPosition);
                    currentConfig.setGameNameInConfig(gameNameAsStr);
                    currentConfig.setMinPoorScoreInConfig(expPoorScore);
                    currentConfig.setMaxBestScoreInConfig(expGreatScore);
                    manager.setItemAtIndex(currentConfigPosition, currentConfig);
                    finish();
                }
            }
        });
    }

    private void convertTxtToStr(){
        //Convert TextEdit into string
        gameNameAsStr = gameNameFromUser.getText().toString();
        expPoorScoreAsStr = expPoorScoreFromUser.getText().toString();
        expGreatScoreAsStr = expGreatScoreFromUser.getText().toString();
    }

    private void convertStringToInt(){
        expPoorScore = Integer.parseInt(expPoorScoreAsStr);
        expGreatScore = Integer.parseInt(expGreatScoreAsStr);
    }

    private boolean isUserInputValid(){
        if (TextUtils.isEmpty(gameNameAsStr)
                || TextUtils.isEmpty(expPoorScoreAsStr)
                || TextUtils.isEmpty(expGreatScoreAsStr)){
            Toast.makeText(this, R.string.ERROR_text_fields_cannot_be_empty, Toast.LENGTH_SHORT).show();
            return false;
        }
        convertStringToInt();
        if (expPoorScore >= expGreatScore){
            Toast.makeText(this, R.string.poor_score_must_be_less_than_great_score_try_again, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    //in the edit config sets variables from the existing config into the edit text fields
    private void setVariablesFromExistingConfig(String gameName,String minScoreStr, String maxScoreStr) {
        getUserInput();

        gameNameFromUser.setText(gameName);
        expPoorScoreFromUser.setText(minScoreStr);
        expGreatScoreFromUser.setText(maxScoreStr);

        convertTxtToStr();
        convertStringToInt();
    }

    //end of the class
}
