package fr.taquin.ui;

import android.content.Context;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.ArrayList;

import fr.taquin.Utils;

public class TaquinAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<Bitmap> answer;
    private ArrayList<Bitmap> imgParts;
    private  int size;
    private Bitmap empty, bmp, hiddenBmp;
    private int screenHeight, screenWidth;
    private boolean success = false;

    public TaquinAdapter(Context c,int s, Uri src, int width, int height){
        mContext = c;
        size = s;
        screenHeight = height;
        screenWidth = width;

        // On crée la photo grâce à son URI
        bmp = Utils.setPicFromUri(screenWidth, screenHeight, mContext, src);

        createPictureSamples();
        initGame();
    }

    /**
     * Initialise la liste d'images du jeu et la mélange
     */
     public void initGame() {
         imgParts = new ArrayList<>(answer);

         //On remplace la dernière image par un fond noir et on récupère cette image
         imgParts.get(imgParts.size()-1).eraseColor(Color.BLACK);
         empty = imgParts.get(imgParts.size() - 1);

         shuffle();
    }

    /**
     * Permet de couper l'image en plusieurs parties avec une taille adaptée
     */
    private void createPictureSamples() {
        answer = new ArrayList<>();

        // On va recupérer la hauteur qu'aurait normalement l'image sans déformation
        int anticipImgHeight = (screenWidth * bmp.getHeight()) / bmp.getWidth();
        int imgPartWidth = bmp.getWidth() / size;
        int imgPartHeight, offset = 0;

        if(anticipImgHeight < screenHeight){  // Si la taille prévue ne dépasse pas celle de l'écran on prends comme base la hauteur réelle de l'image
            imgPartHeight = bmp.getHeight() / size;
        }
        else { //Sinon on fait en sorte que l'image prenne toute la hauteur et on prend le centre de l'image
            imgPartHeight = (screenHeight*bmp.getWidth()/screenWidth)/size;
            offset = bmp.getHeight()/2 - imgPartHeight*size/2;
        }
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                Bitmap part = Bitmap.createBitmap(bmp, imgPartWidth * j, imgPartHeight * i + offset, imgPartWidth, imgPartHeight);
                answer.add(part);
            }
        }

        // On crée une copie de l'image cachée
        hiddenBmp = answer.get(answer.size()-1).copy(answer.get(answer.size() - 1).getConfig(), true);
    }

    /**
     * Cette fonction joue un déplacement d'une case
     * @param position Identifiant du la case cliquée
     * @param init Permet de savoir si c'est un changement d'initialisation ou en cours de jeu
     * @return Retourn vrai si le taquin est terminé
     */
    public boolean play(int position, boolean init){

        boolean goodCase = false;
        success = false;

        if(position >= 0 && position < imgParts.size()) { // On vérifie que l'id de la position est cohérente

            int posEmpty = imgParts.indexOf(empty);
            int[] leftRightUpDown = {-1, -1, -1, -1};

            if (position % size != 0) { //On exclut les images tout à gauche
                leftRightUpDown[0] = position - 1;
            }
            if (position % size != size - 1) { // On exclut les images tout à droite
                leftRightUpDown[1] = position + 1;
            }
            leftRightUpDown[2] = position + size;
            leftRightUpDown[3] = position - size;

            // Si le clic a été fait sur une image voisine de l'image vide on échangera
            for (int i = 0; i < 4; i++) {
                if (leftRightUpDown[i] == posEmpty) {
                    goodCase = true;
                }
            }

            // On réalise le changement
            if (goodCase) {
                Bitmap tmp = imgParts.get(position);
                imgParts.set(position, empty);
                imgParts.set(posEmpty, tmp);
                if(!init && (success = success())){
                    imgParts.set(position, hiddenBmp);
                }
            }
        }
        return success;
    }

    /**
     * Réalise 1000 déplacements aléatoires du taquin pour le mélanger
     */
    public void shuffle(){
        for(int i = 0 ; i < 1000 ; i++){
            if(imgParts.indexOf(empty) ==-1){ // La partie est terminée donc on ne peut pas mélanger
                initGame();
            } else {
                int position = imgParts.indexOf(empty);
                int random = (int) (Math.random() * 4);
                switch (random) {
                    case 0:
                        play(position - 1, true);
                        break;
                    case 1:
                        play(position + 1, true);
                        break;
                    case 2:
                        play(position - size, true);
                        break;
                    case 3:
                        play(position + size, true);
                        break;
                }
            }
        }
    }

    /**
     *
     * @return Retourn vrai si le taquin est réussi
     */
    public boolean success(){
        for(int i=0 ; i < answer.size() ; i++){
            if(!answer.get(i).sameAs(imgParts.get(i))){
                return false;
            }
        }
        return true;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(mContext);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setAdjustViewBounds(true);
        } else {
            imageView = (ImageView) convertView;
        }
        if(success){
            imageView.setPadding(0,0,0,0);
        } else {
            imageView.setPadding(5,5,5,5);
        }
        imageView.setImageBitmap(imgParts.get(position));
        return imageView;
    }


    @Override
    public int getCount() {return imgParts.size();}

    @Override
    public Object getItem(int position) {return imgParts.get(position);}

    @Override
    public long getItemId(int position) {return answer.indexOf(imgParts.get(position));}

}