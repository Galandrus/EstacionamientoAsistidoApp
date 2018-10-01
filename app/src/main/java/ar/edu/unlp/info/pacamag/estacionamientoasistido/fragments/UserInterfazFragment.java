package ar.edu.unlp.info.pacamag.estacionamientoasistido.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import ar.edu.unlp.info.pacamag.estacionamientoasistido.R;
import ar.edu.unlp.info.pacamag.estacionamientoasistido.actividades.MainActivity;
import ar.edu.unlp.info.pacamag.estacionamientoasistido.bluetooth.BTAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UserInterfazFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UserInterfazFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
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
    ImageView ledLeft, ledRight, ledFront;
    Button conectar;

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
        conectar = vista.findViewById(R.id.idConectarBT);

        btAdapter = new BTAdapter();
        device = recuperarDispositivo();
        if (device != null)
            ConectarConDispositivo(device);
        else
            Toast.makeText(context, "No se pudo recuperar el dispositivo", Toast.LENGTH_SHORT).show();

        bluetoothIn = new Handler() {
            //////////////////////////////// VER COMO RECIBIR LAS COSAS /////////////////////
            public void handleMessage(android.os.Message msg) {
                if (msg.what == handlerState) {
                    String readMessage = (String) msg.obj;
                    recDataString.append(readMessage); //Obtiene el mensaje
                    int endOfLineIndex = recDataString.indexOf("\n"); // Determina el final de linea
                    if (endOfLineIndex > 0) {                                           // Se asegura que haya un mensaje
                        String dataInPrint = recDataString.substring(0, endOfLineIndex);    // Extrae el string
                        char[] arreglo = dataInPrint.toCharArray();
                        String datos="";
                        int index = 1;
                        for (int i=0;i<dataInPrint.length();i++){
                            if(arreglo[i] !='#'){
                                datos+=arreglo[i];
                            } else {
                                setearTextoVista(datos, index++);
                            }
                        }
                        recDataString.delete(0, recDataString.length());      //clear all string data
                    }
                }
            }
        };
/////////////////////////////////////////////////////////////////////////////////////
        conectar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (device != null)
                    ConectarConDispositivo(device);
                else
                    Toast.makeText(context, "No se pudo recuperar el dispositivo", Toast.LENGTH_SHORT).show();
            }
        });
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
        if (btSocket!= null)
            btAdapter.desconectarSocket(btSocket);
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

    private void setearTextoVista(String datos, int i) {
        // EnviarInformacion(String timer, String distanciaLeft, String distanciaRight, String distanciaFront, char ledLeft, char ledRight, char ledFront )
        switch (i){
            case 1: setearTimer(datos); break;
            case 2: distanciaLeft.setText(datos); break;
            case 3: distanciaRight.setText(datos); break;
            case 4: distanciaFront.setText(datos); break;
            case 5: setearLed(datos,1); break;
            case 6: setearLed(datos,2); break;
            case 7: setearLed(datos,3); break;
        }
    }

    private void setearLed(String datos, int i) {
        ImageView led = null;
        switch (i){
            case 1: led = ledLeft; break;
            case 2: led = ledRight; break;
            case 3: led = ledFront; break;
        }
        if(datos.equals("1")){
            //Prendo el Led
            led.setImageResource(R.drawable.led_off);
        } else {
            //Apago el Led
            led.setImageResource(R.drawable.led_on);
        }
    }

    private void setearTimer(String datos) {
        String minutos = datos.substring(0,1);
        String segundos = datos.substring(2,3);
        String tiempo=minutos+":"+segundos;
        timer.setText(tiempo);
    }

    private void ConectarConDispositivo( BluetoothDevice device) {
        btSocket = btAdapter.conectarSocket(device);
        if (btSocket!=null) {
            MyConexionBT = new UserInterfazFragment.ConnectedThread(btSocket);
            MyConexionBT.start();
        } else{
            Toast.makeText(context, "Fallo la conexion del socket", Toast.LENGTH_SHORT).show();
        }
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

    @Override
    public void onResume() {
        super.onResume();
        if (device != null)
            ConectarConDispositivo(device);
        else
            Toast.makeText(context, "No se pudo recuperar el dispositivo", Toast.LENGTH_SHORT).show();
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