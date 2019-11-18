package com.travelmaster.Fragment;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.travelmaster.Controlador.Constantes;
import com.travelmaster.R;

import java.util.List;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.content.Context.LOCATION_SERVICE;


public class MapCrearFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;

    MapView mMapView;

    private MapCrearFragment.OnFragmentInteractionListener mListener;

    LatLng coordenadasIniciales;


    public MapCrearFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment BuscarFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MapCrearFragment newInstance(LatLng coordinates) {
        MapCrearFragment fragment = new MapCrearFragment();
        Bundle args = new Bundle();
        args.putDouble("latitud", coordinates.latitude);
        args.putDouble("longitud", coordinates.longitude);
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
        View rootView = inflater.inflate(R.layout.fragment_map_crear, container, false);
        if(getArguments()!=null)
            coordenadasIniciales = new LatLng(getArguments().getDouble("latitud"), getArguments().getDouble("longitud"));
        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume(); // needed to get the map to display immediately
        mMapView.getMapAsync(this);

        ((TextView) getActivity().findViewById(R.id.titulo)).setText(R.string.crearLugarMapa);

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
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof BuscarFragment.OnFragmentInteractionListener) {
            mListener = (MapCrearFragment.OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof BuscarFragment.OnFragmentInteractionListener) {
            mListener = (MapCrearFragment.OnFragmentInteractionListener) activity;
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
        if(coordenadasIniciales != null){
            CameraPosition cameraPosition = new CameraPosition.Builder().target(coordenadasIniciales).zoom(15).build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }else{
            LocationManager locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);

            List<String> listaProvider = locationManager.getProviders(true);
            Location location = null, bestLocation = null;
            for(int i = 0; i < listaProvider.size(); i++){
                if(locationManager.isProviderEnabled(listaProvider.get(i))){
                    locationManager.requestLocationUpdates(
                            listaProvider.get(i),1,1, new LocationListener() {
                                @Override
                                public void onLocationChanged(Location location) {}

                                @Override
                                public void onStatusChanged(String s, int i, Bundle bundle) {}

                                @Override
                                public void onProviderEnabled(String s) {}

                                @Override
                                public void onProviderDisabled(String s) {}
                            });
                    location = locationManager.getLastKnownLocation(listaProvider.get(i));
                    if(location != null){
                        if(bestLocation == null || location.getAccuracy() > bestLocation.getAccuracy()){
                            bestLocation = location;
                        }
                    }
                }
            }
            if(bestLocation != null){
                LatLng coord = new LatLng(bestLocation.getLatitude(), bestLocation.getLongitude());
                CameraPosition cameraPosition = new CameraPosition.Builder().target(coord).zoom(15).build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        }

       googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng coord) {
               anadirMarcador(coord);
            }
        });
    }

    /**
     * Anade el marcador al mapa. Para ello se le pasa sus coordenadas.
     * Se mostrara un dialog para introducir el nombre del hospital y un boton de guardar y cancelar.
     * @param coord
     */
    private void anadirMarcador(final LatLng coord){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.anadir_lugar_titulo))
                .setMessage(getActivity().getString(R.string.crear_lugar_mensaje)+" \nLat: "+coord.latitude+"\nLng: "+coord.longitude)
                .setCancelable(true)
                .setPositiveButton(getString(R.string.crear), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mListener.cambiarFragment(CrearLugarFragment.newInstance(coord.latitude, coord.longitude));
                    }
                })
                .setNegativeButton(getString(R.string.cancelar), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).show();
    }





    /**
     * Al aceptar los permisos de geolocalizacion, se llamara a cargar el mapa de nuevo
     */
    public void permissionsAccept(){
        onMapReady(mMap);
    }
}
