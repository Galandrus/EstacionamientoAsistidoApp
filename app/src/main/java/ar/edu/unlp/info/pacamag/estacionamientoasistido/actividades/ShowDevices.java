package ar.edu.unlp.info.pacamag.estacionamientoasistido.actividades;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.Tag;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;

import ar.edu.unlp.info.pacamag.estacionamientoasistido.R;
import ar.edu.unlp.info.pacamag.estacionamientoasistido.adaptadores.DeviceAdapter;
import ar.edu.unlp.info.pacamag.estacionamientoasistido.bluetooth.BTAdapter;
import ar.edu.unlp.info.pacamag.estacionamientoasistido.entidades.DeviceItem;

public class ShowDevices extends AppCompatActivity {

    private static BTAdapter btAdapter;
    public static String EXTRA_DEVICE_ADDRESS = "device_address";
    ArrayList<DeviceItem> deviceItemList;
    RecyclerView recyclerDevice;
    Button scan,stop;
    DeviceAdapter adapter;
    Activity activity;
    IntentFilter filter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_devices);
        activity = this;
        deviceItemList = new ArrayList<>();
        btAdapter = new BTAdapter();
        if (btAdapter.SoportaBT())
            btAdapter.ActivarBluetooth(activity);
        else
            Toast.makeText( this, "Su dispositivo no soporta Bluetooth", Toast.LENGTH_SHORT).show();
        configurarRecycler();
        configurarBotones();
        filter= new IntentFilter(BluetoothDevice.ACTION_FOUND);


    }

    private void configurarRecycler(){
        recyclerDevice = findViewById(R.id.idRecyclerDevice);
        recyclerDevice.setLayoutManager(new LinearLayoutManager(this));
        btAdapter.ObtenerListaDispositivos(deviceItemList);
        adapter = new DeviceAdapter(deviceItemList);
        adapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btAdapter.getAdapter().cancelDiscovery();
                DeviceItem devItem = deviceItemList.get(recyclerDevice.getChildAdapterPosition(view));
                Toast.makeText(getBaseContext(), "Seleccion: " +devItem.getNombreDispositivo() , Toast.LENGTH_SHORT).show();
                Intent i = new Intent(ShowDevices.this, UserInterfaz.class);
                i.putExtra(EXTRA_DEVICE_ADDRESS, devItem.getDireccionDispositivo());
                startActivity(i);
            }
        });
        recyclerDevice.setAdapter(adapter);
    }

    private void configurarBotones(){
        scan = findViewById(R.id.idScan);
        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filter.addAction(BluetoothDevice.ACTION_FOUND);
                stop.setEnabled(true);
                scan.setEnabled(false);
                deviceItemList.clear();
                deviceItemList.add(new DeviceItem("No hay Dispositivos","","false"));
                adapter.notifyDataSetChanged();
                registerReceiver(bReciever, filter);
                btAdapter.getAdapter().startDiscovery();
            }
        });

        stop = findViewById(R.id.idStop);
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stop.setEnabled(false);
                scan.setEnabled(true);
                unregisterReceiver(bReciever);
                btAdapter.getAdapter().cancelDiscovery();
            }
        });
    }

    private final BroadcastReceiver bReciever = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // Create a new device item
                DeviceItem newDevice = new DeviceItem(device.getName(), device.getAddress(), "false");
                // Add it to our adapter
                if (deviceItemList.get(0).getDireccionDispositivo().equals(""))
                    deviceItemList.remove(0);
                deviceItemList.add(newDevice);
                adapter.notifyDataSetChanged();
            }
        }
    };


    @Override
    protected void onDestroy() {
        unregisterReceiver(bReciever);
        super.onDestroy();
    }


}
