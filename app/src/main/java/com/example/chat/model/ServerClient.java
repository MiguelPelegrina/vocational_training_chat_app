package com.example.chat.model;

import static com.example.chat.MainActivity.SERVER_PORT;

import android.content.Context;
import android.os.Handler;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerClient {
    private final Handler handler;
    private Paquete paquete;

    public ServerClient(Context context){
        this.handler = new Handler(context.getMainLooper());
    }

    public void start(Runnable runnable){
        startServer(runnable);
        startClient(runnable);
    }

    private void startServer(Runnable runnable){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
                    while(true) {
                        // Conectamos el canal
                        Socket socket = serverSocket.accept();
                        // Obtenemos la información del canal a través de un flujo de datos
                        ObjectInputStream paquete_datos = new ObjectInputStream(socket.getInputStream());
                        // Casteamos el paquete recibido
                        paquete = (Paquete) paquete_datos.readObject();
                        // Modificamos los elementos gráficos
                        runOnUiThread(runnable);
                        // TODO Mandar desde el servidor un paquete de datos a otro usuario indicado
                        // por el usuario
                        /*Socket enviaDestinatario = new Socket(USERIPDIFERENTE???,SERVER_PORT);
                        ObjectOutputStream paqueteReenvio = new ObjectOutputStream(enviaDestinatario.getOutputStream());
                        paqueteReenvio.writeObject(paquete_recibido);
                        paqueteReenvio.close();
                        enviaDestinatario.close();*/
                        // Cerramos el canal
                        socket.close();
                    }
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void startClient(Runnable runnable){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    // Nos creamos el canal del servidor del cliente para obtener los datos del
                    // servidor
                    ServerSocket servidor_cliente = new ServerSocket(SERVER_PORT);

                    while(true){
                        // Conectamos el canal
                        Socket cliente = servidor_cliente.accept();
                        // Obtenemos la información del canal a través de un flujo de datos
                        ObjectInputStream flujoentrada = new ObjectInputStream(cliente.getInputStream());
                        // Casteamos el paquete recibido
                        paquete = (Paquete) flujoentrada.readObject();
                        // Modificamos los elementos gráficos
                        runOnUiThread(runnable);
                        cliente.close();
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void runOnUiThread(Runnable runnable){
        handler.post(runnable);
    }

    public Paquete getPaquete(){
        return this.paquete;
    }
}
