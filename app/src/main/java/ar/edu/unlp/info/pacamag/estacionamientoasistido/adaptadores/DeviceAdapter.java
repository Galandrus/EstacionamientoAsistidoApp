package ar.edu.unlp.info.pacamag.estacionamientoasistido.adaptadores;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import ar.edu.unlp.info.pacamag.estacionamientoasistido.R;
import ar.edu.unlp.info.pacamag.estacionamientoasistido.entidades.DeviceItem;


public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder> implements View.OnClickListener {

    ArrayList<DeviceItem> listaDevice;
    private View.OnClickListener listener;

    public DeviceAdapter(ArrayList<DeviceItem> listaDevice){
        this.listaDevice = listaDevice;
    }

    @Override
    public void onClick(View view) {
        if (listener != null){
            listener.onClick(view);
        }
    }

    public void setOnClickListener(View.OnClickListener listener){
        this.listener = listener;
    }

    @Override
    public DeviceAdapter.DeviceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Muestra la lista de dispositivos
        //Setea el modelo a seguir para sus filas
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.device_list_item, null, false);
        view.setOnClickListener(this);
        return new DeviceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DeviceAdapter.DeviceViewHolder holder, int position) {
        //Seteo el texto de cada fila
        holder.nombreDevice.setText(listaDevice.get(position).getNombreDispositivo());
        holder.macDevice.setText(listaDevice.get(position).getDireccionDispositivo());
    }

    @Override
    public int getItemCount() {
        return listaDevice.size();
    }

    public class DeviceViewHolder extends RecyclerView.ViewHolder {
        TextView nombreDevice, macDevice;

        public DeviceViewHolder(View itemView) {
            super(itemView);
            //Matcheo con todos los componentes de mi vista de items
            nombreDevice= itemView.findViewById(R.id.idNombreDevice);
            macDevice = itemView.findViewById(R.id.idMacDevice);
        }
    }
}
