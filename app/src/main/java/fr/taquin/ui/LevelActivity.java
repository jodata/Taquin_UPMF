package fr.taquin.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.Spinner;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import fr.taquin.R;
import fr.taquin.Utils;

public class LevelActivity extends AppCompatActivity {

    Uri mImageURI;
    ImageButton btnImage;
    Button btnValider;
    Boolean modePortrait = true;
    static final int REQUEST_TAKE_PHOTO = 1;
    static final int SEEK_PICTURE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level);

        btnValider = (Button) findViewById(R.id.btnValider);
        btnValider.setEnabled(false);
        btnImage = (ImageButton) findViewById(R.id.mImageButton);
        final CheckBox checkPhoto = (CheckBox) findViewById(R.id.checkBoxPhoto);
        final Spinner spinnerLevel = (Spinner) findViewById(R.id.spinner);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        List<String> list = new ArrayList<>();
        list.add("Débutant (2*2)");
        list.add("Normal (3*3)");
        list.add("Expert (4*4)");
        list.add("Mission impossible");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>
                (this, android.R.layout.simple_spinner_item,list);

        dataAdapter.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);

        spinnerLevel.setAdapter(dataAdapter);
        btnValider.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                if(checkPhoto.isChecked()){
                    Utils.galleryAddPic(LevelActivity.this, mImageURI);
                }
                int level = spinnerLevel.getSelectedItemPosition()+2;
                Intent intent = new Intent(LevelActivity.this, TaquinActivity.class);
                intent.putExtra("level",level);
                intent.putExtra("imgURI", mImageURI.toString());
                intent.putExtra("portrait", modePortrait);
                startActivity(intent);
            }
        });

        btnImage.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                displayAlert();
            }
        });

    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = Utils.createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                mImageURI = Uri.fromFile(photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == SEEK_PICTURE && resultCode == RESULT_OK) {
           mImageURI = data.getData();
        }
        //On fait ce code pour toutes les startActivity
        if(resultCode == RESULT_OK) {
            Bitmap bitmap = Utils.setPicFromUri(btnImage.getWidth(), btnImage.getHeight(), LevelActivity.this, mImageURI);
            if (bitmap.getWidth() > bitmap.getHeight()) {
                modePortrait = false;
            } else {
                modePortrait = true;
            }
            btnImage.setImageBitmap(bitmap);
            btnValider.setEnabled(true);
        }
    }

    private void seekPicture(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SEEK_PICTURE);
    }

    public void displayAlert()
    {
        new AlertDialog.Builder(this).setMessage("Veuillez charger ou prendre une photo")
                .setTitle("Ma photo")
                .setCancelable(true)
                .setNeutralButton("Charger",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                seekPicture();
                            }
                        })
                .setPositiveButton("Prendre",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dispatchTakePictureIntent();
                            }
                        })
                .show();
    }
}
