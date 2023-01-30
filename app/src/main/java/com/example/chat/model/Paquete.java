package com.example.chat.model;

import java.io.Serializable;

public class Paquete implements Serializable {
    private String nombre, ip, mensaje;

    public String getNombre() {
        return nombre;
    }

    public String getIp() {
        return ip;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}
