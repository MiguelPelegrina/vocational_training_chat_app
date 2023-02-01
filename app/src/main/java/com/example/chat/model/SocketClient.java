package com.example.chat.model;

import static com.example.chat.MainActivity.SERVER_PORT;

import android.content.Context;
import android.os.Handler;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketClient {
    private final Handler handler;
    private SocketServer socketServer;
    private Socket socket;

    public SocketClient(Context context, SocketServer socketServer,String ip){
        this.handler = new Handler(context.getMainLooper());
        this.socketServer = socketServer;
        try {
            this.socket = new Socket(ip, SERVER_PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void runSocketClient() {
        // COMO ACCEDER A LOS ELEMENTOS GRÁFICOS
        /*if(!txtMensaje.getText().toString().isEmpty()){
            try {
                // Nos creamos un canal de conexión con el servidor
                Socket socket = new Socket(txtIPUser2.getText().toString(), SERVER_PORT);
                // Preparamos el objeto con la información antes de mandarlo por el canal
                Paquete datos = new Paquete();
                datos.setNombre(txtNombre.getText().toString());
                datos.setIp(txtIPUser2.getText().toString());
                datos.setMensaje(txtMensaje.getText().toString());
                // Abrimos un nuevos flujo de datos
                ObjectOutputStream paquete_datos = new ObjectOutputStream(socket.getOutputStream());
                // Escribimos el objeto
                paquete_datos.writeObject(datos);
                // Cerramos el flujo y el canal
                paquete_datos.close();
                socket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }*/
    }

    public void runSocketServer(Runnable runnable){
        this.socketServer.runSocketServer(runnable);
    }
}
