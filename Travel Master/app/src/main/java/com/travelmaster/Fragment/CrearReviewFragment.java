package com.travelmaster.Fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.travelmaster.Controlador.Constantes;
import com.travelmaster.Datos.GestorRemoto;
import com.travelmaster.R;
import com.travelmaster.model.Usuario;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CrearReviewFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CrearReviewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CrearReviewFragment extends Fragment {


    private OnFragmentInteractionListener mListener;

    List<CheckBox> valoracion = new ArrayList<>();

    public CrearReviewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment CrearReview.
     */
    // TODO: Rename and change types and number of parameters
    public static CrearReviewFragment newInstance(int idLugar) {
        CrearReviewFragment fragment = new CrearReviewFragment();
        Bundle args = new Bundle();
        args.putInt("IdLugar", idLugar);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_crear_review, container, false);
    }

    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        valoracion.add((CheckBox) getActivity().findViewById(R.id.valoracion1));
        valoracion.add((CheckBox) getActivity().findViewById(R.id.valoracion2));
        valoracion.add((CheckBox) getActivity().findViewById(R.id.valoracion3));
        valoracion.add((CheckBox) getActivity().findViewById(R.id.valoracion4));
        valoracion.add((CheckBox) getActivity().findViewById(R.id.valoracion5));
        for(int i = 0; i < valoracion.size(); i++){
            final int pos = i;
            valoracion.get(i).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        for(int i = 0; i < pos; i++){
                            valoracion.get(i).setChecked(true);
                        }
                    }else{
                        for(int i = pos; i < valoracion.size(); i++){
                            valoracion.get(i).setChecked(false);
                        }
                    }
                }
            });
        }
        ((Button) getActivity().findViewById(R.id.botonCrear)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nombre = ((EditText) getActivity().findViewById(R.id.nombreReview)).getText().toString();
                String descripcion = ((EditText) getActivity().findViewById(R.id.descripcionReview)).getText().toString();
                if (nombre.trim().length() == 0) {
                    Constantes.ponerError((EditText) getActivity().findViewById(R.id.nombreReview), getString(R.string.error_nombre_review), getActivity());
                    return;
                }
                JSONObject object = new JSONObject();
                int val = 0;
                for(CheckBox valo : valoracion){
                    if(valo.isChecked()){
                        val++;
                    }
                }
                try {
                    object.put("id_usuario", mListener.getUsuarioActual().getIdUsuario());
                    object.put("id_lugar", getArguments().getInt("IdLugar"));
                    object.put("titulo_review", nombre);
                    object.put("descripcion_review", descripcion);
                    object.put("valoracion_review", val);
                    Constantes.insertarDato(getActivity(), GestorRemoto.reqSetReviews, object);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof CrearReviewFragment.OnFragmentInteractionListener) {
            mListener = (CrearReviewFragment.OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof CrearReviewFragment.OnFragmentInteractionListener) {
            mListener = (CrearReviewFragment.OnFragmentInteractionListener) activity;
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
        void cambiarFragment(android.app.Fragment fragment);
        Usuario getUsuarioActual();
    }
}
