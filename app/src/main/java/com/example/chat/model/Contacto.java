package com.example.chat.model;

import java.util.ArrayList;

public class Contacto {
    private String nombreContacto;
    private String ipContacto;
    private ArrayList<String> mensajes;

    public Contacto(String nombreContacto, String ipContacto) {
        this.nombreContacto = nombreContacto;
        this.ipContacto = ipContacto;
        this.mensajes = new ArrayList<>();
    }

    public String getNombreContacto() {
        return nombreContacto;
    }

    public void setNombreContacto(String nombreContacto) {
        this.nombreContacto = nombreContacto;
    }

    public String getIpContacto() {
        return ipContacto;
    }

    public void setIpContacto(String ipContacto) {
        this.ipContacto = ipContacto;
    }

    public ArrayList<String> getMensajes() {
        return mensajes;
    }

    public void setMensajes(ArrayList<String> mensajes) {
        this.mensajes = mensajes;
    }
}
