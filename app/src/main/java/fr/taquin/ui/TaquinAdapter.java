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
        bmp = Utils.setPicFromUri(screenWidth, screenHeight, mContext, src);
        createPictureSamples();
        initGame();
    }

     public void initGame() {
         imgParts = new ArrayList<>(answer);
         imgParts.get(imgParts.size()-1).eraseColor(Color.BLACK);
         empty = imgParts.get(imgParts.size() - 1);
         shuffle();
    }

    /**
     *
     */
    private void createPictureSamples() {
        answer = new ArrayList<>();
        int realImgHeight = (screenWidth * bmp.getHeight()) / bmp.getWidth();
        int imgPartWidth = bmp.getWidth() / size;
        int imgPartHeight, offset = 0;

        if(realImgHeight < screenHeight){
            imgPartHeight = bmp.getHeight() / size;
        }
        else {
            imgPartHeight = (screenHeight*bmp.getWidth()/screenWidth)/size;
            offset = bmp.getHeight()/2 - imgPartHeight*size/2;
        }
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                Bitmap part = Bitmap.createBitmap(bmp, imgPartWidth * j, imgPartHeight * i + offset, imgPartWidth, imgPartHeight);
                answer.add(part);
            }
        }
        hiddenBmp = answer.get(answer.size()-1).copy(answer.get(answer.size() - 1).getConfig(), true); // On crée une copie de l'image cachée
    }
    public boolean change(int position, boolean init){
        boolean change = false;
        success = false;
        if(position >= 0 && position < imgParts.size()) {
            int posEmpty = imgParts.indexOf(empty);
            int[] leftRightUpDown = {-1, -1, -1, -1};
            if (position % size != 0) {
                leftRightUpDown[0] = position - 1;
            }
            if (position % size != size - 1) {
                leftRightUpDown[1] = position + 1;
            }
            leftRightUpDown[2] = position + size;
            leftRightUpDown[3] = position - size;

            for (int i = 0; i < 4; i++) {
                if (leftRightUpDown[i] == posEmpty) {
                    change = true;
                }
            }
            if (change) {
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

    public void shuffle(){
        for(int i = 0 ; i < 1000 ; i++){
            if(imgParts.indexOf(empty) ==-1){ // La partie est terminée donc on ne peut pas mélanger
                initGame();
            } else {
                int position = imgParts.indexOf(empty);
                int random = (int) (Math.random() * 4);
                switch (random) {
                    case 0:
                        change(position - 1, true);
                        break;
                    case 1:
                        change(position + 1, true);
                        break;
                    case 2:
                        change(position - size, true);
                        break;
                    case 3:
                        change(position + size, true);
                        break;
                }
            }
        }
    }

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
    //public int getCount() {return samples.length;}
    public int getCount() {return imgParts.size();}

    @Override
    //public Object getItem(int position) {return samples[position];}
    public Object getItem(int position) {return imgParts.get(position);}

    @Override
    //public long getItemId(int position) {return 0;}
    public long getItemId(int position) {return answer.indexOf(imgParts.get(position));}

}