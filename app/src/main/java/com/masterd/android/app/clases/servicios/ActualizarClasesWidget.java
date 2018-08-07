package com.masterd.android.app.clases.servicios;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.RemoteViews;

import com.masterd.android.app.clases.R;
import com.masterd.android.app.clases.servicios.componentes.CamposClasesDB;
import com.masterd.android.app.clases.datos.ProveedorClases;
import com.masterd.android.app.clases.widget.ClassesWidget;

/**
 * Servicio del widget que corre detrás en el sistema para ir así ir consultando a la base de datos e ir actualizando
 * las clases que se encuentran, además de ir reflejando los resultados en el widget de la aplicación
 */
public class ActualizarClasesWidget extends Service {
    /**
     * Se crea un contador para que de esta manera, se cuantifique más adelante el tamaño y de esa manera progresivamente
     * vaya reflejando en el widget las clases colocadas en la base de datos...
     */
    public static int contador=0;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * iniciar el servicio, recibiendo el id y el intent que se envió cuando desde el AppWidget
     * cuando se solocitó el inicio del servicio.
     * @param intent
     * @param startId
     */
    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        //las columnas que se solicitarán a la base de datos para la consulta a la base de datos.
        String[] columnas=new String[]{CamposClasesDB.Datos.NOMBRE,CamposClasesDB.Datos.CURSO};
        //identificar la vista en el servicio para colocar los diferentes elementos dentro de esta.
        RemoteViews actualizarVistas= new RemoteViews(this.getPackageName(), R.layout.widget_aplicacion);
        /**
         * hacer la consulta en la base de datos, siendo guardada esta dentro del cursor correspondiente
         * introduciendole la Uri y las columnas a consultar
         */
        Cursor cursor=getContentResolver().query(ProveedorClases.CONTENT_URI,columnas,null,null,null);
        //en caso de que contador tuviera el valor del tamaño del cursor, reiniciarlo nuevamente.
        if (contador==cursor.getCount()){
            contador=0;
        }
        //si la consulta tiene resultados
        if (cursor.getCount()>0){
            //mover el cursor hacia el contador, hacia la clase identificada en el por medio del contador
            //que irá creciendo su valor a medida que se hacen más consultas.
            cursor.moveToPosition(contador);
            //Identificar los componentes de la vista del widget.
            String nombre=cursor.getString(cursor.getColumnIndex(CamposClasesDB.Datos.NOMBRE));
            String curos=cursor.getString(cursor.getColumnIndex(CamposClasesDB.Datos.CURSO));
            //colocar los resultados dentro de los componentes del widget que se encuentran en este.
            actualizarVistas.setTextViewText(R.id.widget_textView_nombre,nombre);
            actualizarVistas.setTextViewText(R.id.widget_text_view_clases,curos);
            actualizarVistas.setImageViewResource(R.id.widget_img_view,R.mipmap.ic_launcher);

        }else{
            //en caso de que no hayan clases, declare entonces que no hay clases.
            String clases=getString(R.string.no_clases);
            actualizarVistas.setTextViewText(R.id.widget_textView_nombre, clases);
            actualizarVistas.setTextViewText(R.id.widget_text_view_clases,"");
            actualizarVistas.setImageViewResource(R.id.widget_img_view,R.mipmap.ic_launcher);
        }
        //crecer el contador ya después de realizada la consulta.
        ActualizarClasesWidget.contador++;
        //declarar el widget que interactua
        ComponentName esteWidget= new ComponentName(this,ClassesWidget.class);
        //creando una instancia del gestor de widgets
        AppWidgetManager manager=AppWidgetManager.getInstance(this);
        //Agregar el widget al gestor de widgets con los resultados y caracteristicas
        manager.updateAppWidget(esteWidget,actualizarVistas);
    }
}
