package com.travelmaster.model;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Pc-Aitor on 09/05/2017.
 */

public class Review extends RealmObject {

    @PrimaryKey
    private String idUnico;

    private int idUsuario;
    private int idLugar;
    private String titulo;
    private String descripcionReview;
    private int valoracion;

    public String getIdUnico() {return idUnico;}
    public void setIdUnico(String idUnico) {this.idUnico = idUnico;}

    public int getIdUsuario() {return idUsuario;}
    public void setIdUsuario(int idUsuario) {this.idUsuario = idUsuario;}

    public int getIdLugar() {return idLugar;}
    public void setIdLugar(int idLugar) {this.idLugar = idLugar;}

    public String getTitulo() {return titulo;}
    public void setTitulo(String titulo) {this.titulo = titulo;}

    public String getDescripcionReview() {return descripcionReview;}
    public void setDescripcionReview(String descripcionReview) {this.descripcionReview = descripcionReview;}

    public int getValoracion() {return valoracion;}
    public void setValoracion(int valoracion) {this.valoracion = valoracion;}
}
