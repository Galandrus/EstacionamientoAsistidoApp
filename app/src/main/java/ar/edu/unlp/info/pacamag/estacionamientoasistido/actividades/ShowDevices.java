package ar.edu.unlp.info.pacamag.estacionamientoasistido.actividades;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import java.util.ArrayList;

import ar.edu.unlp.info.pacamag.estacionamientoasistido.R;
import ar.edu.unlp.info.pacamag.estacionamientoasistido.adaptadores.DeviceAdapter;
import ar.edu.unlp.info.pacamag.estacionamientoasistido.bluetooth.BTAdapter;
import ar.edu.unlp.info.pacamag.estacionamientoasistido.entidades.DeviceItem;

public class ShowDevices extends AppCompatActivity {

    private static BTAdapter btAdapter;
    RecyclerView recyclerDevice;
    ToggleButton scan;
    DeviceAdapter adapter;
    Activity activity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_devices);
        activity=this;
        btAdapter = new BTAdapter(this);
        btAdapter.ActivarBluetooth(activity);
        recyclerDevice = findViewById(R.id.idRecyclerDevice);
        recyclerDevice.setLayoutManager(new LinearLayoutManager(this));
        btAdapter.ObtenerListaDispositivosConocidos();
        configurarScan();

        adapter = new DeviceAdapter(btAdapter.getDeviceItemList());
        recyclerDevice.setAdapter(adapter);

    }

    private void configurarScan() {
        scan=findViewById(R.id.idScan);
        scan.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                btAdapter.ObtenerListaDispositivosDesconocidos(activity, isChecked);
            }
        });
    }


}
