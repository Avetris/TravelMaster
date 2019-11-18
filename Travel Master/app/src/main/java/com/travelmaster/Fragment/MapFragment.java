package com.travelmaster.Fragment;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.travelmaster.Controlador.Constantes;
import com.travelmaster.R;
import com.travelmaster.model.Amigo;
import com.travelmaster.model.Lugar;
import com.travelmaster.model.Usuario;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.content.Context.LOCATION_SERVICE;


public class MapFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;

    MapView mMapView;

    com.arlib.floatingsearchview.FloatingSearchView mSearchView;

    private MapFragment.OnFragmentInteractionListener mListener;

    List<HashMap<String, Object>> markers = new ArrayList<>();

    Realm realm;

    RealmResults<Lugar> lugares;

    ArrayList<String> categorias = new ArrayList<>();

    HashMap<String, Bitmap> categoriesResources = new HashMap<>();



    boolean usuario = false, lugar = false;
    Integer id;


    public MapFragment() {
        // Required empty public constructor
    }

    public static MapFragment newInstance(Integer id, String key) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        if(id != null && key != null) args.putInt(key, id);
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
        if(getArguments() != null){
            usuario = getArguments().containsKey("idUsuario");
            lugar = getArguments().containsKey("idLugar");
            if(usuario) id = getArguments().getInt("idUsuario");
            else if(lugar) id = getArguments().getInt("idLugar");
        }
        obtenerLugares(true);
        categoriesResources.put(Constantes.categoriaHotel, crearBitmap(R.drawable.hotel));
        categoriesResources.put(Constantes.categoriaHosteleria, crearBitmap(R.drawable.hosteleria));
        categoriesResources.put(Constantes.categoriaOcio, crearBitmap(R.drawable.ocio));
        categoriesResources.put(Constantes.categoriaSanidad, crearBitmap(R.drawable.sanidad));
        categoriesResources.put(Constantes.categoriaInteres, crearBitmap(R.drawable.interes));
        categoriesResources.put(Constantes.categoriaTransporte, crearBitmap(R.drawable.transporte));
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);
        categorias = Constantes.obtenerCategorias();

        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        return rootView;
    }

    private Bitmap crearBitmap(int resource){
        int height = 150;
        int width = 150;
        BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(resource);
        Bitmap b=bitmapdraw.getBitmap();
        return Bitmap.createScaledBitmap(b, width, height, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume(); // needed to get the map to display immediately
        mMapView.getMapAsync(this);
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getChildFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                CameraPosition cameraPosition = new CameraPosition.Builder().target(place.getLatLng()).zoom(15).build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("T", "An error occurred: " + status);
            }
        });

        if(!lugar){
            final Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbarMap);
            toolbar.inflateMenu(R.menu.mapa);
            toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if(item.getItemId() == R.id.actualLocation){
                        localizar();
                    }else {
                        item.setChecked(!item.isChecked());
                        switch (item.getItemId()) {
                            case R.id.todos:
                                for (int i = 0; i < toolbar.getMenu().size(); i++) {
                                    if (toolbar.getMenu().getItem(i).getItemId() != R.id.soloSiguiendo && toolbar.getMenu().getItem(i).getItemId() != R.id.todos) {
                                        toolbar.getMenu().getItem(i).setChecked(item.isChecked());
                                    }
                                }
                                if(item.isChecked()){
                                    categorias =Constantes.obtenerCategorias();
                                }else{
                                    categorias.clear();
                                }
                                break;
                            case R.id.hoteles:
                                if (!item.isChecked()) {
                                    toolbar.getMenu().findItem(R.id.todos).setChecked(false);
                                    categorias.remove(Constantes.categoriaHotel);
                                } else {
                                    categorias.add(Constantes.categoriaHotel);
                                }
                                break;
                            case R.id.ocio:
                                if (!item.isChecked()) {
                                    toolbar.getMenu().findItem(R.id.todos).setChecked(false);
                                    categorias.remove(Constantes.categoriaOcio);
                                } else {
                                    categorias.add(Constantes.categoriaOcio);
                                }
                                break;
                            case R.id.hosteleria:
                                if (!item.isChecked()) {
                                    toolbar.getMenu().findItem(R.id.todos).setChecked(false);
                                    categorias.remove(Constantes.categoriaHosteleria);
                                } else {
                                    categorias.add(Constantes.categoriaHosteleria);
                                }
                                break;
                            case R.id.sanidad:
                                if (!item.isChecked()) {
                                    toolbar.getMenu().findItem(R.id.todos).setChecked(false);
                                    categorias.remove(Constantes.categoriaSanidad);
                                } else {
                                    categorias.add(Constantes.categoriaSanidad);
                                }
                                break;
                            case R.id.transportes:
                                if (!item.isChecked()) {
                                    toolbar.getMenu().findItem(R.id.todos).setChecked(false);
                                    categorias.remove(Constantes.categoriaTransporte);
                                } else {
                                    categorias.add(Constantes.categoriaTransporte);
                                }
                                break;
                            case R.id.interes:
                                if (!item.isChecked()) {
                                    toolbar.getMenu().findItem(R.id.todos).setChecked(false);
                                    categorias.remove(Constantes.categoriaInteres);
                                } else {
                                    categorias.add(Constantes.categoriaInteres);
                                }
                                break;
                        }
                        obtenerLugares(toolbar.getMenu().findItem(R.id.soloSiguiendo).isChecked());
                        posicionarLugares();
                    }
                    return false;
                }
            });

            if(usuario){
                ((android.support.design.widget.FloatingActionButton) getActivity().findViewById(R.id.botonCrear)).setVisibility(View.GONE);
                toolbar.getMenu().findItem(R.id.soloSiguiendo).setVisible(false);
            }else{
                ((android.support.design.widget.FloatingActionButton) getActivity().findViewById(R.id.botonCrear)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mListener.cambiarFragment(MapCrearFragment.newInstance(mMap.getCameraPosition().target));
                    }
                });
            }

        }else if(getArguments().containsKey("idLugar")){
            ((android.support.design.widget.FloatingActionButton) getActivity().findViewById(R.id.botonCrear)).setVisibility(View.GONE);
            getActivity().findViewById(R.id.toolbarMap).setVisibility(View.GONE);
        }

    }

    private void obtenerLugares(boolean soloAmigos){
        RealmQuery query = realm.where(Lugar.class);
        if(lugar){
            query.equalTo("idLugar", id);
        }else{
            if(usuario){
                query.equalTo("idCreador", id);
            }else{
                if(soloAmigos && mListener.getUsuarioActual() != null){
                    RealmResults<Amigo> amigos = realm.where(Amigo.class).equalTo("idUsuario", mListener.getUsuarioActual().getIdUsuario()).findAll();
                    if(amigos.size() > 0) {
                        query.beginGroup();
                        for (int i = 0; i < amigos.size(); i++) {
                            if (i != 0) query.or();
                            query.equalTo("idCreador", amigos.get(i).getIdAmigo());
                        }
                        query.endGroup();
                    }
                }
            }
            if(categorias.size() > 0) {
                query.beginGroup();
                for (int i = 0; i < categorias.size(); i++) {
                    if (i != 0) query.or();
                    query.equalTo("categoria", categorias.get(i));
                }
                query.endGroup();
            }
        }
        lugares = query.findAll();
    }

    private void posicionarLugares(){
        mMap.clear();
        if(lugares != null){
            for(int i = 0; i < lugares.size(); i++){

                mMap.addMarker(new MarkerOptions().position(new LatLng(lugares.get(i).getLatitud(), lugares.get(i).getLongitud()))
                        .title(lugares.get(i).getNombreLugar()).draggable(false)
                        .icon(BitmapDescriptorFactory.fromBitmap(categoriesResources.get(lugares.get(i).getCategoria()))).snippet(String.valueOf(lugares.get(i).getIdLugar())));
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof BuscarFragment.OnFragmentInteractionListener) {
            mListener = (MapFragment.OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof BuscarFragment.OnFragmentInteractionListener) {
            mListener = (MapFragment.OnFragmentInteractionListener) activity;
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
        void cambiarFragment(Fragment fragment);
        Usuario getUsuarioActual();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        localizar();

        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                mListener.cambiarFragment(LugarFragment.newInstance(Integer.valueOf(marker.getSnippet())));
                return false;
            }
        });
        if(!lugar || lugares.size() > 0){
            posicionarLugares();
        }else{
            LatLng coord = new LatLng(lugares.get(0).getLatitud(), lugares.get(0).getLongitud());
            CameraPosition cameraPosition = new CameraPosition.Builder().target(coord).zoom(15).build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    private void localizar(){
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{ACCESS_FINE_LOCATION},
                    Constantes.REQUEST_LOCATION
            );
            return;
        }
        mMap.setMyLocationEnabled(true);
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);

        if(!lugar && (!usuario ||lugares == null || lugares.size()==0)) {
            List<String> listaProvider = locationManager.getProviders(true);
            Location location = null, bestLocation = null;
            for (int i = 0; i < listaProvider.size(); i++) {
                if (locationManager.isProviderEnabled(listaProvider.get(i))) {
                    locationManager.requestLocationUpdates(
                            listaProvider.get(i), 1, 1, new LocationListener() {
                                @Override
                                public void onLocationChanged(Location location) {
                                }

                                @Override
                                public void onStatusChanged(String s, int i, Bundle bundle) {
                                }

                                @Override
                                public void onProviderEnabled(String s) {
                                }

                                @Override
                                public void onProviderDisabled(String s) {
                                }
                            });
                    location = locationManager.getLastKnownLocation(listaProvider.get(i));
                    if (location != null) {
                        if (bestLocation == null || location.getAccuracy() > bestLocation.getAccuracy()) {
                            bestLocation = location;
                        }
                    }
                }
            }
            if (bestLocation != null) {
                LatLng coord = new LatLng(bestLocation.getLatitude(), bestLocation.getLongitude());
                CameraPosition cameraPosition = new CameraPosition.Builder().target(coord).zoom(15).build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        }else{
            LatLng coord = new LatLng(lugares.get(0).getLatitud(), lugares.get(0).getLongitud());
            CameraPosition cameraPosition = new CameraPosition.Builder().target(coord).zoom(15).build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        }
    }


    /**
     * Al aceptar los permisos de geolocalizacion, se llamara a cargar el mapa de nuevo
     */
    public void permissionsAccept(){
        onMapReady(mMap);
    }
}
