package com.travelmaster.Dialog;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;

import java.util.Calendar;

import android.os.Bundle;
import android.widget.DatePicker;

import com.travelmaster.Activity.RegisterActivity;

/**
 * Created by Pc-Aitor on 13/05/2017.
 */

public class DateDialog extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    Activity activity = null;
    static final String TAG = "Comprobacion DateDialog";

    /**
     * Crea el dialog, poniendole el limite el dia de hoy
     * @param savedInstanceState
     * @return
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar calendar = Calendar.getInstance();
        int ano = calendar.get(Calendar.YEAR);
        int mes = calendar.get(Calendar.MONTH);
        int dia = calendar.get(Calendar.DAY_OF_MONTH);
        if(activity == null){
            activity = getActivity();
        }
        return new DatePickerDialog(activity, this, ano, mes, dia);
    }

    /**
     * Asocia la activity
     * @param activity
     */
    public void attach(Activity activity){
        this.activity = activity;
    }

    /**
     * Accion a realizar cuando se cambie la fecha y se pulse el boton
     * @param datePicker
     * @param ano
     * @param mes
     * @param dia
     */
    @Override
    public void onDateSet(DatePicker datePicker, int ano, int mes, int dia) {
        Calendar cal = Calendar.getInstance();
        cal.set(datePicker.getYear(),datePicker.getMonth(),datePicker.getDayOfMonth());
        if(activity instanceof RegisterActivity){
            ((RegisterActivity) activity).setFecha(cal.getTimeInMillis());
        }

    }
}
