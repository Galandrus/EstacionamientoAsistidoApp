package ar.edu.unlp.info.pacamag.estacionamientoasistido.bluetooth;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import ar.edu.unlp.info.pacamag.estacionamientoasistido.entidades.DeviceItem;


public class BTAdapter {

    private BluetoothAdapter btAdapter;


    // Identificador unico de servicio - SPP UUID
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");


    public BTAdapter(){
        // Asigna a adapter el adaptor por defecto del telefono
        this.btAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public BluetoothAdapter getAdapter(){
        return btAdapter;
    }


    public boolean SoportaBT(){
        if(btAdapter==null) return false;
        else return true;
    }

    public void ActivarBluetooth(Activity activity){
        if (!btAdapter.isEnabled()){
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(enableBtIntent, 1);
        }
    }

    public void ObtenerListaDispositivos(ArrayList<DeviceItem> deviceItemList){
        Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                DeviceItem newDevice= new DeviceItem(device.getName(),device.getAddress(),"false");
                deviceItemList.add(newDevice);
            }
        } else {
            deviceItemList.add(new DeviceItem("Active el Bluetooth","","false"));
        }
    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException
    {
        //crea un conexion de salida segura para el dispositivo
        //usando el servicio UUID
        return device.createRfcommSocketToServiceRecord(BTMODULEUUID);
    }

    public BluetoothDevice getDeviceFromMac(String address){
        //Setea la direccion MAC
        return btAdapter.getRemoteDevice(address);
    }

    public BluetoothSocket conectarSocket(BluetoothDevice device){
        BluetoothSocket btSocket;
        try
        {
            btSocket = createBluetoothSocket(device);
        } catch (IOException e) {
            return null;
        }

        // Establece la conexión con el socket Bluetooth.
        try
        {
            btSocket.connect();
            return btSocket;
        } catch (IOException e) {
            try {
                btSocket.close();
                return null;
            } catch (IOException e2) {
                return null;
            }
        }
    }

    public boolean desconectarSocket (BluetoothSocket btSocket ){
        try
        { // Cuando se sale de la aplicación esta parte permite
            // que no se deje abierto el socket
            btSocket.close();
            return true;
        } catch (IOException e2) {
            return false;
        }
    }


}
