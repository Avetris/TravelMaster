package com.travelmaster.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Pc-Aitor on 11/05/2017.
 */

public class Favorito extends RealmObject {

    @PrimaryKey
    private String idUnico;

    private int idUsuario;
    private int idLugar;
    private boolean favorito;

    public String getIdUnico() {return idUnico;}
    public void setIdUnico(String idUnico) {this.idUnico = idUnico;}

    public int getIdUsuario() {return idUsuario;}
    public void setIdUsuario(int idUsuario) {this.idUsuario = idUsuario;}

    public int getIdLugar() {return idLugar;}
    public void setIdLugar(int idLugar) {this.idLugar = idLugar;}

    public boolean isFavorito() {return favorito;}

    public void setFavorito(boolean favorito) {
        this.favorito = favorito;
    }
}
