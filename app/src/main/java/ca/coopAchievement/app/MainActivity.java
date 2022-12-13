package ca.coopAchievement.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import ca.coopAchievement.app.model.Configuration;
import ca.coopAchievement.app.model.ConfigurationsManager;
import ca.coopAchievement.app.model.SaveUsingGson;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

/*
* main activity that shows the list of all the configs
* list allows to click on items and go to view config activity
* activity allows to add a new config
* and main activity provides a help button to do to help activity
*/

public class MainActivity extends AppCompatActivity {

    private SaveUsingGson toSaveUsingGsonAndSP = new SaveUsingGson();
    private ConfigurationsManager manager = ConfigurationsManager.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //retrieve config manager
        toSaveUsingGsonAndSP.retrieveFromSharedPrefs(this);

        setUpAboutButton();
        UpdateUI();

        registerClickCallBack();
        setUpAddConfigurationButton();

        //to save config manager
        toSaveUsingGsonAndSP.saveToSharedRefs(this);
    }


    @Override
    protected void onResume() {
        super.onResume();
        UpdateUI();
        //to save config manager
        toSaveUsingGsonAndSP.saveToSharedRefs(this);
    }

    public static Intent makeIntent(Context context){
        return new Intent(context, MainActivity.class);
    }

    private void setUpAboutButton() {
        Button helpBtn = (Button)findViewById(R.id.aboutBtn);
        helpBtn.setOnClickListener(v -> {
            Intent intent = AboutActivity.makeIntent(MainActivity.this);
            startActivity(intent);
        });
    }

    private void UpdateUI() {
        // image made from miro
        // https://miro.com
        ImageView image = findViewById(R.id.image_main);
        if(manager.isEmpty()){
            image.setVisibility(View.VISIBLE);
        }
        else {
            //get rid of image
            image.setVisibility(View.INVISIBLE);
            //populate list view with games from manager
            populateListView(manager);
        }
    }

    private void populateListView(ConfigurationsManager manager) {
        ArrayAdapter<Configuration> adapter1 = new MyListAdapter();
        ListView list = findViewById(R.id.configList);
        list.setAdapter(adapter1);
    }

    private class MyListAdapter extends ArrayAdapter<Configuration> {
        public MyListAdapter() {
            super(MainActivity.this, R.layout.config_item_list_and_image_view, manager.getListOfConfigurations());
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            //makes a view to work with
            View itemView = convertView;
            if(itemView == null){
                itemView = getLayoutInflater().inflate(R.layout.config_item_list_and_image_view, parent, false);
            }
            //file the imageView
            Configuration currentConfig = manager.getItemAtIndex(position);
            ImageView imageIcon = itemView.findViewById(R.id.item_iconForConfigList);
            imageIcon.setImageBitmap(currentConfig.imageStringToBitMap());
            //file the textView
            TextView makeText = itemView.findViewById(R.id.item_textViewForConfigList);
            String strConfigName = "\n" + currentConfig.getGameNameFromConfig() + "\n";
            makeText.setText(strConfigName);

            return itemView;
            }
    }

    private void registerClickCallBack() {
        ListView list = findViewById(R.id.configList);
        list.setOnItemClickListener((parent, viewClicked, position, id) -> {

            manager.setIndexOfCurrentConfiguration(position);
            //make an intent for view configuration activity
            Intent intent = ViewConfiguration.makeIntent(MainActivity.this);
            startActivity(intent);
        });
    }


    private void setUpAddConfigurationButton() {
        FloatingActionButton addConfigBtn = findViewById(R.id.addBtn);
        addConfigBtn.setOnClickListener(v ->{
            Intent intent = AddEditConfiguration.makeIntent(MainActivity.this);
            startActivity(intent);
        });
    }

}