package com.masterd.android.app.clases.acciones;

import android.content.Intent;

import com.masterd.android.app.clases.servicios.componentes.Constants;

/**
 * clase que se refiere a acciones de la clase de seleccionado, en este caso uno de ellos.
 */
public class AccionSeleccionado {
    //objeto de la actividad Seleccionado
    private final Seleccionado seleccionado;
    //constructor para inicializar a seleccionado
    public AccionSeleccionado(Seleccionado seleccionado) {
        this.seleccionado = seleccionado;
    }
    //método que se encarga de lanzar el intent de ir a modificar al seleccionado
    void modificarSeleccionado(){
        seleccionado.setIntent(new Intent());
        seleccionado.getIntent().putExtra(Constants.ESCOGIDO,seleccionado.usuario);
        seleccionado.getIntent().setClass(seleccionado.getApplicationContext(),Modificar.class);
        seleccionado.startActivity(seleccionado.getIntent());
    }


}
