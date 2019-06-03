package com.example.autosv2;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Random;

public class InicioFragment extends Fragment {
    private ListView lista;
    private final int AUTOS = 0, PERSONAS = 1, SERVICIOS = 2;
    private ArrayList<Contenido>[] contenido;
    private ArrayAdapter<Contenido> adapter[];

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle("Nisson - Inicio");
        return inflater.inflate(R.layout.fragment_inicio, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        crearListas(view);
        setupTabLayout(view);
    }

    private void crearListas(View view) {

        BaseDeDatos bd = BaseDeDatos.getBD();
        SQLiteDatabase query = bd.getWritableDatabase();

        contenido = new ArrayList[3];
        for(int i = 0; i< contenido.length; i++)
            contenido[i] = new ArrayList<>();

        Cursor c = query.rawQuery("SELECT * from autos", null);
        while(c.moveToNext()) {
            String[] atributos = new String[6];
            atributos[0] = c.getString(0);
            atributos[1] = c.getString(1);
            atributos[3] = c.getString(2);
            atributos[4] = "-" +c.getInt(4);
            contenido[AUTOS].add(new Contenido(c.getBlob(3), atributos, c.getInt(5) == 0));
        }

        c = query.rawQuery("SELECT * from personas", null);
        while(c.moveToNext()) {
            String[] atributos = new String[6];
            atributos[0] = c.getString(0);
            atributos[1] = c.getString(1);
            atributos[3] = c.getString(2);
            contenido[PERSONAS].add(new Contenido(null, atributos, c.getInt(3) == 0));
        }

        c = query.rawQuery("SELECT * from servicios", null);
        while(c.moveToNext()) {
            String[] atributos = new String[6];
            atributos[0] = "#" +c.getInt(0);
            atributos[1] = c.getString(1);
            atributos[2] = c.getString(2);
            atributos[3] = c.getInt(3) +" KM";
            atributos[4] = "$" +c.getDouble(4);
            atributos[5] = fieldToFecha(c.getString(5));
            contenido[SERVICIOS].add(new Contenido(null, atributos, true));
        }

        adapter = new ArrayAdapter[3];
        for(int i = 0; i < contenido.length; i++)
            adapter[i] = new ListAdapter(i);

        query.close();
    }

    public String fieldToFecha(String fecha) {
        return fecha.substring(6)+"/"+fecha.substring(4, 6)+"/"+fecha.substring(0, 4);
    }

    private void setupTabLayout(final View view) {
        TabLayout mTabLayout = (TabLayout) view.findViewById(R.id.tabLayout);
        lista = (ListView) view.findViewById(R.id.lista);
        lista.setAdapter(adapter[AUTOS]);

        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            public void onTabSelected(TabLayout.Tab tab) {
                lista.setAdapter(adapter[tab.getPosition()]);
            }
            public void onTabUnselected(TabLayout.Tab tab) { }
            public void onTabReselected(TabLayout.Tab tab) { }
        });
    }

    private class ListAdapter extends ArrayAdapter<Contenido> {
        int datos;

        public ListAdapter(int datos) {
            super(InicioFragment.this.getActivity(), R.layout.item_vista, contenido[datos]);
            this.datos = datos;
        }

        public View getView(int Position, View convertView, ViewGroup parent) {
            View itemView = convertView;
            if (itemView == null)
                itemView = getLayoutInflater().inflate(R.layout.item_vista, parent, false);

            Contenido itemActual = contenido[datos].get(Position);
            if(!itemActual.isEnabled())
                itemView.setAlpha((float)0.30);

            ImageView imagen = (ImageView) itemView.findViewById(R.id.imagen);
            byte[] byteImagen = itemActual.getImagen();
            if(byteImagen != null) {

                byteArrayToImagen(imagen, byteImagen);
            }

            if(datos == SERVICIOS)
                imagen.setImageResource(getResources().getIdentifier("item_servicio" , "drawable", getActivity().getPackageName()));

            TextView tv;
            String texto;
            for(int i = 1; i <= 6; i++) {
                tv = (TextView) itemView.findViewById(getResources().getIdentifier("atributo"+i, "id", "com.example.autosv2"));
                texto = itemActual.getAtributo(i-1);
                if(texto != null)
                    tv.setText(texto);
                else
                    tv.setVisibility(View.GONE);
            }

            return itemView;
        }

        public void byteArrayToImagen(ImageView imagen, byte[] byteArray) {
            Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            imagen.setImageBitmap(Bitmap.createScaledBitmap(bmp, 96, 96,false));
        }
    }
}