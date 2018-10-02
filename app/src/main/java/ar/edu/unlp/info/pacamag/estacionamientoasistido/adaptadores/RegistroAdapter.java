package ar.edu.unlp.info.pacamag.estacionamientoasistido.adaptadores;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;


import ar.edu.unlp.info.pacamag.estacionamientoasistido.R;
import ar.edu.unlp.info.pacamag.estacionamientoasistido.entidades.RegistroEstacionamiento;

public class RegistroAdapter extends RecyclerView.Adapter<RegistroAdapter.RegistroViewHolder> implements View.OnClickListener {


    public RegistroAdapter(ArrayList<RegistroEstacionamiento> listaRegistro) {
        this.listaRegistro = listaRegistro;
    }

    ArrayList<RegistroEstacionamiento> listaRegistro;
    private View.OnClickListener listener;


    @Override
    public RegistroAdapter.RegistroViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Muestra la lista de dispositivos
        //Setea el modelo a seguir para sus filas
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.registro_list_item, null, false);
        view.setOnClickListener(this);
        return new RegistroViewHolder(view);
    }

    public void setOnClickListener(View.OnClickListener listener){
        this.listener = listener;
    }

    @Override
    public void onBindViewHolder(RegistroAdapter.RegistroViewHolder holder, int position) {
        //Seteo el texto de cada fila
        holder.horaRegistro.setText(listaRegistro.get(position).getHora());
        holder.fechaRegistro.setText(listaRegistro.get(position).getFecha());
        holder.tiempoRegistro.setText(listaRegistro.get(position).getTiempo());
    }

    @Override
    public int getItemCount() {
        return listaRegistro.size();
    }

    @Override
    public void onClick(View view) {
        if (listener != null){
            listener.onClick(view);
        }
    }

    public class RegistroViewHolder extends RecyclerView.ViewHolder {
        TextView horaRegistro, fechaRegistro, tiempoRegistro;
        public RegistroViewHolder(View itemView) {
            super(itemView);
            //Matcheo con todos los componentes de mi vista de items
            horaRegistro= itemView.findViewById(R.id.idHoraRegistro);
            fechaRegistro = itemView.findViewById(R.id.idFechaRegistro);
            tiempoRegistro = itemView.findViewById(R.id.idTiempoRegistro);

        }
    }
}
