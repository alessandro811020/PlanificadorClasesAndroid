package com.masterd.android.app.clases;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
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

import com.masterd.android.app.clases.mapa.servicios.ComprobarCampos;
import com.masterd.android.app.clases.servicios.componentes.Constants;

import java.util.Calendar;
import java.util.concurrent.ExecutionException;


/**
 * Actividad que permitirá al usuario introducir los datos de las clases a los alumnos. Estos datos son
 * Nombre del Alumno(TextView), Curso que está realizando(TextView), fecha de la clase(DatePickerDialog)
 * hora de la clase(TimePickerDialog), Dirección de la casa, latitud y longitud(datos que serán recibidos
 * a través del mapa que enviará la información al cual accederemos por un botón. Finalmente, se agregará
 * una foto del alumno o como referencia, la cual puede ser introducida desde la galería o por medio de la
 * cámara. La imagen se almacena en una carpeta FotosClases de la memoria interna del teléfono, la cual es
 * eliminada cuando se elimina al estudiante de la base de datos.
 * Los Datos son almacenados en una base de datos SQLite de la cual se extraerá la información para la utilización.
 */
public class Formulario extends AppCompatActivity implements View.OnClickListener {
    /**
     * Campos utilizados en la Actividad, los tres primeros son las constantes utilizadas para el intercambio de
     * información entre actividades.
      */
    protected static final int CODIGO_SOLICITUD=1000;
    protected static final int TOMAR_FOTO=2000;
    protected static final int TOMAR_FOTO_GALERIA=3000;
    //Esta es una clase creada para el intercambio de información entre esta actividad y otros elementos del programa.
    private final AccionFormulario accionFormulario = new AccionFormulario(this);
    //Objeto intent creado para el intercambio de información entre actividades.
    Intent intent;
    //Botones de la actividad
    Button btRegistrar, btIrMapa, btFoto;
    //Otros campos y Views de la actividad donde se recogen y muestran informaciones.
    EditText etNombre, etCurso, etFecha, etHora;
    TextView tvDireccion, tvLongitud, tvLatitud, tvRuta;
    //Contenedor de valores para enviar a la base de datos.
    ContentValues valores;
    //Los diálodos para las fechas y para las horas de las clases
    DatePickerDialog dialogoFecha;
    TimePickerDialog dialogoTiempo;
    //Cadenas de texto utilizadas a lo largo del programa
    String direccionR, latitudR, longitudR, nombreAlumno, curso, fecha, hora, direccion, longitud, latitud, nombreArchivo,ubicacionFoto;

    /**
     * Este método de la actividad se llama al crear la actividad donde entonces se deben inicializar todos los
     * componentes necesarios, además de darles los valores respectivos en caso de ser necesario.
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //el layout de la Actividad
        setContentView(R.layout.activity_formulario);
        //inicializar estos campos porque luego recibirán valores más adelante
        direccionR= "";
        latitudR="";
        longitudR= "";

        //localizando la barra de acciones con el titulo
        Toolbar barraTareas= (Toolbar) findViewById(R.id.barra_tarea1);
        setSupportActionBar(barraTareas);
        //Identificar en el layout los componentes de la actividad referidas a la información
        etNombre=(EditText) findViewById(R.id.et_nombre_ingresar);
        etCurso=(EditText) findViewById(R.id.et_curso);
        etFecha=(EditText) findViewById(R.id.et_fecha_clase);
        etFecha.setInputType(InputType.TYPE_NULL);
        etHora=(EditText) findViewById(R.id.et_hora_clase);
        etHora.setInputType(InputType.TYPE_NULL);
        tvDireccion =(TextView) findViewById(R.id.et_direccion_final);
        tvLongitud =(TextView) findViewById(R.id.et_longitud);
        tvLatitud =(TextView) findViewById(R.id.et_latitud);
        tvRuta=(TextView) findViewById(R.id.tv_direccion_foto);

        //Identificar los botones de la actividad
        btRegistrar =(Button) findViewById(R.id.bt_registrar);
        btIrMapa=(Button) findViewById(R.id.bt_ir_mapas);
        btFoto=(Button) findViewById(R.id.bt_hacer_foto);

        //Agregarle los listeners a cada uno de los componentes que los necesiten en la utilidad de la actividad
        etFecha.setOnClickListener(this);
        etHora.setOnClickListener(this);
        btRegistrar.setOnClickListener(this);
        btIrMapa.setOnClickListener(this);
        btFoto.setOnClickListener(this);
    }

    /**
     * Al regresar a esta actividad, si viene del mapa recibirá la información de la dirección, latitud y longitud,
     * en caso de que no reciba cambios, no se modifican los valores de dichos campos, lo demás se mantiene constante
     */
    @Override
    protected void onResume() {
        super.onResume();
        try {
            if (direccionR.length()!=0){
                tvDireccion.setText(direccionR);
            }
            if (latitudR.length()!=0){
                tvLatitud.setText(latitudR);
            }
            if (longitudR.length()!=0){
                tvLongitud.setText(longitudR);
            }
        }catch (NullPointerException e){
            e.printStackTrace();
        }

    }

    /**
     * Uno de los métodos que se refiere al Action Bar de la actividad, donde se identifica al menú.
     * Además se le identifica al layout que lo utilizará
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.lista_menu,menu);
        return  true;
    }

    /**
     * Método que al introdcirle click a uno de los items de la barra de acción, le dará respuesta
     * a la acción deo usuario.
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.mBt_cerrar:
                finish();
                return true;
            case R.id.mBt_salir:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Método que implementa la interface OnClickListener donde a aquellos Listeners que se identificaron
     * en el método onCreate(), según su id, entonces dará respuesta.
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //Botón para registrar las clases en la base de datos
            case R.id.bt_registrar:
                //Primero guardamos los datos en las variables asociadas.
                nombreAlumno=etNombre.getText().toString();
                curso=etCurso.getText().toString();
                fecha=etFecha.getText().toString();
                hora=etHora.getText().toString();
                direccion= tvDireccion.getText().toString();
                longitud= tvLongitud.getText().toString();
                latitud= tvLatitud.getText().toString();
                //Son guardados en un Array de String para ser introducidos en un AsyncTask que comprobará si hay algún campo vacío
                String[] campos=new String[]{nombreAlumno,curso,fecha,hora,direccion,longitud,latitud};
                //Crear el objeto referido al AsyncTask introduciéndole los datos en el constructor
                ComprobarCampos comprobarCampos=new ComprobarCampos(campos);
                //ejecutar la tarea
                comprobarCampos.execute();
                try {
                    //obtener respuesta de la tarea
                    boolean respuesta= (boolean) comprobarCampos.get();
                    /*
                    Según la respuesta, si es verdadero, los campos están llenos, entonces se procede a insertar
                    los datos en la base de datos y cerrar la actividad. Señalo que la foto no es un campo obligatorio,
                    por ese motivo no es introducido como obligatorio. Su inserción está directa en el método de insertar.
                    En caso de no tener foto, el mismo programa no la realiza y no la guarda en la carpeta de fotos.
                    En caso de que sea falsa la variable respuesta, entonces el programa lanzará un Toast especificando
                    que existen campos vacíos.
                     */
                    if (respuesta){
                        insertar(nombreAlumno,curso,fecha,hora,direccion,longitud,latitud);
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
            case R.id.bt_ir_mapas:
                /*
                Desde este botón iremo al mapa de madrid, señalando el centro de Madrid como coordenadas para que desde allí
                se envíen los datos de la dirección y de la longitud y la latitud
                 */
                irMapa();
                break;
            case R.id.bt_hacer_foto:
                /*
                se accede a un cuadro de dialogo que tiene tres opciones, ir a galería, cancelar o ir a realizar una foto para
                guardar en el programa.
                 */
                //Estos dos campos son los importantes para darle nombre al archivo de la foto, porque será nombrefecha.jpg
                nombreAlumno=etNombre.getText().toString();
                fecha=etFecha.getText().toString();
                //en caso de que se accione ese botón y ambos campos estén vacíos un toast lo informará
                if (nombreAlumno.length()!=0&&fecha.length()!=0){
                    accionFormulario.mostrarDialogoFoto();
                }else {
                    Toast.makeText(this,R.string.toast_error_de_campos,Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.et_fecha_clase:
                /**
                 * este EditText que tiene Listener, permitirá al usuario brindar con exactitud la fecha de la clase desde
                 * un DatePickerDialog.
                 */
                mostrarDialogoFecha();
                break;
            case R.id.et_hora_clase:
                /**
                 * este EditText que tiene Listener, permitirá al usuario brindar con exactitud la hora de la clase desde
                 * un TimePickerDialog.
                 */
                mostrarDialogoTiempo();
                break;
        }

    }

    /**
     * método que se comentó anteriormente, donde se mostrará un diálogo al usuario para que escoja la fecha y se guarde
     * en una cadena y de esta se muestre en el EditText que fue accionado
     */
    private void mostrarDialogoTiempo() {
        Calendar calendario2= Calendar.getInstance();
        dialogoTiempo= new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                hora=hourOfDay+":"+minute;
                etHora.setText(hora);
            }
        },calendario2.get(Calendar.HOUR),calendario2.get(Calendar.MINUTE),true);
        dialogoTiempo.show();
    }
    /**
     * método que se comentó anteriormente, donde se mostrará un diálogo al usuario para que escoja la hora y se guarde
     * en una cadena y de esta se muestre en el EditText que fue accionado
     */
    private void mostrarDialogoFecha() {
        Calendar calendario= Calendar.getInstance();
        dialogoFecha=new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                fecha=dayOfMonth+"-"+(month+1)+"-"+year;
                etFecha.setText(fecha);
            }
        },calendario.get(Calendar.YEAR),calendario.get(Calendar.MONTH),calendario.get(Calendar.DAY_OF_MONTH));
        dialogoFecha.show();
    }
    /**
     * Mñetodo donde se accede a la clase AccionFormulario, donde desde allí accederemos al Mapa de la aplicación
     */
    private void irMapa() {
        accionFormulario.irMapa();
    }
    /**
     * método que se insertará en la base de datos la información recogida por cada clase, pasando por la clase AccionFormulario
     * @param nombreAlumno
     * @param curso
     * @param fecha
     * @param hora
     * @param direccion
     * @param longitud
     * @param latitud
     */
    private void insertar(String nombreAlumno, String curso, String fecha, String hora, String direccion, String longitud, String latitud) {
        accionFormulario.insertar(nombreAlumno,curso,fecha,hora,direccion,longitud,latitud);
    }

    /**
     * Método que mermite el feedback de la información solicitada a las diferentes Actividades y clases cuando se realizó un intent por medio
     * del metodo starForResult.
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try{
            if(requestCode==CODIGO_SOLICITUD){
                //Código del mapa, para recibir su información
                accionFormulario.addOnActivityResultDatosMapas(data);
            }
            if (resultCode== Activity.RESULT_OK&&requestCode==TOMAR_FOTO){
                //Recibimos la información cuando viene de tomarnos una foto con la cámara.
                accionFormulario.addOnActivityTomarFotos(data);
            }
            if (resultCode== Activity.RESULT_OK&&requestCode==TOMAR_FOTO_GALERIA){
                //Tomar los datos desde la galería de fotos del sistema.
                accionFormulario.addOnActivityTomarFotosGaleria(data);
            }
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    /**
     * getter y setter del intent, que será necesitado por
     * @return
     */
    @Override
    public Intent getIntent() {
        return intent;
    }

    @Override
    public void setIntent(Intent intent) {
        this.intent = intent;
    }

    /**
     * @Override
    protected void onSaveInstanceState(Bundle outState){
    super.onSaveInstanceState(outState);
    outState.putInt("CONT", cont);
    }
     @Override
     protected void onRestoreInstanceState(Bundle savedInstanceState){
     super.onRestoreInstanceState(savedInstanceState);
     cont = savedInstanceState.getInt("CONT");
     tvCont.setText(String.valueOf(cont));
     }
     * @param outState
     */

    //Los siguientes métodos permiten que no se pierda la información cuando se rote la actividad
    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putString(Constants.NOMBRE_MANTENER, etNombre.getText().toString());
        outState.putString(Constants.CURSO_MANTENER, etCurso.getText().toString());
        outState.putString(Constants.FECHA_MANTENER, etFecha.getText().toString());
        outState.putString(Constants.HORA_MANTENER, etHora.getText().toString());
        outState.putString(Constants.DIRECCION_MANTENER, tvDireccion.getText().toString());
        outState.putString(Constants.LATITUD_MANTENER, tvLatitud.getText().toString());
        outState.putString(Constants.LONGITUD_MANTENER, tvLongitud.getText().toString());
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
        tvDireccion.setText(dir_mantener);
        tvLatitud.setText(lat_mantener);
        tvLongitud.setText(long_mantener);
        tvRuta.setText(foto_ruta);
    }

}
