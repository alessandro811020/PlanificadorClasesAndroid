package com.masterd.android.app.clases.datos;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.masterd.android.app.clases.datos.basedatos.ClasesSQLHelper;
import com.masterd.android.app.clases.servicios.componentes.CamposClasesDB;
import com.masterd.android.app.clases.servicios.componentes.Constants;

/**
 *Esta clase es el proveedor de la aplicación de información con la base de datos referida a las clases.
 * Por medio de ella podremos realizar todas las operaciones pertinentes a la base de datos.
 */
public class ProveedorClases extends ContentProvider {
    //creamos los campos para este proveedor
    //partimos del nombre del proveedor, que coincide con el nombre del paquete
    private static final String NOMBRE_PROVIDER = "com.masterd.android.app.clases";
    //Creación de la Uti con que identificará al proveedor en su contacto con la app
    public static final Uri CONTENT_URI=Uri.parse("content://" + NOMBRE_PROVIDER+"/clases");
    //según el tipo de consultas, si es por grupo, este será el identificador
    private static final int CLASES=1;
    //según el tipo de consultas, si es individual, este será el identificador
    private static final int CLASES_ID=2;
    //el objeto que se encargará de identificar el tipo de consulta que se realizará, si es individual o en grupo
    private static final UriMatcher uriMatcher;
    static {
        uriMatcher=new UriMatcher(UriMatcher.NO_MATCH);
        //consulta de un grupo de clases
        uriMatcher.addURI(NOMBRE_PROVIDER,"clases",CLASES);
        //consulta de una clase en particular
        uriMatcher.addURI(NOMBRE_PROVIDER,"clases/#",CLASES_ID);
    }
    //objeto que hace referencia a la base de datos.
    private SQLiteDatabase clasesBD;
    //se crea el proveedor, la base de datos que será trabajada durante toda la aplicación.
    @Override
    public boolean onCreate() {
        //se capta el contexto en que se realiza
        Context context =getContext();
        //Se inicializa la base de datos de las clases, con el contexto introducido como parámetro
        ClasesSQLHelper dbHelper= new ClasesSQLHelper(context);
        //Establecer dicha base de datos como escribible
        clasesBD=dbHelper.getWritableDatabase();
        //devolver la base de datos
        return(clasesBD==null)?false:true;
    }

    /**
     * Este método permite la realización de las consultas a la base de datos según el tipo de solicitud
     * ya sea una consulta de un grupo de elementos, como de uno en particular.
     */
    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        //El cursor que guardará los datos recogidos en la base de datos
        Cursor c;
        //según la Uri recibida
        switch (uriMatcher.match(uri)) {
            //Consulta para un grupo de datos
            case CLASES:
                // Consultando los registros solicitados
                c = clasesBD.query(CamposClasesDB.Datos.NOMBRE_TABLA, projection,selection, selectionArgs,null, null, sortOrder);
                //realizar la notificación
                c.setNotificationUri(getContext().getContentResolver(),CONTENT_URI);
                break;
            //Consulta para un componente en específico
            case CLASES_ID:
                //asignar a selection el valor a consultar, obteniéndolo desde el getPathSegment de la Uri
                selection= CamposClasesDB.Datos._ID+"="+uri.getPathSegments().get(1);
                //abrir un constructor de querys para realizar las consultas.
                SQLiteQueryBuilder sqlBuilder= new SQLiteQueryBuilder();
                //establecer la tabla que se realizará la consulta, el nombre de esta.
                sqlBuilder.setTables(CamposClasesDB.Datos.NOMBRE_TABLA);
                //indicarle el componente que será solicitado
                sqlBuilder.appendWhere(CamposClasesDB.Datos._ID+"="+uri.getLastPathSegment());
                //establecer el orden de información
                if (sortOrder==null||sortOrder==""){
                    sortOrder= CamposClasesDB.Datos.ORDEN_POR_DEFECTO;
                }
                //realizar la consulta al abase de datos y guardar el resultado en el cursor c
                c=sqlBuilder.query(clasesBD,projection,selection,selectionArgs,null,null,sortOrder);
                //notificar a la Uri
                c.setNotificationUri(getContext().getContentResolver(), CONTENT_URI);
                break;
            default:
                throw new IllegalArgumentException(Constants.URI_DESCONOCIDA + uri);
        }
        return c;
    }
    //devuelve el MymeType, aunque en esta app no se le de da uso, es requisito de la superclase contenerla
    @Nullable
    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)){
            case CLASES:
                return Constants.GET_TYPE_CLASES;
            case CLASES_ID:
                return Constants.GET_TYPE_CLASES_ID;
            default:
                throw new IllegalArgumentException(Constants.URI_DESCONOCIDA+uri);
        }
    }

    /**
     * en este método se realizará la insersión de los datos respectivos a una clase en la base de datos
     * por loque se introducirá la uri, y los valores en un contenedor de valores
     * @param uri
     * @param values
     * @return
     */
    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        //se guarda en la variable rowID la realización de los datos, se guardará el número de filas afectadas
        long rowID=clasesBD.replace(CamposClasesDB.Datos.NOMBRE_TABLA,"",values);
        //si se ha añadido al menos una fila
        if (rowID>0){
            //agregar la fila al contenedor
            Uri _uri= ContentUris.withAppendedId(CONTENT_URI,rowID);
            //realizar la notificación
            getContext().getContentResolver().notifyChange(_uri,null);
            //devolver la uri
            return _uri;
        }
        throw new SQLException(Constants.FALLO_URI +uri);
    }

    /**
     * Método que se llamará en caso de la eliminación de elementos de la base de datos, por ello es introducido la uri, la selección
     * de aquellos que serán eliminados y otros elementos que podrían servir como filtro de eliminación en caso de ser un grupo a eliminar.
     * En nuestro caso, se elimina en la aplicación solo de uno en uno, aunque se ha creado el genérico en el método
     * @param uri
     * @param selection
     * @param selectionArgs
     * @return
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        //inicializar la variable que realizará el conteo de los cambios realizados en la eliminación
        int count=0;
        //Según el tipo de cantidad a eliminar.
        switch (uriMatcher.match(uri)){
            //eliminar un grupo de elementos
            case CLASES:
                //se guardarán el número de filas que han sido afectados en la eliminación según lo solicitado
                count=clasesBD.delete(CamposClasesDB.Datos.NOMBRE_TABLA, CamposClasesDB.Datos._ID+"="+selection,selectionArgs);
                break;
            case CLASES_ID:
                //primero identificar ese elemento en especial que quiere ser eliminado
                String id=uri.getPathSegments().get(1);
                //realizar la eliminación del elemento.
                count=clasesBD.delete(CamposClasesDB.Datos.NOMBRE_TABLA, CamposClasesDB.Datos._ID+"="+id+
                        (!TextUtils.isEmpty(selection)?" AND ("+selection+")":""),selectionArgs);
                break;
            default:
                throw new IllegalArgumentException(Constants.URI_DESCONOCIDA+uri);
        }
        //realizar la notificación
        getContext().getContentResolver().notifyChange(uri,null);
        //devolver la cantidad de filas afectadas
        return count;
    }

    /**
     * método para la actualización de alguna fila en particular que será modificada dentro de la base de datos
     * según los valores que se entreguen, ese seleccionado en especial que será mosificado, o seleccionados.
     * @param uri
     * @param values
     * @param selection
     * @param selectionArgs
     * @return
     */
    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        //inicializar la variable que realizará el conteo de los cambios realizados en la modificación
        int count=0;
        //Según el tipo de cantidad a modificar.
        switch (uriMatcher.match(uri)){
            //modificar un grupo de elementos
            case CLASES:
                //se guardarán el número de filas que han sido afectados en la modificación según lo solicitado
                count=clasesBD.update(CamposClasesDB.Datos.NOMBRE_TABLA,values,selection,selectionArgs);
                break;
            case CLASES_ID:
                //primero identificar ese elemento en especial que quiere ser modificado
                String id=uri.getPathSegments().get(1);
                //realizar la modificación del elemento.
                count=clasesBD.update(CamposClasesDB.Datos.NOMBRE_TABLA,values,CamposClasesDB.Datos._ID+"="+id+
                        (!TextUtils.isEmpty(selection)?" AND ("+selection+")":""),selectionArgs);
                break;
            default:
                throw new IllegalArgumentException(Constants.URI_DESCONOCIDA +uri);
        }
        //realizar la notificación
        getContext().getContentResolver().notifyChange(uri,null);
        //devolver la cantidad de filas afectadas
        return count;
    }
}
