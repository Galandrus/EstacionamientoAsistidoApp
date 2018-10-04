package ar.edu.unlp.info.pacamag.estacionamientoasistido.fragments;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;

import ar.edu.unlp.info.pacamag.estacionamientoasistido.R;
import ar.edu.unlp.info.pacamag.estacionamientoasistido.adaptadores.DeviceAdapter;
import ar.edu.unlp.info.pacamag.estacionamientoasistido.bluetooth.BTAdapter;
import ar.edu.unlp.info.pacamag.estacionamientoasistido.entidades.DeviceItem;
import ar.edu.unlp.info.pacamag.estacionamientoasistido.interfaces.IComunicacionFragment;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ShowDevicesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ShowDevicesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ShowDevicesFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    // TODO: Mis parametros

    private static BTAdapter btAdapter;
    public static String EXTRA_DEVICE_ADDRESS = "device_address";
    ArrayList<DeviceItem> deviceItemList;
    RecyclerView recyclerDevice;
    Button scan,stop;
    DeviceAdapter adapter;
    Activity activity;
    Context context;
    IntentFilter filter;
    IComunicacionFragment interfazcComunicacionFragment;
    View vista;
    ProgressBar progressBar;
    private ObjectAnimator anim;
    LinearLayout showDeviceLayout;


    public ShowDevicesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ShowDevicesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ShowDevicesFragment newInstance(String param1, String param2) {
        ShowDevicesFragment fragment = new ShowDevicesFragment();
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
        context= getContext();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        vista = inflater.inflate(R.layout.fragment_show_devices, container, false);
        deviceItemList = new ArrayList<>();
        btAdapter = new BTAdapter();
        if (btAdapter.SoportaBT())
            btAdapter.ActivarBluetooth(activity);
        else
            Toast.makeText( context, "Su dispositivo no soporta Bluetooth", Toast.LENGTH_SHORT).show();

        configurarBotones();
        configurarRecycler();

        filter= new IntentFilter(BluetoothDevice.ACTION_FOUND);
        showDeviceLayout = vista.findViewById(R.id.idShowDevicesLayout);
        progressBar=vista.findViewById(R.id.idProgressBar);
        anim = ObjectAnimator.ofInt(progressBar, "progress", 0, 100);
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
        if (context instanceof Activity){
            this.activity = (Activity) context;
            interfazcComunicacionFragment = (IComunicacionFragment) this.activity;
        }
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


    private void configurarRecycler(){
        recyclerDevice = vista.findViewById(R.id.idRecyclerDevice);
        recyclerDevice.setLayoutManager(new LinearLayoutManager(context));
        btAdapter.ObtenerListaDispositivos(deviceItemList);
        adapter = new DeviceAdapter(deviceItemList);
        adapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btAdapter.getAdapter().cancelDiscovery();
                DeviceItem devItem = deviceItemList.get(recyclerDevice.getChildAdapterPosition(view));
                mostrarProgress();
                interfazcComunicacionFragment.enviarMAC(devItem.getDireccionDispositivo());
            }
        });
        recyclerDevice.setAdapter(adapter);
    }

    private void configurarBotones(){
        scan = vista.findViewById(R.id.idScan);
        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filter.addAction(BluetoothDevice.ACTION_FOUND);
                stop.setEnabled(true);
                scan.setEnabled(false);
                deviceItemList.clear();
                deviceItemList.add(new DeviceItem("No hay Dispositivos","","false"));
                recyclerDevice.setEnabled(false);
                adapter.notifyDataSetChanged();
                context.registerReceiver(bReciever, filter);
                btAdapter.getAdapter().startDiscovery();
            }
        });

        stop = vista.findViewById(R.id.idStop);
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stop.setEnabled(false);
                scan.setEnabled(true);
                context.unregisterReceiver(bReciever);
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
                if (deviceItemList.get(0).getDireccionDispositivo().equals("")){
                    deviceItemList.remove(0);
                }
                deviceItemList.add(newDevice);
                adapter.notifyDataSetChanged();
            }
        }
    };

    private void mostrarProgress(){
        //agregamos el tiempo de la animacion a mostrar
        showDeviceLayout.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);


        anim.setDuration(15000);
        anim.setInterpolator(new DecelerateInterpolator());
        //iniciamos el progressbar
        anim.start();
    }

}
