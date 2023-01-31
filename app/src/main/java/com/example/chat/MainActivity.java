package com.example.chat;

import androidx.appcompat.app.AppCompatActivity;

import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.chat.model.Paquete;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity {
    //Variables de clase
    //public static String SERVER_IP = "";
    public static String USER_IP = "";
    public static final int SERVER_PORT = 1234;

    //Declaración de variables
    private Button btnMandar;
    private TextView txtMensaje;
    private TextView txtNombre;
    private TextView txtIpUser;
    private TextView txtIPUser2;
    private TextView taMensajes;

    private Thread hiloCliente = null;
    private Thread hiloServidor = null;
    private Paquete paqueteCliente;
    private Paquete paqueteServidor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnMandar = findViewById(R.id.btnMandar);
        txtMensaje = findViewById(R.id.txtMensaje);
        txtNombre = findViewById(R.id.txtNombre);
        txtIpUser = findViewById(R.id.txtIpUser);
        txtIPUser2 = findViewById(R.id.txtIpUser2);
        taMensajes = findViewById(R.id.taMensajes);

        // Obtenemos la IP del usuario
        USER_IP = getUserIpAddress();
        // Mostramos la IP del usuario
        txtIpUser.setText(USER_IP);

        hiloServidor = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Nos creamos el canal del servidor
                    ServerSocket servidor = new ServerSocket(SERVER_PORT);

                    while(true){
                        // Conectamos el canal
                        Socket socket = servidor.accept();
                        // Obtenemos la información del canal a través de un flujo de datos
                        ObjectInputStream paquete_datos = new ObjectInputStream(socket.getInputStream());
                        // Casteamos el paquete recibido
                        paqueteServidor = (Paquete) paquete_datos.readObject();
                        // Modificamos los elementos gráficos
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                taMensajes.append("\n" + paqueteServidor.getNombre() + ": " +
                                        paqueteServidor.getMensaje() + " para " +
                                        paqueteServidor.getIp());
                            }
                        });
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
                } catch (IOException | ClassNotFoundException ex) {
                    ex.printStackTrace();
                }
            }
        });
        hiloServidor.start();

        hiloCliente = new Thread(new Runnable() {
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
                        paqueteCliente = (Paquete) flujoentrada.readObject();
                        // Modificamos los elementos gráficos
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                taMensajes.append("\n" + paqueteCliente.getNombre() + ": " + paqueteCliente.getMensaje());
                            }
                        });
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });
        hiloCliente.start();

        btnMandar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if(!txtMensaje.getText().toString().isEmpty()){
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
                        }
                    }
                }).start();
            }
        });
    }


    //Métodos auxiliares

    /**
     *
     * @return
     */
    private String getUserIpAddress(){
        WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);

        return Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
    }

}