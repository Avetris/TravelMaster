package com.travelmaster.Fragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.travelmaster.Controlador.Constantes;
import com.travelmaster.Datos.GestorRemoto;
import com.travelmaster.R;
import com.travelmaster.model.Lugar;
import com.travelmaster.model.Review;
import com.travelmaster.model.Usuario;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BuscarFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BuscarFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CrearLugarFragment extends Fragment {

    OnFragmentInteractionListener mListener;
    Realm realm;

    Double latidud, longitud;

    Bitmap imagenSeleccionada = null;

    HashMap<String, String> categorias = new HashMap<String, String>();

    public CrearLugarFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment BuscarFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CrearLugarFragment newInstance(Double latitud, Double longitud) {
        CrearLugarFragment fragment = new CrearLugarFragment();
        Bundle args = new Bundle();
        args.putDouble("latitud", latitud);
        args.putDouble("longitud", longitud);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        realm = Realm.getInstance(getActivity());
        latidud = getArguments().getDouble("latitud");
        longitud = getArguments().getDouble("longitud");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        categorias.put(getString(R.string.hosteleria), Constantes.categoriaHosteleria);
        categorias.put(getString(R.string.hoteles), Constantes.categoriaHotel);
        categorias.put(getString(R.string.ocio), Constantes.categoriaOcio);
        categorias.put(getString(R.string.transportes), Constantes.categoriaTransporte);
        categorias.put(getString(R.string.puntosInteres), Constantes.categoriaInteres);
        categorias.put(getString(R.string.sanidad), Constantes.categoriaSanidad);
        return inflater.inflate(R.layout.fragment_crear_lugar, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof CrearLugarFragment.OnFragmentInteractionListener) {
            mListener = (CrearLugarFragment.OnFragmentInteractionListener) activity;
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

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((TextView) getActivity().findViewById(R.id.locationNewLugar)).setText(Constantes.getCountryName(getActivity(), latidud, longitud));

        ((TextView) getActivity().findViewById(R.id.titulo)).setText(R.string.crearLugar);
        ((ImageView) getActivity().findViewById(R.id.imagenLugar)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Constantes.verifyCamera(getActivity()) && Constantes.verifyStoragePermissionsRead(getActivity()) && Constantes.verifyStoragePermissionsWrite(getActivity())) {
                    Constantes.elegirImagen(getActivity());
                }
            }
        });

        List<String> listaCategorias = new ArrayList<>();
        listaCategorias.add("");
        for (String key : categorias.keySet()) {
            listaCategorias.add(key);
        }

        Collections.sort(listaCategorias);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, listaCategorias);
        ((android.support.v7.widget.AppCompatSpinner) getActivity().findViewById(R.id.spinnerCategoria)).setAdapter(adapter);
        ((Button) getActivity().findViewById(R.id.botonCrear)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                crearLugar();
            }
        });
    }

    public void cambiarImagen(Uri imagen) {
        Constantes.ponerImagen((ImageView) getActivity().findViewById(R.id.imagenLugar), getActivity(), imagen.toString());
        try {
            imagenSeleccionada = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(),imagen);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void crearLugar() {
        String nombre = ((EditText) getActivity().findViewById(R.id.nombreLugar)).getText().toString();
        String descripcion = ((EditText) getActivity().findViewById(R.id.descripcionLugar)).getText().toString();
        if (imagenSeleccionada == null) {
            Toast.makeText(getActivity(), getString(R.string.error_imagen_lugar), Toast.LENGTH_LONG).show();
            return;
        }
        if (nombre.trim().length() == 0) {
            Constantes.ponerError((EditText) getActivity().findViewById(R.id.nombreLugar), getString(R.string.error_nombre_lugar), getActivity());
            return;
        }
        if (descripcion.trim().length() == 0) {
            Constantes.ponerError((EditText) getActivity().findViewById(R.id.descripcionLugar), getString(R.string.error_descripcion_lugar), getActivity());
            return;
        }
        String selected = String.valueOf(((android.support.v7.widget.AppCompatSpinner) getActivity().findViewById(R.id.spinnerCategoria)).getSelectedItem());
        if (selected.equals("")) {
            Toast.makeText(getActivity(), getString(R.string.error_categoria), Toast.LENGTH_LONG).show();
            return;
        }
        selected = categorias.get(selected);
        JSONObject object = new JSONObject();
        try {
            object.put("id_creador", mListener.getUsuarioActual().getIdUsuario());
            object.put("categoria_lugar", selected);
            object.put("nombre_lugar", nombre);
            object.put("descripcion_lugar", descripcion);
            object.put("latitud_lugar", latidud);
            object.put("longitud_lugar", longitud);
            object.put("valoracion_lugar", 0);
            object.put("imagen_lugar", Constantes.obtenerImagen(imagenSeleccionada));
            Constantes.insertarDato(getActivity(), GestorRemoto.reqSetLugares, object);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void cambiarFragment(Fragment fragment);
        Usuario getUsuarioActual();
    }
}
