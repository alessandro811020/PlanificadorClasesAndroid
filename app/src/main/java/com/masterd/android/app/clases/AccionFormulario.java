package com.masterd.android.app.clases;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

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
 * Esta clase complementa algunas acciones que se realizan dentro de la Actividad Formulario. El nombre
 * de cada método especifica que función realiza.
 */
public class AccionFormulario {
    //se instancia a la Actividad Formulario
    private final Formulario formulario;
    //Se declara un constructor para inicializar la instancia creada de Formulario, relancionando así
    //ambas clases.
    public AccionFormulario(Formulario formulario) {
        this.formulario = formulario;
    }

    /**
     * método que hace mostrar el dialogo que pregunta al usuario por cual vía desea la foto de la clase
     */
    void mostrarDialogoFoto() {
        //crear el objeto diálogo para ser configurado
        AlertDialog.Builder builder = new AlertDialog.Builder(formulario);
        //introducirle las distintas características al diálogo que se mostrará
        builder.setMessage(R.string.mensaje_dialogo_camara);
        builder.setCancelable(false);
        builder.setTitle(R.string.agregar_foto);
        //los tres botones con sus respectivos intents par la recepción de las fotos por las dos vías,
        //por la cámara y por la galería
        builder.setPositiveButton(R.string.nombre_boton_positivo_dialogo_camara, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intentFoto = new Intent(Constants.INTENT_TOMAR_FOTO);
                formulario.startActivityForResult(intentFoto, Formulario.TOMAR_FOTO);
            }
        });
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setNeutralButton(R.string.nombre_boton_neutral_dialogo_camara, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                formulario.setIntent(new Intent());
                formulario.getIntent().setType("image/*");
                formulario.getIntent().setAction(Intent.ACTION_GET_CONTENT);
                formulario.startActivityForResult(formulario.getIntent(), Formulario.TOMAR_FOTO_GALERIA);
            }
        });
        //crearlo y mostrarlo
        builder.create();
        builder.show();
    }

    /**
     * método que lanza el intent donde se recibirá la información de la dirección y las coordenadas de
     * la misma
     */
    void irMapa(){
        //crear el intent
        formulario.setIntent(new Intent());
        //agregarle el extra a la clase que se introducirá al mapa para un filtro de información
        formulario.getIntent().putExtra(Constants.ORIGEN, Constants.FORMULARIO);
        //a que clase se debe dirigir
        formulario.getIntent().setClass(formulario.getApplicationContext(),Mapa.class);
        //iniciar el intent
        formulario.startActivityForResult(formulario.getIntent(),Formulario.CODIGO_SOLICITUD);
    }

    /**
     * Método que realiza la inserción de los datos dentro de la base de datos , se le introducen como parámetros
     * los cuales son añadidos a la base de datos en sus respectivas columnas.
     * @param nombreAlumno
     * @param curso
     * @param fecha
     * @param hora
     * @param direccion
     * @param longitud
     * @param latitud
     */
    void insertar(String nombreAlumno, String curso, String fecha, String hora, String direccion, String longitud, String latitud){
        //instanciar el contenedor de valores de la clase Formulario para así poder colocar los valores.
        formulario.valores=new ContentValues();
        //Agregar los valores al contenedor
        formulario.valores.put(CamposClasesDB.Datos.NOMBRE,nombreAlumno);
        formulario.valores.put(CamposClasesDB.Datos.CURSO,curso);
        formulario.valores.put(CamposClasesDB.Datos.FECHA,fecha);
        formulario.valores.put(CamposClasesDB.Datos.HORA,hora);
        formulario.valores.put(CamposClasesDB.Datos.DIRECCION,direccion);
        formulario.valores.put(CamposClasesDB.Datos.LONGITUD,longitud);
        formulario.valores.put(CamposClasesDB.Datos.LATITUD,latitud);
        formulario.valores.put(CamposClasesDB.Datos.FOTO, Environment.getExternalStorageDirectory().getAbsolutePath()+Constants.CARPETA_FOTOS+nombreAlumno+fecha+Constants.EXTENSION);
        //A través del ContentResolver, acceder al Provider para introducir los datos dentro de la base de daros.
        formulario.getContentResolver().insert(ProveedorClases.CONTENT_URI,formulario.valores);
    }

    /**
     * Se reciben los datos que viene de la Actividad Mapa con los datos de la dirección y  las coordenadas
     * de la dirección a través del intent que entra al método addOnActivityResult de la Actividad Formulario
     * @param datos
     */
    void addOnActivityResultDatosMapas(Intent datos){
        formulario.direccionR=datos.getStringExtra(CamposClasesDB.Datos.DIRECCION);
        formulario.latitudR=datos.getStringExtra(CamposClasesDB.Datos.LATITUD);
        formulario.longitudR=datos.getStringExtra(CamposClasesDB.Datos.LONGITUD);
    }
    /**
     * Se reciben los datos que viene de la toma de las Fotos con la foto que es recibida y de esta manera crear el archivo
     * y poder guardarlo dentro de la carpeta del programa, información que es entrada al método addOnActivityResult de la Actividad Formulario
     * @param datos
     */
    void addOnActivityTomarFotos(Intent datos){
        //desde el intent datos, recibir la foto y guardarla en un objeto Bitmap
        Bitmap bm=(Bitmap) datos.getExtras().get("data");
        //instanciar los dos componentes de texto que crearán el nombre de los archivos. el nombre y la fecha de la clase
        formulario.nombreAlumno=formulario.etNombre.getText().toString();
        formulario.fecha=formulario.etFecha.getText().toString();
        //cadena de texto que direcciona hacia donde se guardarán las fotos.
        formulario.nombreArchivo= Environment.getExternalStorageDirectory().getAbsolutePath()+Constants.SLASH_CARPETA;
        //crear ell onjeto archivo que hace referencia al directorio donde se guardará la información
        File directorio= new File(formulario.nombreArchivo);
        //Si el directorio no existe, crearlo
        if (!directorio.exists()){
            directorio.mkdir();
        }
        //instanciar el objeto que refiere al nombre del archivo
        File archivoFoto= new File(directorio,formulario.nombreAlumno+formulario.fecha+Constants.EXTENSION);
        //Flujo donde crearemos el archivo dentro del teléfono
        try{
            //Crear el flujo de salida, con la ruta de esta
            FileOutputStream fileOutputStream=new FileOutputStream(archivoFoto);
            //comprimir la foto con extensión jpg a través del flujo que se ha creado
            bm.compress(Bitmap.CompressFormat.JPEG,70,fileOutputStream);
            //Hacer el flujo, que cree el archivo
            fileOutputStream.flush();
            //cerrar el flujo del archivo
            fileOutputStream.close();
            //obtener la ruta del archivo
            formulario.ubicacionFoto =Environment.getExternalStorageDirectory().getAbsolutePath()+Constants.CARPETA_SLASH+formulario.nombreAlumno+formulario.fecha+Constants.EXTENSION;
            //Aquí es cuando se especifica que se escribirá en la actividad despuás que se introduzca la información
            formulario.tvRuta.setText(R.string.foto_guardada);
            //los catch de las distintas excepciones que podría ocurrir
        }catch (FileNotFoundException e){
            Toast.makeText(formulario.getApplicationContext(), R.string.error_foto, Toast.LENGTH_SHORT).show();
        }catch (IOException e){
            Toast.makeText(formulario.getApplicationContext(), R.string.error_foto, Toast.LENGTH_SHORT).show();
        }
    }
    /**
     * Se reciben los datos que viene de la galería que es recibida. De esta manera crear el archivo
     * y poder guardarlo dentro de la carpeta del programa, información que es entrada desde método
     * addOnActivityResult de la Actividad Formulario
     * @param datos
     */
    void addOnActivityTomarFotosGaleria(Intent datos){
        //guardar la uri de la ubicación de la galería
        Uri imagenSeleccion=datos.getData();
        //crear el objeto para el flujo de entrada
        InputStream flujoEntrada=null;
        //Crear el objeto que se referirá al objeto bitmap de imagen
        Bitmap imagenCaptada=null;
        //instanciar los dos componentes de texto que crearán el nombre de los archivos. el nombre y la fecha de la clase
        formulario.nombreAlumno=formulario.etNombre.getText().toString();
        formulario.fecha=formulario.etFecha.getText().toString();
        //cadena de texto que direcciona hacia donde se guardarán las fotos.
        formulario.nombreArchivo= Environment.getExternalStorageDirectory().getAbsolutePath()+Constants.SLASH_CARPETA;
        //Si el directorio no existe, crearlo
        File directorio= new File(formulario.nombreArchivo);
        if (!directorio.exists()){
            directorio.mkdir();
        }
        //obtener la ruta del archivo
        File archivoTodo=new File(directorio,formulario.nombreAlumno+formulario.fecha+Constants.EXTENSION);
        //aquí se realiza el flujo de información
        try{
            //guardar dada la dirección especificada los datos de la foto
            flujoEntrada=formulario.getContentResolver().openInputStream(imagenSeleccion);
            //guardar la imagen decodificándola
            imagenCaptada= BitmapFactory.decodeStream(flujoEntrada);
            //luego de ser recibida, ahora se guardará en la carpeta que se ha creado para las imágenes del programa
            //se crea el flujo de salida para el archivo.
            FileOutputStream fileOutputStream=new FileOutputStream(archivoTodo);
            //guardamos el archivo dentro del flujo especificado
            imagenCaptada.compress(Bitmap.CompressFormat.JPEG,85,fileOutputStream);
            //se realiza el flujo
            fileOutputStream.flush();
            //cerramos el flujo creado
            fileOutputStream.close();
            //Se realiza la comprobación para de esta manera reportar la captura de la foto realizada.
            formulario.ubicacionFoto =Environment.getExternalStorageDirectory().getAbsolutePath()+Constants.CARPETA_SLASH+formulario.nombreAlumno+formulario.fecha+Constants.EXTENSION;
            if (formulario.ubicacionFoto!=null){
                formulario.tvRuta.setText(R.string.foto_guardada);
            }else{
                formulario.tvRuta.setText(R.string.problema_direccion);
            }
        }catch (FileNotFoundException e){
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}