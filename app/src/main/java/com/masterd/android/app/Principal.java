package com.masterd.android.app;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.masterd.android.app.clases.Formulario;
import com.masterd.android.app.clases.R;
import com.masterd.android.app.clases.acciones.Seleccionado;
import com.masterd.android.app.clases.datos.ProveedorClases;
import com.masterd.android.app.clases.mapa.Mapa;
import com.masterd.android.app.clases.servicios.componentes.CamposClasesDB;
import com.masterd.android.app.clases.servicios.componentes.ClasesAdapter;
import com.masterd.android.app.clases.servicios.componentes.Constants;


/**
 * Actividad Principal de la Aplicación donde salen reflejadas todas las clases que se han planificado
 * en la app. Botones para agregar las clases, cons sus respectivos datos, además del acceso al mapa para
 * ver la localización de estas, además de buscar alguna dirección de ser necesario.
 */
public class Principal extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    /**
     * Campos de la Actividad
     */
    Button btverMapas, btAgregarClase;
    TextView titulo;
    ListView listaClases;

    Intent intent;
    ClasesAdapter clasesAdapter;
    String[] columnas=new String[]{CamposClasesDB.Datos._ID,CamposClasesDB.Datos.NOMBRE, CamposClasesDB.Datos.CURSO, CamposClasesDB.Datos.FOTO};
    String presentacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        //localizando la barra de acciones con el titulo
        Toolbar barraTareas= (Toolbar) findViewById(R.id.barra_tarea);
        setSupportActionBar(barraTareas);

        listaClases= (ListView) findViewById(R.id.lista_clases);

        //inicializar el cursor de la búsqueda
        Cursor cursor = getContentResolver().query(ProveedorClases.CONTENT_URI,columnas,null,null,null);
        //la vista titulo es identificada
        titulo= (TextView) findViewById(R.id.tv_titulo_clases);
        //método que hace el filtro de lo que se visualizará en la Actividad
        reflejarPrincipal(cursor, titulo);
        //identificación de los botones que tiene la actividad, con sus respectivos listeners
        btAgregarClase= (Button) findViewById(R.id.bt_agregar_clases);
        btverMapas= (Button) findViewById(R.id.bt_mapas);
        btAgregarClase.setOnClickListener(this);
        btverMapas.setOnClickListener(this);
    }
    /*
    método que se llama cuando regresa la actividad y buscamos que muestre nuevamente la información
     */
    @Override
    protected void onResume() {
        super.onResume();

        Cursor cursor = getContentResolver().query(ProveedorClases.CONTENT_URI,columnas,null,null,null);
        reflejarPrincipal(cursor, titulo);
    }

    /*
        el método que refleja las acciones que se realizarán en los respepctivos botones declarados en el método onCreate()
         */
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.bt_mapas:
                lanzarIntentBotonesPrincipal(Constants.PRINCIPAL,Mapa.class);
                break;
            case R.id.bt_agregar_clases:
                lanzarIntentBotonesPrincipal(null,Formulario.class);
                break;
        }
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
                return true;
            case R.id.mBt_salir:
                System.exit(0);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    /*
    método que se activa al darle click a cualquiera de los Adapter referidos a las clases creadas
    dentro de la actividad Principal
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id){
        TextView identificador= (TextView) view.findViewById(R.id.tv_id);
        String idFinal= identificador.getText().toString();
        lanzarIntentDesdeItem(idFinal);
    }
    //los siguientes métodos complementan a los métodos anteriores para su trabajo
    /*
    lanzar el intent a las clases creadas en el adapter
     */
    private void lanzarIntentDesdeItem(String idFinal) {
        intent =new Intent();
        intent.putExtra(Constants.ESCOGIDO,idFinal);
        intent.setClass(Principal.this, Seleccionado.class);
        startActivity(intent);
    }
    /*
    desde los botones de la actividad, lanzamientos de los intent para las acciones solicitadas
    */
    private void lanzarIntentBotonesPrincipal(String extra, Class clase) {
        intent=new Intent();
        intent.setClass(this, clase);
        intent.putExtra(Constants.ORIGEN,extra);
        startActivity(intent);
    }
    /*
    método que pemite mostrar en la activity las acciones o el texto inicial de entrada
     */
    private void reflejarPrincipal(Cursor cursor, TextView titulo) {
        //si la consulta no tiene clases guardadas
        if (cursor.getCount()==0){
            presentacion=getString(R.string.presentacion);
            titulo.setText(presentacion);
            listaClases.setAdapter(null);
        }else{
            //si hay clases guardadas incluirlas en el adapter
            cursor.moveToFirst();
            presentacion=getString(R.string.tv_titulo_clases);
            titulo.setText(presentacion);
            clasesAdapter= new ClasesAdapter(Principal.this,cursor);
            listaClases.setAdapter(clasesAdapter);
            listaClases.setOnItemClickListener(this);
        }
    }
}
