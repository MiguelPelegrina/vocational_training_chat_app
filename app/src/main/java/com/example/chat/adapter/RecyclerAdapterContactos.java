package com.example.chat.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chat.R;
import com.example.chat.model.Contacto;

import java.util.List;

public class RecyclerAdapterContactos extends RecyclerView.Adapter<RecyclerAdapterContactos.RecyclerHolder> {
    List<Contacto> listContactos;

    public RecyclerAdapterContactos(List<Contacto> listContactos){
        this.listContactos = listContactos;
    }

    @NonNull
    @Override
    public RecyclerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contactos_list,parent, false);
        RecyclerHolder recyclerHolder = new RecyclerHolder(view);

        return recyclerHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerHolder holder, int position) {
        Contacto contacto = listContactos.get(position);
        holder.txtViewNombre.setText(contacto.getNombreContacto());
        holder.txtViewMensaje.setText(contacto.getMensajes().get(contacto.getMensajes().size()));
        holder.imgContacto.setImageResource(R.drawable.ic_launcher_foreground);
    }

    @Override
    public int getItemCount() {
        return listContactos.size();
    }

    public class RecyclerHolder extends RecyclerView.ViewHolder {
        ImageView imgContacto;
        TextView txtViewNombre;
        TextView txtViewMensaje;

        public RecyclerHolder(@NonNull View itemView) {
            super(itemView);

            imgContacto = itemView.findViewById(R.id.imgContacto);
            txtViewNombre = itemView.findViewById(R.id.txtViewNombre);
            txtViewMensaje = itemView.findViewById(R.id.txtViewMensaje);
        }
    }
}
