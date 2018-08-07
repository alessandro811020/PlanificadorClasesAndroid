package com.masterd.android.app.clases.mapa.servicios;

import android.os.AsyncTask;

/**
 * Esta tarea tiene como objetivo aliviar el trabajo de comprobación de las actividades y entonces de manera sincronizada
 * se realiza en un proceso paralelo para que esta compruebe si hay campos vacíos a la hora de introducir los datos en la
 * base de datos
 */
public class ComprobarCampos extends AsyncTask {
    //campos que recibirán en el constructor los campos a comprobar sus valores.
    String[] campos;
    //obtener los campos que se han introducido por el usuario en el constructor
    public ComprobarCampos(String[] camposEntrantes) {
        campos=camposEntrantes;
    }
    //realización de la comprobación de los campos
    @Override
    protected Boolean doInBackground(Object[] params) {
        //crear la variable boolean que respondera si están los campos llenos o no.
        boolean respuesta=true;
        //para todos los campos comprobar y en caso de que alguno esté vacío, asignar el valor de falso
        for (int i=0;i<campos.length;i++){
            if (campos[i].equals("")){
                respuesta=false;
            }
        }
        //devolver la respuesta de la comprobación
        return respuesta;
    }
}
