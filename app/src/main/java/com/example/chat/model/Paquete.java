package com.example.chat.model;

import java.io.Serializable;

/**
 *
 */
public class Paquete implements Serializable {
    // Atributos de la instancia
    private String nombre, ip, ipOther, mensaje;

    /**
     * Constructor por parámetros
     * @param nombre Nombre del usuario que manda el mensaje
     * @param ip Ip del usuario que manda el mensaje
     * @param ipOther Ip del usuario al que se manda el mensaje
     * @param mensaje Mensaje que se manda al otro usuario
     */
    public Paquete(String nombre, String ip, String ipOther, String mensaje) {
        this.nombre = nombre;
        this.ip = ip;
        this.ipOther = ipOther;
        this.mensaje = mensaje;
    }

    // Getter
    public String getNombre() {
        return nombre;
    }

    public String getIp() {
        return ip;
    }

    public String getIpOther() {
        return ipOther;
    }

    public String getMensaje() {
        return mensaje;
    }

    // Setter
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setIpOther(String ipOther) {
        this.ipOther = ipOther;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}
