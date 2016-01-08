package fr.taquin;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.widget.ImageButton;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utils {

    /**
     *
     * @return Retourne le fichier image créé
     * @throws IOException
     */
    public static File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmmss", Locale.FRANCE).format(new Date());
        String imageFileName = "Taquin_"+ timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        return image;
    }

    /**
     * Ajoute une image dans la galerie
     * @param c contexte de l'activité
     * @param mImageURI URI de l'image qui sera ajoutée
     */
    public static void galleryAddPic(Context c, Uri mImageURI) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(mImageURI);
        c.sendBroadcast(mediaScanIntent);
    }


    /**
     * Fonction fournies par android développeur permettant de créer une Bitmap avec des dimensions adaptées
     * @param img Vue dans laquelle l'image sera affichée
     * @param path chemin de l'image
     * @return Fichier bitmap créé à partir de l'image avec une taille adaptée
     */
    public static Bitmap setPic(ImageButton img, String path) {
        // Get the dimensions of the View
        int targetW = img.getWidth();
        int targetH = img.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(path, bmOptions);

        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        return  BitmapFactory.decodeFile(path, bmOptions);
    }

    /**
     *
     * @param targetW Largeur souhaitée du bitmap créé
     * @param targetH Hauteur souhaitée du bitmap créé
     * @param c Contexte de l'activité
     * @param path Uri de l'image
     * @return Fichier bitmap créé à partir de l'image avec une taille choisie
     */
    public static Bitmap setPicFromUri(int targetW, int targetH, Context c, Uri path) {
        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        // Permet d'obtenir des infos sur le Bitmap sans le chargé
        bmOptions.inJustDecodeBounds = true;
        InputStream is = null;
        try {
            is = c.getContentResolver().openInputStream(path);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        BitmapFactory.decodeStream(is, null, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        try {
            is.close();
            //Il faut réinitialisé l'inputStream car il a déjà été utilisé
            is = c.getContentResolver().openInputStream(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return BitmapFactory.decodeStream(is, null, bmOptions);
    }
}
