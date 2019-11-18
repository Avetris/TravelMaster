package com.travelmaster.Fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.travelmaster.Controlador.Constantes;
import com.travelmaster.Datos.GestorRemoto;
import com.travelmaster.R;
import com.travelmaster.model.Amigo;
import com.travelmaster.model.Usuario;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Random;

import io.realm.Realm;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PerfilFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PerfilFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PerfilFragment extends Fragment {

    private PerfilFragment.OnFragmentInteractionListener mListener;

    Realm realm;
    Usuario usuario;

    public PerfilFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment BuscarFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PerfilFragment newInstance(Integer id) {
        PerfilFragment fragment = new PerfilFragment();
        Bundle args = new Bundle();
        if(id != null)
            args.putInt("Id", id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        load();
    }

    public void load(){
        realm = Realm.getInstance(getActivity());
        if (getArguments() != null && getArguments().containsKey("Id")) {
            usuario = realm.where(Usuario.class).equalTo("idUsuario", getArguments().getInt("Id")).findFirst();
        }else{
            GestorRemoto.obtenerDatos(getActivity());
            usuario = mListener.getUsuarioActual();
        }
        ((TextView) getActivity().findViewById(R.id.descripcionPerfil)).setText(usuario.getDescripcion());
        Constantes.ponerImagen((ImageView) getActivity().findViewById(R.id.imagenPerfil), getActivity(), (usuario.getImagen() != null && usuario.getImagen().length() > 0) ? usuario.getImagen() : "drawable://"+R.drawable.ic_account_circle_black_36dp);

        if(getArguments() == null || !getArguments().containsKey("Id") || (usuario != null && usuario.isActual())){
            ((Button) getActivity().findViewById(R.id.botonSeguir)).setVisibility(View.GONE);
            final Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.botonOpciones);
            toolbar.setVisibility(View.VISIBLE);
            toolbar.inflateMenu(R.menu.opciones);
            toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.modificar:
                            mListener.cambiarFragment(ModificarDescripcionFragment.newInstance());
                            break;
                        case R.id.cambiarContrasena:
                            mListener.cambiarFragment(ModificarContrasenaFragment.newInstance());
                            break;
                        case R.id.salir:
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setTitle(getString(R.string.salir))
                                    .setMessage(getString(R.string.seguroSalir))
                                    .setPositiveButton(R.string.aceptar, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            mListener.salir();
                                        }
                                    })
                                    .setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    }).show();
                            break;
                    }
                    return false;
                }
            });
            ((ImageView) getActivity().findViewById(R.id.imagenPerfil)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(Constantes.verifyCamera(getActivity()) && Constantes.verifyStoragePermissionsRead(getActivity()) && Constantes.verifyStoragePermissionsWrite(getActivity())) {
                        Constantes.elegirImagen(getActivity());
                    }
                }
            });
        }else{
            Log.d("TAG", "ENTRA");
            final Amigo amigo = realm.where(Amigo.class).equalTo("idUsuario", mListener.getUsuarioActual().getIdUsuario()).equalTo("amigo", true).findFirst();
            Button seguir = ((Button) getActivity().findViewById(R.id.botonSeguir));
            seguir.setText(amigo != null ? getString(R.string.dejarSeguir) : getString(R.string.seguir));
            seguir.setVisibility(View.VISIBLE);
            seguir.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    JSONObject object = new JSONObject();
                    try {
                        object.put("id_usuario", mListener.getUsuarioActual().getIdUsuario());
                        object.put("id_amigo", usuario.getIdUsuario());
                        object.put("estado_amigo", amigo == null ? 0 : 1);
                        Constantes.insertarDato(getActivity(), GestorRemoto.reqSetAmigos, object);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            getActivity().findViewById(R.id.botonOpciones).setVisibility(View.GONE);
            ((TextView) getActivity().findViewById(R.id.titulo)).setText(usuario.getNick());
        }
        ((ImageButton) getActivity().findViewById(R.id.mapa)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.cambiarFragment(MapFragment.newInstance(usuario.getIdUsuario(), "idUsuario"));
            }
        });
        ((ImageButton) getActivity().findViewById(R.id.placeList)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.cambiarFragment(ListaLugaresFragment.newInstance(usuario.getIdUsuario()));
            }
        });
    }

    public void cambiarImagen(Uri imagen){
        Constantes.ponerImagen((ImageView) getActivity().findViewById(R.id.imagenPerfil), getActivity(), imagen.toString());
        try {
            Bitmap img = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(),imagen);
            JSONObject object = new JSONObject();
            object.put("id_usuario", mListener.getUsuarioActual().getIdUsuario());
            object.put("imagen_usuario", Constantes.obtenerImagen(img));
            Constantes.insertarDato(getActivity(), GestorRemoto.reqSetImagenUsuarios, object);
        } catch (IOException e) {
            e.printStackTrace();
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_perfil, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof PerfilFragment.OnFragmentInteractionListener) {
            mListener = (PerfilFragment.OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof PerfilFragment.OnFragmentInteractionListener) {
            mListener = (PerfilFragment.OnFragmentInteractionListener) activity;
        } else {
            throw new RuntimeException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void cambiarFragment(Fragment fragment);
        Usuario getUsuarioActual();
        void salir();
    }
}
