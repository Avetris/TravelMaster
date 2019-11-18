package com.travelmaster.Controlador;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.decode.BaseImageDecoder;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.utils.MemoryCacheUtils;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.travelmaster.Datos.GestorRemoto;
import com.travelmaster.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

/**
 * Created by Aitor on 09/05/2017.
 */

public class Constantes {

    public static final int STATUS_REGISTRAR = 0;
    public static final int STATUS_CODE_CAMARA = 600;



    public static final int REQUEST_EXTERNAL_STORAGE_WRITE = 1;
    public static final int REQUEST_EXTERNAL_STORAGE_READ = 2;
    public static final int REQUEST_LOCATION = 3;
    public static final int REQUEST_CAMERA = 4;
    public static final int REQUEST_PICK_IMAGE = 5;

    public static String sharedPrefAuthToken = "auth_token";
    public static String sharedPrefIdDispositivo = "id_dispositivo";

    private static SharedPreferences sharedPreferences;

    static ProgressDialog progressDialog;

    public static String categoriaHotel = "Hotel";
    public static String categoriaOcio = "Ocio";
    public static String categoriaHosteleria = "Hosteleria";
    public static String categoriaSanidad = "Sanidad";
    public static String categoriaTransporte = "Transporte";
    public static String categoriaInteres = "Punto de Interes";

    private static Uri actualCameraUri = null;

    private static List<AsyncTask<String, Void, String>> sincronizandos = new ArrayList<>();


    public static boolean iniciarSincronizacion(){
        if(sincronizandos.size() > 0){
            return false;
        }else{
            return true;
        }
    }

    public static void reiniciarSincro(){
        for(int i = 0; i < sincronizandos.size(); i++){
            sincronizandos.get(i).cancel(true);
        }
        sincronizandos.clear();
    }

    public static void sincronizado(AsyncTask<String, Void, String> asyncTask){
        sincronizandos.remove(asyncTask);
    }

    public static ArrayList<String> obtenerCategorias(){
        ArrayList<String> categorias = new ArrayList<>();
        categorias.add(categoriaHotel);
        categorias.add(categoriaOcio);
        categorias.add(categoriaHosteleria);
        categorias.add(categoriaSanidad);
        categorias.add(categoriaTransporte);
        categorias.add(categoriaInteres);
        return categorias;
    }

    /**
     * Comprueba si tiene mayusculas
     * @param texto
     * @return
     */
    public static int tieneMayuscula(String texto){
        if(texto == null || texto.length() == 0){
            return 1;
        }else if(!texto.matches(".*[A-ZÁÉÍÓÚ].*")){
            return 2;
        }
        return 0;
    }

    /**
     * Comprueba si tiene minusculas
     * @param texto
     * @return
     */
    public static int tieneMinuscula(String texto){
        if(texto == null || texto.length() == 0){
            return 1;
        }else if(!texto.matches(".*[a-záéíóú].*")){
            return 2;
        }
        return 0;
    }

    /**
     * Comprueba si tiene numero
     * @param texto
     * @return
     */
    public static int tieneNumero(String texto){
        if(texto == null || texto.length() == 0){
            return 1;
        }else if(!texto.matches(".*[0-9].*")){
            return 2;
        }
        return 0;
    }

    /**
     * Comprueba si tiene el formato de correo __@__.__
     * @param texto
     * @return
     */
    public static int esCorreo(String texto){
        if(texto == null || texto.length() == 0){
            return 1;
        }else if(!texto.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")){
            return 2;
        }
        return 0;
    }

    /**
     * Comprueba si tiene solo texto
     * @param texto
     * @return
     */
    public static int soloTexto(String texto){
        if(texto == null || texto.length() == 0){
            return 1;
        }else if(!texto.matches("^[A-Za-zñÑÁÉÍÓÚáéíóú ]*$")){
            return 2;
        }
        return 0;
    }

    /**
     * Comprueba si tiene solo numeros
     * @param texto
     * @return
     */
    public static int todoNumero(String texto){
        if(!texto.matches("[0-9]*$") || texto.length() < 6){
            return 1;
        }
        return 0;
    }

    /**
     * Poner la imagen de exclamacion y el error al editText introducido como parametro
     * @param v
     * @param error
     * @param context
     */
    public static void ponerError(EditText v, String error, Context context){
        Drawable exclamacion;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            exclamacion = context.getDrawable(R.drawable.ic_error_black_24dp);
        }else{
            exclamacion = context.getResources().getDrawable(R.drawable.ic_error_black_24dp);
        }
        exclamacion.setBounds(0, 0, v.getHeight() / 2, v.getHeight() / 2);
        v.setError(error, exclamacion);
    }

    /**
     * Cambia el idioma.
     * @param context
     * @param idioma
     */
    public static void cambiarIdioma(Context context, String idioma){
        Locale locale = new Locale(idioma);
        Locale.setDefault(locale);
        Configuration config = context.getResources().getConfiguration();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocale(locale);
            context.createConfigurationContext(config);
        } else {
            config.locale = locale;
            context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
        }
    }

    /**
     * Pregunta por los derechos para escritura en la memoria externa (A partir de Android N no vale con ponerlo en el manifest)
     * @param activity
     */
    public static boolean verifyStoragePermissionsWrite(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_EXTERNAL_STORAGE_WRITE
            );
            return false;
        }
        return true;
    }

    /**
     * Pregunta por los derechos para lectura en la memoria externa (A partir de Android N no vale con ponerlo en el manifest)
     * @param activity
     */
    public static boolean verifyStoragePermissionsRead(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_EXTERNAL_STORAGE_READ
            );
            return false;
        }else{
            return true;
        }
    }

    /**
     * Pregunta por los derechos para utilizar la camara (A partir de Android N no vale con ponerlo en el manifest)
     * @param activity
     */
    public static boolean verifyCamera(Activity activity) {
        // Check if we have write permission
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
        }
        if(ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            return false;
        }else{
            return true;
        }
    }

    public static void elegirImagen(Activity activity){
        try {
            Intent pickIntent = new Intent(Intent.ACTION_PICK);
            pickIntent.setType("image/*");
            pickIntent.setAction(Intent.ACTION_GET_CONTENT);

            Calendar cal = Calendar.getInstance();
            String title = getSharedPreferencesValue(activity).getString("id_dispositivo", "")+cal.getTimeInMillis();

            File tempFile = File.createTempFile(title, ".jpg", activity.getExternalCacheDir());
            actualCameraUri = Uri.fromFile(tempFile);

            Intent takePhotoIntent = new Intent(
                    MediaStore.ACTION_IMAGE_CAPTURE);
            takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, actualCameraUri);

            // strings.xml
            Intent chooserIntent = Intent.createChooser(pickIntent,
                    "SELECCIONAR ");
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS,
                    new Intent[] { takePhotoIntent });

            activity.startActivityForResult(chooserIntent,Constantes.REQUEST_PICK_IMAGE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Uri getActualCameraUri(){
        return actualCameraUri;
    }

    /**
     * Muestra el dialog de carga.
     * @param context
     * @param texto
     * @return
     */
    public static ProgressDialog getProgressDialog(Context context, String texto){
        if(progressDialog == null || !progressDialog.isShowing()){
            progressDialog = new ProgressDialog(context);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage(texto);
            progressDialog.setCancelable(false);
        }
        return progressDialog;
    }

    public static void insertarDato(Context mContext, String reqId, JSONObject jsonObject) {
        String auth_token = Constantes.getSharedPreferencesValue(mContext).getString(Constantes.sharedPrefAuthToken, "");
        String id_dispositivo = Constantes.getSharedPreferencesValue(mContext).getString(Constantes.sharedPrefIdDispositivo, "");
        String[] datos = new String[]{auth_token, id_dispositivo, jsonObject.toString()};
        Date fechaInicio = Calendar.getInstance().getTime();
        GestorRemoto remote = new GestorRemoto(mContext, reqId, null, datos, fechaInicio);
        remote.execute();
    }

    /**
     * Convierte la imagen a string
     * @param bitmap
     */
    public static String obtenerImagen(Bitmap bitmap){
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bao); // Podemos comprimirlo
        byte[] ba = bao.toByteArray();
        return Base64.encodeToString(ba, Base64.NO_WRAP);
    }

    /**
     * Transforma unos datos pasados en una lista de hashmap a un jsonarray.
     * @param datos
     * @param nombreContenido
     * @return
     */
    private static JSONArray getJSONArray(ArrayList<HashMap<String, String>> datos, String[] nombreContenido){
        JSONArray json = new JSONArray();
        try {
            for(int i = 0; i < datos.size(); i++){
                JSONObject fila = new JSONObject();
                for (String nombre : nombreContenido) {
                    fila.put(nombre, datos.get(i).get(nombre));
                }
                json.put(fila);
            }
            return json;
        } catch (JSONException e) {
            e.printStackTrace();
            return json;
        }
    }

    /**
     * Pone la imagen en el imagebutton del navigationview. Esta imagen la obtiene de la url que se le pasa como parametro.
     * Para esto, utiliza la libreria ImageLoader
     * @param activity
     * @param imagePath
     */
    public static void ponerImagen(final ImageView image, Activity activity, String imagePath){
        if(imagePath != null){
            ImageLoader imageLoader = ImageLoader.getInstance();

            File cacheDir = StorageUtils.getCacheDirectory(activity);

            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(activity)
                    .memoryCacheExtraOptions(480, 800) // default = device screen dimensions
                    .diskCacheExtraOptions(480, 800, null)
                    .threadPoolSize(3) // default
                    .threadPriority(Thread.NORM_PRIORITY - 2) // default
                    .tasksProcessingOrder(QueueProcessingType.FIFO) // default
                    .denyCacheImageMultipleSizesInMemory()
                    .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                    .memoryCacheSize(2 * 1024 * 1024)
                    .memoryCacheSizePercentage(13) // default
                    .diskCache(new UnlimitedDiskCache(cacheDir)) // default
                    .diskCacheSize(50 * 1024 * 1024)
                    .diskCacheFileCount(100)
                    .diskCacheFileNameGenerator(new HashCodeFileNameGenerator()) // default
                    .imageDownloader(new BaseImageDownloader(activity)) // default
                    .imageDecoder(new BaseImageDecoder(true)) // default
                    .defaultDisplayImageOptions(DisplayImageOptions.createSimple()) // default
                    .build();
            imageLoader = imageLoader.getInstance();
            if (!imageLoader.isInited()) {
                imageLoader.init(config);
            }
            DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                    .cacheInMemory(true)
                    .cacheOnDisc(true)
                    .build();
            ImageLoader.getInstance().init(config);
            imageLoader.displayImage(imagePath, image ,defaultOptions, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String url, View view) {
                }

                @Override
                public void onLoadingFailed(String s, View view, FailReason failReason) {
                    ((ImageView) view).setImageBitmap(null);
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap bitmap) {
                 /*   Log.d("PRUEBA", image.getMaxHeight()+", "+image.getMaxWidth());
                    image.setMaxWidth(image.getWidth());
                    image.setMaxHeight(image.getHeight());
                    Log.d("PRUEBA", image.getMaxHeight()+", "+image.getMaxWidth());*/
                    image.setImageBitmap(bitmap);
                }

                @Override
                public void onLoadingCancelled(String s, View view) {
                }
            }, new ImageLoadingProgressListener() {
                @Override
                public void onProgressUpdate(String imageUri, View view, int current, int total) {
                }
            });
        }
    }

    public static String getCountryName(Context context, double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            Address result;
            if (addresses != null && !addresses.isEmpty()) {

                return addresses.get(0).getCountryName()+", "+addresses.get(0).getLocality();
            }
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static SharedPreferences getSharedPreferencesValue(Context context){
        if(sharedPreferences == null)
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences;
    }

    public static SharedPreferences.Editor setSharedPreferencesValue(Context context){
        if(sharedPreferences == null)
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.edit();
    }

}
