package com.masterd.android.app.clases.mapa.servicios;

import android.content.ContentResolver;
import android.database.Cursor;
import android.os.AsyncTask;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.masterd.android.app.clases.datos.ProveedorClases;
import com.masterd.android.app.clases.servicios.componentes.CamposClasesDB;

import java.util.ArrayList;

/**
 * Tarea sincronizada, que se dedica a buscar los datos de la base de datos para enviarlos a la clase mapa para que sean colocados en sus respetivos
 * marcadores.
 */
public class SeleccionDatosMarcadorMapa extends AsyncTask {
    //se crea el array con las columnas que guardarán las columnas que serán introducias en la consulta a la base de datos.
    String[] columnas=new String[]{CamposClasesDB.Datos.NOMBRE, CamposClasesDB.Datos.DIRECCION, CamposClasesDB.Datos.LATITUD, CamposClasesDB.Datos.LONGITUD};
    //el cursor donde se guardarán los datos recibidos de la base de datos.
    Cursor cursor;
    //objeto que permite obtener del provider los datos de la base de datos
    ContentResolver contentResolver;
    //la tearea recibirá el content resolver de la clase que o utilizará para de esta manera asociarles.
    public SeleccionDatosMarcadorMapa(ContentResolver contentResolver) {
        this.contentResolver = contentResolver;
    }
    //sobreescribir el métodoprincipal donde obtendrémos el arraylist de los marcadores con los respectivos datos.
    @Override
    protected ArrayList<MarkerOptions> doInBackground(Object[] params) {
        //crear el arraylist que luego recibirá sus datos.
        ArrayList<MarkerOptions>listadoMarcadores = null;
        try{
            //realizar la consulta a la base de datos almacenando los datos en el cursor anteriormente creado
            cursor=contentResolver.query(ProveedorClases.CONTENT_URI,columnas,null,null,null);
            //mover el cursor al inicio
            cursor.moveToFirst();
            //instanciar el listado de los marcadores
            listadoMarcadores= new ArrayList<>();
            //guardar tantos marcadores como clases existan guardadas dentro de la base de datos.
            for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()){
                MarkerOptions marcador= new MarkerOptions();
                marcador.position(new LatLng(Double.valueOf(cursor.getString(cursor.getColumnIndex(CamposClasesDB.Datos.LATITUD))), Double.valueOf(cursor.getString(cursor.getColumnIndex(CamposClasesDB.Datos.LONGITUD)))));
                marcador.icon(BitmapDescriptorFactory.defaultMarker());
                marcador.title(cursor.getString(cursor.getColumnIndex(CamposClasesDB.Datos.NOMBRE)));
                marcador.snippet(cursor.getString(cursor.getColumnIndex(CamposClasesDB.Datos.DIRECCION)));
                listadoMarcadores.add(marcador);
            }
            //controlar las excepciones que pudieran producirse
        }catch(NullPointerException | IllegalStateException e){
            e.printStackTrace();
        }
        //cerrar el cursor
        cursor.close();
        return listadoMarcadores;
    }
}
