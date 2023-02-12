package com.example.chat;

import static com.example.chat.ConnectActivity.USER_IP;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;

import com.example.chat.adapter.RecyclerAdapter;
import com.example.chat.model.Connection;
import com.example.chat.model.Paquete;

import java.util.ArrayList;

/**
 * Actividad encargada de mostrar la conversación entre dos usuarios previamente conectados
 */
public class ChatActivity extends AppCompatActivity {
    // Atributos de la instancia
    private ImageButton btnSend;
    private EditText txtMensaje;
    private Connection connection;
    private ArrayList<Paquete> listaPaquetes = new ArrayList<>();
    private RecyclerView recyclerView;
    private RecyclerAdapter recyclerAdapter;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Inicialización de las variables
        btnSend = findViewById(R.id.ibSend);
        txtMensaje = findViewById(R.id.etMensaje);
        intent = getIntent();

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setTitle(intent.getStringExtra("nombre"));
        }

        // Asignamos un oyente para registrar los cambios en el campo de texto de mensaje de tal
        // forma que si está vacío, el botón de mandar mensajes no es visible, pero en cuanto tenga
        // algún carácter (que no sea un espacio en blanco), visibiliza el botón
        txtMensaje.addTextChangedListener(new TextWatcher() {
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

        // Inicializamos el recyclerView
        recyclerView = findViewById(R.id.recyclerViewChat);
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

        // Iniciamos una nueva conexión de tal forma que podemos recibir los mensajes de otro usuario
        connection = new Connection(this);
        connection.startSocket(new Runnable() {
            @Override
            public void run() {
                // Obtenemos cada paquete
                Paquete paquete = connection.getPaquete();
                // Lo añadimos al recyclerView y le notificamos de que se ha modificado
                listaPaquetes.add(0, paquete);
                recyclerAdapter.notifyDataSetChanged();
                // Nos desplazamos hacia abajo en el recyclerView. De esta forma se mostrarán los
                // últimos mensajes
                scrollToBottom();
            }
        });

        // Cada vez que se presiona le botón de mandar, se creará un paquete que se manda a la IP
        // indicada y posteriormente se actualizan los elementos de la vista
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Preparamos el paquete
                Paquete datos = new Paquete(intent.getStringExtra("nombre"), USER_IP, intent.getStringExtra("ip"), txtMensaje.getText().toString());
                connection.sendMessage(datos, intent.getStringExtra("ip"), new Runnable(){
                    @Override
                    public void run() {
                        // Modificamos los elementos de la vista
                        listaPaquetes.add(0, datos);
                        recyclerAdapter.notifyDataSetChanged();
                        // Vaciamos el campo de texto del mensaje, ya que ya se ha mandado
                        txtMensaje.setText("");
                    }
                });
                scrollToBottom();
            }
        });
    }

    // Controlamos que el socket que recibe los mensajes se cierra al cerrar la aplicación y al
    // cambiar de una actividad a otra
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

    // Cuando volvemos a esta actividad reiniciamos la conexión de tal forma que se vuelven a recibir
    // mensajes
    @Override
    protected void onResume() {
        super.onResume();
        connection.startSocket(new Runnable() {
            @Override
            public void run() {
                Paquete paquete = connection.getPaquete();
                listaPaquetes.add(0, paquete);
                recyclerAdapter.notifyDataSetChanged();
                scrollToBottom();
            }
        });
    }

    // Métodos auxiliares
    /**
     * Método encargado de desplazarse a la "primera" posición del RecyclerView. Si se invierte el
     * orden con el método setReverseLayout() del LayoutManager el usuario no tiene que "bajar" de
     * forma manual para ver los mensajes más recientes.
     */
    private void scrollToBottom(){
        recyclerView.scrollToPosition(0);
    }

    /**
     * Método encargado de actualizar la visibilidad del botón de mandar en función del contenido
     * del campo de texto de los mensajes
     */
    private void update() {
        if(txtMensaje.getText().toString().trim().isEmpty()){
            btnSend.setVisibility(View.INVISIBLE);
        }else{
            btnSend.setVisibility(View.VISIBLE);
        }
    }
}