package ca.coopAchievement.app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;

import ca.coopAchievement.app.model.ConfigurationsManager;

/*
* This class, allows to view image in a larger scale
* and make another picture (that overwrites previous or take an image from gallery)
*/

public class ViewImage extends AppCompatActivity {

    public static final int CAMERA_REQUEST_CODE = 102;
    public static final int CAMERA_PERMISSION_CODE = 101;
    public static final int WES_PERMISSION_CODE = 103;
    private ConfigurationsManager manager = ConfigurationsManager.getInstance();
    private int indexOfGamePlay = -1;
    private ImageView image;
    private Bitmap finalPhotoInBitMap;
    boolean isThereAPhoto = false;
    private Button rotateRightBtn;
    private Button rotateLeftBtn;
    private Button saveImageBtn;
    private int indexOfConfig;
    private int angle = 0;
    private Bundle b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);
        setTitle("Manage Image");

        image = findViewById(R.id.image_view_for_activity);
        rotateRightBtn = findViewById(R.id.rotateRightBtn);
        rotateLeftBtn = findViewById(R.id.rotateLeftBtn);
        saveImageBtn = findViewById(R.id.saveImagebtn);
        setRotationBtns();
        setSaveBtn();
        setUpCameraBtn();

        ActionBar bar = getSupportActionBar();
        bar.setDisplayHomeAsUpEnabled(true);

        UpdateImageUI();
    }

    private void UpdateImageUI() {

        b = getIntent().getExtras();
        indexOfConfig = manager.getIndexOfCurrentConfiguration();
        if(b != null){
            //activity is open from AddNewGame
            indexOfGamePlay = b.getInt("Selected GamePlay position");
            if(manager.getItemAtIndex(indexOfConfig).getGame(indexOfGamePlay).getImageStringForGamePlay() != null){
                isThereAPhoto = true;
                finalPhotoInBitMap = manager.getItemAtIndex(indexOfConfig).getGame(indexOfGamePlay).imageStringToBitMap();
                image.setImageBitmap(finalPhotoInBitMap);
            }
        }
        else{
            //activity is open from ViewConfiguration
            if(manager.getItemAtIndex(indexOfConfig).getImageStringForConfig() != null){
                isThereAPhoto = true;
                finalPhotoInBitMap = manager.getItemAtIndex(indexOfConfig).imageStringToBitMap();
                image.setImageBitmap(finalPhotoInBitMap);
            }
        }
    }

    private void setSaveBtn() {
        saveImageBtn.setOnClickListener(view -> {
            if (isThereAPhoto){
                //sets current bitmap of taken photo to a string variable in current config object
                image.buildDrawingCache();
                finalPhotoInBitMap = image.getDrawingCache();
                if(b != null){
                    //for AddNewGame
                    manager.getItemAtIndex(indexOfConfig).getGame(indexOfGamePlay).setImageStringForGamePlay(imageBitMapToString(finalPhotoInBitMap));
                } else {
                    //for Config
                    manager.getItemAtIndex(indexOfConfig).setImageStringForConfig(imageBitMapToString(finalPhotoInBitMap));
                }
                Toast.makeText(this, "Your photo was saved!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "You need a make a photo first", Toast.LENGTH_SHORT).show();
            }
        }
        );
    }

    private void setRotationBtns() {
        //rotate image view to the left 90 deg
        rotateLeftBtn.setOnClickListener(view -> {
            if (isThereAPhoto){
                Matrix matrix = new Matrix();
                matrix.postRotate(angle -= 90);
                Bitmap scaledBitmap = Bitmap.createScaledBitmap(finalPhotoInBitMap, finalPhotoInBitMap.getWidth(), finalPhotoInBitMap.getHeight(), true);
                Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
//                Bitmap rotatedBitmap = Bitmap.createBitmap(finalPhotoInBitMap, 0, 0, finalPhotoInBitMap.getWidth(), finalPhotoInBitMap.getHeight(), matrix, true);
                image.setImageBitmap(rotatedBitmap);

            } else {
                Toast.makeText(this, "You need a make a photo first", Toast.LENGTH_SHORT).show();
            }
        });
        //rotate image view to the right 90 deg
        rotateRightBtn.setOnClickListener(view -> {
            if (isThereAPhoto){
                Matrix matrix = new Matrix();
                matrix.postRotate(angle += 90);
                Bitmap scaledBitmap = Bitmap.createScaledBitmap(finalPhotoInBitMap, finalPhotoInBitMap.getWidth(), finalPhotoInBitMap.getHeight(), true);
                Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
                image.setImageBitmap(rotatedBitmap);
            } else {
                Toast.makeText(this, "You need a make a photo first", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //checks if the class was selected in the AddNewGame activity
                if (indexOfGamePlay != -1) {
                    //goes to celebration page
                    Intent intent = new Intent(this, AchievementCelebration.class);
                    intent.putExtra("selected game", indexOfGamePlay);
                    startActivity(intent);
                }
                // checks if the class was selected in the ViewConfig activity
                else {
                    this.finish();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CAMERA_PERMISSION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera();
                } else {
                    Toast.makeText(this, "Camera Permission is required to use camera", Toast.LENGTH_SHORT).show();
                    break;
                }
            case WES_PERMISSION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera();
                } else {
                    Toast.makeText(this, "Gallery Permission is required to use camera", Toast.LENGTH_SHORT).show();
                    break;
                }
            default:
                break;

        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Bundle bundle = data.getExtras();
            finalPhotoInBitMap = (Bitmap) bundle.get("data");
            image.setImageBitmap(finalPhotoInBitMap);
            isThereAPhoto = true;
            Toast.makeText(this, "photo made", Toast.LENGTH_SHORT).show();
        }
        else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void askCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, WES_PERMISSION_CODE);
        } else {
            openCamera();
        }
    }

    private void setUpCameraBtn() {
        Button cameraBtn = findViewById(R.id.cameraBtn);
        cameraBtn.setOnClickListener(view -> {
            //check for the camera permission
            askCameraPermission();
        });
    }

    // inspired/source: https://stackoverflow.com/questions/13562429/how-many-ways-to-convert-bitmap-to-string-and-vice-versa
    public String imageBitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b = baos.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    public static Intent makeIntent(Context context) {
        return new Intent(context, ViewImage.class);
    }

//class ends here
}