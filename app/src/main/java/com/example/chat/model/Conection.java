package com.example.chat.model;

import static com.example.chat.ChatActivity.SERVER_PORT;

import android.content.Context;
import android.os.Handler;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Clase que gestiona las conexiones y nos permite recibir y mandar paquetes
 */
public class Conection {
    // Atributos de la instancia
    private final Handler handler;
    // TODO ES NECESARIO TENER DOS PAQUETES, UNO PARA MANDAR Y OTRO PARA RECIBIR PORQUE PUEDE DAR
    // PROBLEMAS CUANDO SE MANDAN DOS PAQUETES A LA VEZ?
    private Paquete paquete;
    private boolean openSocket;

    /**
     * Constructor por parámetros
     * @param context Obtenemos la actividad que llama a esta clase para poder instanciar un Handler
     *                que posteriormente se encarga de modificar los elementos gráficos en la vista
     */
    public Conection(Context context){
        this.handler = new Handler(context.getMainLooper());
        this.openSocket = true;
    }

    /**
     * Método
     * @param runnable
     */
    public void startSocket(Runnable runnable){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //
                    ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
                    while(openSocket) {
                        // Conectamos el canal
                        Socket socket = serverSocket.accept();
                        // Obtenemos la información del canal a través de un flujo de datos
                        ObjectInputStream flujo_entrada = new ObjectInputStream(socket.getInputStream());
                        // Casteamos el paquete recibido
                        paquete = (Paquete) flujo_entrada.readObject();
                        // Modificamos los elementos gráficos
                        runOnUiThread(runnable);
                        // Cerramos
                        flujo_entrada.close();
                        socket.close();
                    }
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     *
     * @param datos
     * @param ipOther
     * @param runnable
     */
    public void sendMessage(Paquete datos, String ipOther, Runnable runnable){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Socket socket = new Socket(ipOther, SERVER_PORT);
                    if (socket.isBound()) {
                        // Abrimos un nuevos flujo de datos
                        ObjectOutputStream flujo_salida = new ObjectOutputStream(socket.getOutputStream());
                        // Escribimos el objeto
                        flujo_salida.writeObject(datos);
                        runOnUiThread(runnable);
                        // Cerramos el flujo y el canal
                        flujo_salida.close();
                        socket.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }}).start();
    }

    /**
     * Método encargado de devolver el paquete
     * @return
     */
    public Paquete getPaquete(){
        return this.paquete;
    }

    public void setOpenSocket(boolean openSocket){
        this.openSocket = openSocket;
    }

    /**
     *
     * @param runnable
     */
    private void runOnUiThread(Runnable runnable){
        handler.post(runnable);
    }
}
