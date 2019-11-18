package com.travelmaster.Fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.travelmaster.Controlador.Constantes;
import com.travelmaster.Datos.GestorRemoto;
import com.travelmaster.R;
import com.travelmaster.model.Favorito;
import com.travelmaster.model.Lugar;
import com.travelmaster.model.Review;
import com.travelmaster.model.Usuario;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

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
public class LugarFragment extends Fragment {

    Lugar lugar;
    RealmResults<Review> reviews;
    OnFragmentInteractionListener mListener;
    ArrayList<HashMap<String, String>> array;
    Realm realm;
    boolean like = false;

    public LugarFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment BuscarFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LugarFragment newInstance(int idLugar) {
        LugarFragment fragment = new LugarFragment();
        Bundle args = new Bundle();
        args.putInt("idLugar", idLugar);
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

        return inflater.inflate(R.layout.fragment_lugar, container, false);
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
        if (activity instanceof LugarFragment.OnFragmentInteractionListener) {
            mListener = (LugarFragment.OnFragmentInteractionListener) activity;
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

    public void onActivityCreated(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        load();
    }

    public void load(){
        realm = Realm.getInstance(getActivity());
        lugar = realm.where(Lugar.class).equalTo("idLugar", getArguments().getInt("idLugar")).findFirst();
        reviews = realm.where(Review.class).equalTo("idLugar", lugar.getIdLugar()).findAll();
        Favorito fav = realm.where(Favorito.class).equalTo("idUsuario", mListener.getUsuarioActual().getIdUsuario()).equalTo("idLugar", lugar.getIdLugar()).equalTo("favorito", true).findFirst();
        like = fav != null;
        ArrayList<HashMap<String, String>> arrayAux = new ArrayList<>();
        for(Review review : reviews){
            Usuario user = realm.where(Usuario.class).equalTo("idUsuario", review.getIdUsuario()).findFirst();
            HashMap<String, String> hash = new HashMap<>();
            hash.put("Nombre", user.getNick());
            hash.put("Titulo", review.getTitulo());
            hash.put("Descripcion", review.getDescripcionReview());
            hash.put("Valoracion", String.valueOf(review.getValoracion()));
            arrayAux.add(hash);
        }

        array = arrayAux;
        ((TextView) getActivity().findViewById(R.id.titulo)).setText(lugar.getNombreLugar());
        ((TextView) getActivity().findViewById(R.id.locationLugar)).setText(Constantes.getCountryName(getActivity(), lugar.getLatitud(), lugar.getLongitud()));
        ((TextView) getActivity().findViewById(R.id.descripcionLugar)).setText(lugar.getDescripcionLugar());
        Constantes.ponerImagen(((ImageView) getActivity().findViewById(R.id.imagenLugar)), getActivity(), lugar.getImagenLugar());
        RealmResults<Favorito> favoritos = realm.where(Favorito.class).equalTo("idLugar", lugar.getIdLugar()).equalTo("favorito", true).findAll();
        ((TextView) getActivity().findViewById(R.id.meGustas)).setText(favoritos.size()+" "+getString(R.string.gustaPersonas));
        ((LinearLayout) getActivity().findViewById(R.id.valoracionLugar)).removeAllViews();
        for(int i = 0; i < 5; i++){
            ImageView imagen = new ImageView(getActivity());
            if(lugar.getValoracionLugar() > i){
                Constantes.ponerImagen(imagen, getActivity(), "drawable://"+R.drawable.star_big_on);
            }else{
                Constantes.ponerImagen(imagen, getActivity(), "drawable://"+R.drawable.star_big_off);
            }
            ((LinearLayout) getActivity().findViewById(R.id.valoracionLugar)).addView(imagen);
        }

        LinearLayout reviewLayout = (LinearLayout) getActivity().findViewById(R.id.reviews);
        reviewLayout.removeAllViews();
        for(int i = 0; i < array.size(); i++){
            View reviewView = View.inflate(getActivity(), R.layout.lista_reviews, null);
            ((TextView) reviewView.findViewById(R.id.nombre)).setText(array.get(i).get("Nombre"));
            ((TextView) reviewView.findViewById(R.id.titulo)).setText(array.get(i).get("Titulo"));
            ((TextView) reviewView.findViewById(R.id.descripcionReview)).setText(array.get(i).get("Descripcion"));

            int valoracionReview = Integer.valueOf(array.get(i).get("Valoracion"));
            ((LinearLayout) reviewView.findViewById(R.id.valoracionReview)).removeAllViews();
            for(int j = 0; j < 5; j++){
                ImageView imagen = new ImageView(getActivity());
                if(Integer.valueOf(array.get(i).get("Valoracion")) > j){
                    Constantes.ponerImagen(imagen, getActivity(), "drawable://"+R.drawable.star_big_on);
                }else{
                    Constantes.ponerImagen(imagen, getActivity(), "drawable://"+R.drawable.star_big_off);
                }
                ((LinearLayout) reviewView.findViewById(R.id.valoracionReview)).addView(imagen);
            }
            reviewLayout.addView(reviewView);
        }
        final ImageButton meGusta = (ImageButton) getActivity().findViewById(R.id.like);
        if(like){
            meGusta.setImageResource(R.drawable.ic_favorite_black_36dp);
        }else{
            meGusta.setImageResource(R.drawable.ic_favorite_border_black_36dp);
        }
        meGusta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject object = new JSONObject();
                Usuario usuarioActual = realm.where(Usuario.class).equalTo("actual", true).findFirst();
                try {
                    object.put("id_usuario", mListener.getUsuarioActual().getIdUsuario());
                    object.put("id_lugar", lugar.getIdLugar());
                    object.put("estado_favorito", like ? 1 : 0);
                    Constantes.insertarDato(getActivity(), GestorRemoto.reqSetFavoritos, object);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        ((ImageButton) getActivity().findViewById(R.id.localizar)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.cambiarFragment(MapFragment.newInstance(lugar.getIdLugar(), "idLugar"));
            }
        });
        android.support.design.widget.FloatingActionButton crearReview = (android.support.design.widget.FloatingActionButton) getActivity().findViewById(R.id.crearReview);
        if(lugar.getIdCreador() == mListener.getUsuarioActual().getIdUsuario()){
            ((ImageButton) getActivity().findViewById(R.id.creador)).setVisibility(View.GONE);
            crearReview.setVisibility(View.GONE);
        }else{
            ((ImageButton) getActivity().findViewById(R.id.creador)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.cambiarFragment(PerfilFragment.newInstance(lugar.getIdCreador()));
                }
            });
            boolean reviewHecha = false;
            for(Review review : reviews){
                if(review.getIdUsuario() == mListener.getUsuarioActual().getIdUsuario()){
                    reviewHecha = true;
                }
            }
            if(reviewHecha){
                crearReview.setVisibility(View.GONE);
            }else{
                crearReview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mListener.cambiarFragment(CrearReviewFragment.newInstance(lugar.getIdLugar()));
                    }
                });
            }
        }
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
