package com.example.chat.model;

import static com.example.chat.ConnectActivity.SERVER_PORT;

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
public class Connection {
    // Atributos de la instancia
    // Variable necesaria para acceder a los elementos visuales
    private final Handler handler;
    private Paquete paquete;
    // Variable semáforo para abrir y cerrar el serversocket
    private boolean socketStateOpen;
    private ServerSocket serverSocket;

    /**
     * Constructor por parámetros
     * @param context Obtenemos la actividad que llama a esta clase para poder instanciar un Handler
     *                que posteriormente se encarga de modificar los elementos gráficos en la vista
     */
    public Connection(Context context){
        // Instanciamo el handler y abrimos el socket
        this.handler = new Handler(context.getMainLooper());
        //this.socketStateOpen = true;
    }

    /**
     * Método encargado de iniciar el socket para recibir mensajes de entrada MIENTRAS que el estado
     * del socket (socketStateOpen) sea verdadero
     * @param runnable Interfaz cuyas instrucciones se van a ejecutar DESPUÉS de haber recibido el
     *                 mensaje. De esta forma podemos modificar los elementos de la vista desde esta
     *                 clase.
     */
    public void startSocket(Runnable runnable){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    socketStateOpen = true;
                    // Instanciamos el ServerSocket
                    serverSocket = new ServerSocket(SERVER_PORT);
                    // Mientras que el estado de apertura del socket sea verdadero, es decir,
                    // mientras que el semáforo está en verde
                    while(socketStateOpen) {
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
     * Método encargado de mandar un paquete a otro usuario indicado a través de una IP y
     * actualizando el recyclerView de la vista
     * @param datos Paquetes de datos con información del usuario (nombre, ip, mensaje)
     * @param ipOther IP del usuario al que se desea mandar el paquete
     * @param runnable Interfaz que se va a ejecutar para actualizar los elementos de la vista
     */
    public void sendMessage(Paquete datos, String ipOther, Runnable runnable){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Declaramos e instanciamos un socket con la IP de otro usuario
                    Socket socket = new Socket(ipOther, SERVER_PORT);
                    // Comprobamos que el socket este asignado
                    if (socket.isBound()) {
                        // Abrimos un nuevos flujo de datos
                        ObjectOutputStream flujo_salida = new ObjectOutputStream(socket.getOutputStream());
                        // Escribimos el objeto
                        flujo_salida.writeObject(datos);
                        // Establecemos el código que se va a ejecutar en la vista
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
     * Método encargado de devolver el paquete actual de la conexión
     * @return Devuelve el paquete actual
     */
    public Paquete getPaquete(){
        return this.paquete;
    }

    /**
     * Método encargado para abrir el estado del socket a abierto, es decir, ponemos el semáforo
     * en verde y cerramos el serverSocket
     */
    public void closeSocket(){
        this.socketStateOpen = false;
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Método encargado de ejecutar las instrucciones de la actividad para actualizar la vista
     * @param runnable Interfaz que se va a ejecutar para actualizar los elementos gráficos
     */
    private void runOnUiThread(Runnable runnable){
        handler.post(runnable);
    }
}
