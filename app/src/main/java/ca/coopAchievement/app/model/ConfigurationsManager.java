package ca.coopAchievement.app.model;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import ca.coopAchievement.app.AddNewGame;
import ca.coopAchievement.app.R;

/*
 * Singleton class ConfigurationsManager
 * Keeps an array of all the game configurations and index of selected config
 * allows add and remove items in the array as well as items access to get and set them
 */
public class ConfigurationsManager {

    private final String FRUITS = "Fruits";
    private final String FANTASY = "Fantasy";
    private final String STAR_WARS = "Star Wars";

    private static ConfigurationsManager instance;
    private int indexOfConfiguration;
    private List<Configuration> ListOfConfigurations = new ArrayList<>();

    private ConfigurationsManager() {
        //private to prevent anyone else from instantiating
    }

    public static ConfigurationsManager getInstance(){
        if(instance == null){
            instance = new ConfigurationsManager();
        }
        return instance;
    }

    public void add(Configuration newConfig) {ListOfConfigurations.add(newConfig);}
    public void remove(int i) {ListOfConfigurations.remove(i);}
    public boolean isEmpty() {return this.ListOfConfigurations.isEmpty();}
    public int configListSize() {return ListOfConfigurations.size();}

    //setters
    public void setListOfConfigurations(List<Configuration> newList) {this.ListOfConfigurations = newList;}
    public void setItemAtIndex(int currentConfigPosition, Configuration newConfig){ListOfConfigurations.set(currentConfigPosition, newConfig);}
    public void setIndexOfCurrentConfiguration(int indexOfConfiguration) {this.indexOfConfiguration = indexOfConfiguration;}
    //getters
    public List<Configuration> getListOfConfigurations() {return ListOfConfigurations;}
    public Configuration getItemAtIndex(int i){return ListOfConfigurations.get(i);}
    public int getIndexOfCurrentConfiguration() {return indexOfConfiguration;}

    public void changeTheme(Context context){
        //Change theme depending on click radio button for theme
        if (AddNewGame.getAchievementTheme(context).equals(FRUITS)) {
            context.setTheme(R.style.fruitsTheme);
        }if (AddNewGame.getAchievementTheme(context).equals(FANTASY)){
            context.setTheme(R.style.fantasyTheme);
        } if (AddNewGame.getAchievementTheme(context).equals(STAR_WARS)){
            context.setTheme(R.style.starWarsTheme);
        }
    }
}