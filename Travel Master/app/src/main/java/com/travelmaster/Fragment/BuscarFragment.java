package com.travelmaster.Fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.travelmaster.Activity.MainActivity;
import com.travelmaster.Adapter.FiltrosAdapter;
import com.travelmaster.Listener.RecyclerTouchListener;
import com.travelmaster.R;
import com.travelmaster.model.Amigo;
import com.travelmaster.model.Categoria;
import com.travelmaster.model.Lugar;
import com.travelmaster.model.Usuario;

import java.util.ArrayList;
import java.util.HashMap;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.RealmResults;


/**
 * @link https://github.com/arimorty/floatingsearchview/blob/master/README.md
 */
public class BuscarFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    ArrayList<HashMap<String, String>> datos = new ArrayList<>();

    RecyclerView lista;

    FiltrosAdapter adapter;

    Realm realm;

    Class claseActual;

    public BuscarFragment() {
        // Required empty public constructor
    }

    public static BuscarFragment newInstance() {
        BuscarFragment fragment = new BuscarFragment();
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
        return inflater.inflate(R.layout.fragment_buscar, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        claseActual = Usuario.class;
        recogerDatosIniciales();
        final com.arlib.floatingsearchview.FloatingSearchView mSearchView = (com.arlib.floatingsearchview.FloatingSearchView) getActivity().findViewById(R.id.barraBusqueda);
        final BottomNavigationView bottomNavigation =(BottomNavigationView) getActivity().findViewById(R.id.barraFiltros);
        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(!item.isChecked()){
                    switch (item.getItemId()){
                        case R.id.filtro_personas:
                            claseActual = Usuario.class;
                            recogerDatosIniciales();
                            break;
                        case R.id.filtro_lugares:
                            claseActual = Lugar.class;
                            recogerDatosIniciales();
                            break;
                    }
                    mSearchView.clearQuery();
                }
                return true;
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
                if(view.findViewById(R.id.valoracionLugar).getVisibility() == View.GONE){
                    ((MainActivity) getActivity()).cambiarFragment(PerfilFragment.newInstance(Integer.valueOf(datos.get(i).get("Id"))));
                }else{
                    ((MainActivity) getActivity()).cambiarFragment(LugarFragment.newInstance(Integer.valueOf(datos.get(i).get("Id"))));
                }
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
        RealmResults<Amigo> amigos = realm.where(Amigo.class).equalTo("idUsuario", mListener.getUsuarioActual().getIdUsuario()).equalTo("amigo", true).findAll();
        RealmQuery<Usuario> query = realm.where(Usuario.class).equalTo("actual", false);
        if(claseActual == Usuario.class) query = query.beginGroup().contains("nombre",busqueda, RealmQuery.CASE_INSENSITIVE).or().contains("nick",busqueda, RealmQuery.CASE_INSENSITIVE).endGroup();
        boolean primero = true;
        RealmResults<Usuario> usuarios = query.findAllSorted("nick");
        if(claseActual == Usuario.class){
            for(Usuario usuario : usuarios){
                HashMap<String, String> hash = new HashMap<>();
                hash.put("Nick", usuario.getNick());
                hash.put("Nombre", usuario.getNombre());
                if(usuario.getImagen() != null && usuario.getImagen().trim().length() > 0)
                    hash.put("Imagen", usuario.getImagen());
                else
                    hash.put("Imagen", "drawable://"+R.drawable.ic_account_circle_black_36dp);
                hash.put("Id", ""+usuario.getIdUsuario());
                datos.add(hash);
            }
        }else if(claseActual == Lugar.class){
            RealmResults<Lugar> results = realm.where(Lugar.class).contains("nombreLugar",busqueda, RealmQuery.CASE_INSENSITIVE).findAllSorted("nombreLugar");
            for(Lugar lugar : results){
                HashMap<String, String> hash = new HashMap<>();
                hash.put("Nombre", lugar.getNombreLugar());
                hash.put("Categoria", lugar.getCategoria());
                hash.put("Imagen", lugar.getImagenLugar());
                hash.put("Valoracion", String.valueOf(lugar.getValoracionLugar()));
                hash.put("Id", ""+lugar.getIdLugar());
                datos.add(hash);
            }
        }
        if(adapter != null){
            adapter.notifyDataSetChanged();
        }
    }

    private void recogerDatosIniciales(){
        datos.clear();
        RealmResults<Amigo> amigos = realm.where(Amigo.class).equalTo("idUsuario", mListener.getUsuarioActual().getIdUsuario()).findAll();
        RealmQuery<Usuario> query = realm.where(Usuario.class).equalTo("actual", false);
        boolean primero = true;
        for(Amigo amigo : amigos){
            if(!primero) query=query.or();
            query=query.equalTo("idUsuario", amigo.getIdAmigo());
            primero = false;
        }
        RealmResults<Usuario> usuarios = query.findAllSorted("nick");
        if(claseActual != Usuario.class){
            for(Usuario usuario : usuarios){
                HashMap<String, String> hash = new HashMap<>();
                hash.put("Nick", usuario.getNick());
                hash.put("Nombre", usuario.getNombre());
                if(usuario.getImagen() != null && usuario.getImagen().trim().length() > 0)
                    hash.put("Imagen", usuario.getImagen());
                else
                    hash.put("Imagen", "drawable://"+R.drawable.ic_account_circle_black_36dp);
                hash.put("Id", ""+usuario.getIdUsuario());
                datos.add(hash);
            }
        }else if(claseActual != Lugar.class){
            RealmQuery<Lugar> queryLugar = realm.where(Lugar.class);
            primero = true;
            for(Usuario usuario : usuarios){
                if(!primero) queryLugar=queryLugar.or();
                queryLugar=queryLugar.equalTo("idCreador", usuario.getIdUsuario());
                primero = false;
            }
            RealmResults<Lugar> results = queryLugar.findAllSorted("nombreLugar");
            for(Lugar lugar : results){
                HashMap<String, String> hash = new HashMap<>();
                hash.put("Nombre", lugar.getNombreLugar());
                hash.put("Categoria", lugar.getCategoria());
                hash.put("Imagen", lugar.getImagenLugar());
                hash.put("Valoracion", String.valueOf(lugar.getValoracionLugar()));
                hash.put("Id", ""+lugar.getIdLugar());
                datos.add(hash);
            }
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
        if (activity instanceof BuscarFragment.OnFragmentInteractionListener) {
            mListener = (BuscarFragment.OnFragmentInteractionListener) activity;
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
