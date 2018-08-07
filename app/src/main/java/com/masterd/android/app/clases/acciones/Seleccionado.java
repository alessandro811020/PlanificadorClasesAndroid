package com.masterd.android.app.clases.acciones;

import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.masterd.android.app.clases.R;
import com.masterd.android.app.clases.datos.ProveedorClases;
import com.masterd.android.app.clases.servicios.componentes.CamposClasesDB;
import com.masterd.android.app.clases.servicios.componentes.Constants;

import java.io.File;

/**
 * En esta Actividad se reflejarán los datos consultados para una clase que se tenga planificada,
 * se mostrarán todos los datos respectivos
 */
public class Seleccionado extends AppCompatActivity implements View.OnClickListener {
    //campos del usuario y de la dirección de la fotoa usar
    public String usuario, direccionFotoUsar;
    //Campos de la base de datos que serán consultadas
    String[] columnas=new String[]{CamposClasesDB.Datos._ID,CamposClasesDB.Datos.NOMBRE, CamposClasesDB.Datos.CURSO, CamposClasesDB.Datos.FECHA, CamposClasesDB.Datos.HORA, CamposClasesDB.Datos.DIRECCION,
            CamposClasesDB.Datos.LONGITUD, CamposClasesDB.Datos.LATITUD, CamposClasesDB.Datos.FOTO};
    //Cursor donde se guardará la consulta realizada al proveedor
    Cursor cursor;
    //uri que se utilizará para la consulta al proveedor.
    Uri nuevaUri;
    //imagen guardada que reflejará la imagen guardada en la carpeta del programa
    ImageView fotoClase;
    //Campos de texto que muestran resultados dentro de la Actividad
    TextView nombre, curso, fecha, hora, direccion;
    //Se crea este objeto co el objetivo de eliminarle carga de trabajo a la Actividad
    AccionSeleccionado accionSeleccionado= new AccionSeleccionado(this);

    /**
     * crear la actividad con sus respectivos componentes y elementos en la aplicación
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seleccionado);
        //identificar la barra de acciones dentro de la actividad
        Toolbar barraTareas= (Toolbar) findViewById(R.id.barra_tarea2);
        setSupportActionBar(barraTareas);
        //al intent se le registra el intent que lanzó la app, para captar los extras que se enviaron.
        Intent intent= getIntent();
        //guardar en usuario el que ha sido seleccionado a mostrar sus datos
        usuario= intent.getStringExtra(Constants.ESCOGIDO);
        //convertir la cadena en un valor entero
        int usuarioURI= Integer.parseInt(usuario);
        //la nueva uri contendra la anterior más el usuario que se le agrega
        nuevaUri= ContentUris.withAppendedId(ProveedorClases.CONTENT_URI, usuarioURI);
        //realizar la consulta donde se guardará esta en el objeto cursor.
        cursor=getContentResolver().query(nuevaUri,columnas, CamposClasesDB.Datos._ID+"="+usuario,null,null);
        //identificamos en el layout todos los componentes que intervienen en brindar la información de la clase
        nombre= (TextView)findViewById(R.id.tvs_nombre);
        curso= (TextView)findViewById(R.id.tvs_curso_escogido);
        fecha= (TextView)findViewById(R.id.tvs_fecha_escogido);
        hora= (TextView)findViewById(R.id.tvs_hora_escogido);
        direccion= (TextView)findViewById(R.id.tvs_direccion_escogido);
        fotoClase=(ImageView) findViewById(R.id.ivs_foto_alumno);
        //localizamos en el cursor la posición para brindar la información.
        cursor.moveToFirst();
        //mostrar los diferentes datos dentro de la actividad
        nombre.setText(cursor.getString(cursor.getColumnIndex(CamposClasesDB.Datos.NOMBRE)));
        curso.setText(cursor.getString(cursor.getColumnIndex(CamposClasesDB.Datos.CURSO)));
        fecha.setText(cursor.getString(cursor.getColumnIndex(CamposClasesDB.Datos.FECHA)));
        hora.setText(cursor.getString(cursor.getColumnIndex(CamposClasesDB.Datos.HORA)));
        direccion.setText(cursor.getString(cursor.getColumnIndex(CamposClasesDB.Datos.DIRECCION)));
        direccionFotoUsar=cursor.getString(cursor.getColumnIndex(CamposClasesDB.Datos.FOTO));
        fotoClase.setImageBitmap(obtenerImagen(direccionFotoUsar));
        //identificar los elementos de la actividad que son botones, no brindan información
        Button btModificar= (Button) findViewById(R.id.bt_modificar);
        Button btEliminar= (Button) findViewById(R.id.bt_eliminar);
        //agregar los respectivos listeners de los botones de la actividad.
        btModificar.setOnClickListener(this);
        btEliminar.setOnClickListener(this);
    }
    /*
    los siguientes métodos hacen referencia a la barra de Acción de la actividad. En el primero se identifica a la barra
    mientras que en el segundo, segun cualquiera de los dos botones, uno no realiza acciones. porque es la pantalla principal
    junto con el layout
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.lista_menu,menu);
        return  true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.mBt_cerrar:
                finish();
                return true;
            case R.id.mBt_salir:
                System.exit(0);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    //en caso de que se hace una consulta de modificar, obtener los nuevos valores que se envien o
    // se mantengan los mismos, deben ser reflejados
    @Override
    protected void onResume() {
        super.onResume();
        //la nueva consulta a la base de datos para obtener los resultados por si han habido cambios.
        cursor=getContentResolver().query(nuevaUri,columnas, CamposClasesDB.Datos._ID+"="+usuario,null,null);
        //mover el cursor al inicio para mostrar los datos
        cursor.moveToFirst();
        //Asignar los valores de nuevo a los componentes de información dentro de la actividad
        nombre.setText(cursor.getString(cursor.getColumnIndex(CamposClasesDB.Datos.NOMBRE)));
        curso.setText(cursor.getString(cursor.getColumnIndex(CamposClasesDB.Datos.CURSO)));
        fecha.setText(cursor.getString(cursor.getColumnIndex(CamposClasesDB.Datos.FECHA)));
        hora.setText(cursor.getString(cursor.getColumnIndex(CamposClasesDB.Datos.HORA)));
        direccion.setText(cursor.getString(cursor.getColumnIndex(CamposClasesDB.Datos.DIRECCION)));
        fotoClase.setImageBitmap(obtenerImagen(direccionFotoUsar));
    }
    //Este método se ha creado con vista a implementar la interface OnClickListener que permite indicar los distintos
    //listeners dentro de la actividad. las acciones que se realizarán.
    @Override
    public void onClick(View v) {
        //Según el id de la vista seleccionada
        switch(v.getId()){
            //se escoge el botón de modificar
            case R.id.bt_modificar:
                //se realiza la acción de modificar que está reflejada en la clase AccionSeleccionado
                accionSeleccionado.modificarSeleccionado();
                break;
            //Se marca el botón de eliminar
            case R.id.bt_eliminar:
                //se capta e identifica el uri de la clase que se va a eliminar
                final Uri nuevaUri= ContentUris.withAppendedId(ProveedorClases.CONTENT_URI, Long.parseLong(usuario));
                //Se crea el diálogo para la eliminación de la clase, el constructor
                AlertDialog.Builder builder= new AlertDialog.Builder(this);
                //mensaje a mostrar
                builder.setMessage(R.string.pregunta_eliminar);
                //que no sea cancelable, que tome decisión
                builder.setCancelable(false);
                //Botón afirmativo de eliminación
                builder.setPositiveButton(R.string.si, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //realización de la eliminación de la clase.
                        getContentResolver().delete(nuevaUri, usuario,null);
                        //Eliminar el archivo de la foto que se creo en el programa
                        try {
                            File fotoEliminar= new File(direccionFotoUsar);
                            fotoEliminar.delete();
                        }catch (NullPointerException e){
                            e.printStackTrace();
                        }
                        finish();
                    }
                });
                //el botón de negación cancela la pregunta y la eliminación
                builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                //Crear el diálogo
                builder.create();
                //mostrar el diálogo
                builder.show();
                break;
        }

    }
    public Bitmap obtenerImagen(String ruta){
        Bitmap fotoAgregar=null;
        fotoAgregar= BitmapFactory.decodeFile(ruta);
        return fotoAgregar;
    }
}
