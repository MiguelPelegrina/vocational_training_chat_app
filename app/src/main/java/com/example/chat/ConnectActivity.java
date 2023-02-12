package com.example.chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chat.model.Paquete;
import com.example.chat.model.Connection;

/**
 * Actividad encargada de establecer la conexión inicial con otro usuario
 */
public class ConnectActivity extends AppCompatActivity {
    // Declaración de variables
    // Variables de clase
    public static final int SERVER_PORT = 1234;
    public static String USER_IP = "";
    // Variables de la instancia
    private Button btnConnect;
    private TextView txtName;
    private TextView txtIpSelf;
    private TextView txtIpOther;
    private Connection connection;
    private SharedPreferences themePreferences;
    private boolean nightTheme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Obtenemos las preferencias encargadas de guardar el tema elegido
        themePreferences = PreferenceManager.getDefaultSharedPreferences(this);
        nightTheme = themePreferences.getBoolean("nightTheme", false);
        // Modificamos el tema
        if(nightTheme){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);

        // Inicialización de las variables
        btnConnect = findViewById(R.id.btnConnect);
        txtName = findViewById(R.id.txtNombre);
        txtIpSelf = findViewById(R.id.txtIpSelf);
        txtIpOther = findViewById(R.id.txtIpOther);

        // Obtenemos la IP del usuario
        USER_IP = getUserIpAddress();
        // Mostramos la IP del usuario
        txtIpSelf.setText(USER_IP);
        // Rellenamos gran parte de la IP necesaria para conectar con otro usuario
        txtIpOther.setText(USER_IP.substring(0, USER_IP.lastIndexOf(".") + 1));

        // Establecemos nuestro socket para obtener paquetes de tal forma que cuando llegue un
        // paquete nuevo se añada al recyclerView
        connection = new Connection(this);
        connectSocket();

        // Implementamos un oyente para conectar con otro usuario
        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Comprobamos que el nombre no esté vacio
                if(!txtName.getText().toString().trim().isEmpty()){
                    // Preparamos el paquete que queremos a mandar
                    Paquete datos = new Paquete(txtName.getText().toString(), USER_IP,
                            txtIpOther.getText().toString(), txtName.getText() +
                            " se ha conectado a la conversación!");
                    // Mandamos el mensaje
                    connection.sendMessage(datos, txtIpOther.getText().toString(), new Runnable(){
                        @Override
                        public void run() {
                            Intent i = new Intent(ConnectActivity.this, ChatActivity.class);
                            // Pasamos la información del mensaje mandado
                            i.putExtra("nombre", txtName.getText().toString());
                            i.putExtra("ip", txtIpOther.getText().toString());
                            // Nos cambiamos a la actividad de la conversación
                            startActivity(i);
                        }
                    });
                }else{
                    Toast.makeText(ConnectActivity.this,"Debe introducir un nombre", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Usamos un inflater para construir la vista pasandole el menu por defecto como parámetro
        // para colocarlo en la vista
        getMenuInflater().inflate(R.menu.action_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    // Sobrescribimos el metodo onOptionsItemSelected para manejar las diferentes opciones del menu
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            // Si queremos modificar las preferencias
            case R.id.switch_theme:
                // Modificamos el tema
                if(nightTheme){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    themePreferences.edit().putBoolean("nightTheme", false).apply();
                }else{
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    themePreferences.edit().putBoolean("nightTheme", true).apply();
                }
                // Recreamos la vista
                recreate();
                break;
        }

        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(connection != null){
            connection.closeSocket();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(connection != null){
            connection.closeSocket();
        }
    }

    // Cuando volvemos a abrir la aplicación nos volvemos a conectar el socket
    @Override
    protected void onResume() {
        super.onResume();
        if(connection != null){
            connectSocket();
        }
    }

    //Métodos auxiliares
    /**
     * Método encargado de obtener la IP actual del usuario
     * @return Devuelve la IP del usuario
     */
    private String getUserIpAddress(){
        WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);

        return Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
    }

    /**
     * Método encargado de conectarse otro socket, mandando un mensaje de tal forma que informa al
     * usuario de que otro quiero conectarse con el y ofreciendo la información para poder
     * conectarse
     */
    private void connectSocket(){
        connection.startSocket(new Runnable() {
            @Override
            public void run() {
                Paquete paquete = connection.getPaquete();
                String mensaje = "";
                // Si el usuario manda el paquete se avisa que se ha conectado
                if(paquete.getIp().equals(USER_IP)){
                    mensaje = "Ha connectado con " + txtIpOther.getText();
                }else{
                    // Si el usuario que manda el mensaje es otro
                    mensaje = paquete.getNombre() + " con la IP " + paquete.getIp() + " quiere conectarse contigo!";
                }
                Toast.makeText(ConnectActivity.this, mensaje, Toast.LENGTH_LONG).show();
            }
        });
    }
}