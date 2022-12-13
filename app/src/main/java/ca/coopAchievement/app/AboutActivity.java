package ca.coopAchievement.app;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/*
* About activity class
* provides a clickable buttons that give info about the course, app, developers, and sources
 */
public class AboutActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        setInfoBtn();
        setDescriptionBtn();
        setAchievementBtn();
        setSourcesBtn();
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public static Intent makeIntent(Context context){
        return new Intent(context, AboutActivity.class);
    }

    private void setInfoBtn() {
        Button infoBtn = findViewById(R.id.info);
        TextView infoContent = findViewById(R.id.info_content);
        infoContent.setVisibility(View.GONE);
        infoBtn.setOnClickListener(v -> {

            if(infoContent.getVisibility() == View.GONE) {
                infoContent.setText(R.string.info_content);
                infoContent.setMovementMethod(LinkMovementMethod.getInstance());
                infoContent.setVisibility(View.VISIBLE);
            }else if (infoContent.getVisibility() == View.VISIBLE){
                infoContent.setText("");
                infoContent.setVisibility(View.GONE);
            }
        });
    }

    private void setDescriptionBtn(){
        Button descriptionBtn = findViewById(R.id.description);
        TextView descriptionContent = findViewById(R.id.description_content);
        descriptionContent.setVisibility(View.GONE);
        descriptionBtn.setOnClickListener(v -> {
            if(descriptionContent.getVisibility() == View.GONE) {
                descriptionContent.setText(R.string.description_content);
                descriptionContent.setVisibility(View.VISIBLE);
            } else if (descriptionContent.getVisibility() == View.VISIBLE){
                descriptionContent.setText("");
                descriptionContent.setVisibility(View.GONE);
            }
        });
    }

    private void setSourcesBtn(){
        Button sources = findViewById(R.id.sourcesBtn);
        TextView sourcesContent = findViewById(R.id.sources_content);
        sourcesContent.setVisibility(View.GONE);
        sources.setOnClickListener(v -> {
            if(sourcesContent.getVisibility() == View.GONE) {
                sourcesContent.setText(R.string.sources_content);
                sourcesContent.setVisibility(View.VISIBLE);
                sourcesContent.setMovementMethod(LinkMovementMethod.getInstance());
            } else if (sourcesContent.getVisibility() == View.VISIBLE){
                sourcesContent.setText("");
                sourcesContent.setVisibility(View.GONE);
            }
        });
    }

    private void setAchievementBtn(){
        Button achieveBtn = findViewById(R.id.achieve);
        TextView achieveContent = findViewById(R.id.achieve_content);
        achieveContent.setVisibility(View.GONE);
        achieveBtn.setOnClickListener(v -> {
            if (achieveContent.getVisibility() == View.GONE) {
                achieveContent.setText(R.string.achievement_themes_plus_levels_content);
                achieveContent.setVisibility(View.VISIBLE);
            } else if (achieveContent.getVisibility() == View.VISIBLE){
                achieveContent.setText("");
                achieveContent.setVisibility(View.GONE);
            }
        });
    }

}