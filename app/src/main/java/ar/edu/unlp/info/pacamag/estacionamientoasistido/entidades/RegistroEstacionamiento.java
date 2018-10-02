package ar.edu.unlp.info.pacamag.estacionamientoasistido.entidades;

import android.annotation.TargetApi;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.support.annotation.RequiresApi;

import java.util.Date;
import java.util.Locale;

public class RegistroEstacionamiento {

    private String tiempo;
    private String fecha;
    private String hora;

    public RegistroEstacionamiento(String tiempo, String fecha, String hora) {
        this.tiempo = tiempo;
        this.fecha = fecha;
        this.hora = hora;
    }

    public RegistroEstacionamiento() {

    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }



    public String getTiempo() {
        return tiempo;
    }

    public void setTiempo(String tiempo) {
        this.tiempo = tiempo;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
}
