package com.example.chat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chat.adapter.RecyclerAdapter;
import com.example.chat.model.Paquete;
import com.example.chat.model.Conection;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {
    //Declaración de variables
    public static String USER_IP = "";
    public static String OTHER_IP = "";
    public static String USER_NAME = "";
    public static final int SERVER_PORT = 1234;

    private ImageButton btnSend;
    private TextView txtConection;
    private TextView txtName;
    private TextView txtIpSelf;
    private TextView txtIpOther;
    private TextView txtMensaje;

    private Conection conection;

    private ArrayList<Paquete> listaPaquetes = new ArrayList<>();
    private RecyclerView recyclerView;
    private RecyclerAdapter recyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);

        btnSend = findViewById(R.id.btnSend);
        txtConection = findViewById(R.id.txtConection);
        txtName = findViewById(R.id.txtNombre);
        txtIpSelf = findViewById(R.id.txtIpSelf);
        txtIpOther = findViewById(R.id.txtIpOther);
        txtMensaje = findViewById(R.id.txtMensaje);

        // Obtenemos la IP del usuario
        USER_IP = getUserIpAddress();
        // Mostramos la IP del usuario
        txtIpSelf.setText(USER_IP);
        // Rellenamos gran parte de la IP necesaria para conectar con otro usuario
        txtIpOther.setText(USER_IP.substring(0, USER_IP.lastIndexOf(".") + 1));

        recyclerView = findViewById(R.id.recyclerView);
        recyclerAdapter = new RecyclerAdapter(listaPaquetes);

        // Nos creamos un LayoutManager, en este caso linear
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        // Configuramos el recyclerView asignandole el adapter y el layoutManager
        recyclerView.setAdapter(recyclerAdapter);
        recyclerView.setLayoutManager(layoutManager);

        conection = new Conection(this);
        conection.startSocket(new Runnable() {
            @Override
            public void run() {
                Paquete paquete = conection.getPaquete();
                listaPaquetes.add(0, paquete);
                Log.d("paquete recibido actividad", paquete.getMensaje());
                recyclerAdapter.notifyDataSetChanged();
                scrollToBottom();
            }
        });

        // TODO Textwatcher que cuando cambia la IP de una que está conectada a otro aún sin
        // conectar cambie el texto de txtConection a "Sin conectar"

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!txtMensaje.getText().toString().isEmpty() && !txtName.getText().toString().isEmpty()){
                    // TODO Se podría informar al usuario con mayor precisión cuando solo el nombre
                    // o solo el mensaje están vacios
                    Paquete datos = new Paquete();
                    datos.setNombre(USER_NAME);
                    datos.setIp(USER_IP);
                    datos.setMensaje(txtMensaje.getText().toString());
                    conection.sendMessage(datos, txtIpOther.getText().toString(), new Runnable(){
                        @Override
                        public void run() {
                            txtConection.setText("Conectado");
                            listaPaquetes.add(0, datos);
                        }
                    });
                    recyclerAdapter.notifyDataSetChanged();
                    scrollToBottom();
                    /*
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                OTHER_IP = txtIpOther.getText().toString();
                                // Nos creamos un canal de conexión con el servidor
                                Socket socket = new Socket(OTHER_IP, SERVER_PORT);
                                if(socket.isBound()){
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            txtConection.setText("Conectado");
                                        }
                                    });
                                    // TODO PONER AQUI LO POSTERIOR
                                }
                                // Preparamos el objeto con la información antes de mandarlo por el canal
                                Paquete datos = new Paquete();
                                datos.setNombre(USER_NAME);
                                datos.setIp(USER_IP);
                                datos.setMensaje(txtMensaje.getText().toString());
                                // Abrimos un nuevos flujo de datos
                                ObjectOutputStream paquete_datos = new ObjectOutputStream(socket.getOutputStream());
                                // Escribimos el objeto
                                paquete_datos.writeObject(datos);
                                listaPaquetes.add(0, datos);
                                // Cerramos el flujo y el canal
                                paquete_datos.close();
                                socket.close();
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                        }
                    }).start();
                    recyclerAdapter.notifyDataSetChanged();
                    scrollToBottom();*/
                }else{
                    Toast.makeText(ChatActivity.this,"Su nombre y el campo no pueden " +
                            "estar vacios", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    //Métodos auxiliares
    /**
     *
     */
    private void scrollToBottom(){
        recyclerView.scrollToPosition(0);
    }

    /**
     *
     * @return
     */
    private String getUserIpAddress(){
        WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);

        return Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
    }
}