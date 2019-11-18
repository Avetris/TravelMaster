package com.travelmaster.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.travelmaster.Controlador.Constantes;
import com.travelmaster.Datos.GestorRemoto;
import com.travelmaster.Fragment.BuscarFragment;
import com.travelmaster.Fragment.ModificarContrasenaFragment;
import com.travelmaster.Fragment.ModificarDescripcionFragment;
import com.travelmaster.Fragment.CrearLugarFragment;
import com.travelmaster.Fragment.CrearReviewFragment;
import com.travelmaster.Fragment.FavoritosFragment;
import com.travelmaster.Fragment.ListaLugaresFragment;
import com.travelmaster.Fragment.LugarFragment;
import com.travelmaster.Fragment.MapCrearFragment;
import com.travelmaster.Fragment.MapFragment;
import com.travelmaster.Fragment.PerfilFragment;
import com.travelmaster.R;
import com.travelmaster.model.Usuario;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.realm.Realm;

public class MainActivity extends Activity implements GestorRemoto.AsyncResponse,
                                                      CrearReviewFragment.OnFragmentInteractionListener,
                                                      BuscarFragment.OnFragmentInteractionListener,
                                                      PerfilFragment.OnFragmentInteractionListener,
                                                      MapFragment.OnFragmentInteractionListener,
                                                      FavoritosFragment.OnFragmentInteractionListener,
                                                      MapCrearFragment.OnFragmentInteractionListener,
                                                      CrearLugarFragment.OnFragmentInteractionListener,
                                                      LugarFragment.OnFragmentInteractionListener,
                                                      ListaLugaresFragment.OnFragmentInteractionListener,
                                                      ModificarDescripcionFragment.OnFragmentInteractionListener,
                                                      ModificarContrasenaFragment.OnFragmentInteractionListener{

    Realm realm;
    List<Fragment> historialVentanas = new ArrayList<>();
    Usuario usuarioActual;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        realm = Realm.getDefaultInstance();
        usuarioActual = realm.where(Usuario.class).equalTo("actual", true).findFirst();
        cambiarFragment(getFragmentManager().findFragmentById(R.id.contenedor));

        final BottomNavigationView bottomNavigation =(BottomNavigationView) findViewById(R.id.menuInferior);
        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(!item.isChecked() || historialVentanas.size() > 1){
                    historialVentanas.clear();
                    switch (item.getItemId()) {
                        case R.id.action_favoritos:
                            cambiarFragment(FavoritosFragment.newInstance());
                            break;
                        case R.id.action_buscar:
                            cambiarFragment(BuscarFragment.newInstance());
                            break;
                        case R.id.action_mapa:
                            cambiarFragment(MapFragment.newInstance(null, null));
                            break;
                        case R.id.action_perfil:
                            cambiarFragment(PerfilFragment.newInstance(null));
                            break;
                    }
                }
                return true;
            }
        });
        bottomNavigation.post(new Runnable() {
            @Override
            public void run() {
                BottomNavigationMenuView menuView = (BottomNavigationMenuView) bottomNavigation.getChildAt(0);
                for (int i = 0; i < menuView.getChildCount(); i++) {
                    final View iconView = menuView.getChildAt(i).findViewById(android.support.design.R.id.icon);
                    final ViewGroup.LayoutParams layoutParams = iconView.getLayoutParams();
                    final DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
                    layoutParams.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, bottomNavigation.getHeight()/4, displayMetrics);
                    layoutParams.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, bottomNavigation.getHeight()/4, displayMetrics);
                    iconView.setLayoutParams(layoutParams);
                }
            }
        });

        findViewById(R.id.botonAtras).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    @Override
    public Usuario getUsuarioActual(){
        return usuarioActual;
    }

    @Override
    public void salir() {
        realm.beginTransaction();
        usuarioActual.setActual(false);
        realm.copyToRealmOrUpdate(usuarioActual);
        realm.commitTransaction();
        Constantes.setSharedPreferencesValue(this).remove("auth_token").commit();
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
        finish();
    }


    @Override
    public void cambiarFragment(Fragment fragment){
        if (fragment != null) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.contenedor, fragment)
                    .commit();
        }else{
            Fragment fAux = getFragmentManager().findFragmentById(R.id.contenedor);
            if (fAux == null) {
                getFragmentManager().beginTransaction()
                        .add(R.id.contenedor, PerfilFragment.newInstance(null))
                        .commit();
            }
        }

        if(historialVentanas.size() == 0 || historialVentanas.get(historialVentanas.size() - 1) != fragment){
            historialVentanas.add(fragment);
        }
        if(historialVentanas.size() > 1){
            findViewById(R.id.toolbar).setVisibility(View.VISIBLE);
        }else{
            findViewById(R.id.toolbar).setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        if(historialVentanas.size() > 1){
            historialVentanas.remove(historialVentanas.size()-1);
            cambiarFragment(historialVentanas.get(historialVentanas.size()-1));
        }else{
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.salirTitle))
                    .setMessage(getString(R.string.salirMessage))
                    .setPositiveButton(R.string.aceptar, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            switch (requestCode){
                case Constantes.REQUEST_PICK_IMAGE:
                    Uri uri = null;
                    if(data == null || data.getData() == null){
                        uri = Constantes.getActualCameraUri();
                    }else{
                        uri = data.getData();
                    }
                    if(getFragmentManager().findFragmentById(R.id.contenedor) instanceof PerfilFragment){
                        ((PerfilFragment) getFragmentManager().findFragmentById(R.id.contenedor)).cambiarImagen(uri);
                    }else if(getFragmentManager().findFragmentById(R.id.contenedor) instanceof CrearLugarFragment){
                        ((CrearLugarFragment) getFragmentManager().findFragmentById(R.id.contenedor)).cambiarImagen(uri);
                    }
                    break;
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case Constantes.REQUEST_CAMERA:
                if(Constantes.verifyStoragePermissionsRead(this) && Constantes.verifyStoragePermissionsWrite(this)){
                    if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        Constantes.elegirImagen(this);
                    }else{
                        Toast.makeText(this, R.string.permisosInternal, Toast.LENGTH_LONG).show();
                    }
                }
                break;
            case Constantes.REQUEST_EXTERNAL_STORAGE_WRITE:
                if(Constantes.verifyCamera(this) && Constantes.verifyStoragePermissionsRead(this)){
                    if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        Constantes.elegirImagen(this);
                    }else{
                        Toast.makeText(this, R.string.permisosCamara, Toast.LENGTH_LONG).show();
                    }
                }
                break;
            case Constantes.REQUEST_EXTERNAL_STORAGE_READ:
                if(Constantes.verifyCamera(this) && Constantes.verifyStoragePermissionsWrite(this)){
                    if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        Constantes.elegirImagen(this);
                    }else{
                        Toast.makeText(this, R.string.permisosCamara, Toast.LENGTH_LONG).show();
                    }
                }
                break;
            case Constantes.REQUEST_LOCATION:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Fragment f = getFragmentManager().findFragmentById(R.id.contenedor);
                    if (f instanceof MapFragment) ((MapFragment) f).permissionsAccept();
                    else if(f instanceof MapCrearFragment) ((MapCrearFragment) f).permissionsAccept();
                }else{
                    Toast.makeText(this, R.string.permisosLocation, Toast.LENGTH_LONG).show();
                }
        }
    }

    @Override
    public void processFinish(HashMap<String, String> output, String mReqId) {
        if(output.containsKey("error_code")) {
            if (output.get("error_code").equals("401")) {
                Toast.makeText(this, getString(R.string.error_auth_code), Toast.LENGTH_LONG).show();
                startActivity(new Intent(this, LoginActivity.class));
                Constantes.reiniciarSincro();
                Constantes.setSharedPreferencesValue(this).remove("auth_token").commit();

            }
            Toast.makeText(this, output.get("error_message"), Toast.LENGTH_LONG).show();
            Log.e("ERROR SINCRONIZACION: " + mReqId, output.get("error_message"));
        }else{
            switch (mReqId){
                case GestorRemoto.reqSetLugares:
                    cambiarFragment(PerfilFragment.newInstance(null));
                    break;
                case GestorRemoto.reqSetReviews:
                    onBackPressed();
                    break;
                case GestorRemoto.reqSetFavoritos:
                    ((LugarFragment) getFragmentManager().findFragmentById(R.id.contenedor)).load();
                    break;
                case GestorRemoto.reqSetAmigos:
                    ((PerfilFragment) getFragmentManager().findFragmentById(R.id.contenedor)).load();
                    break;
                case GestorRemoto.reqSetImagenUsuarios:
                case GestorRemoto.reqSetUsuarioContrasena:
                case GestorRemoto.reqSetUsuariosDescripcion:
                    historialVentanas.clear();
                    cambiarFragment(PerfilFragment.newInstance(null));
                    break;
            }
        }
    }


}
