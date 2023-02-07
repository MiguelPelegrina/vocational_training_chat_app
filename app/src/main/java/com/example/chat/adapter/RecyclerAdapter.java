package com.example.chat.adapter;


import static com.example.chat.ConnectActivity.USER_IP;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chat.R;
import com.example.chat.model.Paquete;

import java.util.List;

/**
 * Clase adaptadoras encargada de mostrar tanto los mensajes recibidos como los mandados por el
 * usuario de la aplicación
 */
public class RecyclerAdapter extends RecyclerView.Adapter {
    // Atributos de la clase
    // Estos atributos nos sirven para identificar el tipo de paquete, es decir, si es uno mandado
    // o recibido, para posteriormente utilizar un XML u otro
    private static final int PAQUETE_MANDADO = 0;
    private static final int PAQUETE_RECIBIDO = 1;
    // Atributos de la instancia
    private List<Paquete> listaPaquetes;

    /**
     * Constructor por parámetros
     * @param listaPaquetes Lista de paquetes que se van a mostrar
     */
    public RecyclerAdapter(List<Paquete> listaPaquetes){
        this.listaPaquetes = listaPaquetes;
    }

    /**
     * Método encargado de comprobar si el paquete que se guardará dentro de la lista pertenece
     * al propio usuario o a otro ajeno
     * @param position Posición del elemento dentro de la lista
     * @return Devuelve 0 si el paquete es recibidido y 1 si el paquete es mandado
     */
    @Override
    public int getItemViewType(int position){
        Paquete paquete = (Paquete) listaPaquetes.get(position);
        int tipoMensaje = PAQUETE_RECIBIDO;

        if(paquete.getIp().equals(USER_IP)){
            tipoMensaje = PAQUETE_MANDADO;
        }

        return tipoMensaje;
    }

    /**
     * Método onCreate que se ejecuta al crear el ViewHolder. Infla el diseño de cada celda, la
     * rellena con el RecyclerHolder y les asigna los oyentes necesarios. En función del tipo de
     * paquete utiliza un layout diferente.
     * @param parent Vista padre en la cual se mostrará el RecyclerHolder
     * @param viewType Tipo de vista. En esta caso llama al método sobreescrito anteriormente:
     *                 getItemViewType()
     * @return Devuelve el objeto de la clase viewHolder creado
     */
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        RecyclerView.ViewHolder viewHolder = null;

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

    /**
     * Método onBind que se encarga rellenar los elementos de la celda creada previamente con la
     * información de la lista de elementos asignada en el constructor.
     * @param holder RecyclerHolder que contiene los componentes que mostrarán la información
     * @param position Position del elemento en la lista de elementos
     */
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

    /**
     * Método que devuelve el tamaño de la lista de paquetes
     * @return Devuelve el número de elementos de la lista
     */
    @Override
    public int getItemCount() {
        return listaPaquetes.size();
    }

    // Clases internas
    // TODO SERÍA MEJORAR CREARNOS UNA CLASE PADRE???
    /**
     * Clase de tipo RecyclerHolder que extiende de ViewHolder. Corresponde a los mensajes mandados
     */
    private class RecyclerHolderSender extends RecyclerView.ViewHolder{
        // Atributos de la clase
        TextView tvMessage;

        /**
         * Constructor por parámetro
         * @param itemView Vista del layout
         */
        public RecyclerHolderSender(@NonNull View itemView) {
            super(itemView);

            tvMessage = itemView.findViewById(R.id.tvOwnMessage);
        }
    }

    /**
     * Clase de tipo RecyclerHolder que extiende de ViewHolder. Corresponde a los mensajes recibidos
     */
    private class RecyclerHolderReceiver extends RecyclerView.ViewHolder{
        // Atributos de la clase
        TextView tvMessage;

        /**
         * Constructor por parámetro
         * @param itemView Vista del layout
         */
        public RecyclerHolderReceiver(@NonNull View itemView) {
            super(itemView);

            tvMessage = itemView.findViewById(R.id.tvOtherMessage);
        }
    }
}
