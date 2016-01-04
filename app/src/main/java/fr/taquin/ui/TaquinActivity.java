package fr.taquin.ui;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;

import fr.taquin.R;


public class TaquinActivity extends AppCompatActivity {

    private TaquinAdapter taquinAdapter;
    private boolean success = false;
    Uri mImageURI;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final GridView grille = (GridView) findViewById(R.id.gridview);
        final Button btnSuffle = (Button) findViewById(R.id.btnShuffle);
        final Button btnretour = (Button) findViewById(R.id.btnRetour);
        int level = 2;

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        Intent intent = getIntent();
        if (intent != null) {
            level = intent.getIntExtra("level", 0);
            mImageURI = Uri.parse(intent.getStringExtra("imgURI"));
            boolean modePortrait = intent.getBooleanExtra("portrait", true);
            if(modePortrait){
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
            else {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
            }
        }
        else{
            finish();
        }

        grille.setNumColumns(level);
        taquinAdapter = new TaquinAdapter(this, level, mImageURI, size.x, size.y-getActionBarHeight()-getStatusBarHeight()-100);
        grille.setAdapter(taquinAdapter);
        grille.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!success) {
                    success = taquinAdapter.change(position, false);
                    grille.invalidateViews();
                    if (success) {
                        // A faire un message de succes
                    }
                }
            }
        });

        btnSuffle.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                taquinAdapter.shuffle();
                grille.invalidateViews();
                success = false;
            }
        });

        btnretour.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public int getActionBarHeight() {
        int result = 0;
        TypedValue tv = new TypedValue();

        if (this.getTheme().resolveAttribute(R.attr.actionBarSize, tv, true))
        {
            result = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }
        return result;
    }
}