package com.masterd.android.app.clases.datos.basedatos;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.masterd.android.app.clases.servicios.componentes.CamposClasesDB;


/**
 * Clase que identifica la base de datos de las clases, porque es aquí donde se recoge la sentencia de la creación de
 * la base de datos, además que es donde se declara que esta sea resscribible. se le crean los campos y sus características.
 * Por ello extiende de SQLiteHelper para que cuente con las características y las consultas de base de datos. Únicamente que
 * en esta base de datos toda consulta y acción se realiza a través del proveedor
 */
public class ClasesSQLHelper extends SQLiteOpenHelper {
    //Se crea el contructor donde se accede al contexto para brindarselo a la base de datos, donde se le nombra y
    //se le indica la versión.
    public ClasesSQLHelper(Context context) {
        super(context, CamposClasesDB.NOMBRE_BD, null, CamposClasesDB.VERSION_BD);
    }
    //crear la base de datos con la característica de que sea escribible
    @Override
    public void onCreate(SQLiteDatabase db) {
        if (db.isReadOnly()){
            db=getWritableDatabase();
        }
        //Sentencia SQL para que se cree la tabla donde se almacenen los datos de las clases
        db.execSQL("CREATE TABLE "+CamposClasesDB.Datos.NOMBRE_TABLA+"("+CamposClasesDB.Datos._ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
        CamposClasesDB.Datos.NOMBRE+" TEXT, "+CamposClasesDB.Datos.CURSO+" TEXT, "+CamposClasesDB.Datos.FECHA+" TEXT, "+CamposClasesDB.Datos.HORA+
        " TEXT, " +CamposClasesDB.Datos.DIRECCION+ " TEXT, "+CamposClasesDB.Datos.LATITUD+" DOUBLE, "+CamposClasesDB.Datos.LONGITUD+" DOUBLE, "+ CamposClasesDB.Datos.FOTO+" TEXT)");
    }
    //este método no se implementa porque no se realiza modificación de la base de datos
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
