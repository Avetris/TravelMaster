package com.travelmaster.model;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Pc-Aitor on 10/05/2017.
 */

public class Tiempos extends RealmObject{

    @PrimaryKey
    private String tabla;
    private Date ultimaFecha;

    public String getTabla() {return tabla;}
    public void setTabla(String tabla) {this.tabla = tabla;}

    public Date getUltimaFecha() {return ultimaFecha;}
    public void setUltimaFecha(Date ultimaFecha) {this.ultimaFecha = ultimaFecha;}
}
