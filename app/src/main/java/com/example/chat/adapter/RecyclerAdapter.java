package com.example.chat.adapter;


import static com.example.chat.ChatActivity.USER_IP;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chat.R;
import com.example.chat.model.Paquete;

import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter {
    private static final int PAQUETE_MANDADO = 0;
    private static final int PAQUETE_RECIBIDO = 1;

    private List<Paquete> listaPaquetes;

    public RecyclerAdapter(List<Paquete> listaPaquetes){
        this.listaPaquetes = listaPaquetes;
    }

    @Override
    public int getItemViewType(int position){
        Paquete paquete = (Paquete) listaPaquetes.get(position);
        int tipoMensaje = PAQUETE_RECIBIDO;

        if(paquete.getIp().equals(USER_IP)){
            tipoMensaje = PAQUETE_MANDADO;
        }

        return tipoMensaje;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        RecyclerView.ViewHolder viewHolder = null;

        // TODO
        switch (viewType) {
            case PAQUETE_MANDADO:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_self, parent, false);
                viewHolder = new RecyclerHolderSender(view);
                break;
            case PAQUETE_RECIBIDO:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_other, parent, false);
                viewHolder = new RecyclerHolderReceiver(view);
                break;
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Paquete paquete = listaPaquetes.get(position);
        switch (holder.getItemViewType()){
            case PAQUETE_MANDADO:
                ((RecyclerHolderSender)holder).tvMessage.setText("A (" + paquete.getIpOther() + "):\n" + paquete.getMensaje());
                break;
            case PAQUETE_RECIBIDO:
                ((RecyclerHolderReceiver)holder).tvMessage.setText("De " + paquete.getNombre() + " (" + paquete.getIp() + "):\n" +paquete.getMensaje());
                break;
        }
    }

    @Override
    public int getItemCount() {
        return listaPaquetes.size();
    }

    private class RecyclerHolderSender extends RecyclerView.ViewHolder{
        // Atributos de la clase
        TextView tvMessage;

        public RecyclerHolderSender(@NonNull View itemView) {
            super(itemView);

            tvMessage = itemView.findViewById(R.id.tvOwnMessage);
        }
    }

    private class RecyclerHolderReceiver extends RecyclerView.ViewHolder{
        // Atributos de la clase
        TextView tvMessage;

        public RecyclerHolderReceiver(@NonNull View itemView) {
            super(itemView);

            tvMessage = itemView.findViewById(R.id.tvOtherMessage);
        }
    }
}
