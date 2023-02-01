package com.example.chat.controller;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.example.chat.R;
import com.example.chat.adapter.RecyclerAdapterContactos;
import com.example.chat.model.Contacto;

import java.util.ArrayList;
import java.util.List;

public class ListActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    RecyclerAdapterContactos recAdapter;

    private String nombre;
    private String ip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        Intent intent = getIntent();
        nombre = intent.getStringExtra("nombre");
        ip = intent.getStringExtra("ip");

        recyclerView = findViewById(R.id.recyclerView);
        recAdapter = new RecyclerAdapterContactos(getListContacto());

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        recyclerView.setAdapter(recAdapter);
        recyclerView.setLayoutManager(layoutManager);
    }

    /**
     *
     * @return
     */
    public List<Contacto> getListContacto(){
        ArrayList<Contacto> list = new ArrayList<>();

        list.add(new Contacto(nombre, ip));

        return list;
    }
}