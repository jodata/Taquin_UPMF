package fr.taquin.ui;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

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

        // On récupère la taille de l'écran
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        // Informations envoyées par l'activité précédente
        Intent intent = getIntent();
        if (intent != null) {
            level = intent.getIntExtra("level", 0);
            mImageURI = Uri.parse(intent.getStringExtra("imgURI"));

            // On adapte l'orientation de l'écran en fonction de l'orientation de l'image
            boolean modePortrait = intent.getBooleanExtra("portrait", true);
            if(modePortrait){
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
            else {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
            }
        }
        else{ // Si on a pas reçu d'intent on termine l'activité
            finish();
        }

        grille.setNumColumns(level);

        // Le taquin adapter a besoin de connaître la taille réelle disponible sur l'écran pour adapter l'image
        taquinAdapter = new TaquinAdapter(this, level, mImageURI, size.x, size.y-getStatusBarHeight()-(int)getResources().getDimension(R.dimen.buttonHeight));
        grille.setAdapter(taquinAdapter);

        grille.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!success) {
                    success = taquinAdapter.play(position, false);
                    //Lors de la fonction play le sens de l'animation est mis à jour
                    Animation anim = taquinAdapter.getAnimation();
                    //On indique la durée du déplacement
                    anim.setDuration(300);
                    anim.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        //On mets en place un listener pour mettre réellement à jour la grille à la fin de l'animation
                        //On affiche un message de fin si la taquin est terminé
                        @Override
                        public void onAnimationEnd(Animation animation) {
                            // On met à jour la grille
                            grille.invalidateViews();
                            if (success) {
                                Toast toast = Toast.makeText(TaquinActivity.this, "Taquin Résolu !", Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                            }
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    //On réccupère la vue de l'image à déplacer
                    View v = grille.getChildAt(position);
                    //On lui applique l'animation précédemment réccupérée
                    v.startAnimation(anim);
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

    /**
     * Retourne la hauteur de la barre de statut du téléphone
     * @return Valeur en pixel de la taille de la barre
     */
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