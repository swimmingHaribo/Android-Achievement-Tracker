package ca.coopAchievement.app;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import ca.coopAchievement.app.model.Configuration;
import ca.coopAchievement.app.model.ConfigurationsManager;
import ca.coopAchievement.app.model.SaveUsingGson;

/*
* view config activity class shows basic info of config after it was clicked from main activity
* it has buttons for edit, delete, view achievements,
* add new game and history (when there were played games)
 */

public class ViewConfiguration extends AppCompatActivity {

    private TextView expPoorScoreEditTxt;
    private TextView expGreatScoreEditTxt;
    private Button editConfigScreenBtn;
    private int currentConfigPosition;
    private ConfigurationsManager manager = ConfigurationsManager.getInstance();
    private SaveUsingGson toSaveUsingGsonAndSP = new SaveUsingGson();
    private ImageView configImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_configuration);

        ActionBar bar = getSupportActionBar();
        bar.setDisplayHomeAsUpEnabled(true);

        UpdateUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpGameHistoryButton();
        UpdateUI();
        //to save config manager
        toSaveUsingGsonAndSP.saveToSharedRefs(ViewConfiguration.this);
    }


    private void UpdateUI() {
        currentConfigPosition = manager.getIndexOfCurrentConfiguration();
        manager = ConfigurationsManager.getInstance();
        Configuration currentConfig = manager.getItemAtIndex(currentConfigPosition);
        //Activity Name
        getSupportActionBar().setTitle(getString(R.string.game___configuration, currentConfig.getGameNameFromConfig()));
        //find locations
        expPoorScoreEditTxt = findViewById(R.id.textFillPoorScoreConfigView);
        expGreatScoreEditTxt = findViewById(R.id.textFillGreatScoreConfigView);
        //populate them
        expPoorScoreEditTxt.setText(String.valueOf(currentConfig.getMinPoorScoreFromConfig()));
        expGreatScoreEditTxt.setText(String.valueOf(currentConfig.getMaxBestScoreFromConfig()));

        //make edit button open edit configuration screen.
        editConfigScreenBtn = findViewById(R.id.btnEditConfig);
        editConfigScreenBtn.setOnClickListener(view -> {
            Intent intent = new Intent(ViewConfiguration.this, AddEditConfiguration.class);
            intent.putExtra(getString(R.string.selected_config_position), currentConfigPosition);
            startActivity(intent);
        });
        //set up buttons for new game and history
        setUpGameHistoryButton();
        setUpDeleteButton(currentConfigPosition);
        setUpAddGameButton();
        setUpAchievementStatsButton();
        expGreatScoreEditTxt.setText(String.valueOf(currentConfig.getMaxBestScoreFromConfig()));
        setUpViewAchievementsButton();
        //to save config manager
        toSaveUsingGsonAndSP.saveToSharedRefs(ViewConfiguration.this);

        //set uo image on click method
        setOnImageClick();
        //set image to Uri from config
        configImage = findViewById(R.id.configImage);
        if(manager.getItemAtIndex(currentConfigPosition).imageStringToBitMap() != null){
            Bitmap imageBM = manager.getItemAtIndex(currentConfigPosition).imageStringToBitMap();
            configImage.setImageBitmap(imageBM);
        }
    }

    public static Intent makeIntent(Context context) {
        return new Intent(context, ViewConfiguration.class);
    }

    private void setUpViewAchievementsButton() {
        Button achievementBtn = findViewById(R.id.viewAchievementsBtn);
        achievementBtn.setOnClickListener(v ->{
            Intent intent = ViewAchievements.makeIntent(ViewConfiguration.this);
            intent.putExtra("Achievement Game Name", currentConfigPosition);
            startActivity(intent);
        });
    }

    private void setUpGameHistoryButton() {
        Button historyBtn = findViewById(R.id.btnHistoryConfig);
        // image made from miro
        // https://miro.com
        ImageView image = findViewById(R.id.image_history);
        if(manager.getItemAtIndex(currentConfigPosition).getSizeOfListOfGamePlays() == 0){
            historyBtn.setVisibility(View.INVISIBLE);
            image.setVisibility(View.VISIBLE);
        }else {
            //get rid of image
            image.setVisibility(View.INVISIBLE);
            historyBtn.setVisibility(View.VISIBLE);
            historyBtn.setOnClickListener(v -> {
                Intent intent2 = GameHistory.makeIntent(ViewConfiguration.this);
                startActivity(intent2);
            });
        }
    }

    private void setUpAddGameButton() {
        Button addBtn = findViewById(R.id.addGameBtn);
        addBtn.setOnClickListener(v -> {
            Intent intent = AddNewGame.makeIntent(ViewConfiguration.this);
            startActivity(intent);
        });
    }

    private void setUpDeleteButton(int currentConfigPosition) {
        Button deleteBtn = findViewById(R.id.btnDeleteConfig);
        deleteBtn.setOnClickListener(v -> deleteOrCancel(currentConfigPosition));
    }

    //ask user on click of delete button
    //if he intents to delete config or not
    private void deleteOrCancel(int currentConfigPosition) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.delete_msg))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.delete), (dialog, id) -> {
                    manager.remove(currentConfigPosition);
                    //saves the change
                    toSaveUsingGsonAndSP.saveToSharedRefs(ViewConfiguration.this);
                    Intent k = new Intent(ViewConfiguration.this, MainActivity.class);
                    startActivity(k);

                })
                .setNegativeButton(getString(R.string.cancel), (dialog, id) -> dialog.cancel());

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void setUpAchievementStatsButton(){
        Button statsBtn = findViewById(R.id.achievementStatsBtn);
        //If no game has been played for the configuration, don't show the achievement stats button
        if (manager.getItemAtIndex(currentConfigPosition).getSizeOfListOfGamePlays() == 0){
            statsBtn.setVisibility(View.GONE);
        } else { //If at least one game has been played, show achievement stats button
            statsBtn.setVisibility(View.VISIBLE);
            statsBtn.setOnClickListener(v -> {
                Intent intent = AchievementStatistics.makeIntent(ViewConfiguration.this);
                startActivity(intent);
            });
        }
    }

    private void setOnImageClick() {
        configImage = findViewById(R.id.configImage);
        configImage.setOnClickListener(view -> {
            manager.setIndexOfCurrentConfiguration(currentConfigPosition);
            Intent intent = ViewImage.makeIntent(ViewConfiguration.this);
            startActivity(intent);
        });
    }
}