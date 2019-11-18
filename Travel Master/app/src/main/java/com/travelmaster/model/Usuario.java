package com.travelmaster.model;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Pc-Aitor on 09/05/2017.
 */

public class Usuario extends RealmObject {

    @PrimaryKey
    private int idUsuario;

    private String nick;
    private String nombre;
    private boolean privado;
    private Date cumpleanos;
    private String origen;
    private String email;
    private int telefono;
    private String descripcion;
    private int valoracion;
    private boolean actual;
    private String imagen;

    public int getIdUsuario() {return idUsuario;}
    public void setIdUsuario(int idUsuario) {this.idUsuario = idUsuario;}

    public String getNick() {return nick;}
    public void setNick(String nick) {this.nick = nick;}

    public String getNombre() {return nombre;}
    public void setNombre(String nombre) {this.nombre = nombre;}

    public boolean isPrivado() {return privado;}
    public void setPrivado(boolean privado) {this.privado = privado;}

    public Date getCumpleanos() {return cumpleanos;}
    public void setCumpleanos(Date cumpleanos) {this.cumpleanos = cumpleanos;}

    public String getOrigen() {return origen;}
    public void setOrigen(String origen) {this.origen = origen;}

    public String getEmail() {return email;}
    public void setEmail(String email) {this.email = email;}

    public int getTelefono() {return telefono;}
    public void setTelefono(int telefono) {this.telefono = telefono;}

    public String getDescripcion() {return descripcion;}
    public void setDescripcion(String descripcion) {this.descripcion = descripcion;}

    public int getValoracion() {return valoracion;}
    public void setValoracion(int valoracion) {this.valoracion = valoracion;}

    public boolean isActual() {return actual;}
    public void setActual(boolean actual) {this.actual = actual;}

    public String getImagen() {return imagen;}
    public void setImagen(String imagen) {this.imagen = imagen;}
}
