package ca.coopAchievement.app;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.util.ArrayList;
import ca.coopAchievement.app.model.ConfigurationsManager;
import ca.coopAchievement.app.model.SaveUsingGson;

/*
* activity class GameHistory
* populates and shows a none clickable list view of all the games played
 */

public class GameHistory extends AppCompatActivity {

    private ConfigurationsManager manager = ConfigurationsManager.getInstance();
    private SaveUsingGson toSaveUsingGsonAndSP = new SaveUsingGson();
    private int indexOfGame = 0;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_history);
        // get selected game name from ViewConfiguration
        indexOfGame = manager.getIndexOfCurrentConfiguration();
        populateListView(manager, indexOfGame);
        registerClickCallBack();
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    public static Intent makeIntent(Context context){
        return new Intent(context, GameHistory.class);
    }

    public boolean onOptionsItemSelected(MenuItem item){
        Intent myIntent = new Intent(getApplicationContext(), ViewConfiguration.class);
        startActivityForResult(myIntent, 0);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        toSaveUsingGsonAndSP.saveToSharedRefs(this);
    }

    //populates a list view with all the games played before in the given config
    private void populateListView(ConfigurationsManager manager, int indexOfGame) {
        // creating list of games items
        ArrayList<String> items = new ArrayList<String>();
        //array of games
        int count = 0;
        while(count < manager.getItemAtIndex(indexOfGame).getSizeOfListOfGamePlays()){
            String strResult = "\n" + manager.getItemAtIndex(indexOfGame).get(count) + "\n";
            items.add(strResult);
            count++;
        }
        //adapter
        adapter = new ArrayAdapter<String>(this, R.layout.game_items, items);
        ListView list = findViewById(R.id.HistoryList);
        list.setAdapter(adapter);
    }

    private void registerClickCallBack() {
        ListView list = findViewById(R.id.HistoryList);
        list.setOnItemClickListener((parent, viewClicked, position, id) -> {
            Intent intent = AddNewGame.makeIntent(GameHistory.this);
            intent.putExtra("selected game", position);
            startActivity(intent);
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent refresh = new Intent(GameHistory.this, ViewConfiguration.class); // back to View Config screen
        startActivity(refresh);
    }
}