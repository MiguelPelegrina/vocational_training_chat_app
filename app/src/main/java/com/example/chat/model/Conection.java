package com.example.chat.model;

import static com.example.chat.ConnectActivity.SERVER_PORT;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.io.ObjectInputStream;
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
                        // TODO DIFERENCIAR ENTRE CONECTAR Y RECIBIR MENSAJES?
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

    public Paquete getPaquete(){
        return this.paquete;
    }

    private void runOnUiThread(Runnable runnable){
        handler.post(runnable);
    }
}
