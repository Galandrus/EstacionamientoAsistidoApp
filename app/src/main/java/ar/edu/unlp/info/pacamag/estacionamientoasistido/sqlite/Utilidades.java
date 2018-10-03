package ar.edu.unlp.info.pacamag.estacionamientoasistido.sqlite;

public class Utilidades {

    //TABLA REGISTRO
    public static final String TABLA_REGISTRO="registro";
    public static final String REGISTRO_ID="_idRegistro";
    public static final String REGISTRO_TIEMPO="tiempoRegistro";
    public static final String REGISTRO_FECHA="fechaRegistro";
    public static final String REGISTRO_HORA="horaRegistro";

    public static final String CREAR_TABLA_REGISTRO="CREATE TABLE "+TABLA_REGISTRO+" ("
            +REGISTRO_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"
            +REGISTRO_FECHA+" TEXT NOT NULL, "
            +REGISTRO_HORA+" TEXT NOT NULL, "
            +REGISTRO_TIEMPO+" TEXT NOT NULL)";

    // CONSULTAS

    public static final String RECUPERAR_REGISTROS_ORDER_BY_TIEMPO =
            "select "+ REGISTRO_FECHA + " , " + REGISTRO_HORA + " , " + REGISTRO_TIEMPO +
            " from " + TABLA_REGISTRO +
            " order by " + REGISTRO_TIEMPO;

    public static final String RECUPERAR_REGISTROS_ORDER_BY_FECHA =
            "select "+ REGISTRO_FECHA + " , " + REGISTRO_HORA + " , " + REGISTRO_TIEMPO +
            " from " + TABLA_REGISTRO +
            " order by " + REGISTRO_FECHA + " , " + REGISTRO_HORA;
}
