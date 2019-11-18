package com.travelmaster.Fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.travelmaster.Activity.MainActivity;
import com.travelmaster.Adapter.FiltrosAdapter;
import com.travelmaster.Controlador.Constantes;
import com.travelmaster.Listener.RecyclerTouchListener;
import com.travelmaster.R;
import com.travelmaster.model.Favorito;
import com.travelmaster.model.Lugar;
import com.travelmaster.model.Usuario;

import java.util.ArrayList;
import java.util.HashMap;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;


/**
 * @link https://github.com/arimorty/floatingsearchview/blob/master/README.md
 */
public class FavoritosFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    ArrayList<HashMap<String, String>> datos = new ArrayList<>();

    RecyclerView lista;

    FiltrosAdapter adapter;

    Realm realm;

    ArrayList<String> categorias = new ArrayList<>();

    public FavoritosFragment() {
        // Required empty public constructor
    }

    public static FavoritosFragment newInstance() {
        FavoritosFragment fragment = new FavoritosFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        realm = Realm.getInstance(getActivity());
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        categorias = Constantes.obtenerCategorias();
        return inflater.inflate(R.layout.fragment_favoritos, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        recogerDatosIniciales();
        final com.arlib.floatingsearchview.FloatingSearchView mSearchView = (com.arlib.floatingsearchview.FloatingSearchView) getActivity().findViewById(R.id.barraBusqueda);
        mSearchView.setOnMenuItemClickListener(new FloatingSearchView.OnMenuItemClickListener() {
           @Override
           public void onActionMenuItemSelected(MenuItem item) {
               item.setChecked(!item.isChecked());
               switch (item.getItemId()) {
                   case R.id.hoteles:
                       if (!item.isChecked()) {
                           categorias.remove(Constantes.categoriaHotel);
                       } else {
                           categorias.add(Constantes.categoriaHotel);
                       }
                       break;
                   case R.id.ocio:
                       if (!item.isChecked()) {
                           categorias.remove(Constantes.categoriaOcio);
                       } else {
                           categorias.add(Constantes.categoriaOcio);
                       }
                       break;
                   case R.id.hosteleria:
                       if (!item.isChecked()) {
                           categorias.remove(Constantes.categoriaHosteleria);
                       } else {
                           categorias.add(Constantes.categoriaHosteleria);
                       }
                       break;
                   case R.id.sanidad:
                       if (!item.isChecked()) {
                           categorias.remove(Constantes.categoriaSanidad);
                       } else {
                           categorias.add(Constantes.categoriaSanidad);
                       }
                       break;
                   case R.id.transportes:
                       if (!item.isChecked()) {
                           categorias.remove(Constantes.categoriaTransporte);
                       } else {
                           categorias.add(Constantes.categoriaTransporte);
                       }
                       break;
                   case R.id.interes:
                       if (!item.isChecked()) {
                           categorias.remove(Constantes.categoriaInteres);
                       } else {
                           categorias.add(Constantes.categoriaInteres);
                       }
                       break;
               }
               if(mSearchView.getQuery() == null || mSearchView.getQuery().trim().length() == 0){
                   recogerDatosIniciales();
               }else{
                   recogerDatosBusqueda(mSearchView.getQuery());
               }
               adapter.notifyDataSetChanged();
           }
       });
        lista = (RecyclerView) getActivity().findViewById(R.id.listaBusqueda);
        lista.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        lista.setItemAnimator(new DefaultItemAnimator());
        adapter = new FiltrosAdapter(getActivity(), datos);
        lista.setAdapter(adapter);
        lista.addOnItemTouchListener(new RecyclerTouchListener(getActivity().getApplicationContext(),
                lista, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int i) {
                ((MainActivity) getActivity()).cambiarFragment(LugarFragment.newInstance(Integer.valueOf(datos.get(i).get("Id"))));
            }

            @Override
            public void onLongClick(View view, final int i) {

            }
        }));
        mSearchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
            @Override
            public void onSearchTextChanged(String oldQuery, String newQuery) {
                if(newQuery.trim().length() > 0){
                    recogerDatosBusqueda(newQuery);
                }else{
                    recogerDatosIniciales();
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void recogerDatosBusqueda(String busqueda){
        datos.clear();
        RealmResults<Favorito> favoritos = realm.where(Favorito.class).equalTo("idUsuario", mListener.getUsuarioActual().getIdUsuario()).equalTo("favorito", true).findAll();
        RealmQuery<Lugar> query = realm.where(Lugar.class).contains("nombreLugar",busqueda, RealmQuery.CASE_INSENSITIVE).beginGroup();
        boolean primero = true;
        for(Favorito favorito : favoritos){
            if(!primero) query = query.or();
            primero = false;
            query = query.equalTo("idLugar", favorito.getIdLugar());
        }
        query = query.endGroup();
        if(categorias.size() > 0){
            query = query.beginGroup();
            primero = true;
            for(String categoria : categorias){
                if(!primero) query = query.or();
                primero = false;
                query = query.equalTo("categoria", categoria);
            }
            query = query.endGroup();
        }
        RealmResults<Lugar> results = query.findAllSorted("nombreLugar");
        for(Lugar lugar : results){
            HashMap<String, String> hash = new HashMap<>();
            hash.put("Nombre", lugar.getNombreLugar());
            hash.put("Imagen", lugar.getImagenLugar());
            hash.put("Categoria", lugar.getCategoria());
            hash.put("Valoracion", String.valueOf(lugar.getValoracionLugar()));
            hash.put("Id", ""+lugar.getIdLugar());
            datos.add(hash);
        }
        if(adapter != null){
            adapter.notifyDataSetChanged();
        }
    }

    private void recogerDatosIniciales(){
        datos.clear();
        RealmResults<Favorito> favoritos = realm.where(Favorito.class).equalTo("idUsuario", mListener.getUsuarioActual().getIdUsuario()).equalTo("favorito", true).findAll();
        RealmQuery<Lugar> query = realm.where(Lugar.class).beginGroup();
        boolean primero = true;
        for(Favorito favorito : favoritos){
            if(!primero) query = query.or();
            primero = false;
            query = query.equalTo("idLugar", favorito.getIdLugar());
        }
        query = query.endGroup();
        if(categorias.size() > 0){
            query = query.beginGroup();
            primero = true;
            for(String categoria : categorias){
                if(!primero) query = query.or();
                primero = false;
                query = query.equalTo("categoria", categoria);
            }
            query = query.endGroup();
        }
        RealmResults<Lugar> results = query.findAllSorted("nombreLugar");
        for(Lugar lugar : results){
            HashMap<String, String> hash = new HashMap<>();
            hash.put("Nombre", lugar.getNombreLugar());
            hash.put("Imagen", lugar.getImagenLugar());
            hash.put("Categoria", lugar.getCategoria());
            hash.put("Valoracion", String.valueOf(lugar.getValoracionLugar()));
            hash.put("Id", ""+lugar.getIdLugar());
            datos.add(hash);
        }
        if(adapter != null){
            adapter.notifyDataSetChanged();
        }
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
        if (activity instanceof FavoritosFragment.OnFragmentInteractionListener) {
            mListener = (FavoritosFragment.OnFragmentInteractionListener) activity;
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
    }
}
