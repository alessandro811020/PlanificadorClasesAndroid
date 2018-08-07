package com.masterd.android.app.clases.acciones;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

import com.masterd.android.app.clases.R;
import com.masterd.android.app.clases.datos.ProveedorClases;
import com.masterd.android.app.clases.mapa.Mapa;
import com.masterd.android.app.clases.servicios.componentes.CamposClasesDB;
import com.masterd.android.app.clases.servicios.componentes.Constants;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Clase que se refiere a acciones que se realizan en la Actividad de Modificar. Acciones específicas que
 * están en dependencia de las acciones que va necesitando el usuario.
 */
public class AccionModificar {
    //objeto que se refiere a la Actividad Modificar
    private final Modificar modificar;

    //constructor que permite inicializar a la actividad modificar dentro de esta clase
    public AccionModificar(Modificar modificar) {
        this.modificar = modificar;
    }

    //método que lanza la modificación de los datos de los mapas. introduce los datos para que se
    //guarden en el extra para que se recojan en la Actividad de Mapa
    void intentMapas(){
        //crear el intent
        modificar.intent=new Intent();
        //Agregar los extras
        modificar.intent.putExtra(Constants.ORIGEN, Constants.MODIFICAR);
        modificar.intent.putExtra(Constants.DIRECCION_MODIFICAR, modificar.direccionR);
        modificar.intent.putExtra(Constants.LATITUD_MODIFICAR, modificar.latitudR);
        modificar.intent.putExtra(Constants.LONGITUD_MODIFICAR, modificar.longitudR);
        //establecer la clase que recibirá el intent
        modificar.intent.setClass(modificar.getApplicationContext(),Mapa.class);
        //lanzar el intent para recibir resultados
        modificar.startActivityForResult(modificar.intent,modificar.CODIGO_SOLICITAR_MODIFICAR);
    }

    /**
     * Método que realiza la actualización de la base de datos del campo que ha sido solicitado la actualización.
     * Para ello son introducidos todos los parámetros a modificar obligatorios
     * @param nombreAlumno
     * @param curso
     * @param fecha
     * @param hora
     * @param direccion
     * @param longitud
     * @param latitud
     */
    void actualizarDatosBaseDatos(String nombreAlumno, String curso, String fecha, String hora, String direccion, String longitud, String latitud){
        //el contenedor de valores es inicializado
        modificar.valores=new ContentValues();
        //agregar los valores al contenedor de valores
        modificar.valores.put(CamposClasesDB.Datos.NOMBRE,nombreAlumno);
        modificar.valores.put(CamposClasesDB.Datos.CURSO,curso);
        modificar.valores.put(CamposClasesDB.Datos.FECHA,fecha);
        modificar.valores.put(CamposClasesDB.Datos.HORA,hora);
        modificar.valores.put(CamposClasesDB.Datos.DIRECCION,direccion);
        modificar.valores.put(CamposClasesDB.Datos.LONGITUD,longitud);
        modificar.valores.put(CamposClasesDB.Datos.LATITUD,latitud);
        if (modificar.ubicacionFoto==modificar.ubicacionInicialFoto){
            modificar.valores.put(CamposClasesDB.Datos.FOTO,modificar.ubicacionInicialFoto);
        }else{
            modificar.valores.put(CamposClasesDB.Datos.FOTO, Environment.getExternalStorageDirectory().getAbsolutePath()+Constants.CARPETA_FOTOS+modificar.nombreAlumno+modificar.fecha+Constants.EXTENSION);
        }
        modificar.nuevaUri= ContentUris.withAppendedId(ProveedorClases.CONTENT_URI, modificar.usuarioURI);
        //modificar los datos pertinentes con los valores introducidos.
        modificar.getContentResolver().update(modificar.nuevaUri,modificar.valores, CamposClasesDB.Datos._ID+"="+modificar.usuario,null);
    }

    //obtención de los datos desde la Actividad de los Mapas
    void delMapa(Intent datos){
        //Darle los datos a los campos respectivos de la Actividad
        modificar.direccionR=datos.getStringExtra(CamposClasesDB.Datos.DIRECCION);
        modificar.latitudR=datos.getStringExtra(CamposClasesDB.Datos.LATITUD);
        modificar.longitudR=datos.getStringExtra(CamposClasesDB.Datos.LONGITUD);
    }

    //método que lanza la cámara para la toma de la foto
    void tomarFoto(Intent datos){
        //recoger los datos de los dos campos que dan nombre al archivo de foto
        modificar.nombreAlumno=modificar.etNombre.getText().toString();
        modificar.fecha=modificar.etFecha.getText().toString();
        //nombre del archivo, que ofrece la ruta donde se guardará la foto
        modificar.nombreArchivo= Environment.getExternalStorageDirectory().getAbsolutePath()+Constants.SLASH_CARPETA;
        //introducir la ruta dentro de un objeto File
        File directorio= new File(modificar.nombreArchivo);
        //En caso de que la carpeta no exista, la cree
        if (!directorio.exists()){
            directorio.mkdir();
        }
        //Crear el File que hace referencia a la dirección con el nombre del archivo a crear
        File archivoFoto= new File(directorio,modificar.nombreAlumno+modificar.fecha+Constants.EXTENSION);
        //El objeto Bitmap que contendrá la foto que ha sido obtenida a través de la cámara y que será guardado en la memoria interna
        Bitmap bm=(Bitmap) datos.getExtras().get(Constants.DATA);
        try{
            //flujo de salida con la ruta planteada por el File creado
            FileOutputStream fileOutputStream=new FileOutputStream(archivoFoto);
            //Creación del objeto jpg de foto en la carpeta
            bm.compress(Bitmap.CompressFormat.JPEG,70,fileOutputStream);
            //realización del flujo
            fileOutputStream.flush();
            //cerrar el flujo
            fileOutputStream.close();
            //establecer la ruta final del archivo
            modificar.ubicacionFoto =Environment.getExternalStorageDirectory().getAbsolutePath()+Constants.CARPETA_SLASH+modificar.nombreAlumno+modificar.fecha+Constants.EXTENSION;
            //escribir en la actividad el resultado obtenido
            if (modificar.ubicacionFoto!=null){
                modificar.tvRuta.setText(R.string.foto_guardada);
            }else{
                modificar.tvRuta.setText(R.string.problema_direccion);
            }
            //controlar las posibles excepciones
        }catch (FileNotFoundException e){
            Toast.makeText(modificar.getApplicationContext(), R.string.error_foto, Toast.LENGTH_SHORT).show();
        }catch (IOException e){
            Toast.makeText(modificar.getApplicationContext(), R.string.error_foto, Toast.LENGTH_SHORT).show();
        }
    }

    //método por el cual se recibe la foto que ha sido escogida desde la galería
    void tomarFotoGaleria(Intent datos){
        //obtener la uri de la foto de la galería
        Uri imagenSeleccion=datos.getData();
        //inicializar el flujo y el objeto Bitmap que trabaja el objeto imagen
        InputStream flujoEntrada=null;
        Bitmap imagenCaptada=null;
        //obtener los datos de nombre y fecha para darle nombre al archivo jpg que será creado
        modificar.nombreAlumno=modificar.etNombre.getText().toString();
        modificar.fecha=modificar.etFecha.getText().toString();
        //obtener la ruta del archivo
        modificar.nombreArchivo= Environment.getExternalStorageDirectory().getAbsolutePath()+Constants.SLASH_CARPETA;
        //colocar la ruta del archivo a guardar dentro de un objeto File para referir ubicación
        File directorio= new File(modificar.nombreArchivo);
        //Si la carpeta está vacía entonces debe ser creada.
        if (!directorio.exists()){
            directorio.mkdir();
        }
        //ruta completa donde se coloca incluso el nombre del archivo que será creado
        File archivoTodo=new File(directorio,modificar.nombreAlumno+modificar.fecha+Constants.EXTENSION);
        try{
            //obtener la imagen desde la galería
            flujoEntrada=modificar.getContentResolver().openInputStream(imagenSeleccion);
            imagenCaptada= BitmapFactory.decodeStream(flujoEntrada);
            //crear ahora el flujo que se refiere a guardar el archivo en la memoria interna del teléfono
            FileOutputStream fileOutputStream=new FileOutputStream(archivoTodo);
            //la imagen es comprimida y guardada dentro de la ruta especificada
            imagenCaptada.compress(Bitmap.CompressFormat.JPEG,85,fileOutputStream);
            //lanzar el flujo
            fileOutputStream.flush();
            //cerrar el flujo
            fileOutputStream.close();
            //recoger la ruta del archivo
            modificar.ubicacionFoto =Environment.getExternalStorageDirectory().getAbsolutePath()+Constants.CARPETA_SLASH+modificar.nombreAlumno+modificar.fecha+ Constants.EXTENSION;
            //Si la ruta del archivo no es nula muestre el resultado de guardada
            if (modificar.ubicacionFoto!=null){
                modificar.tvRuta.setText(R.string.foto_guardada);
            }else{
                modificar.tvRuta.setText(R.string.problema_direccion);
            }
        //Controlando las excepciones.
        }catch (FileNotFoundException e){
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
