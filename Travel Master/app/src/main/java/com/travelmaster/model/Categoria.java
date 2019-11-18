package com.travelmaster.model;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Pc-Aitor on 09/05/2017.
 */

public class Categoria  extends RealmObject {

    @PrimaryKey
    private int idCategoria;

    private String nombreCategoria;
    private String imagenCategoria;

    public int getIdCategoria() {return idCategoria;}
    public void setIdCategoria(int idCategoria) {this.idCategoria = idCategoria;}

    public String getNombreCategoria() {return nombreCategoria;}
    public void setNombreCategoria(String nombreCategoria) {this.nombreCategoria = nombreCategoria;}

    public String getImagenCategoria() {return imagenCategoria;}
    public void setImagenCategoria(String imagenCategoria) {this.imagenCategoria = imagenCategoria;}
}
