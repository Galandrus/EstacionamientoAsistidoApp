package ar.edu.unlp.info.pacamag.estacionamientoasistido.entidades;

public class DeviceItem {

    private String nombreDispositivo;
    private String direccionDispositivo;
    private boolean conectado;

    public DeviceItem(String nombreDispositivo, String direccionDispositivo, String conectado) {
        this.nombreDispositivo = nombreDispositivo;
        this.direccionDispositivo = direccionDispositivo;
        if (conectado == "true") {
            this.conectado = true;
        } else {
            this.conectado = false;
        }
    }

    public String getNombreDispositivo() {
        return nombreDispositivo;
    }

    public void setNombreDispositivo(String nombreDispositivo) {
        this.nombreDispositivo = nombreDispositivo;
    }

    public String getDireccionDispositivo() {
        return direccionDispositivo;
    }

    public boolean isConectado() {
        return conectado;
    }

}

