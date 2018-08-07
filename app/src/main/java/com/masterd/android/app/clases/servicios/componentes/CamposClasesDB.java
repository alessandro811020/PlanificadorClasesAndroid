package com.masterd.android.app.clases.servicios.componentes;

import android.provider.BaseColumns;

/**
 * Clase que recoge los detalles de la base de datos de la aplicación. Es de observar la clase interna
 * referida a los campos de la base de datos, de las columnas.
 */
public class CamposClasesDB {
    //nombre de la base de datos
    public static final String NOMBRE_BD="clases.db";
    //Versión de la base de datos
    public static final int VERSION_BD=1;
    //constructor para que esta clase no pueda ser instanciada.
    private CamposClasesDB(){}
    //Clase interna con las columnas de la base de datos, incluyendo el nombre de la tabla y el orden
    //que será mostrada la información, implementando la interface BaseColumns
    public static final class Datos implements BaseColumns{
        //Constructor para que no pueda ser instanciada.
        private Datos(){}
        public static final String ORDEN_POR_DEFECTO="_ID DESC";
        public static final String NOMBRE_TABLA="alumnos";
        public static final String _ID="_id";
        public static final String NOMBRE="nombre";
        public static final String CURSO="curso";
        public static final String FECHA="fecha";
        public static final String HORA="hora";
        public static final String DIRECCION="direccion";
        public static final String LATITUD="latitud";
        public static final String LONGITUD="longitud";
        public static final String FOTO="foto";
        public static final String _COUNT="9";
    }

}
