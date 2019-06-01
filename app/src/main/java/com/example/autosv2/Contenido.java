package com.example.autosv2;

import android.widget.ImageView;
import android.widget.TextView;

public class Contenido {
    byte[] imagen;
    String[] atributos;
    boolean enabled;

    public Contenido(byte[] imagen, String[] atributos, boolean enabled) {
        this.imagen = imagen;
        this.atributos = atributos;
        this.enabled = enabled;
    }

    public byte[] getImagen() {
        return imagen;
    }

    public String getAtributo(int index) {
        return atributos[index];
    }

    public boolean isEnabled() {
        return enabled;
    }
}
