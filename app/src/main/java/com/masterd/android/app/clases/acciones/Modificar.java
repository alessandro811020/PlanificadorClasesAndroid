package com.masterd.android.app.clases.acciones;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.masterd.android.app.clases.R;
import com.masterd.android.app.clases.datos.ProveedorClases;
import com.masterd.android.app.clases.mapa.servicios.ComprobarCampos;
import com.masterd.android.app.clases.servicios.componentes.CamposClasesDB;
import com.masterd.android.app.clases.servicios.componentes.Constants;

import java.util.Calendar;
import java.util.concurrent.ExecutionException;

/**
 * Actividad que se refiere a la modificación de los datos que se han introducido en la base de datos
 * para la clase, por lo que utilizará el mismo layout, implementando métodos muy parecidos al de Formulario
 */
public class Modificar extends AppCompatActivity implements View.OnClickListener {

    //los diferentes campos que se refieren al intercambio de información entre las diferentes actividades
    protected static final int CODIGO_SOLICITUD=4000;
    protected static final int TOMAR_FOTO_GALERIA=5000;
    protected static final int TOMAR_FOTO=7000;
    protected static final int CODIGO_SOLICITAR_MODIFICAR=6000;
    //objeto de la clase que realiza acciones de la actividad
    private final AccionModificar accionModificar = new AccionModificar(this);
    //componentes botones de la actividad
    Button btRegistrar, btIrMapa,btFoto;
    //Campos de texto, ya sean editables, como no editables
    EditText etNombre, etCurso, etFecha, etHora;
    TextView etDireccion, etLongitud, etLatitud, tvRuta;
    //contenedor de valores que se utilizarán para enviar la información a la base de datos en la modificación
    ContentValues valores;
    //campos de texto que se utilizarán en la actividad
    String nombreAlumno, curso, fecha, hora, longitud, latitud,nombreArchivo,ubicacionInicialFoto,ubicacionFoto,direccion, direccionR, latitudR, longitudR, usuario;
    //Diálogo que permite buscar la fecha de la clase en caso de cambio
    DatePickerDialog dialogoFecha;
    //Diálogo que permite buscar la hora de la clase en caso de cambio
    TimePickerDialog dialogoTiempo;
    //Intent que trabajará la actividad
    Intent intent;
    //cursor de las consultas a realizar
    Cursor cursor;
    //uri que se guardará la consulta específica a realizar
    Uri nuevaUri;
    //el usuario que será en entero
    int usuarioURI;
    //Array que guarda las columnas que se desean consultar dentro de la base de datos.
    String[] columnas=new String[]{CamposClasesDB.Datos._ID,CamposClasesDB.Datos.NOMBRE, CamposClasesDB.Datos.CURSO, CamposClasesDB.Datos.FECHA, CamposClasesDB.Datos.HORA, CamposClasesDB.Datos.DIRECCION,
            CamposClasesDB.Datos.LONGITUD, CamposClasesDB.Datos.LATITUD, CamposClasesDB.Datos.FOTO};

    //creación e  la actividad
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario);
        //estos tres componentes son los que se refieren a los datos de la dirección y coordenadas que se
        //trabajan con el Mapa
        direccionR=new String();
        latitudR=new String();
        longitudR=new String();

        //localizando la barra de acciones con el titulo
        Toolbar barraTareas= (Toolbar) findViewById(R.id.barra_tarea1);
        setSupportActionBar(barraTareas);
        //obtenemos el intent que ha lanzado la modificación
        intent= getIntent();
        //guardar en usuario el escogido a mdificar que fue enviado en el intent.
        usuario= intent.getStringExtra(Constants.ESCOGIDO);
        //convertirlo en entero
        usuarioURI= Integer.parseInt(usuario);
        //guardar en la uri el valor que será modificado dentro de la base de datos.
        nuevaUri= ContentUris.withAppendedId(ProveedorClases.CONTENT_URI, usuarioURI);
        //realizar la consulta y guardarla en el cursor
        cursor=getContentResolver().query(nuevaUri,columnas, CamposClasesDB.Datos._ID+"="+usuario,null,null);
        //identificar los elementos que muestran la información
        etNombre=(EditText) findViewById(R.id.et_nombre_ingresar);
        etCurso=(EditText) findViewById(R.id.et_curso);
        etFecha=(EditText) findViewById(R.id.et_fecha_clase);
        etHora=(EditText) findViewById(R.id.et_hora_clase);
        etFecha.setInputType(InputType.TYPE_NULL);
        etHora.setInputType(InputType.TYPE_NULL);
        etDireccion=(TextView) findViewById(R.id.et_direccion_final);
        etLongitud=(TextView) findViewById(R.id.et_longitud);
        etLatitud=(TextView) findViewById(R.id.et_latitud);
        tvRuta=(TextView)findViewById(R.id.tv_direccion_foto);
        //mover el cursor para realizar la consulta
        cursor.moveToFirst();
        //guardar en los tres campos respectivos los datos del mapa
        direccionR=cursor.getString(cursor.getColumnIndex(CamposClasesDB.Datos.DIRECCION));
        latitudR=cursor.getString(cursor.getColumnIndex(CamposClasesDB.Datos.LATITUD));
        longitudR=cursor.getString(cursor.getColumnIndex(CamposClasesDB.Datos.LONGITUD));
        //Establecer los valores a mostrar para que se sepan los datos en caso que se quieran modificar.
        etNombre.setText(cursor.getString(cursor.getColumnIndex(CamposClasesDB.Datos.NOMBRE)));
        etCurso.setText(cursor.getString(cursor.getColumnIndex(CamposClasesDB.Datos.CURSO)));
        etFecha.setText(cursor.getString(cursor.getColumnIndex(CamposClasesDB.Datos.FECHA)));
        etHora.setText(cursor.getString(cursor.getColumnIndex(CamposClasesDB.Datos.HORA)));
        etDireccion.setText(direccionR);
        etLatitud.setText(latitudR);
        etLongitud.setText(longitudR);
        //En caso de qeu exista la foto, muestre el resultado de que está encontrada la foto
        if (cursor.getString(cursor.getColumnIndex(CamposClasesDB.Datos.FOTO)).length()>0){
            ubicacionInicialFoto=cursor.getString(cursor.getColumnIndex(CamposClasesDB.Datos.FOTO));
            tvRuta.setText(R.string.foto_guardada);
        }
        //Identificar el boton de modificar con su texto a mostrar por ahora ser modificar
        btRegistrar =(Button) findViewById(R.id.bt_registrar);
        btRegistrar.setText(R.string.bt_modificar);
        //identificar el boton de ir al mapa y de la foto
        btIrMapa=(Button) findViewById(R.id.bt_ir_mapas);
        btFoto=(Button) findViewById(R.id.bt_hacer_foto);
        //Agregar los respectivos listeners a cada uno de los botones o elementos de acceso de la actividad
        etFecha.setOnClickListener(this);
        etHora.setOnClickListener(this);
        btRegistrar.setOnClickListener(this);
        btIrMapa.setOnClickListener(this);
        btFoto.setOnClickListener(this);

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

    /**
     * este método hace referencia a los elementos que pueden ser cambiados en caso de que se regrese
     * a la atividad proque puede lanzarse intent a mapa entonces los valores pueden cambiar y ser modificados
     * y estos se recogen en los siguientes campos.
     */
    @Override
    protected void onResume() {
        super.onResume();
        try {
            if (direccionR.length()!=0){
                etDireccion.setText(direccionR);
            }
            if (latitudR.length()!=0){
                etLatitud.setText(latitudR);
            }
            if (longitudR.length()!=0){
                etLongitud.setText(longitudR);
            }
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    /**
     * método que implementa la interface OnClickListener porque ejecuta los listeners que se declararon en el método onCreate
     * @param v
     */
    @Override
    public void onClick(View v) {
        //Según el id de la vista será la acción que ocurrirá
        switch (v.getId()){
            //Se escogió registrar dentro de los botones
            case R.id.bt_registrar:
                //recoger en las cadenas los datos obtenidos dentro de los campos de la actividad
                nombreAlumno=etNombre.getText().toString();
                curso=etCurso.getText().toString();
                fecha=etFecha.getText().toString();
                hora=etHora.getText().toString();
                direccion=etDireccion.getText().toString();
                longitud=etLongitud.getText().toString();
                latitud=etLatitud.getText().toString();
                //guardar los resultados dentro de un Array
                String[] campos=new String[]{nombreAlumno,curso,fecha,hora,direccion,longitud,latitud};
                //realizar la comprobación de que haya algun campo vació
                ComprobarCampos comprobarCampos=new ComprobarCampos(campos);
                comprobarCampos.execute();
                try {
                    //obtener si existe o no algún dato vacío
                    boolean respuesta= (boolean) comprobarCampos.get();
                    //en caso afirmativo hacer la actualización de la clase.
                    if (respuesta){
                        actualizar(nombreAlumno,curso,fecha,hora,direccion,longitud,latitud);
                        finish();
                    }else {
                        Toast.makeText(this,R.string.existen_campos_vacios,Toast.LENGTH_SHORT).show();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                break;
            //En caso de que se accione los mapas
            case R.id.bt_ir_mapas:
                //ir hacia los mapas
                accionModificar.intentMapas();
                break;
            //Se acciona el botón de las fotos
            case R.id.bt_hacer_foto:
                //se registran los datos de fecha y alumno su nombre, porque son losprincipales para el nombre del archivo.
                nombreAlumno=etNombre.getText().toString();
                fecha=etFecha.getText().toString();
                //en caso de que los dos estén escritos
                if (nombreAlumno.length()!=0&&fecha.length()!=0){
                    //Se crea el diálogo para mostrar que escoja por qué camino quiere la foto
                    AlertDialog.Builder builder= new AlertDialog.Builder(this);
                    builder.setMessage(R.string.mensaje_dialogo_camara);
                    builder.setCancelable(false);
                    builder.setTitle(R.string.agregar_foto);
                    //Escoger si por medio de una foto
                    builder.setPositiveButton(R.string.nombre_boton_positivo_dialogo_camara, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Mandar el intent para hacerse la foto con la cámara del sistema
                            Intent intentFoto=new Intent(Constants.INTENT_TOMAR_FOTO);
                            //Así podemos obtener los resultados de la foto por cámara
                            startActivityForResult(intentFoto,TOMAR_FOTO);
                        }
                    });
                    //cancelar modificar la foto
                    builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    //modificar la foto por medio de la galería
                    builder.setNeutralButton(R.string.nombre_boton_neutral_dialogo_camara, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //hacer el intent para tomar la foto desde la galería
                            intent= new Intent();
                            intent.setType("image/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            //así podremos obtener los resultados de la foto por galería
                            startActivityForResult(intent, Modificar.TOMAR_FOTO_GALERIA);
                        }
                    });
                    //crear el dialogo
                    builder.create();
                    //mostrar el diálogo
                    builder.show();
                }else{
                    //Si hay campos sin llenar, muestre el Toast
                    Toast.makeText(this,R.string.toast_error_de_campos,Toast.LENGTH_SHORT).show();
                }
                break;
            //se acciona el boton de la fecha
            case R.id.et_fecha_clase:
                //mostrar el dialogo referido a cambiar la fecha
                Calendar calendario= Calendar.getInstance();
                dialogoFecha=new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        //guardar el dia, el mes y el año escogido
                        fecha=dayOfMonth+"-"+(month+1)+"-"+year;
                        //colocarlos en la actividad
                        etFecha.setText(fecha);
                    }
                },calendario.get(Calendar.YEAR),calendario.get(Calendar.MONTH),calendario.get(Calendar.DAY_OF_MONTH));
                //mostrar el dialogo
                dialogoFecha.show();
                break;
            //se escoge modificar la hora de las clases
            case R.id.et_hora_clase:
                //Se prepara el diálogo para las clases
                Calendar calendario2= Calendar.getInstance();
                dialogoTiempo= new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        //Se guarda la hora y los minutos
                        hora=hourOfDay+":"+minute;
                        //guardarlo en la actividad
                        etHora.setText(hora);
                    }
                },calendario2.get(Calendar.HOUR),calendario2.get(Calendar.MINUTE),true);
                //mostrar el diálogo
                dialogoTiempo.show();
                break;
        }
    }
    //metodo que hace referencia a actualizar en la base de datos los campos que se les introducen en el método como parámetros del método
    private void actualizar(String nombreAlumno, String curso, String fecha, String hora, String direccion, String longitud, String latitud) {
        //método de la otra clase que hace referencia a la actualización de la base de datos en el campo requerido e introducido.
        accionModificar.actualizarDatosBaseDatos(nombreAlumno, curso, fecha, hora, direccion, longitud, latitud);
    }
    //método que se encarga de la recepción de los datos obtenidos según los códigos los resultados a guardar.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try{
            //modificar los datos del mapa
            if(requestCode==CODIGO_SOLICITAR_MODIFICAR){
                accionModificar.delMapa(data);
            }
            //foto por foto de la cámara
            if (resultCode== Activity.RESULT_OK&&requestCode==TOMAR_FOTO){
                accionModificar.tomarFoto(data);
            }//foto xtraida de la galería
            if (resultCode== Activity.RESULT_OK&&requestCode==TOMAR_FOTO_GALERIA){
                accionModificar.tomarFotoGaleria(data);
            }
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    //Los siguientes métodos permiten que no se pierda la información cuando se rote la actividad
    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putString(Constants.NOMBRE_MANTENER, etNombre.getText().toString());
        outState.putString(Constants.CURSO_MANTENER, etCurso.getText().toString());
        outState.putString(Constants.FECHA_MANTENER, etFecha.getText().toString());
        outState.putString(Constants.HORA_MANTENER, etHora.getText().toString());
        outState.putString(Constants.DIRECCION_MANTENER, etDireccion.getText().toString());
        outState.putString(Constants.LATITUD_MANTENER, etLatitud.getText().toString());
        outState.putString(Constants.LONGITUD_MANTENER, etLongitud.getText().toString());
        outState.putString(Constants.FOTO_ANALISIS,tvRuta.getText().toString());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);
        String nomb_mantener=savedInstanceState.getString(Constants.NOMBRE_MANTENER);
        String curso_mantener=savedInstanceState.getString(Constants.CURSO_MANTENER);
        String fecha_mantener=savedInstanceState.getString(Constants.FECHA_MANTENER);
        String hora_mantener=savedInstanceState.getString(Constants.HORA_MANTENER);
        String dir_mantener=savedInstanceState.getString(Constants.DIRECCION_MANTENER);
        String lat_mantener=savedInstanceState.getString(Constants.LATITUD_MANTENER);
        String long_mantener=savedInstanceState.getString(Constants.LONGITUD_MANTENER);
        String foto_ruta=savedInstanceState.getString(Constants.FOTO_ANALISIS);
        etNombre.setText(nomb_mantener);
        etCurso.setText(curso_mantener);
        etFecha.setText(fecha_mantener);
        etHora.setText(hora_mantener);
        etDireccion.setText(dir_mantener);
        etLatitud.setText(lat_mantener);
        etLongitud.setText(long_mantener);
        tvRuta.setText(foto_ruta);
    }
}
