package com.travelmaster.Fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.travelmaster.Controlador.Constantes;
import com.travelmaster.Datos.GestorRemoto;
import com.travelmaster.Datos.SHA1;
import com.travelmaster.R;
import com.travelmaster.model.Usuario;

import org.json.JSONException;
import org.json.JSONObject;

import io.realm.Realm;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ModificarContrasenaFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ModificarContrasenaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ModificarContrasenaFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    Realm realm;

    public ModificarContrasenaFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment CambiarDatosFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ModificarContrasenaFragment newInstance() {
        ModificarContrasenaFragment fragment = new ModificarContrasenaFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        realm = Realm.getInstance(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_modificar_contrasena, container, false);
    }

    public void onActivityCreated(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        ((TextView) getActivity().findViewById(R.id.titulo)).setText(R.string.cambiarContrasena);
        ((Button) getActivity().findViewById(R.id.guardar)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String contrasena = ((EditText) getActivity().findViewById(R.id.contrasena)).getText().toString();
                String repetirContrasena = ((EditText) getActivity().findViewById(R.id.repite_contrasena)).getText().toString();
                String error = gestionarContrasena(contrasena);
                if(error !=  null){
                    Constantes.ponerError(((EditText) getActivity().findViewById(R.id.contrasena)),error, getActivity());
                    return;
                }
                if(!repetirContrasena.equals(contrasena)){
                    Constantes.ponerError(((EditText) getActivity().findViewById(R.id.repite_contrasena)), getString(R.string.error_misma_contrasena), getActivity());
                    return;
                }
                JSONObject object = new JSONObject();
                try {
                    object.put("id_usuario", mListener.getUsuarioActual().getIdUsuario());
                    object.put("contrasena_usuario", SHA1.getStringMensageDigest(contrasena));
                    Constantes.insertarDato(getActivity(), GestorRemoto.reqSetUsuarioContrasena, object);
                } catch (JSONException e) {
                    e.printStackTrace();
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
    }
}
