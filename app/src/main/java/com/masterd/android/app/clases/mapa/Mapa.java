package com.masterd.android.app.clases.mapa;

import android.content.ContentResolver;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.masterd.android.app.clases.R;
import com.masterd.android.app.clases.mapa.servicios.LocalizarDireccion;
import com.masterd.android.app.clases.mapa.servicios.SeleccionDatosMarcadorMapa;
import com.masterd.android.app.clases.servicios.componentes.CamposClasesDB;
import com.masterd.android.app.clases.servicios.componentes.Constants;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Clase referida al mapa, es la clase que muestra diversa información, utilizando el serivicio de
 * googlempap, como mostrar las clases, como buscar las direcciones de las clases, otras direcciones,
 * además que podrá verse el plano sobre madrid directamente.
 */
public class Mapa extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener{
    //Código que especifica que cuando se devuelva al intent, viene de esta clase.
    private static final int CODIGO_RETORNO_OK=8100;
    //Se crean los campos de toda la clase
    //los dos intent que se trabajan dentro de la Actividad
    Intent intent, intentOp;
    //el EditText que se utilizará para recoger la información de la dirección escrita por el usuario.
    EditText etBuscar;
    //Objeto Googlemap que sirve para comunicar entre los diferentes métodos para el intercambio de
    // informacion con el usuario.
    GoogleMap mapaTrabajo;
    //Objeto SupportMapFragment que se utilizará dentro de la actividad
    SupportMapFragment mapa;
    //los dos botones que utiliza la actividad
    Button btBuscar, btSalir;
    //las cadenas de texto que utilizará la Actividad
    String direccion, longitud, latitud, entrada;
    //Las coordenadas de Madrid, para el localizador inicial
    static final LatLng Madrid= new LatLng(40.418889,-3.691944);
    //ContentResolver para las consultas a la base de datos
    ContentResolver contentResolver;
    //objeto que se dedicará a realizar la tarea sincronizada de selecionar los marcadores a mostrar en el mapa,
    //por medio de este, se obtendrán las coordenadas y la dirección de las clases para mostrarlas todas dentro
    //de los marcadores
    SeleccionDatosMarcadorMapa sMarcadores;
    //ArrayList que recoge todos los datos de los marcadores
    ArrayList<MarkerOptions> marcadores;

    /**
     * Método que crea la actividad, por lo que se debe crear y reflejar los datos según las características de
     * entrada. Además de instanciar cada unos de los componentes que trabajará la Actividad
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        //Instanciamos el ContentResolver que permite hacer las consultas a la base de datos.
        contentResolver=getContentResolver();
        /**
         * los marcadores que se recibirán en caso de que hayan clases registradas, se observarán en el
         * mapa los respectivos marcadores de las direcciones.
         */
        sMarcadores= new SeleccionDatosMarcadorMapa(contentResolver);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa);
        //hace referencia a la barra de acción de la actividad. referenciarla y establecerla.
        Toolbar barraTareas= (Toolbar) findViewById(R.id.barra_tarea4);
        setSupportActionBar(barraTareas);
        //instanciando el intent, obteniendo los datos que la lanzaron.
        intentOp=getIntent();
        //obtener los datos desde los extras, para saber el origen del lanzamiento
        entrada=intentOp.getStringExtra(Constants.ORIGEN);

        try {
            //ejecutar la tarea sincronizada
            sMarcadores.execute();
            //obtener los datos de de los marcadores de la tarea
            marcadores=(ArrayList<MarkerOptions>) sMarcadores.get();
            //agarrar las excepciones posibles a producirse.
        }catch (InterruptedException e){
            Toast.makeText(this,R.string.busqueda_interrumpida,Toast.LENGTH_SHORT).show();
        }catch (ExecutionException e){
            Toast.makeText(this,R.string.problemas_ejecución,Toast.LENGTH_SHORT).show();
        }
        //identificar en la activity los componentes que se reflejan en el layout, para el intercambio
        //de información con el usuario.
        etBuscar=(EditText) findViewById(R.id.editText);
        btBuscar=(Button) findViewById(R.id.btm_buscar);
        btSalir=(Button) findViewById(R.id.btm_aceptar);
        //identificar el mapa dentro de la actividad.
        mapa=((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragment));
        //permitir el trabajo y el intercambio de información con el usuario
        mapa.getMapAsync(this);
        //declararles los Listeners a los botones del layout de la actividad.
        btBuscar.setOnClickListener(this);
        btSalir.setOnClickListener(this);
    }

    /**
     * método que establece ya el trabajo interno dentro del mapa, porque prefija los marcadores, además
     * de que establece los marcadores ya de las clases, o de aquellas direcciones que se consulten.
     * @param googleMap
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        switch (entrada){
            //si la entrada es desde la actividad de modificar
            case Constants.MODIFICAR:
                //obtener de las coordenadas que fueron introducidas por los intent.
                String direccionModificar=intentOp.getStringExtra(Constants.DIRECCION_MODIFICAR);
                String latitudModificar=intentOp.getStringExtra(Constants.LATITUD_MODIFICAR);
                String longitudModificar=intentOp.getStringExtra(Constants.LONGITUD_MODIFICAR);
                //crear en el mapa el marcador con la direccion y las coordenadas
                googleMap.addMarker(new MarkerOptions()
                        .draggable(true)
                        .zIndex(6)
                        .position(new LatLng(Double.valueOf(latitudModificar),Double.valueOf(longitudModificar)))
                        .title(direccionModificar));
                //se crea un objeto cámara para que se mueva hacia la dirección especificada
                CameraUpdate camMod =CameraUpdateFactory.newLatLngZoom(new LatLng(Double.valueOf(latitudModificar),Double.valueOf(longitudModificar)),13);
                googleMap.animateCamera(camMod);
                break;
            //si viene desde principal
            case Constants.PRINCIPAL:
                //si hay marcadores creados porque ya se han registrado clases en la base de datos
                if (marcadores!=null){
                    for (int i=0;i<marcadores.size();i++){
                        //agregar los marcadores al mapa
                        googleMap.addMarker(marcadores.get(i));
                    }
                    //mover la camara al centro de madrid, para observar todas las localizaciones de las clases.
                    CameraUpdate cam =CameraUpdateFactory.newLatLngZoom(Madrid,10);
                    googleMap.animateCamera(cam);
                }else {
                    //si no hay marcadores, entonces centrarse en madrid unicamente sin marcadores
                    googleMap.addMarker(new MarkerOptions()
                            .draggable(true)
                            .zIndex(7)
                            .position(Madrid)
                            .title(Constants.MADRID));
                    //mover la cámara hacia madrid
                    CameraUpdate cam =CameraUpdateFactory.newLatLngZoom(Madrid,11);
                    googleMap.animateCamera(cam);
                }
                break;
            //valor por defecto, marcador de madrid
            default:googleMap.addMarker(new MarkerOptions()
                    .draggable(true)
                    .zIndex(7)
                    .position(Madrid)
                    .title(Constants.MADRID));
                CameraUpdate cam =CameraUpdateFactory.newLatLngZoom(Madrid,11);
                googleMap.animateCamera(cam);

        }
        //le damos valor al objeto GoogleMap de la clase, el de este método
        mapaTrabajo=googleMap;
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

    /*
    el método que refleja las acciones que se realizarán en los respepctivos botones declarados en el método onCreate()
     */
    @Override
    public void onClick(View v) {
        //según el botón que ha sido pulsado, entonces las acciones
        switch(v.getId()){
            //Se ha pulsado aceptar
            case R.id.btm_aceptar:
                //Creamos el intent obteniendo el origen que lanzó la actividad Mapa originalmente
                intent=getIntent();
                //obtener de donde se lanzó la actividad para que de esa manera enviarle los resultados
                String origen=intent.getStringExtra(Constants.ORIGEN);
                //Si el origen fue formulario
                if (origen.equals(Constants.FORMULARIO)){
                    //se crea el intent con los datos de la dirección y las coordenadas de las clases recién introducidas.
                    Intent datos = new Intent();
                    datos.putExtra(CamposClasesDB.Datos.DIRECCION,direccion);
                    datos.putExtra(CamposClasesDB.Datos.LATITUD,latitud);
                    datos.putExtra(CamposClasesDB.Datos.LONGITUD,longitud);
                    //devolver los resultados
                    setResult(CODIGO_RETORNO_OK,datos);
                }
                //si el origen es modificar
                if (entrada.equals(Constants.MODIFICAR)){
                    //Se crea el intent para introducir los datos cno vistas a enviarlos para la modificación
                    Intent datos = new Intent();
                    datos.putExtra(CamposClasesDB.Datos.DIRECCION,direccion);
                    datos.putExtra(CamposClasesDB.Datos.LATITUD,latitud);
                    datos.putExtra(CamposClasesDB.Datos.LONGITUD,longitud);
                    //Devolver los resultados
                    setResult(CODIGO_RETORNO_OK,datos);
                }
                //Sea cual sea el origen y destino, se cierra la actividad.
                finish();
                break;
            //Se presiona el boton de buscar
            case R.id.btm_buscar:
                //obtener la dirección a buscar del EditText
                direccion=etBuscar.getText().toString();
                //crear la tarea sincronizada de buscar la dirección para devolverla a la actividad cuando
                //esta haya concluido
                ArrayList<String> datos = null;
                LocalizarDireccion localizarDireccion= new LocalizarDireccion(this,direccion);
                try {
                    //Ejecutamos la tarea para obtener los datos de la dirección
                    localizarDireccion.execute();
                    //obtener los datos de la tarea
                    datos= (ArrayList<String>) localizarDireccion.get();
                    //filtro que si no encuentra la dirección responde y si es así muestra el marcador
                    if (datos!=null){
                        //Crear el marcador para introducirlelos datos
                        MarkerOptions marcadorDireccion= new MarkerOptions();
                        //posición del marcador
                        marcadorDireccion.position(new LatLng(Double.valueOf(datos.get(1)),Double.valueOf(datos.get(2))));
                        //título del marcador
                        marcadorDireccion.title(datos.get(0));
                        //establecer el ícono del marcador
                        marcadorDireccion.icon(BitmapDescriptorFactory.defaultMarker());
                        //agregarle al mapa el marcador
                        mapaTrabajo.addMarker(marcadorDireccion);
                        //crear la cámara para que se localice según las coordenadas introducidas.
                        CameraUpdate cam =CameraUpdateFactory.newLatLngZoom(new LatLng(Double.valueOf(datos.get(1)),Double.valueOf(datos.get(2))),14);
                        //mover la cámara en el mapa
                        mapaTrabajo.animateCamera(cam);
                        //asignarle los valores de dirección, latitud y longitud a los campos de la clase respectivos por
                        //si después se desean guardar  y enviar a las otras actividades como formulario en este caso o modificar también
                        direccion=datos.get(0);
                        latitud=datos.get(1);
                        longitud=datos.get(2);
                    }else{
                        Toast.makeText(this, R.string.no_localizada,Toast.LENGTH_SHORT).show();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }catch (NullPointerException e){
                    e.printStackTrace();
                }catch (IndexOutOfBoundsException e){
                    e.printStackTrace();
                }
                break;
        }
    }
}
