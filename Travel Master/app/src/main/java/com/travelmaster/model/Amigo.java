package com.travelmaster.model;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Pc-Aitor on 09/05/2017.
 */

public class Amigo extends RealmObject {

    @PrimaryKey
    private String idUnico;

    private int idUsuario;
    private int idAmigo;
    private boolean amigo;

    public String getIdUnico() {return idUnico;}
    public void setIdUnico(String idUnico) {this.idUnico = idUnico;}

    public int getIdUsuario() {return idUsuario;}
    public void setIdUsuario(int idUsuario) {this.idUsuario = idUsuario;}

    public int getIdAmigo() {return idAmigo;}
    public void setIdAmigo(int idAmigo) {this.idAmigo = idAmigo;}

    public boolean isAmigo() {return amigo;}
    public void setAmigo(boolean amigo) {this.amigo = amigo;}
}
