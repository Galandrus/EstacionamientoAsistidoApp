package ar.edu.unlp.info.pacamag.estacionamientoasistido.fragments;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.icu.text.SimpleDateFormat;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.Locale;

import ar.edu.unlp.info.pacamag.estacionamientoasistido.R;
import ar.edu.unlp.info.pacamag.estacionamientoasistido.bluetooth.BTAdapter;
import ar.edu.unlp.info.pacamag.estacionamientoasistido.sonido.ReproductorAdapter;
import ar.edu.unlp.info.pacamag.estacionamientoasistido.sqlite.ConexionSQLiteHelper;
import ar.edu.unlp.info.pacamag.estacionamientoasistido.sqlite.Utilidades;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UserInterfazFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UserInterfazFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
@TargetApi(Build.VERSION_CODES.N)
@RequiresApi(api = Build.VERSION_CODES.N)
public class UserInterfazFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    // TODO: Mis parametros
    TextView timer, distanciaLeft, distanciaRight, distanciaFront;
    ImageView ledLeft, ledRight, ledFront, parlante;
    Button BORRAR;

    private BTAdapter btAdapter;
    private BluetoothSocket btSocket;
    private UserInterfazFragment.ConnectedThread MyConexionBT;
    private static String address;
    Handler bluetoothIn;
    final int handlerState = 0;
    private StringBuilder recDataString = new StringBuilder();
    private BluetoothDevice device;
    private Activity activity;
    private Context context;
    private Menu menu;
    private MediaPlayer mediaPlayer;
    private boolean esLaPrimeraVezQueSuena;

    public UserInterfazFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UserInterfazFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UserInterfazFragment newInstance(String param1, String param2) {
        UserInterfazFragment fragment = new UserInterfazFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        activity=getActivity();
        context=getContext();

    }

    @SuppressLint("HandlerLeak")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.fragment_user_interfaz, container, false);

        //Enlaza los controles con sus respectivas vistas
        timer=vista.findViewById(R.id.idTimer);
        distanciaFront=vista.findViewById(R.id.idDistanciaFront);
        distanciaLeft=vista.findViewById(R.id.idDistanciaLeft);
        distanciaRight=vista.findViewById(R.id.idDistanciaRight);
        ledLeft=vista.findViewById(R.id.idLedLeftImagen);
        ledRight=vista.findViewById(R.id.idLedRightImagen);
        ledFront=vista.findViewById(R.id.idLedFrontImagen);
        parlante = vista.findViewById(R.id.idParlante);
        setHasOptionsMenu(true);

        esLaPrimeraVezQueSuena = true;

        btAdapter = new BTAdapter();
        device = recuperarDispositivo();

        //ConectarConDispositivo(device);

        bluetoothIn = new Handler() {
            //////////////////////////////// VER COMO RECIBIR LAS COSAS /////////////////////

            public void handleMessage(android.os.Message msg) {
                if (msg.what == handlerState) {
                    String readMessage = (String) msg.obj;
                    recDataString.append(readMessage); //Obtiene el mensaje
                    int endOfLineIndex = recDataString.indexOf("#"); // Determina el final de linea
                    if (endOfLineIndex > 0) {                                           // Se asegura que haya un mensaje
                        String dataInPrint = recDataString.substring(1, endOfLineIndex);    // Extrae el string de datos
                        char index = recDataString.charAt(0);

                        setearTextoVista(dataInPrint, index );
                    }
                    recDataString.delete(0, endOfLineIndex+1);      //clear all string data
                }
            }
        };
/////////////////////////////////////////////////////////////////////////////////////
        // Inflate the layout for this fragment

        return vista;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;

        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        desconectarDispositivo();
        mediaPlayer.stop();
        mediaPlayer.release();
    }

    private void desconectarDispositivo() {
        if (btSocket!= null) {
            btAdapter.desconectarSocket(btSocket);
            menu.findItem(R.id.idBleConectar).setVisible(true);
            menu.findItem(R.id.idBleDesconectar).setVisible(false);
            Toast.makeText(context, "Dispositivo Desconectado", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


    private void setearTextoVista(String datos, char index) {
        switch (index){
            case '1': setearTimer(datos); break;
            case '2': distanciaLeft.setText(datos); break;
            case '3': distanciaRight.setText(datos); break;
            case '4': distanciaFront.setText(datos); break;
            case '5': setearLed(datos, ledLeft); break;
            case '6': setearLed(datos,ledRight); break;
            case '7': setearLed(datos,ledFront); break;
            case '8': stop(datos); break;
            case '9': chicharra(datos); break;
            default:
                Toast.makeText(context, "Index Incorrecto", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void chicharra(String datos) {
        //Hacer sonar la bocina
        int sonido=0;
        switch (datos){
            case "1":
                sonido=R.raw.freq1;
                break;
            case "2":
                sonido=R.raw.freq2;
                break;
            case "3":
                sonido=R.raw.freq3;
                break;
            case "4":
                sonido=R.raw.freq4;
                break;
            case "5":
                sonido=0;
        }

        if (sonido != 0) {
            if (!esLaPrimeraVezQueSuena)
                DetenerSonido();
            else
                esLaPrimeraVezQueSuena = false;
            ReproducirSonido(sonido);
        }else{
            DetenerSonido();
        }

    }

    private void ReproducirSonido(int sonido) {
        parlante.setVisibility(View.VISIBLE);
        mediaPlayer = MediaPlayer.create(context, sonido);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
}
    private void DetenerSonido(){
        mediaPlayer.stop();
        mediaPlayer.release();
        parlante.setVisibility(View.INVISIBLE);
    }


    private void stop(String datos) {
        if (datos.equals("1")){
            guardarRegistro();
        }
    }

    private void setearLed(String datos, ImageView led) {
        if(datos.equals("1")){
            //Prendo el Led
            led.setImageResource(R.drawable.led_on);
        } else {
            //Apago el Led
            led.setImageResource(R.drawable.led_off);
        }
    }

    private void setearTimer(String datos) {
        String minutos = datos.substring(0,2);
        String segundos = datos.substring(2,4);
        String tiempo=minutos+":"+segundos;
        timer.setText(tiempo);
    }

    private void ConectarConDispositivo( BluetoothDevice device) {
        if (device != null) {

            btSocket = btAdapter.conectarSocket(device);
            if (btSocket != null) {
                MyConexionBT = new UserInterfazFragment.ConnectedThread(btSocket);
                MyConexionBT.start();
                menu.findItem(R.id.idBleConectar).setVisible(false);
                menu.findItem(R.id.idBleDesconectar).setVisible(true);
                Toast.makeText(context, "Dispositivo Conectado", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(context, "Fallo la conexion del socket", Toast.LENGTH_SHORT).show();
            }
        } else
            Toast.makeText(context, "No se pudo recuperar el dispositivo", Toast.LENGTH_SHORT).show();
    }

    private BluetoothDevice recuperarDispositivo() {

        Bundle bundle =getArguments();

        //Consigue la direccion MAC desde el bundle
        if (bundle != null) {
            address = (String) bundle.getSerializable("mandoMac");
        //Setea la direccion MAC
        if (address == "")
            return null;
        else
            return btAdapter.getDeviceFromMac(address);
        } else
            return null;
    }



    public void guardarRegistro(){
        //instancio la bd
        ConexionSQLiteHelper conn = new ConexionSQLiteHelper(context);

        //abro la base para poder editarla
        SQLiteDatabase db=conn.getWritableDatabase();

        //Agrego Tabla Registro
        ContentValues valoresRegistro =new ContentValues();
        valoresRegistro.put(Utilidades.REGISTRO_FECHA, obtenerFecha());
        valoresRegistro.put(Utilidades.REGISTRO_HORA, obtenerHora());
        valoresRegistro.put(Utilidades.REGISTRO_TIEMPO, timer.getText().toString());

        try {
            db.insert(Utilidades.TABLA_REGISTRO,null,valoresRegistro);
        } catch (IOError e){
            Toast.makeText(context,"Fallo la inserción en Registro", Toast.LENGTH_SHORT).show();
        }

        //Aviso que se creo bien
        Toast.makeText(context,"Se guardo exitosamente", Toast.LENGTH_SHORT).show();

        //Cierro bd
        db.close();
    }


    private String obtenerHora() {
        SimpleDateFormat hourFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return hourFormat.format(date);
    }


    private String obtenerFecha() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        this.menu=menu;
        inflater.inflate(R.menu.user_interfaz_menu,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.idBleConectar:
                ConectarConDispositivo(device);

                break;
            case R.id.idBleDesconectar:
                desconectarDispositivo();

                break;
        }


        return super.onOptionsItemSelected(item);

    }


    //Crea la clase que permite crear el evento de conexion
    private class ConnectedThread extends Thread
    {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket)
        {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            try
            {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }
            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run()
        {
            byte[] buffer = new byte[256];
            int bytes;

            // Se mantiene en modo escucha para determinar el ingreso de datos
            while (true) {
                try {
                    bytes = mmInStream.read(buffer);
                    String readMessage = new String(buffer, 0, bytes);
                    // Envia los datos obtenidos hacia el evento via handler
                    bluetoothIn.obtainMessage(handlerState, bytes, -1,readMessage).sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }
        }
        //Envio de trama
        public void write(String input)
        {
            try {
                mmOutStream.write(input.getBytes());
            }
            catch (IOException e)
            {
                //si no es posible enviar datos se cierra la conexión
                Toast.makeText(activity, "La Conexión fallo", Toast.LENGTH_LONG).show();
                activity.finish();
            }
        }
    }

}