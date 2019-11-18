package com.travelmaster.Datos;

import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Pc-Aitor on 10/03/2017.
 */

public class SHA1 {

    private static final String TAG = "Comprobacion SHA1";

    /***
     * Convierte un arreglo de bytes a String usando valores hexadecimales
     * @param digest arreglo de bytes a convertir
     * @return String creado a partir de <code>digest</code>
     */

    private static String aHexadecimal(byte[] digest){
        String hash = "";
        for(byte aux:digest){
            int b = aux & 0xff;
            if(Integer.toHexString(b).length() == 1) hash += "0";
            hash += Integer.toHexString(b);
        }
        return hash;
    }

    /**
     * Obtiene el texto encriptado
     * @param pCont
     * @return
     */
    public static String getStringMensageDigest(String pCont){
        byte[] digest = null;
        byte[] buffer = pCont.getBytes();
        try{
            MessageDigest mensajeDigest = MessageDigest.getInstance("SHA-1");
            mensajeDigest.reset();
            mensajeDigest.update(buffer);
            digest = mensajeDigest.digest();
        }catch(NoSuchAlgorithmException ex){
            Log.e(TAG, "Error creando Digest");
        }
        return aHexadecimal(digest);
    }
}
