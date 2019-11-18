package com.travelmaster.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.travelmaster.Controlador.Constantes;
import com.travelmaster.Datos.GestorRemoto;
import com.travelmaster.R;
import com.travelmaster.model.Usuario;

import org.json.JSONException;
import org.json.JSONObject;

import io.realm.Realm;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ModificarDescripcionFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ModificarDescripcionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ModificarDescripcionFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    Realm realm;

    public ModificarDescripcionFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment CambiarDatosFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ModificarDescripcionFragment newInstance() {
        ModificarDescripcionFragment fragment = new ModificarDescripcionFragment();
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
        return inflater.inflate(R.layout.fragment_modificar_descripcion, container, false);
    }

    public void onActivityCreated(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        ((TextView) getActivity().findViewById(R.id.titulo)).setText(R.string.cambiarDescripcion);
        ((EditText) getActivity().findViewById(R.id.descripcion)).setText(mListener.getUsuarioActual().getDescripcion());
        ((Button) getActivity().findViewById(R.id.guardar)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                JSONObject object = new JSONObject();
                try {
                    object.put("id_usuario", mListener.getUsuarioActual().getIdUsuario());
                    object.put("descripcion_usuario", ((EditText) getActivity().findViewById(R.id.descripcion)).getText().toString());
                    Constantes.insertarDato(getActivity(), GestorRemoto.reqSetUsuariosDescripcion, object);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
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
