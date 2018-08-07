package com.masterd.android.app.clases.servicios.componentes;

import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.masterd.android.app.clases.R;

/**
 * ClaseAdapter extiende de BaseAdapter, donde se colocarán las distintas clases que se han guardado
 * dentro de la base de datos. Esta classe será instanciada en la clase Principal, por loq ue deberá
 * contener los componentes principales de cada clase.
 */
public class ClasesAdapter extends BaseAdapter {
    //para el manejo de la vista del adapter
    private LayoutInflater inflater;
    //para el cursor de la consulta a la base de datos
    private Cursor cursor;
    //constructor de la clase, introduciéndole la consulta de la base de datos, además del contexto
    //que lo solicita.
    public ClasesAdapter(Context context,Cursor cursor) {
        //inicializar el cursor y el inflater de la vista
        inflater=LayoutInflater.from(context);
        this.cursor = cursor;
    }
    //método para conocer el tamaño de la consulta
    @Override
    public int getCount() {
        return cursor.getCount();
    }
    //obtener el cursor, según la posicion que se introduzca.
    @Override
    public Cursor getItem(int position) {
        cursor.moveToPosition(position);
        return cursor;
    }
    //devuelve el id de tipo long del adapter.
    @Override
    public long getItemId(int pinga) {
        return pinga;
    }

    /**
     * Método para trabajar con la vista del Adapter. introduciendo la posicion clickeada, la vista,
     * y la vista madre. De esta manera se aclara que datos introducir y en que lugar dentro de la vista.
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //creamos un objeto de la clase interna ViewVistas que contiene todos los elementos que contiene
        //una vista de este adapter
        ViewVistas views=null;
        try{
            //Si la vista no esta creada, pues crearla
            if (convertView==null){
                //Creamos la vista en función de llayout especificado dentro del inflater.
                convertView=inflater.inflate(R.layout.item_lista_clase,null);
                //instanciamos la clase interna
                views=new ViewVistas();
                //identificamos cada componente del layout del Adapter
                views.id=(TextView) convertView.findViewById(R.id.tv_id);
                views.imagenFoto=(ImageView) convertView.findViewById(R.id.item_iv_foto);
                views.nombre=(TextView) convertView.findViewById(R.id.item_tv_claseNombre);
                views.curso=(TextView) convertView.findViewById(R.id.item_tv_classSubNombre);
                //colocar el cursor en la posicion para mostrar los datos
                cursor.moveToPosition(position);
                //Escribir en cada componente los datos de cada columna entro de la vista.
                views.id.setText(cursor.getString(cursor.getColumnIndex(CamposClasesDB.Datos._ID)));
                views.nombre.setText(cursor.getString(cursor.getColumnIndex(CamposClasesDB.Datos.NOMBRE)));
                views.curso.setText(cursor.getString(cursor.getColumnIndex(CamposClasesDB.Datos.CURSO)));
                views.imagenFoto.setImageBitmap(leerFoto(cursor.getString(cursor.getColumnIndex(CamposClasesDB.Datos.FOTO))));
                //Establecer los componentes dentro de la vista
                convertView.setTag(views);
            }else {
                //Si la vista ya está creada, simplemente se le colocan los resultados dentro de los componentes.
                //moviendo el cursor a la posicion entregada
                cursor.moveToPosition(position);
                //Asignarle los valores a los componentes del Adapter
                views.id.setText(cursor.getString(cursor.getColumnIndex(CamposClasesDB.Datos._ID)));
                views.nombre.setText(cursor.getString(cursor.getColumnIndex(CamposClasesDB.Datos.NOMBRE)));
                views.curso.setText(cursor.getString(cursor.getColumnIndex(CamposClasesDB.Datos.CURSO)));
                views.imagenFoto.setImageBitmap(leerFoto(cursor.getString(cursor.getColumnIndex(CamposClasesDB.Datos.FOTO))));
            }
        //Controlar las excepciones que podrían ocurrir.
        }catch (CursorIndexOutOfBoundsException e){
            e.printStackTrace();
        }catch (NullPointerException e){
            e.printStackTrace();
        }
        //DEvolver la vista para ser utilizada
        return convertView;
    }
    //Clase interna que hace referencia a los elementos que tiene el layout referido al Adapter
    class ViewVistas{
        TextView id;
        ImageView imagenFoto;
        TextView nombre;
        TextView curso;
    }
    //Método que lee el archivo introduciéndole la ruta
    public Bitmap leerFoto(String ruta){
        Bitmap fotoAgregar=null;
        fotoAgregar=BitmapFactory.decodeFile(ruta);
        return fotoAgregar;
    }

}
