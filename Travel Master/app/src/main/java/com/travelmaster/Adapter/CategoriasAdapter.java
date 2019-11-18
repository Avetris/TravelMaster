package com.travelmaster.Adapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.travelmaster.Controlador.Constantes;
import com.travelmaster.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Pc-Aitor on 09/05/2017.
 */

public class CategoriasAdapter extends RecyclerView.Adapter
{
    public ArrayList<HashMap<String, String>> list;
    Activity activity;

    /**
     * Crea el adapter
     * @param activity
     * @param list
     */
    public CategoriasAdapter(Activity activity, ArrayList<HashMap<String, String>> list) {
        super();
        this.activity = activity;
        this.list = list;
    }

    /**
     * Clase del holder
     */
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView nombre;
        public ImageView imagen;
        public CardView card;
        public MyViewHolder(View view) {
            super(view);
            card = (CardView) view.findViewById(R.id.card);
            nombre = (TextView) view.findViewById(R.id.nombre);
            imagen = (ImageView) view.findViewById(R.id.imagen);
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
        View holder = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista, parent, false );
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
        Constantes.ponerImagen(((MyViewHolder) holder).imagen, activity, hash.get("Imagen"));
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