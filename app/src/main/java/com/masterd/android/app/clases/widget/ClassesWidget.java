package com.masterd.android.app.clases.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.masterd.android.app.Principal;
import com.masterd.android.app.clases.R;
import com.masterd.android.app.clases.servicios.ActualizarClasesWidget;

/**
 * Esta clase se refiere al proveedor del widget de la aplicación. aquí es donde realizará la acción
 * y las características respectivas que tiene este.
 */
public class ClassesWidget extends AppWidgetProvider {
    //Se hará referencia al buscador que hará el provider dentro de la base de datos para que de
    //esa manera se vaya modificando las clases dentro del widget de la aplicación.
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        //para la longitud de veces
        final int N = appWidgetIds.length;
        for (int i=0; i<N; i++) {
            int appWidgetId = appWidgetIds[i];
            //iniciar el servicio de buscar dentro de la base de datos
            context.startService(new Intent(context, ActualizarClasesWidget.class));
            //declarar hacia donde ir en caso de que se le de click al widget.
            //a través de un PendingIntent hacia la Actividad Principal
            Intent intent = new Intent(context,Principal.class);
            //declaración del PendingIntent
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            //Identificar a la vista que se trabajará en el widget
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_aplicacion);
            //Agregarle el PendingIntent a la vista del widget de la aplicación
            views.setOnClickPendingIntent(R.id.widget_clases, pendingIntent);
            //Al gestor de Widgets agregarle la vista y el widget.
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }







    }
}
