package com.example.chat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.Formatter;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chat.adapter.RecyclerAdapter;
import com.example.chat.model.Paquete;
import com.example.chat.model.Conection;

import java.util.ArrayList;

public class ConnectActivity extends AppCompatActivity {
    // Declaración de variables
    // Variables de clase
    public static final int SERVER_PORT = 1234;
    public static String USER_IP = "";
    public static String OTHER_IP = "";
    // Variables de la instancia
    private ImageButton btnSend;
    //TODO
    private Button btnConnect;
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

        // Inicialización de las variables
        btnSend = findViewById(R.id.btnSend);
        //TODO
        btnConnect = findViewById(R.id.btnConnect);
        txtConection = findViewById(R.id.txtConection);
        txtName = findViewById(R.id.txtNombre);
        txtIpSelf = findViewById(R.id.txtIpSelf);
        txtIpOther = findViewById(R.id.txtIpOther);
        txtMensaje = findViewById(R.id.txtMensaje);
        recyclerView = findViewById(R.id.recyclerView);
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

        // Obtenemos la IP del usuario
        USER_IP = getUserIpAddress();
        // Mostramos la IP del usuario
        txtIpSelf.setText(USER_IP);
        // Rellenamos gran parte de la IP necesaria para conectar con otro usuario
        txtIpOther.setText(USER_IP.substring(0, USER_IP.lastIndexOf(".") + 1));

        // Establecemos nuestro socket para obtener paquetes de tal forma que cuando llegue un
        // paquete nuevo se añada al recyclerView
        conection = new Conection(this);
        conection.startSocket(new Runnable() {
            @Override
            public void run() {
                Paquete paquete = conection.getPaquete();

                // TODO
                Toast.makeText(ConnectActivity.this,"ha connectado con " + paquete.getIpOther(), Toast.LENGTH_LONG).show();

                listaPaquetes.add(0, paquete);
                recyclerAdapter.notifyDataSetChanged();
                //recyclerView.swapAdapter(recyclerAdapter, true);
                //recyclerView.scrollBy(0,0);
                scrollToBottom();
            }
        });

        //
        txtIpOther.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                update();
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                update();
            }
            @Override
            public void afterTextChanged(Editable editable) {
                update();
            }
        });

        //
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!txtMensaje.getText().toString().isEmpty() && !txtName.getText().toString().isEmpty()){
                    // TODO Se podría informar al usuario con mayor precisión cuando solo el nombre
                    // o solo el mensaje están vacios
                    Paquete datos = new Paquete(txtName.getText().toString(), USER_IP, txtIpOther.getText().toString(), txtMensaje.getText().toString());
                    conection.sendMessage(datos, txtIpOther.getText().toString(), new Runnable(){
                        @Override
                        public void run() {
                            txtConection.setText("Conectado");
                            listaPaquetes.add(0, datos);
                        }
                    });
                    recyclerAdapter.notifyDataSetChanged();
                    //recyclerView.swapAdapter(recyclerAdapter,true);
                    //recyclerView.scrollBy(0,0);
                    scrollToBottom();
                }else{
                    Toast.makeText(ConnectActivity.this,"Su nombre y el campo no pueden " +
                            "estar vacios", Toast.LENGTH_LONG).show();
                }
            }
        });

        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO
                Paquete datos = new Paquete(txtName.getText().toString(), USER_IP, txtIpOther.getText().toString(), txtMensaje.getText().toString());
                conection.sendMessage(datos, txtIpOther.getText().toString(), new Runnable(){
                    @Override
                    public void run() {
                        Toast.makeText(ConnectActivity.this,"ha connectado con " + datos.getIpOther(), Toast.LENGTH_LONG).show();
                        Intent i = new Intent(ConnectActivity.this, ChatActivity.class);
                        conection.setSocketState(false);
                        i.putExtra("nombre", txtName.getText().toString());
                        i.putExtra("ip", txtIpOther.getText().toString());
                        startActivity(i);
                    }
                });
            }
        });
    }

    //Métodos auxiliares
    /**
     * Método encargado de modificar el estado de la conexión cada vez que se modifica la IP a la
     * que se desea mandar un mensaje.
     */
    private void update() {
        txtConection.setText("Sin conectar");
    }

    //TODO
    /**
     * Método encargado de desplazarse a la "primera" posición del RecyclerView. Si se invierte el
     * orden con el método setReverseLayout() del LayoutManager el usuario no tiene que "bajar" de
     * forma manual para ver los mensajes más recientes.
     */
    private void scrollToBottom(){
        recyclerView.scrollToPosition(0);
    }

    /**
     * Método encargado de obtener la IP actual del usuario
     * @return Devuelve la IP del usuario
     */
    private String getUserIpAddress(){
        WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);

        return Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
    }
}