package ar.edu.unlp.info.pacamag.estacionamientoasistido.bluetooth;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;

import java.util.ArrayList;
import java.util.Set;

import ar.edu.unlp.info.pacamag.estacionamientoasistido.entidades.DeviceItem;


public class BTAdapter {

    private BluetoothAdapter adapter;
    public static int REQUEST_BLUETOOTH = 1;
    private ArrayList<DeviceItem> deviceItemList;

    private final BroadcastReceiver bReciever = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // Create a new device item
                DeviceItem newDevice = new DeviceItem(device.getName(), device.getAddress(), "false");
                // Add it to our adapter
                deviceItemList.add(newDevice);
            }
        }
    };

    public BTAdapter(Context context){
        // Asigna a adapter el adaptor por defecto del telefono
        this.adapter = BluetoothAdapter.getDefaultAdapter();
        SoportaBT(context);
    }

    public BluetoothAdapter getAdapter(){
        return adapter;
    }

    public ArrayList<DeviceItem> getDeviceItemList() {
        return deviceItemList;
    }

    // Si su telefono no soporta Bluetooht le aviso que no puede usar la app.
    public void SoportaBT(Context context){
        if (adapter == null) {
            new AlertDialog.Builder(context)
                    .setTitle("No compatible")
                    .setMessage("Su telefono no soporta Bluetooth")
                    .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            System.exit(0);
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    }

    public void ActivarBluetooth(Activity activity){
        if (!adapter.isEnabled()) {
            Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(enableBT, REQUEST_BLUETOOTH);
        }
    }

    public void ObtenerListaDispositivosConocidos (){
        Set<BluetoothDevice> pairedDevices = adapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                DeviceItem newDevice= new DeviceItem(device.getName(),device.getAddress(),"false");
                deviceItemList.add(newDevice);
            }
        }
    }


    public void ObtenerListaDispositivosDesconocidos( Activity activity, boolean isChecked) {
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        if (isChecked) {
            deviceItemList.clear();
            activity.registerReceiver(bReciever, filter);
            adapter.startDiscovery();
        } else {
            activity.unregisterReceiver(bReciever);
            adapter.cancelDiscovery();
        }
    }


}
