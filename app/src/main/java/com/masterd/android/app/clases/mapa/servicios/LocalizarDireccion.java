package com.masterd.android.app.clases.mapa.servicios;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class LocalizarDireccion extends AsyncTask {
    List<Address> addresses = null;
    Geocoder geocoder;
    String direccion;
    Context context;
    public LocalizarDireccion(Context context,String direccion) {
        geocoder= new Geocoder(context);
        this.direccion=direccion;
        this.context=context;
    }

    @Override
    protected ArrayList<String> doInBackground(Object[] params) {
        ArrayList<String> datos=new ArrayList<>();
        try {
            addresses=geocoder.getFromLocationName(direccion.trim(),1);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } catch (IllegalArgumentException illegalArgumentException) {
            illegalArgumentException.printStackTrace();
        }

        if (addresses == null || addresses.size()  == 0) {
            return null;
        } else {
            LatLng coordenadas= new LatLng(addresses.get(0).getLatitude(),addresses.get(0).getLongitude());
            String direccionLugar=addresses.get(0).getAddressLine(0)+"\n"+addresses.get(0).getAddressLine(1);
            datos.add(0,direccionLugar);
            datos.add(1, String.valueOf(coordenadas.latitude));
            datos.add(2,String.valueOf(coordenadas.longitude));
        }
        return datos;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

}
