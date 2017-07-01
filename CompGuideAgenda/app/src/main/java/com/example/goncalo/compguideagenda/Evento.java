package com.example.goncalo.compguideagenda;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Goncalo on 30/06/2017.
 */

public class Evento {

    public String inicio;
    public String fim;
    public String descricao;

    public Evento() {
    }


    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String toString(){
        String st = "########## inicio "+inicio+" fim"+fim+" descricao"+descricao;
        return st;
    }
}
