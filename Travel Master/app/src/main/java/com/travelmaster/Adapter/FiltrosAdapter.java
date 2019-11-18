package com.travelmaster.Adapter;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.travelmaster.Controlador.Constantes;
import com.travelmaster.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Pc-Aitor on 09/05/2017.
 */

public class FiltrosAdapter extends RecyclerView.Adapter
{
    public ArrayList<HashMap<String, String>> list;
    Activity activity;
    HashMap<String, Integer> categoriesResources;
    /**
     * Crea el adapter
     * @param activity
     * @param list
     */
    public FiltrosAdapter(Activity activity, ArrayList<HashMap<String, String>> list) {
        super();
        this.activity = activity;
        this.list = list;
        categoriesResources = new HashMap<>();
        categoriesResources.put(Constantes.categoriaHotel, R.string.hoteles);
        categoriesResources.put(Constantes.categoriaHosteleria, R.string.hosteleria);
        categoriesResources.put(Constantes.categoriaOcio, R.string.ocio);
        categoriesResources.put(Constantes.categoriaSanidad, R.string.sanidad);
        categoriesResources.put(Constantes.categoriaInteres, R.string.puntosInteres);
        categoriesResources.put(Constantes.categoriaTransporte, R.string.transportes);
    }

    /**
     * Clase del holder
     */
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView nombre;
        public TextView nick;
        public ImageView imagen;
        public LinearLayout puntuaciones;
        public CardView card;
        public MyViewHolder(View view) {
            super(view);
            card = (CardView) view.findViewById(R.id.card);
            nombre = (TextView) view.findViewById(R.id.nombre);
            nick = (TextView) view.findViewById(R.id.nick);
            imagen = (ImageView) view.findViewById(R.id.imagen);
            puntuaciones = (LinearLayout) view.findViewById(R.id.valoracionLugar);
        }
    }

    /**
     * Obtiene el layout del holder y llama a su clase privada para crearlo
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View holder = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_busqueda, parent, false );
        return new MyViewHolder(holder);
    }

    /**
     * Accion que se realiza al cargar cada elemento.
     * Pone el valor a cada view y dependiente si esta seleccionado, lo pone de un color u otro
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        HashMap<String, String> hash = list.get(position);
        ((MyViewHolder) holder).nombre.setText(hash.get("Nombre"));
        ((MyViewHolder) holder).nick.setText(hash.get("Nick"));
        Constantes.ponerImagen(((MyViewHolder) holder).imagen, activity, hash.get("Imagen"));
        if(hash.containsKey("Valoracion")){
            ((MyViewHolder) holder).puntuaciones.removeAllViews();
            int valoracion = Integer.valueOf(hash.get("Valoracion"));
            for(int i = 0; i < 5; i++){
                ImageView imagen = new ImageView(activity);
                if(valoracion > i){
                    imagen.setTag(R.drawable.star_big_on);
                    imagen.setImageResource(R.drawable.star_big_on);
                }else{
                    imagen.setTag(R.drawable.star_big_off);
                    imagen.setImageResource(R.drawable.star_big_off);
                }
                ((MyViewHolder) holder).puntuaciones.addView(imagen);
            }
            ((MyViewHolder) holder).puntuaciones.setVisibility(View.VISIBLE);
        }else{
            ((MyViewHolder) holder).puntuaciones.setVisibility(View.GONE);
        }
        if(!hash.containsKey("Nick")){
            ((MyViewHolder) holder).nick.setText(hash.get("Nombre"));
            ((MyViewHolder) holder).nombre.setText(categoriesResources.get(hash.get("Categoria")));
        }
    }

    /**
     * Obtiene el identificador del item e la posicion
     * @param position
     * @return
     */
    @Override
    public long getItemId(int position) {
        return 0;
    }

    /**
     * Obtiene el numero de items totales
     * @return
     */
    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}