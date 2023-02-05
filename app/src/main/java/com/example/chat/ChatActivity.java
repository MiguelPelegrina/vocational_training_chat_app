package com.example.chat;

import static com.example.chat.ConnectActivity.OTHER_IP;
import static com.example.chat.ConnectActivity.USER_IP;
import static com.example.chat.ConnectActivity.USER_NAME;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.chat.adapter.RecyclerAdapter;
import com.example.chat.model.ConectionChat;
import com.example.chat.model.Paquete;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {
    public static final int SERVER_PORT_2 = 1235;
    // Declaración de variables
    private ImageButton btnSend;
    private TextView txtMensaje;

    private ArrayList<Paquete> listaPaquetes = new ArrayList<>();
    private RecyclerView recyclerView;
    private RecyclerAdapter recyclerAdapter;

    private ConectionChat connexion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        btnSend = findViewById(R.id.btnSend);
        txtMensaje = findViewById(R.id.txtMensaje);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerAdapter = new RecyclerAdapter(listaPaquetes);

        // Nos creamos un LayoutManager, en este caso linear
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        //layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.setStackFromEnd(true);
        //layoutManager.setSmoothScrollbarEnabled(false);
        layoutManager.setReverseLayout(true);
        // Configuramos el recyclerView asignandole el adapter y el layoutManager
        recyclerView.setAdapter(recyclerAdapter);
        recyclerView.setLayoutManager(layoutManager);

        connexion = new ConectionChat(this);
        // TODO NO RUNNABLE NEEDED ANYMORE
        connexion.startSocket(new Runnable() {
            @Override
            public void run() {
                Paquete paquete = connexion.getPaquete();
                listaPaquetes.add(paquete);
                recyclerAdapter.notifyDataSetChanged();
                scrollToBottom();
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if(!txtMensaje.getText().toString().isEmpty()){
                            try {
                                // TODO CHECK WHAT YOU NEED TO SEND
                                // Nos creamos un canal de conexión con el servidor
                                Socket socket = new Socket(OTHER_IP, SERVER_PORT_2);
                                // Preparamos el objeto con la información antes de mandarlo por el canal
                                Paquete datos = new Paquete();
                                datos.setNombre(USER_NAME);
                                datos.setIp(USER_IP);
                                datos.setMensaje(txtMensaje.getText().toString());
                                // Abrimos un nuevos flujo de datos
                                ObjectOutputStream paquete_datos = new ObjectOutputStream(socket.getOutputStream());
                                // Escribimos el objeto
                                paquete_datos.writeObject(datos);
                                listaPaquetes.add(datos);
                                // Cerramos el flujo y el canal
                                paquete_datos.close();
                                socket.close();
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                        }
                    }
                }).start();
                recyclerAdapter.notifyDataSetChanged();
                scrollToBottom();
            }
        });
    }

    private void scrollToBottom(){
        recyclerView.scrollToPosition(0);
    }
}