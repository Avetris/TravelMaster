package com.travelmaster.Datos;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import com.travelmaster.Activity.MainActivity;
import com.travelmaster.Controlador.Constantes;
import com.travelmaster.R;
import com.travelmaster.model.Amigo;
import com.travelmaster.model.Favorito;
import com.travelmaster.model.Lugar;
import com.travelmaster.model.Review;
import com.travelmaster.model.Tiempos;
import com.travelmaster.model.Usuario;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import io.realm.Realm;

/**
 * Created by Aitor on 02/05/2017.
 */

public class GestorRemoto extends AsyncTask<String, Void, String> {

    // Interfaz:
    public interface AsyncResponse {
        void processFinish(HashMap<String, String> output, String mReqId);
    }

    public AsyncResponse delegate = null;

    // Variables:
    private static final String TAG = "GestorRemoto";
    private static String SERVER_PATH = "http://galan.ehu.eus/umateos002/WEB/TravelMaster/";

    public static final String reqIdLogin = "conectar";
    public static final String reqIdRegister = "registrar";

    public static final String reqUsuarios = "usuarios";
    public static final String reqAmigos = "amigos";
    public static final String reqLugares = "lugares";
    public static final String reqReviews = "reviews";
    public static final String reqFavoritos = "favoritos";

    public static final String reqSetUsuariosDescripcion = "setUsuariosDescripcion";
    public static final String reqSetUsuarioContrasena = "setUsuariosContrasena";
    public static final String reqSetImagenUsuarios = "setImagenUsuarios";
    public static final String reqSetAmigos = "setAmigos";
    public static final String reqSetLugares = "setLugares";
    public static final String reqSetReviews = "setReviews";
    public static final String reqSetFavoritos = "setFavoritos";

    private Context mContext;
    private String mReqId;
    private String param = "";
    private static ProgressDialog mProgressDialog = null;
    private HttpURLConnection urlConnection = null;
    private String errorMessage = "";
    private Usuario usuarioActual;

    private Realm realm;

    private Date fechaInicio;

    // Constructor:
    public GestorRemoto(Context context, String reqId, ProgressDialog progressDialog, String[] datos, Date fechaInicio) {
        mContext = context;
        realm = Realm.getInstance(context);
        mReqId = reqId;
        this.fechaInicio = fechaInicio;
        if(progressDialog != null){
            if (mProgressDialog == null || !mProgressDialog.isShowing()) {
                mProgressDialog = progressDialog;
            }
        }else{
            mProgressDialog = progressDialog;
        }
        errorMessage = "";
        delegate = (AsyncResponse) context;

        usuarioActual = realm.where(Usuario.class).equalTo("actual", true).findFirst();
        // Poner los datos en formato URL:

        switch (mReqId) {
            case reqIdLogin:
                try {
                    param = "nick=" + URLEncoder.encode(datos[0], "UTF-8");
                    param += "&password=" + URLEncoder.encode(datos[1], "UTF-8");
                    param += "&id_dispositivo=" + URLEncoder.encode(datos[2], "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                break;
            case reqIdRegister:
                try {
                    param = "nick=" + URLEncoder.encode(datos[0], "UTF-8");
                    param += "&pass=" + URLEncoder.encode(datos[1], "UTF-8");
                    param += "&name=" + URLEncoder.encode(datos[2], "UTF-8");
                    param += "&birth=" + URLEncoder.encode(datos[3], "UTF-8");
                    param += "&origin=" + URLEncoder.encode(datos[4], "UTF-8");
                    param += "&mail=" + URLEncoder.encode(datos[5], "UTF-8");
                    param += "&telf=" + URLEncoder.encode(datos[6], "UTF-8");
                    param += "&description=" + URLEncoder.encode(datos[7], "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                break;
            case reqUsuarios:
            case reqAmigos:
            case reqLugares:
            case reqReviews:
            case reqFavoritos:
                try {
                    param = "auth_token=" + URLEncoder.encode(datos[0], "UTF-8");
                    param += "&id_dispositivo=" + URLEncoder.encode(datos[1], "UTF-8");
                    if (datos[2] != null)
                        param += "&fecha_desde=" + URLEncoder.encode(datos[2], "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                break;
            case reqSetUsuariosDescripcion:
            case reqSetUsuarioContrasena:
            case reqSetImagenUsuarios:
            case reqSetAmigos:
            case reqSetLugares:
            case reqSetReviews:
            case reqSetFavoritos:

                Log.d(TAG, datos[2]);
                try {
                    param = "auth_token=" + URLEncoder.encode(datos[0], "UTF-8");
                    param += "&id_dispositivo=" + URLEncoder.encode(datos[1], "UTF-8");
                    param += "&datos=" + URLEncoder.encode(datos[2], "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    /**
     * En caso de que no estre el dialog visible, lo muestra antes de realizar las peticiones
     */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        if (mProgressDialog != null && !mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }
    }

    /**
     * Realiza la peticion al web service, y devuelve la respuesta.
     *
     * @param params
     * @return response
     */
    @Override
    protected String doInBackground(String... params) {
        // Configurar a que fichero php se va a llamar desde cada peticion:
        String peticion = SERVER_PATH + mReqId + ".php";
        // Comprobar si hay conexion a Internet:
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (!(netInfo != null && netInfo.isConnected())) {
            errorMessage = "{'error_code': 600, 'error_message': 'No Internet Connection'}";
            return errorMessage;
        }

        InputStream inputStream;
        try {
            // Crear un objeto URL:
            URL targetURL = new URL(peticion);
            urlConnection = (HttpURLConnection) targetURL.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setRequestProperty("Accept-Language", Locale.getDefault().getLanguage() + "-" + Locale.getDefault().getCountry());
            urlConnection.setConnectTimeout(5000);
            urlConnection.setReadTimeout(5000);
            urlConnection.setDoOutput(true);

            OutputStreamWriter wr = new OutputStreamWriter(urlConnection.getOutputStream());
            wr.write(param);
            wr.close();

            int statusCode = urlConnection.getResponseCode();

            if (statusCode == 200) {
                inputStream = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                String line;
                String result = "";
                while ((line = bufferedReader.readLine()) != null) {
                    result += line;
                }
                // Cerra stream:
                inputStream.close();
                return result;
            } else {
                errorMessage = "[{'error_code': " + statusCode + ", 'error_message': '" + urlConnection.getInputStream() + "'}]";
                urlConnection.disconnect();
                return errorMessage;
            }
        } catch (Exception e) {
            errorMessage = "[{'error_code': 400, 'error_message': '" + e + "'}]";
            return errorMessage;
        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();
        }
    }

    /**
     * Metodo encargado de decir que realizar cuando termine de obtener la respuesta del servidor.
     * En caso de que no devuelva un error_code, parseara el resultado y lo mandara como una lista de HashMaps
     *
     * @param result
     */
    @Override
    protected void onPostExecute(final String result) {
        Log.d(TAG, result + " - " + mReqId);
        HashMap<String, String> output = new HashMap<String, String>();
        try {
            JSONArray jsonArray = new JSONArray(result);
            if (jsonArray.length() == 1 && jsonArray.getJSONObject(0).has("error_code")) {
                output.put("error_code", jsonArray.getJSONObject(0).getString("error_code"));
                output.put("error_message", jsonArray.getJSONObject(0).getString("error_message"));
            } else {
                switch (mReqId) {
                    case reqIdLogin:
                        realm.beginTransaction();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            getUsuario(jsonArray.getJSONObject(i));
                            Constantes.setSharedPreferencesValue(mContext).putString("auth_token", jsonArray.getJSONObject(i).getString("auth_token")).commit();
                            Log.d("T", String.valueOf(Constantes.getSharedPreferencesValue(mContext).contains("auth_token")));
                        }
                        realm.commitTransaction();
                        break;
                    case reqIdRegister:
                        output = new HashMap<String, String>();
                        output.put("exito", jsonArray.getJSONObject(0).getString("exito"));
                        break;
                    case reqSetUsuariosDescripcion:
                        realm.beginTransaction();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            Usuario usuario = realm.where(Usuario.class).equalTo("actual", true).findFirst();
                            usuario.setDescripcion( jsonArray.getJSONObject(i).getString("descripcion_usuario"));
                            realm.copyToRealmOrUpdate(usuario);
                        }
                        realm.commitTransaction();
                        obtenerDatos(mContext);
                        break;
                    case reqSetUsuarioContrasena:
                        obtenerDatos(mContext);
                        break;
                    case reqSetImagenUsuarios:
                        realm.beginTransaction();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            Usuario user = realm.where(Usuario.class).equalTo("idUsuario", jsonObject.getInt("id_usuario")).findFirst();
                            user.setImagen(jsonObject.getString("imagen_usuario"));
                            realm.copyToRealmOrUpdate(user);
                        }
                        realm.commitTransaction();
                        obtenerDatos(mContext);
                        break;
                    case reqSetReviews:
                        realm.beginTransaction();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            getReview(jsonArray.getJSONObject(i));
                        }
                        realm.commitTransaction();
                        obtenerDatos(mContext);
                        break;
                    case reqSetAmigos:
                        realm.beginTransaction();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            getAmigo(jsonArray.getJSONObject(i));
                        }
                        realm.commitTransaction();
                        obtenerDatos(mContext);
                        break;
                    case reqSetFavoritos:
                        realm.beginTransaction();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            getFavorito(jsonArray.getJSONObject(i));
                        }
                        realm.commitTransaction();
                        obtenerDatos(mContext);
                        break;
                    case reqSetLugares:
                        realm.beginTransaction();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            getLugar(jsonArray.getJSONObject(i));
                        }
                        realm.commitTransaction();
                        obtenerDatos(mContext);
                        break;
                    case reqUsuarios:
                        realm.beginTransaction();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            getUsuario(jsonArray.getJSONObject(i));
                        }
                        Tiempos fechaUsuarios = realm.where(Tiempos.class).contains("tabla", reqUsuarios).findFirst();
                        if(fechaUsuarios == null){
                            fechaUsuarios = new Tiempos();
                            fechaUsuarios.setTabla(reqUsuarios);
                        }
                        fechaUsuarios.setUltimaFecha(fechaInicio);
                        realm.copyToRealmOrUpdate(fechaUsuarios);
                        realm.commitTransaction();
                        break;
                    case reqAmigos:
                        realm.beginTransaction();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            getAmigo(jsonArray.getJSONObject(i));
                        }
                        Tiempos fechaAmigos = realm.where(Tiempos.class).contains("tabla", reqAmigos).findFirst();
                        if(fechaAmigos == null){
                            fechaAmigos = new Tiempos();
                            fechaAmigos.setTabla(reqAmigos);
                        }
                        fechaAmigos.setUltimaFecha(fechaInicio);
                        realm.copyToRealmOrUpdate(fechaAmigos);
                        realm.commitTransaction();
                        break;
                    case reqFavoritos:
                        realm.beginTransaction();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            getFavorito(jsonArray.getJSONObject(i));
                        }
                        Tiempos fechaFavoritos = realm.where(Tiempos.class).contains("tabla", reqFavoritos).findFirst();
                        if(fechaFavoritos == null){
                            fechaFavoritos = new Tiempos();
                            fechaFavoritos.setTabla(reqFavoritos);
                        }
                        fechaFavoritos.setUltimaFecha(fechaInicio);
                        realm.copyToRealmOrUpdate(fechaFavoritos);
                        realm.commitTransaction();
                        break;
                    case reqLugares:
                        realm.beginTransaction();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            getLugar(jsonArray.getJSONObject(i));
                        }
                        Tiempos fechaLugar = realm.where(Tiempos.class).contains("tabla", reqLugares).findFirst();
                        if(fechaLugar == null){
                            fechaLugar = new Tiempos();
                            fechaLugar.setTabla(reqLugares);
                        }
                        fechaLugar.setUltimaFecha(fechaInicio);
                        realm.copyToRealmOrUpdate(fechaLugar);
                        realm.commitTransaction();
                        break;
                    case reqReviews:
                        realm.beginTransaction();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            getReview(jsonArray.getJSONObject(i));
                        }
                        Tiempos fechaReviews = realm.where(Tiempos.class).contains("tabla", reqReviews).findFirst();
                        if(fechaReviews == null){
                            fechaReviews = new Tiempos();
                            fechaReviews.setTabla(reqReviews);
                        }
                        fechaReviews.setUltimaFecha(fechaInicio);
                        realm.copyToRealmOrUpdate(fechaReviews);
                        realm.commitTransaction();
                        break;
                }
                Constantes.sincronizado(this);
            }
            delegate.processFinish(output, mReqId);
        } catch (JSONException e) {
            e.printStackTrace();
            output.put("error_code", "500");
            output.put("error_message", result);
            delegate.processFinish(output, mReqId);
        }
        // Esconder la barra de progreso:
        if (mProgressDialog != null && mProgressDialog.isShowing()) mProgressDialog.dismiss();
    }

    /**
     * Accion que se realiza si se cancela la peticion
     */
    @Override
    protected void onCancelled() {
        // Esconder la barra de progreso:
        if (mProgressDialog != null && mProgressDialog.isShowing()) mProgressDialog.dismiss();
    }

    private void getUsuario(JSONObject jsonObject) {
        Usuario usuario = new Usuario();
        try {
            usuario.setIdUsuario(jsonObject.getInt("id_usuario"));
            usuario.setNick(jsonObject.getString("nick_usuario"));
            usuario.setNombre(jsonObject.getString("nombre_usuario"));
            usuario.setImagen(!jsonObject.isNull("imagen_usuario") ? jsonObject.getString("imagen_usuario") : null);
            usuario.setPrivado(jsonObject.getInt("privado_usuario") == 1);
            Calendar cal = Calendar.getInstance();
            String[] fechas = jsonObject.getString("cumpleanos_usuario").split("-");
            cal.set(Integer.valueOf(fechas[0]), Integer.valueOf(fechas[1]), Integer.valueOf(fechas[2]));
            usuario.setCumpleanos(cal.getTime());
            usuario.setEmail(jsonObject.getString("email_usuario"));
            usuario.setTelefono(jsonObject.getInt("telefono_usuario"));
            usuario.setDescripcion(jsonObject.getString("descripcion_usuario"));
            usuario.setValoracion(jsonObject.getInt("valoracion_usuario"));
            usuario.setOrigen(jsonObject.getString("pais_usuario"));
            usuario.setActual(usuarioActual == null || usuarioActual.getIdUsuario() == usuario.getIdUsuario());
            realm.copyToRealmOrUpdate(usuario);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getAmigo(JSONObject jsonObject) {
        Amigo amigo = new Amigo();
        try {
            amigo.setIdUsuario(jsonObject.getInt("id_usuario"));
            amigo.setIdAmigo(jsonObject.getInt("id_amigo"));
            amigo.setAmigo(jsonObject.getInt("estado") == 0);
            amigo.setIdUnico(amigo.getIdUsuario() + ";" + amigo.getIdAmigo());
            realm.copyToRealmOrUpdate(amigo);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getFavorito(JSONObject jsonObject) {
        Favorito favorito = new Favorito ();
        try {
            favorito.setIdUsuario(jsonObject.getInt("id_usuario"));
            favorito.setIdLugar(jsonObject.getInt("id_lugar"));
            favorito.setFavorito(jsonObject.getInt("estado") == 0);
            favorito.setIdUnico(favorito.getIdUsuario() + ";" + favorito.getIdLugar());
            realm.copyToRealmOrUpdate(favorito);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getLugar(JSONObject jsonObject) {
        Lugar lugar = new Lugar();
        try {
            lugar.setIdLugar(jsonObject.getInt("id_lugar"));
            lugar.setNombreLugar(jsonObject.getString("nombre_lugar"));
            lugar.setDescripcionLugar(jsonObject.getString("descripcion_lugar"));
            lugar.setImagenLugar(jsonObject.getString("imagen_lugar"));
            lugar.setLatitud(jsonObject.getDouble("latitud_lugar"));
            lugar.setLongitud(jsonObject.getDouble("longitud_lugar"));
            lugar.setIdCreador(jsonObject.getInt("id_creador"));
            lugar.setValoracionLugar(jsonObject.getInt("valoracion_lugar"));
            lugar.setCategoria(jsonObject.getString("categoria_lugar"));
            realm.copyToRealmOrUpdate(lugar);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getReview(JSONObject jsonObject) {
        Review review = new Review();
        try {
            review.setIdUsuario(jsonObject.getInt("id_usuario"));
            review.setIdLugar(jsonObject.getInt("id_lugar"));
            review.setTitulo(jsonObject.getString("titulo_review"));
            review.setDescripcionReview(jsonObject.getString("descripcion_review"));
            review.setValoracion(jsonObject.getInt("valoracion_review"));
            review.setIdUnico(review.getIdUsuario()+";"+review.getIdLugar());
            realm.copyToRealmOrUpdate(review);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void obtenerDatos(Context context){
        Realm realm = Realm.getInstance(context);
        String auth_token = Constantes.getSharedPreferencesValue(context).getString(Constantes.sharedPrefAuthToken, "");
        String id_dispositivo = Constantes.getSharedPreferencesValue(context).getString(Constantes.sharedPrefIdDispositivo, "");
        String[] datos = new String[]{auth_token, id_dispositivo, null};
        String[] reqs = new String[]{GestorRemoto.reqUsuarios,GestorRemoto.reqAmigos,GestorRemoto.reqLugares,GestorRemoto.reqReviews, GestorRemoto.reqFavoritos};
        Date fechaInicio = Calendar.getInstance().getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd H:m:s");
        if(Constantes.iniciarSincronizacion()) {
            for (String reqId : reqs) {
                Tiempos fechas = realm.where(Tiempos.class).contains("tabla", reqId).findFirst();
                if (fechas != null) {
                    datos[2] = simpleDateFormat.format(fechas.getUltimaFecha().getTime());
                }
                GestorRemoto remote = new GestorRemoto(context, reqId, Constantes.getProgressDialog(context, context.getString(R.string.sincronizando)), datos, fechaInicio);
                remote.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        }
    }
}
