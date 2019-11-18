package com.travelmaster.model;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Pc-Aitor on 09/05/2017.
 */

public class Lugar extends RealmObject {

    @PrimaryKey
    private int idLugar;

    private int idCreador;
    private String categoria;
    private String nombreLugar;
    private String descripcionLugar;
    private String imagenLugar;
    private double latitud;
    private double longitud;
    private int valoracionLugar;
    private Date fechaModificacionLugar;

    public int getIdCreador() {return idCreador;}
    public void setIdCreador(int idCreador) {this.idCreador = idCreador;}

    public int getIdLugar() {return idLugar;}
    public void setIdLugar(int idLugar) {this.idLugar = idLugar;}

    public String getCategoria() {return categoria;}
    public void setCategoria(String categoria) {this.categoria = categoria;}

    public String getNombreLugar() {return nombreLugar;}
    public void setNombreLugar(String nombreLugar) {this.nombreLugar = nombreLugar;}

    public String getDescripcionLugar() {return descripcionLugar;}
    public void setDescripcionLugar(String descripcionLugar) {this.descripcionLugar = descripcionLugar;}

    public String getImagenLugar() {return imagenLugar;}
    public void setImagenLugar(String imagenLugar) {this.imagenLugar = imagenLugar;}

    public double getLatitud() {return latitud;}
    public void setLatitud(double latitud) {this.latitud = latitud;}

    public double getLongitud() {return longitud;}
    public void setLongitud(double longitud) {this.longitud = longitud;}

    public Date getFechaModificacionLugar() {return fechaModificacionLugar;}
    public void setFechaModificacionLugar(Date fechaModificacionLugar) {this.fechaModificacionLugar = fechaModificacionLugar;}

    public int getValoracionLugar() {return valoracionLugar;}
    public void setValoracionLugar(int valoracionLugar) {this.valoracionLugar = valoracionLugar;}
}
