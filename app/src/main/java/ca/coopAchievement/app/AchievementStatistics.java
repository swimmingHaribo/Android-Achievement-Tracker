//Referenced https://www.youtube.com/watch?v=pi1tq-bp7uA and https://www.youtube.com/watch?v=H6QxMBI2QH4
//Creating a Simple Bar Graph for your Android Application (part 1/2) and Creating a Simple Bar Graph for your Android Application (part 2/2) by CodingWithMitch
//Watched the above youtube videos to learn how to make a bar graph
//https://weeklycoding.com/mpandroidchart-documentation/  (used this documentation to modify the graph)
//https://stackoverflow.com/a/69951831 (used this to add repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS) and maven { url 'https://jitpack.io' } in settings.gradle

package ca.coopAchievement.app;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;

import ca.coopAchievement.app.model.ConfigurationsManager;

/*
* AchievementStatistics class will display in a graph how many times user has achieved the each level
* regardless of theme. If user edits the game play and achieves a new level, the previous level will be deleted from stats
 */
public class AchievementStatistics extends AppCompatActivity {
    private final int LOWEST_LEVEL = 1;
    private final int GREATEST_LEVEL = 10;
    private BarChart statsGraph;
    private ConfigurationsManager manager = ConfigurationsManager.getInstance();
    private int selectedGamePosition;
    private ArrayList<Integer> achievementLevelNumbers; //Arraylist of all of the achievement levels number values
    private ArrayList<BarEntry> timesLevelEarned; //Arraylist of the number of times user achieved a level for a game configuration
    private PopupWindow popUpLevelNames;
    private LayoutInflater inflater;
    private View popUpView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        selectedGamePosition = manager.getIndexOfCurrentConfiguration();
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        //Check if selected game configuration was made before achievement stats was added
        if (manager.getItemAtIndex(selectedGamePosition).isAchievementsEarnedStatsArrNull()){
            setContentView(R.layout.achievements_statistics_null); //Display msg telling user to add new game config
        } else { //Else display achievement stats graph
            setContentView(R.layout.activity_achievement_statistics);
            statsGraph = findViewById(R.id.stats_graph);
            createAchievementStatisticsGraph();
            setUpPopUpLevelNameButton();
        }
    }

    public static Intent makeIntent(Context context){
        return new Intent(context, AchievementStatistics.class);
    }

    public void createAchievementStatisticsGraph() {
        //The x-axis values for the number of achievement levels
        achievementLevelNumbers = new ArrayList<>();
        for (int i = LOWEST_LEVEL; i <= GREATEST_LEVEL; i ++) {
            achievementLevelNumbers.add(i);
        }
        //The y-axis values for the times user has earned each level
        timesLevelEarned = new ArrayList<>();
        for (int i = 0; i < achievementLevelNumbers.size(); i++) {
            //If level is not earned at least once
            if (manager.getItemAtIndex(selectedGamePosition).getAchievementsEarnedStats(i) == 0){
                //Don't add to graph
            } else { //Else, add number of each achievement level is earned to the level number
                timesLevelEarned.add(new BarEntry(achievementLevelNumbers.get(i), manager.getItemAtIndex(selectedGamePosition).getAchievementsEarnedStats(i)));
            }
        }
        //Create the data set for the number of times user has earned each level
        BarDataSet levelsEarnedDataSet = new BarDataSet(timesLevelEarned, "Times Level Earned");
        levelsEarnedDataSet.setColor(getResources().getColor(R.color.white)); //bar colour
        //Display the bar of the data set
        BarData barLevelsEarned = new BarData(levelsEarnedDataSet);
        statsGraph.setData(barLevelsEarned);
        statsGraph.setDragEnabled(true);
        statsGraph.setTouchEnabled(false);
        Description description = new Description();
        description.setText(""); //No description, textview instead
        statsGraph.setDescription(description);

        //x-axis modifications
        XAxis xAxis = statsGraph.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisMinimum(0f);
        xAxis.setAxisMaximum((float)GREATEST_LEVEL);
        xAxis.setLabelCount(GREATEST_LEVEL - 0); //Number of labels on x-axis
        xAxis.setDrawAxisLine(true); //Draw base line for axis
        xAxis.setDrawGridLines(true);
        xAxis.setAxisLineColor(getResources().getColor(R.color.black));

        //y-axis modifications
        YAxis leftYAxis = statsGraph.getAxisLeft();
        leftYAxis.setAxisMinimum(0f);
        leftYAxis.setGranularity(1f); //Increase by 1 for y-axis
        leftYAxis.setAxisLineColor(getResources().getColor(R.color.black));
        leftYAxis.setDrawGridLines(true);
        leftYAxis.setGridColor(getResources().getColor(R.color.purple_700));
        YAxis rightYAxis = statsGraph.getAxisRight();
        rightYAxis.setEnabled(false); //Remove right y-axis
    }

    //When user clicks button, pop-up window is displayed showing the levels for each theme with numbers
    private void setUpPopUpLevelNameButton(){
        Button levelBtn = findViewById(R.id.levelsBtn);
        levelBtn.setOnClickListener(v -> {
            //Display the view from layout file
            inflater = LayoutInflater.from(AchievementStatistics.this);
            popUpView = inflater.inflate(R.layout.pop_up_level_names, null);
            popUpLevelNames = new PopupWindow(popUpView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
            popUpLevelNames.setAnimationStyle(android.R.style.Animation_Dialog);
            popUpLevelNames.showAtLocation(popUpView, Gravity.CENTER, 0, 0);
            popUpLevelNames.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            //Buttons that when clicked in the pop-up window, will show levels for the clicked theme
            setUpFruitLevelsButton();
            setUpFantasyLevelsButton();
            setUpStarWarsLevelsButton();
            //Button to close the pop-up window
            setUpClosePopUpButton();
        });
    }

    private void setUpFruitLevelsButton(){
        Button fruitsBtn = popUpView.findViewById(R.id.fruitsLevelsBtn);
        TextView fruitsLevelsWithNum = popUpView.findViewById(R.id.fruitsLevelsWithNums);
        fruitsLevelsWithNum.setVisibility(View.GONE);
        fruitsBtn.setOnClickListener(v -> {
            //Display levels when clicked
            if(fruitsLevelsWithNum.getVisibility() == View.GONE) {
                fruitsLevelsWithNum.setText(R.string.fruits_levels_with_num);
                fruitsLevelsWithNum.setVisibility(View.VISIBLE);
            } else if (fruitsLevelsWithNum.getVisibility() == View.VISIBLE){
                fruitsLevelsWithNum.setText("");
                fruitsLevelsWithNum.setVisibility(View.GONE);
            }
        });
    }

    private void setUpFantasyLevelsButton(){
        Button fantasyBtn = popUpView.findViewById(R.id.fantasyLevelsBtn);
        TextView fantasyLevelsWithNum = popUpView.findViewById(R.id.fantasyLevelsWithNum);
        fantasyLevelsWithNum.setVisibility(View.GONE);
        fantasyBtn.setOnClickListener(v -> {
            //Display levels when clicked
            if(fantasyLevelsWithNum.getVisibility() == View.GONE) {
                fantasyLevelsWithNum.setText(R.string.fantasy_levels_with_num);
                fantasyLevelsWithNum.setVisibility(View.VISIBLE);
            } else if (fantasyLevelsWithNum.getVisibility() == View.VISIBLE){
                fantasyLevelsWithNum.setText("");
                fantasyLevelsWithNum.setVisibility(View.GONE);
            }
        });
    }

    private void setUpStarWarsLevelsButton(){
        Button starwarsBtn = popUpView.findViewById(R.id.starwarsLevelsBtn);
        TextView starwarsLevelsWithNum = popUpView.findViewById(R.id.starwarsLevelsWithNum);
        starwarsLevelsWithNum.setVisibility(View.GONE);
        starwarsBtn.setOnClickListener(v -> {
            //Display levels when clicked
            if(starwarsLevelsWithNum.getVisibility() == View.GONE) {
                starwarsLevelsWithNum.setText(R.string.starwars_levels_with_num);
                starwarsLevelsWithNum.setVisibility(View.VISIBLE);
            } else if (starwarsLevelsWithNum.getVisibility() == View.VISIBLE){
                starwarsLevelsWithNum.setText("");
                starwarsLevelsWithNum.setVisibility(View.GONE);
            }
        });
    }

    private void setUpClosePopUpButton(){
        Button closeBtn = popUpView.findViewById(R.id.closeBtn);
        closeBtn.setOnClickListener(v -> {
            popUpLevelNames.dismiss();
        });
    }
}
