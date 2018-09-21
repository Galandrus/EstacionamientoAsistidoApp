package ar.edu.unlp.info.pacamag.estacionamientoasistido.actividades;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import android.os.Handler;

import ar.edu.unlp.info.pacamag.estacionamientoasistido.R;
import ar.edu.unlp.info.pacamag.estacionamientoasistido.bluetooth.BTAdapter;

public class UserInterfaz extends AppCompatActivity {

    TextView timer, distanciaLeft, distanciaRight, distanciaFront;
    ImageView ledLeft, ledRight, ledFront;

    private BTAdapter btAdapter;
    private BluetoothSocket btSocket;
    private ConnectedThread MyConexionBT;
    private static String address;
    Handler bluetoothIn;
    final int handlerState = 0;
    private StringBuilder recDataString = new StringBuilder();


    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_interfaz);

        //Enlaza los controles con sus respectivas vistas
        timer=findViewById(R.id.idTimer);
        distanciaFront=findViewById(R.id.idDistanciaFront);
        distanciaLeft=findViewById(R.id.idDistanciaLeft);
        distanciaRight=findViewById(R.id.idDistanciaRight);
        ledLeft=findViewById(R.id.idLedLeftImagen);
        ledRight=findViewById(R.id.idLedRightImagen);
        ledFront=findViewById(R.id.idLedFrontImagen);

        btAdapter = new BTAdapter();

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

    @Override
    protected void onResume() {
        super.onResume();
        //Consigue la direccion MAC desde DeviceListActivity via intent
        Intent intent = getIntent();
        //Consigue la direccion MAC desde DeviceListActivity via EXTRA
        address = intent.getStringExtra(ShowDevices.EXTRA_DEVICE_ADDRESS);
        //Setea la direccion MAC
        BluetoothDevice device = btAdapter.getDeviceFromMac(address);

        btAdapter.conectarSocket(device,btSocket);
        MyConexionBT = new ConnectedThread(btSocket);
        MyConexionBT.start();
    }

    public void onPause()
    {
        super.onPause();
        btAdapter.desconectarSocket(btSocket);
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
                Toast.makeText(getBaseContext(), "La Conexión fallo", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }
}
