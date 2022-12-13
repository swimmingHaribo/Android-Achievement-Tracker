package ca.coopAchievement.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import ca.coopAchievement.app.model.Achievements;
import ca.coopAchievement.app.model.ConfigurationsManager;
import ca.coopAchievement.app.model.Game;
import ca.coopAchievement.app.model.Util;

/*
* add new game activity class adds new game to the list of games in configuration
* checks user input and gives warning in case input is bot valid
 */
public class AddNewGame extends AppCompatActivity {

    private final String FRUITS = "Fruits";
    private final String FANTASY = "Fantasy";
    private final String STAR_WARS = "Star Wars";
    private int numOfPlayers; // int user input
    private int combinedScores = 0;
    private String dateGamePlayed; // date time
    private String numOfPlayersAsStr = "";  // String user input
    private EditText numOfPlayerFromUser;
    private TextView playerMsg; // alert message
    private boolean isPlayerValid; // check if user input is valid
    private boolean isScoresValid; // check if user input is valid
    private ConfigurationsManager manager = ConfigurationsManager.getInstance();
    private int selectedConfigPosition; // user selected game config index
    public int selectedTheme;
    private double adjustedMax;
    private double adjustedMin;
    private Achievements addNewGameAchievements;
    private boolean isCalculatingRangeForLevels;
    private final int MAX_PLAYERS = 1000;
    private int indexOfPlayer = 0; // textview player index
    private int indexOfScore = 0; // edittext score index
    private List<Integer> scoreList;
    private int indexOfGame = -1; // selected game index in game history
    private int currentConfigPosition = 0;
    private String gameTheme;
    private boolean isEditing;
    private int temp;
    private Stack<EditText> edList = new Stack<>();
    private ArrayList<String> edList_temp = new ArrayList<>();
    private int indexOfOriginalAchievementLevel;
    private int indexOfEditedAchievementLevel;
    private Button viewImageBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        manager.changeTheme(this);
        super.onCreate(savedInstanceState);
        addNewGameAchievements = new Achievements(getAchievementTheme(this));
        setContentView(R.layout.activity_add_new_game);
        chooseGame();
        storeSelectedGame();
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        Bundle bundle = getIntent().getExtras(); // from game history
        Spinner dropdown = findViewById(R.id.gameName);
        numOfPlayerFromUser = findViewById(R.id.num_players_input);
        TextView tv_numOfPlayer = findViewById(R.id.num_players);
        viewImageBtn = findViewById(R.id.view_image_btn);

        if(bundle != null){
            // go to edit game screen
            isEditing = true;
            getSupportActionBar().setTitle("Edit Game");
            edList.clear();
            edList_temp.clear();
            indexOfGame = bundle.getInt("selected game"); // get selected game position from game history
            dropdown.setVisibility(View.GONE);
            tv_numOfPlayer.setText("Number of Player:");
            setVariablesFromExistingGame(indexOfGame);
            resetDifficultyRadioButtons(indexOfGame);
            saveInputForEditGame(manager.getIndexOfCurrentConfiguration());
            viewImageBtn.setVisibility(View.VISIBLE);
            setUpViewImageBtn();
        } else {
            // go to add new game screen
            isEditing = false;
            getSupportActionBar().setTitle("Add New Game");
            dropdown.setVisibility(View.VISIBLE);
            numOfPlayerFromUser.setText("2");
            tv_numOfPlayer.setText(R.string.num_player);
            createDifficultyRadioButtons();
            viewImageBtn.setVisibility(View.GONE);
        }
        storeSelectedAchievementTheme();
    }

    public static Intent makeIntent(Context context){
        return new Intent(context, AddNewGame.class);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // On new game page, going back goes to view configuration
                if (!isEditing) {
                    this.finish();
                } else { //On edit game page, going back goes to game history
                    Intent refresh = new Intent(AddNewGame.this, GameHistory.class);
                    startActivity(refresh);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void chooseGame() {
        // get selected game config index
        int name = manager.getIndexOfCurrentConfiguration();
        // drop down menu for games
        Spinner dropdown = findViewById(R.id.gameName);

        // list of items
        ArrayList<String> items = new ArrayList<>();
        int count = 0;
        int defaultGameIndex = 0;
        while(count < manager.configListSize()){
            String strResult = manager.getItemAtIndex(count).getGameNameFromConfig();
            items.add(strResult);
            if(count == name){
                defaultGameIndex = count;
            }
            count++;
        }
        // create an adapter to describe how the items are displayed
        // basic variant
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        // set the spinners adapter to the dropdown menu
        dropdown.setAdapter(adapter);
        dropdown.setSelection(defaultGameIndex); // set default game in drop down menu
    }

    private void storeSelectedGame(){
        Spinner dropdown = findViewById(R.id.gameName);
        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                selectedConfigPosition = dropdown.getSelectedItemPosition();

                // set text again when the user changes selection
                numOfPlayerFromUser = findViewById(R.id.num_players_input);
                playerMsg = findViewById(R.id.player_msg);
                numOfPlayerFromUser.setText("");
                playerMsg.setText("");
                removeViewsInLinearLayout();
                indexOfPlayer = 0;
                indexOfScore = 0;
                combinedScores = 0;
                edList.clear();
                // call function according to current selection
                checkInput(selectedConfigPosition);
                setSetBtn();
                createFields(2);
                saveInput(selectedConfigPosition);
            }
            public void onNothingSelected(AdapterView<?> arg0) {}
        });
    }

    private void storeSelectedAchievementTheme(){
        Spinner dropdown = findViewById(R.id.dropdownTheme);
        String[] themesArray = getResources().getStringArray(R.array.achievementThemes);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(AddNewGame.this, R.array.achievementThemes, android.R.layout.simple_spinner_dropdown_item);
        dropdown.setAdapter(adapter);
        //Display Set a Theme prompt
        dropdown.setPrompt(getResources().getString(R.string.select_theme_prompt));

        //Set the dropdown item to start from the last selected item
        if (!isEditing) { //If new game is being added, achievement theme is last selected theme
            for (int i = 0; i < themesArray.length; i++) {
                if (themesArray[i].equals(getAchievementTheme(AddNewGame.this))) {
                    dropdown.setSelection(i);
                }
            }
        }
        else { //If game is being edited, achievement theme is saved theme in game being edited
            for (int i = 0; i < themesArray.length; i++) {
                if (themesArray[i].equals(gameTheme)) {
                    dropdown.setSelection(i);
                }
            }
        }
        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int [] background_image = new int[]{R.drawable.fruitsbackground, R.drawable.fantasybackground, R.drawable.starwarsbackground};
                //When user selects a new theme, recreate activity to display theme levels and achievements
                selectedTheme = dropdown.getSelectedItemPosition();
                String[] themesArray = getResources().getStringArray(R.array.achievementThemes);
                for (int i = 0; i < themesArray.length; i++){
                    if (i == selectedTheme && !addNewGameAchievements.getAchievementTheme().equals(themesArray[i])) {
                        final String achievementTheme = themesArray[i];
                        saveAchievementTheme(achievementTheme);
                        addNewGameAchievements.setAchievementTheme(achievementTheme);
                        getWindow().setBackgroundDrawableResource(background_image[i]);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //Do nothing
            }
        });
    }

    private void displayMaxPlayerMsg(){
        AlertDialog alertDialog = new AlertDialog.Builder(AddNewGame.this).create();
        alertDialog.setTitle(getString(R.string.too_many_players));
        alertDialog.setMessage(getString(R.string.Sorry_too_many_players));
        alertDialog.setButton(getString(R.string.OK), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //Stay on AddNewGame activity
            }
        });
        alertDialog.show();
        //set num of player to the minimum default 1
        numOfPlayerFromUser = findViewById(R.id.num_players_input);
        numOfPlayerFromUser.setText("2");
    }


    // check if user input player is valid
    private void checkInput(int selectedGameInt){
        numOfPlayerFromUser = findViewById(R.id.num_players_input);
        playerMsg = findViewById(R.id.player_msg); // alert message
        playerMsg.setTextColor(getResources().getColor(R.color.purple_700));

        numOfPlayerFromUser.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                numOfPlayersAsStr = numOfPlayerFromUser.getText().toString();
                try{
                    numOfPlayers = Integer.parseInt(numOfPlayersAsStr);
                    if (numOfPlayers < 1) {
                        isPlayerValid = false;
                        playerMsg.setText(R.string.PlayerMinimum1);
                    }else if (numOfPlayers >= MAX_PLAYERS) {
                        isPlayerValid = false;
                        displayMaxPlayerMsg();
                    }else{
                        isPlayerValid = true;
                        playerMsg.setText("");
                        //Calculate the expected poor and expected great score for the total number of players
                        adjustedMax = addNewGameAchievements.calculateMinMaxScore(manager.getItemAtIndex(selectedGameInt).getMaxBestScoreFromConfig(), numOfPlayers);
                        adjustedMin = addNewGameAchievements.calculateMinMaxScore(manager.getItemAtIndex(selectedGameInt).getMinPoorScoreFromConfig(), numOfPlayers);

                        //Adjust the poor and great score depending on difficulty selected
                        switch(addNewGameAchievements.getDifficultyLevel()){
                            case 0:
                                adjustedMax *= 0.75;
                                adjustedMin *= 0.75;
                                break;
                            case 1:
                                break;
                            case 2:
                                adjustedMin *= 1.25;
                                adjustedMax *= 1.25;
                                break;
                        }
                        //Range will be calculated for each achievement level if great score - poor score is > 8
                        if (Math.abs(adjustedMax - adjustedMin) > 8) {
                            isCalculatingRangeForLevels = true;
                        } else { //One score will be calculated for each achievement level otherwise
                            isCalculatingRangeForLevels = false;
                        }
                    }
                }catch (NumberFormatException ex){
                    isPlayerValid = false;
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

    }


    // create textview // create edittext and return edittext
    private EditText createRightFields(){
        // add textview
        LinearLayout ll_test = findViewById(R.id.ll_test);
        LinearLayout ll_both = new LinearLayout(this);
        ll_both.setOrientation(LinearLayout.HORIZONTAL);
        ll_both.setWeightSum(2);

        TextView tv = new TextView(this);
        indexOfPlayer++;
        tv.setText("Player " + indexOfPlayer + ":");
        tv.setTextColor(getColor(R.color.black));
        ll_both.addView(tv);


        // add edittext
        EditText et = new EditText(this);
        et.setText("");
        et.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
        et.setTextColor(getColor(R.color.black));
        et.setId(indexOfScore);
        LinearLayout.LayoutParams lp_et = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp_et.setMargins(10, 0, 5, 0);
        et.setLayoutParams(lp_et);
        ll_both.addView(et);

        LinearLayout.LayoutParams lp_both = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp_both.setMargins(10, 2, 10, 10);
        ll_both.setLayoutParams(lp_both);
        ll_test.addView(ll_both);
        indexOfScore++;
        return et;
    }

    private void removeViewsInLinearLayout(){
        LinearLayout ll_test = findViewById(R.id.ll_test);
        ll_test.removeAllViewsInLayout();
    }

    private void setSetBtn(){
        Button setBtn = findViewById(R.id.set_btn);
        setBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if(isPlayerValid && !isEditing){

                    Iterator<EditText> iterator = edList.iterator();

                    if (!edList_temp.isEmpty() && edList_temp.size() < edList.size()){
                        int count = 0;
                        int temp_size = edList_temp.size();
                        while (iterator.hasNext()) {
                            EditText editText = (EditText) iterator.next();
                            if(count >= temp_size) {
                                edList_temp.add(editText.getText().toString());
                            }
                            count++;
                        }
                    }

                    Iterator<EditText> iterator2 = edList.iterator();

                    if(!edList_temp.isEmpty()){
                        int count = 0;
                        while (iterator2.hasNext()) {
                            EditText editText = (EditText) iterator2.next();
                            edList_temp.set(count,editText.getText().toString());
                            count++;
                        }
                    }

                    Iterator<EditText> iterator3 = edList.iterator();

                    if(edList_temp.isEmpty()) {
                        while (iterator3.hasNext()) {
                            EditText editText = (EditText) iterator3.next();
                            edList_temp.add(editText.getText().toString());
                        }
                    }


                    isScoresValid = false;
                    createFieldsAgain(numOfPlayers);
                }
                    else if(isPlayerValid) {
                        createFieldsAgainForEditGame(numOfPlayers);
                    }
            }
        });
    }

    // check if user input scores is valid
    public boolean validateFields(){
        isScoresValid = true;
        for(EditText editText:edList){
            if(!Util.isValidScore(editText.getText().toString())){
                editText.setError("Invalid input!");
                isScoresValid = false;
            }
        }
        return isScoresValid;
    }

    private void createFields(int numOfPlayers){
        numOfPlayerFromUser = findViewById(R.id.num_players_input);
        numOfPlayerFromUser.setText(""+ numOfPlayers);
        for (int i = 0; i < numOfPlayers; i++) {
            edList.push(createRightFields());
        }

        temp = numOfPlayers;

    }

    private void createFieldsAgain(int numOfPlayers){

        LinearLayout ll_test = findViewById(R.id.ll_test);
        if(temp < numOfPlayers) {
            int count = 0;
            Iterator<EditText> iterator = edList.iterator();
            while (iterator.hasNext()) {
                EditText editText = (EditText) iterator.next();
                editText.setText("" + edList_temp.get(count));
                count ++;
            }

            int count2 = edList.size();

            for (int i = temp; i < numOfPlayers; i++) {
                edList.push(createRightFields());
            }


            int count3 = 0;
            Iterator<EditText> iterator2 = edList.iterator();
            while (iterator2.hasNext()) {
                EditText editText = (EditText) iterator2.next();
                if(count2 <= count3) {
                    if(edList_temp.size() >= count3 + 1){
                        editText.setText("" + edList_temp.get(count3));
                    }
                }
                count3++;
            }
            temp = numOfPlayers;

        }else if(temp >= numOfPlayers){

            if(temp > numOfPlayers) {
                for (int i = temp; i > numOfPlayers; i--) {
                    edList.pop();
                    ll_test.removeViewAt(ll_test.getChildCount() - 1);
                }
            }

            int count = 0;
            Iterator<EditText> iterator = edList.iterator();
            while (iterator.hasNext()) {
                EditText editText = (EditText) iterator.next();
                editText.setText("" + edList_temp.get(count));
                count ++;
            }

            indexOfPlayer = numOfPlayers;
            indexOfScore = numOfPlayers;
            temp = numOfPlayers;

        }
    }

    private void createFieldsAgainForEditGame(int numOfPlayers){
        LinearLayout ll_test = findViewById(R.id.ll_test);
        if(temp < numOfPlayers) {

            int count = edList.size();

            for (int i = temp; i < numOfPlayers; i++) {
                edList.push(createRightFields());
            }

            int count2 = 0;
            Iterator<EditText> iterator = edList.iterator();
            while (iterator.hasNext()) {
                EditText editText = (EditText) iterator.next();
                if(count <= count2 ) {
                    editText.setText("" + 0);
                }
                count2++;
            }

            temp = numOfPlayers;
        }else if(temp > numOfPlayers){

            for (int i = temp; i > numOfPlayers; i--) {
                edList.pop();
                ll_test.removeViewAt(ll_test.getChildCount() - 1);
            }

            indexOfPlayer = numOfPlayers;
            indexOfScore = numOfPlayers;
            temp = numOfPlayers;
        }else temp = numOfPlayers;

    }


    // store score list and update combined score
    private void storeScores(){
        scoreList = new ArrayList<>();
        int score;
        for (EditText editText : edList) {
            score = Integer.parseInt(editText.getText().toString());
            scoreList.add(score);
            combinedScores += score;
        }
    }

    // get date time in add new game screen
    public String saveDatePlayed(){
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern(getString(R.string.date_format));
        LocalDateTime currentDate = LocalDateTime.now();
        dateGamePlayed = currentDate.format(dateFormat);
        return dateGamePlayed;
    }

    private void setVariablesFromExistingGame(int indexOfGame){
        numOfPlayerFromUser = findViewById(R.id.num_players_input);
        currentConfigPosition = manager.getIndexOfCurrentConfiguration();
        //Get the theme from the game that is being edited
        gameTheme = manager.getItemAtIndex(currentConfigPosition).getTheme(indexOfGame);
        if (gameTheme.equals(FRUITS)){
            selectedTheme = 0;
        }
        if (gameTheme.equals(FANTASY)){
            selectedTheme = 1;
        }
        if (gameTheme.equals(STAR_WARS)){
            selectedTheme = 2;
        }
        numOfPlayerFromUser.setText("" + manager.getItemAtIndex(currentConfigPosition).getGame(indexOfGame).getPlayers());
        numOfPlayers = manager.getItemAtIndex(currentConfigPosition).getGame(indexOfGame).getPlayers();
        indexOfOriginalAchievementLevel = manager.getItemAtIndex(currentConfigPosition).getGame(indexOfGame).getIndexLevelAchieved();

        indexOfPlayer = 0;
        indexOfScore = 0;
        checkInput(currentConfigPosition);
        createFields(numOfPlayers);
        setSetBtn();

        for (int i = 0; i < edList.size(); i++) {
            edList.get(i).setText("" + manager.getItemAtIndex(currentConfigPosition).getListOfValues(indexOfGame).get(i));
        }
    }

    // reset Achievement level in edit game screen
    private void resetAchievementLevel(int selectedGameInt, int numOfPlayers){
        adjustedMax = addNewGameAchievements.calculateMinMaxScore(manager.getItemAtIndex(selectedGameInt).getMaxBestScoreFromConfig(), numOfPlayers);
        adjustedMin = addNewGameAchievements.calculateMinMaxScore(manager.getItemAtIndex(selectedGameInt).getMinPoorScoreFromConfig(), numOfPlayers);
        switch(addNewGameAchievements.getDifficultyLevel()){
            case 0:
                adjustedMin *= 0.75;
                adjustedMax *= 0.75;
                break;
            case 1:
                break;
            case 2:
                adjustedMin *= 1.25;
                adjustedMax *= 1.25;
                break;
            default:
                break;
        }
        if (Math.abs(adjustedMax - adjustedMin) > 8) {
            isCalculatingRangeForLevels = true;
        } else {
            isCalculatingRangeForLevels = false;
        }
    }

    // save input to the list in edit game screen
    private void saveInputForEditGame(int currentConfigPosition){
        Button save = findViewById(R.id.save_btn);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateFields();
                if (isScoresValid) {
                    combinedScores = 0;

                    // set combined score
                    storeScores();
                    // reset numOfPlayers
                    manager.getItemAtIndex(currentConfigPosition).getGame(indexOfGame).setPlayers(edList.size());
                    // reset scoreList
                    manager.getItemAtIndex(currentConfigPosition).getGame(indexOfGame).setListOfValues(scoreList);
                    // reset combined score
                    manager.getItemAtIndex(currentConfigPosition).getGame(indexOfGame).setScores(combinedScores);
                    // reset achievement
                    resetAchievementLevel(currentConfigPosition, manager.getItemAtIndex(currentConfigPosition).getGame(indexOfGame).getPlayers());
                    manager.getItemAtIndex(manager.getIndexOfCurrentConfiguration()).getGame(indexOfGame).setDifficulty(addNewGameAchievements.getDifficultyLevel());
                    manager.getItemAtIndex(currentConfigPosition).getGame(indexOfGame).setLevelAchieved(manager.getItemAtIndex(currentConfigPosition).getGame(indexOfGame).setAchievementForEditGame(edList.size(), combinedScores, manager.getItemAtIndex(currentConfigPosition), isCalculatingRangeForLevels, addNewGameAchievements.getAchievementTheme(), addNewGameAchievements.getDifficultyLevel()));
                    // reset theme
                    manager.getItemAtIndex(currentConfigPosition).getGame(indexOfGame).setTheme(addNewGameAchievements.getAchievementTheme());
                    // Add level earned to achievement statistics if new achievement level is different than original level
                    indexOfEditedAchievementLevel = manager.getItemAtIndex(currentConfigPosition).getGame(indexOfGame).getIndexGameLevelAchieved();
                    if (indexOfOriginalAchievementLevel != indexOfEditedAchievementLevel) {
                        manager.getItemAtIndex(currentConfigPosition).removeAchievementsEarnedStats(indexOfOriginalAchievementLevel); //Remove original achievement level from statistics
                        manager.getItemAtIndex(currentConfigPosition).addAchievementsEarnedStats(indexOfEditedAchievementLevel); //Add new achievement level from statistics
                    }
                    // pass achievement level to appropriate theme layout to be displayed
                    //display alert dialog for new image
                    displayNewImageQuestionMsg();
                } else {
                    Toast.makeText(AddNewGame.this, R.string.emptyOrInvalid, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //Button to view image for game played
    private void setUpViewImageBtn(){
        Button viewImageBtn = findViewById(R.id.view_image_btn);
        viewImageBtn.setOnClickListener(v -> {
            //make an intent for ViewImage activity
            Intent intent = ViewImage.makeIntent(AddNewGame.this);
            int gamePosition= manager.getItemAtIndex(manager.getIndexOfCurrentConfiguration()).getSizeOfListOfGamePlays() - 1;
            intent.putExtra("Selected GamePlay position", gamePosition);
            startActivity(intent);
        });
    }

    private void displayNewImageQuestionMsg() {
        LayoutInflater inflater = LayoutInflater.from(AddNewGame.this);
        View newImageQuestion = inflater.inflate(R.layout.new_image_alert_dialog, null);

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(AddNewGame.this)
                .setView(newImageQuestion)
                .setMessage("")
                .setTitle(R.string.new_photo_alert_dialog);
        AlertDialog dialog = alertDialog.create();
        dialog.show();

        Button btnYes = newImageQuestion.findViewById(R.id.yesBtn);
        Button btnNo = newImageQuestion.findViewById(R.id.noBtn);
        btnYes.setOnClickListener(view -> {
            //make an intent for ViewImage activity
            Intent intent = ViewImage.makeIntent(AddNewGame.this);
            int gamePosition;
            //initialize position of the game
            if(isEditing){gamePosition = indexOfGame;}
            else {gamePosition = manager.getItemAtIndex(manager.getIndexOfCurrentConfiguration()).getSizeOfListOfGamePlays() - 1;}

            intent.putExtra("Selected GamePlay position",gamePosition);
            startActivity(intent);
        });
        btnNo.setOnClickListener(view -> {
            goToAchievementCelebrationPage();
        });
    }

    // save input to the list in add new game screen
    private void saveInput(int selectedGameInt) {
        Button save = findViewById(R.id.save_btn);
        save.setOnClickListener(v -> {
            validateFields();
            if (isPlayerValid && isScoresValid) {
                storeScores();
                Game gamePlayed = new Game(scoreList.size(), combinedScores, scoreList, manager.getItemAtIndex(selectedGameInt), saveDatePlayed(),
                        isCalculatingRangeForLevels, addNewGameAchievements.getAchievementTheme(), addNewGameAchievements.getDifficultyLevel());
                manager.getItemAtIndex(selectedGameInt).add(gamePlayed);
                // pass achievement level to appropriate theme layout in add new game screen
                manager.setIndexOfCurrentConfiguration(selectedConfigPosition);
                // Add level earned to achievement statistics
                manager.getItemAtIndex(selectedGameInt).addAchievementsEarnedStats(gamePlayed.getIndexLevelAchieved());
                //display alert dialog for new image
                displayNewImageQuestionMsg();
            } else {
                Toast.makeText(AddNewGame.this, R.string.emptyOrInvalid, Toast.LENGTH_SHORT).show();
            }
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

    // create radio buttons for the activity that will change the levels of achievement
    // according to selected difficulty level
    private void createDifficultyRadioButtons() {
        RadioGroup difficultiesGroup = findViewById(R.id.radioGroupDifficulty);
        String[] difficultyLevels = getResources().getStringArray(R.array.difficultyLevels);
        LinearLayout.LayoutParams radio_lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        radio_lp.setMargins(5, 20, 5, 5);

        for (int i = 0; i < difficultyLevels.length; i++){
            final int selectedDifficulty = i;
            final String difficulty = difficultyLevels[i];

            RadioButton btn = new RadioButton(this);
            btn.setText(difficulty);
            btn.setOnClickListener(v -> {
                    //Set difficulty for this game
                    addNewGameAchievements.setDifficultyLevel(selectedDifficulty);
            });
            btn.setLayoutParams(radio_lp);
            difficultiesGroup.addView(btn);
            //Set Normal as default difficulty
            if (difficulty.equals("Normal")){
                btn.setChecked(true);
            }
        }
    }

    // create radio buttons for edit game
    private void resetDifficultyRadioButtons(int indexOfGame){
        RadioGroup difficultiesGroup = findViewById(R.id.radioGroupDifficulty);
        String[] difficultyLevels = getResources().getStringArray(R.array.difficultyLevels);
        LinearLayout.LayoutParams radio_lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        radio_lp.setMargins(5, 20, 5, 5);

        for (int i = 0; i < difficultyLevels.length; i++){
            final int selectedDifficulty = i;
            final String difficulty = difficultyLevels[i];

            RadioButton btn = new RadioButton(this);
            btn.setText(difficulty);
            btn.setOnClickListener(v -> {
                //Set difficulty for this game
                addNewGameAchievements.setDifficultyLevel(selectedDifficulty);
            });
            btn.setLayoutParams(radio_lp);
            difficultiesGroup.addView(btn);
            //Set user selection as default difficulty
            if (i == manager.getItemAtIndex(manager.getIndexOfCurrentConfiguration()).getGame(indexOfGame).getDifficulty()){
                btn.setChecked(true);
            }
        }
    }

    private void goToAchievementCelebrationPage(){
        Intent intent = new Intent(AddNewGame.this, AchievementCelebration.class);
        startActivity(intent);
    }
}