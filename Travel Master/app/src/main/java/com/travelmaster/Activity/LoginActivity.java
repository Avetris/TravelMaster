package com.travelmaster.Activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.travelmaster.Controlador.Constantes;
import com.travelmaster.Datos.GestorRemoto;
import com.travelmaster.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.UUID;

import com.travelmaster.Datos.SHA1;


public class LoginActivity extends Activity implements GestorRemoto.AsyncResponse {

    static final String TAG = "Comprobacion LOGIN";

    //GestorBD gestorBD;
    SharedPreferences.Editor editor;

    /**
     * Crea la activity de login. Si existe el parametro guardar en sharedPreferences, y es true, pasa a la activity MainActivity.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Constantes.cambiarIdioma(getBaseContext(), PreferenceManager.getDefaultSharedPreferences(this).getString("Idioma", "es"));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        findViewById(R.id.registrar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registrar(view);
            }
        });

        if(Constantes.getSharedPreferencesValue(this).contains("auth_token")){
            Intent i = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(i);
            finish();
        }
    }

    /**
     * Funccion llamada al pulsar el boton login. Comprueba que los datos introducidos son correctos.
     * En caso afirmativo, llama al web service de conectar con el usuario y la contrasaena en cifrada.
     * En caso negativo, muestra error en los editText.
     * @param v
     */
    public void login(View v){
        v.setPressed(true);
        String idDispositivo = Constantes.getSharedPreferencesValue(this).getString(Constantes.sharedPrefIdDispositivo, "");
        if(idDispositivo == null || idDispositivo.length() == 0){
            idDispositivo = UUID.randomUUID().toString();
            Constantes.setSharedPreferencesValue(this).putString(Constantes.sharedPrefIdDispositivo, idDispositivo).commit();
        }
        String usuario = ((android.support.design.widget.TextInputLayout) findViewById(R.id.idUsuario)).getEditText().getText().toString();
        String contrasena = ((android.support.design.widget.TextInputLayout) findViewById(R.id.contrasena)).getEditText().getText().toString();
        if(usuario.length() > 0 && contrasena.length() > 0){

            GestorRemoto remote = new GestorRemoto(this, GestorRemoto.reqIdLogin, Constantes.getProgressDialog(this, getString(R.string.iniciando_sesion)), new String[]{usuario, SHA1.getStringMensageDigest(contrasena), idDispositivo}, Calendar.getInstance().getTime());
            remote.execute();
        }else{
            Constantes.ponerError(((android.support.design.widget.TextInputLayout) findViewById(R.id.idUsuario)).getEditText(), getString(R.string.error_login), this);
            Constantes.ponerError(((android.support.design.widget.TextInputLayout) findViewById(R.id.contrasena)).getEditText(), getString(R.string.error_login), this);
        }

    }

    /**
     * Funcion llamada al pulsar registrar. Llama a la activity Registrar y se queda a la espera.
     * @param v
     */
    public void  registrar(View v){
        Intent i = new Intent(this,RegisterActivity.class);
        startActivityForResult(i, Constantes.STATUS_REGISTRAR);
    }

    /**
     * Si se obtiene resultado de la activity registrar, se ponen en loos campos.
     * @param requestCode
     * @param resultCode
     * @param data
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constantes.STATUS_REGISTRAR && resultCode == RESULT_OK) {
            ((android.support.design.widget.TextInputLayout) findViewById(R.id.idUsuario)).getEditText().setText(data.getStringExtra("Usuario"));
            ((android.support.design.widget.TextInputLayout) findViewById(R.id.contrasena)).getEditText().setText(data.getStringExtra("Contrasena"));
            Toast.makeText(this, getString(R.string.registro_correcto_toast), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Obtiene y procesa la peticion en formato hashmap. Si no hay error, mete los datos en sharedpreferences y llama a la actividad principal.
     * @param output
     * @param mReqId
     */
    @Override
    public void processFinish(HashMap<String, String> output, String mReqId) {
        if(output.containsKey("error_code")){
            if(output.get("error_code").equals("401")){
                Toast.makeText(this, getString(R.string.error_login), Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(this, output.get("error_message").toString(), Toast.LENGTH_LONG).show();
            }
        }else{
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }
    }
}
