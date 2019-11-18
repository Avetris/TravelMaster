package com.travelmaster.Activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.travelmaster.Controlador.Constantes;
import com.travelmaster.Datos.GestorRemoto;
import com.travelmaster.Datos.SHA1;
import com.travelmaster.Dialog.DateDialog;
import com.travelmaster.R;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class RegisterActivity extends Activity implements GestorRemoto.AsyncResponse{

    private static final String TAG = "Comprobacion RegisterActivity";
    DatePickerDialog  dialog;

    Date fecha_nacimiento = null;

    EditText editUsuario, editContrasena, editRepetir_Contrasena, editCorreo, editNombre, editTelefono, editFecha, editCiudad, editDesc;

    /**
     * Crea el dialog de fecha y obtiene los editText
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        DateDialog d = new DateDialog();
        d.attach(this);
        dialog = (DatePickerDialog ) d.onCreateDialog(savedInstanceState);

        dialog.getDatePicker().setMaxDate(Calendar.getInstance().getTime().getTime());

        editUsuario = ((android.support.design.widget.TextInputLayout) findViewById(R.id.idUsuario)).getEditText();
        editContrasena = ((android.support.design.widget.TextInputLayout) findViewById(R.id.contrasena)).getEditText();
        editRepetir_Contrasena = ((android.support.design.widget.TextInputLayout) findViewById(R.id.repite_contrasena)).getEditText();
        editCorreo= ((android.support.design.widget.TextInputLayout) findViewById(R.id.correo)).getEditText();
        editNombre= ((android.support.design.widget.TextInputLayout) findViewById(R.id.nombreApe)).getEditText();
        editTelefono = ((android.support.design.widget.TextInputLayout) findViewById(R.id.telefono)).getEditText();
        editFecha = ((android.support.design.widget.TextInputLayout) findViewById(R.id.fecha_nacimiento)).getEditText();
        editCiudad = ((android.support.design.widget.TextInputLayout) findViewById(R.id.ciudad)).getEditText();
        editDesc = ((android.support.design.widget.TextInputLayout) findViewById(R.id.descripcion)).getEditText();
        lostFocus();
    }


    /**
     * Se pone que hacer cuando se pierde el foco de cada edit text. Para esto, se llama a la clase Constantes.
     * Si algun campo no es correcto, le pone un error.
     */
    private void lostFocus(){
        editNombre.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                EditText view = (EditText) v;
                if (!hasFocus) {
                    int error = Constantes.soloTexto(view.getText().toString());
                    if (error == 1) {
                        Constantes.ponerError(view, getString(R.string.error_nombre_vacio), RegisterActivity.this);
                    } if (error == 2) {
                        Constantes.ponerError(view, getString(R.string.error_nombre_formato), RegisterActivity.this);
                    } else{
                        view.setError(null);
                    }
                }
            }
        });

        editUsuario.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                EditText view = (EditText) v;
                if (!hasFocus) {
                    if (view.getText().toString().length() == 0) {
                        Constantes.ponerError(view, getString(R.string.error_usuario_vacio), RegisterActivity.this);
                    } else{
                        view.setError(null);
                    }
                }
            }
        });


        editCorreo.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                EditText view = (EditText) v;
                if (!hasFocus) {
                    int error = Constantes.esCorreo(view.getText().toString());
                    if (error == 1) {
                        Constantes.ponerError(view, getString(R.string.error_correo_vacio), RegisterActivity.this);
                    } if (error == 2) {
                        Constantes.ponerError(view, getString(R.string.error_correo_formato), RegisterActivity.this);
                    } else{
                        view.setError(null);
                    }
                }
            }
        });

        editContrasena.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                EditText view = (EditText) v;
                if (!hasFocus) {
                    String error = gestionarContrasena(view.getText().toString());
                    if (error != null && !error.equals("")) {
                        Constantes.ponerError(view, error, RegisterActivity.this);
                    } else{
                        editRepetir_Contrasena.setText("");
                        view.setError(null);
                    }
                }else{
                    editRepetir_Contrasena.setText("");
                    view.setText("");
                }
            }
        });

        editRepetir_Contrasena.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                EditText view = (EditText) v;
                if (!hasFocus) {
                    if (!view.getText().toString().equals(editContrasena.getText().toString())) {
                        Constantes.ponerError(view, getString(R.string.error_misma_contrasena), RegisterActivity.this);
                    } else{
                        view.setError(null);
                    }
                }else{
                    view.setText("");
                }
            }
        });

        editTelefono.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                EditText view = (EditText) v;
                if (!hasFocus) {
                    int error = Constantes.todoNumero(((EditText) v).getText().toString().trim());
                    if (error == 1){
                        Constantes.ponerError(view, getString(R.string.error_telefono), RegisterActivity.this);
                    } else{
                        view.setError(null);
                    }
                }
            }
        });
    }

    public String gestionarContrasena(String texto){
        String error = null;
        if(texto.length() == 0){
            error = getString(R.string.error_contrasena_vacio);
        }else if(texto.length() < 4){
            error = getString(R.string.error_contrasena_formato);
        }else if(Constantes.tieneMayuscula(texto) != 0){
            error = getString(R.string.error_contrasena_formato);
        }else if(Constantes.tieneMinuscula(texto) != 0){
            error = getString(R.string.error_contrasena_formato);
        }else if(Constantes.tieneNumero(texto) != 0){
            error = getString(R.string.error_contrasena_formato);
        }
        return error;
    }

    public void seleccionarFecha(View v){
        dialog.show();
        Calendar cal = Calendar.getInstance();
        cal.set(dialog.getDatePicker().getYear(),dialog.getDatePicker().getMonth(),dialog.getDatePicker().getDayOfMonth());
        setFecha(cal.getTimeInMillis());
    }

    public void setFecha(long milisegundos){
        fecha_nacimiento = new Date(milisegundos);
        SimpleDateFormat format = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy", getResources().getConfiguration().locale);
        String fecha = format.format(fecha_nacimiento);
        editFecha.setText(fecha);
    }


    /**
     * Pierde el foco de todos los editText (para comprobar si hay errores).
     *  Si existe algun error, lo muestra y se para.
     *  SI no hay errores, inserta en bbdd.
     * @return
     */
    public void registrar(View v){
        v.setPressed(true);

        editNombre.clearFocus();
        boolean guardar = true;
        if(editNombre.getError() != null){
            editNombre.requestFocus();
            guardar = false;
        }
        editUsuario.clearFocus();
        if(editUsuario.getError() != null){
            editUsuario.requestFocus();
            guardar = false;
        }
        editCorreo.clearFocus();
        if(editCorreo.getError() != null){
            editCorreo.requestFocus();
            guardar = false;
        }
        editContrasena.clearFocus();
        if(editContrasena.getError() != null){
            editContrasena.requestFocus();
            guardar = false;
        }
        editRepetir_Contrasena.clearFocus();
        if(editRepetir_Contrasena.getError() != null){
            editRepetir_Contrasena.requestFocus();
            guardar = false;
        }
        editTelefono.clearFocus();
        if(editTelefono.getError() != null){
            editTelefono.requestFocus();
            guardar = false;
        }
        if(fecha_nacimiento == null){
            editFecha.requestFocus();
            guardar = false;
        }
        editCiudad.clearFocus();
        if(editCiudad.getError() != null){
            editCiudad.requestFocus();
            guardar = false;
        }
        if(guardar){
            String[] datos = new String[]{
                    editUsuario.getText().toString(),
                    SHA1.getStringMensageDigest(editContrasena.getText().toString()),
                    editNombre.getText().toString(),
                    fecha_nacimiento.toString(),
                    editCiudad.getText().toString(),
                    editCorreo.getText().toString().trim(),
                    editTelefono.getText().toString().trim(),
                    editDesc.getText().toString()};
            GestorRemoto remote = new GestorRemoto(this, GestorRemoto.reqIdRegister, Constantes.getProgressDialog(this, getString(R.string.iniciando_sesion)), datos,Calendar.getInstance().getTime());
            remote.execute();
        }
    }

    /**
     *Se llama mediante el botón atrás, y vuelve a la activity Login con un resultado cancelado.
     * @param v
     */
    public void atras(View v){
        if(v != null) v.setPressed(true);
        Intent i = new Intent();
        setResult(RESULT_CANCELED, i);
        finish();
    }

    /**
     * Se llama al pulsar el boton atras del movil, y simula el boton atras.
     */
    @Override
    public void onBackPressed() {
        atras(null);
    }

    @Override
    public void processFinish(HashMap<String, String> output, String mReqId) {
        if(output.containsKey("exito")){
            Intent i = new Intent();
            i.putExtra("Usuario", editUsuario.getText().toString());
            i.putExtra("Contrasena", editContrasena.getText().toString());
            setResult(RESULT_OK, i);
            finish();

        }else if(output.containsKey("error_code")){
            String error = output.get("error_code").toString();
            if(error.equals("409")){
                error = getString(R.string.error_usuario_existe);
                editUsuario.requestFocus();
                Constantes.ponerError(editUsuario, getString(R.string.error_usuario_existe), RegisterActivity.this);
            }else{
                error = getString(R.string.error_no_conectado);
            }
            Toast.makeText(this, error, Toast.LENGTH_LONG).show();
        }
    }
}
