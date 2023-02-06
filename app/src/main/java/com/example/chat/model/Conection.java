package com.example.chat.model;

import static com.example.chat.ChatActivity.SERVER_PORT;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.example.chat.R;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Conection {
    private final Handler handler;
    private Paquete paquete;

    public Conection(Context context){
        this.handler = new Handler(context.getMainLooper());
    }

    public void startSocket(Runnable runnable){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //
                    ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
                    while(true) {
                        // Conectamos el canal
                        Socket socket = serverSocket.accept();
                        // Obtenemos la información del canal a través de un flujo de datos
                        ObjectInputStream flujo_entrada = new ObjectInputStream(socket.getInputStream());
                        // Casteamos el paquete recibido
                        paquete = (Paquete) flujo_entrada.readObject();
                        Log.d("mensaje recibido clase", paquete.getMensaje());
                        // Modificamos los elementos gráficos
                        runOnUiThread(runnable);
                        //
                        flujo_entrada.close();
                        socket.close();
                    }
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void sendMessage(Paquete datos, String ipOther, Runnable runnable){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Socket socket = new Socket(ipOther, SERVER_PORT);
                    if (socket.isBound()) {
                        // Abrimos un nuevos flujo de datos
                        ObjectOutputStream paquete_datos = new ObjectOutputStream(socket.getOutputStream());
                        // Escribimos el objeto
                        paquete_datos.writeObject(datos);
                        // Cerramos el flujo y el canal
                        paquete_datos.close();
                        socket.close();
                    }
                    runOnUiThread(runnable);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }}).start();
    }

    public Paquete getPaquete(){
        return this.paquete;
    }

    private void runOnUiThread(Runnable runnable){
        handler.post(runnable);
    }
}
