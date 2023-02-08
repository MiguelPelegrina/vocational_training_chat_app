package com.example.chat;

import static com.example.chat.ConnectActivity.USER_IP;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.chat.adapter.RecyclerAdapter;
import com.example.chat.model.Connection;
import com.example.chat.model.Paquete;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {
    private ImageButton btnSend;
    private EditText txtMensaje;
    private Connection connection;

    private ArrayList<Paquete> listaPaquetes = new ArrayList<>();
    private RecyclerView recyclerView;
    private RecyclerAdapter recyclerAdapter;

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        btnSend = findViewById(R.id.ibSend);
        txtMensaje = findViewById(R.id.etMensaje);

        recyclerView = findViewById(R.id.recyclerViewChat);
        recyclerAdapter = new RecyclerAdapter(listaPaquetes);

        // Configuramos el layoutManager
        // Nos creamos un LayoutManager, en este caso linear
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        // Invertimos el orden en el que se muestran los elementos de la lista. Los primeros
        // elementos se muestran al final del recyclerView
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        // Configuramos el recyclerView asignandole el adapter y el layoutManager
        recyclerView.setAdapter(recyclerAdapter);
        recyclerView.setLayoutManager(layoutManager);

        intent = getIntent();

        connection = new Connection(this);
        connection.startSocket(new Runnable() {
            @Override
            public void run() {
                Log.d("socket", "segundo socket con misma ip");
                Paquete paquete = connection.getPaquete();
                listaPaquetes.add(0, paquete);
                recyclerAdapter.notifyDataSetChanged();
                //recyclerView.swapAdapter(recyclerAdapter, true);
                //recyclerView.scrollBy(0,0);
                scrollToBottom();
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!txtMensaje.getText().toString().isEmpty()){
                    Paquete datos = new Paquete(intent.getStringExtra("nombre"), USER_IP, intent.getStringExtra("ip"), txtMensaje.getText().toString());
                    connection.sendMessage(datos, intent.getStringExtra("ip"), new Runnable(){
                        @Override
                        public void run() {
                            listaPaquetes.add(0, datos);
                        }
                    });
                    recyclerAdapter.notifyDataSetChanged();
                    //recyclerView.swapAdapter(recyclerAdapter,true);
                    //recyclerView.scrollBy(0,0);
                    scrollToBottom();
                }else{
                    Toast.makeText(ChatActivity.this,"Debe introducir un mensaje",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /**
     * Método encargado de desplazarse a la "primera" posición del RecyclerView. Si se invierte el
     * orden con el método setReverseLayout() del LayoutManager el usuario no tiene que "bajar" de
     * forma manual para ver los mensajes más recientes.
     */
    private void scrollToBottom(){
        recyclerView.scrollToPosition(0);
    }

    @Override
    protected void onStop() {
        super.onStop();
        connection.setSocketState(false);
    }

    @Override
    protected void onPause() {
        super.onPause();
        connection.setSocketState(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        connection.startSocket(new Runnable() {
            @Override
            public void run() {
                Paquete paquete = connection.getPaquete();
                listaPaquetes.add(0, paquete);
                recyclerAdapter.notifyDataSetChanged();
                //recyclerView.swapAdapter(recyclerAdapter, true);
                //recyclerView.scrollBy(0,0);
                scrollToBottom();
            }
        });
    }
}