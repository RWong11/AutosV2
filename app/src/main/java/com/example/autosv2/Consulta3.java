package com.example.autosv2;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class Consulta3 extends AppCompatActivity {

    ArrayList<Contenido> contenido;
    ArrayAdapter<Contenido> adapter;
    ListView listV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consulta3);
        hacerLista();
    }

    protected  void onResume() {
        super.onResume();
        hacerLista();
    }


    private void hacerLista(){

        listV = (ListView) findViewById(R.id.lista3);
        BaseDeDatos bd = BaseDeDatos.getBD();
        SQLiteDatabase query = bd.getWritableDatabase();
        Cursor c = query.rawQuery("select per_rfc, per_nombre, per_ciudad from personas P left join servicios " +
                "S on (P.per_rfc = S.ser_rfc) where S.ser_rfc is null" ,null);

        contenido = new ArrayList<>();
        while(c.moveToNext()){
            String atributos [] = new String[3];

            atributos[0] = c.getString(0);
            atributos[1] = c.getString(1);
            atributos[2] = c.getString(2);
            contenido.add(new Contenido(null,atributos,true));
        }

        adapter = new Consulta3.ListAdapter();
        listV.setAdapter(adapter);
        query.close();

    }

    private class ListAdapter extends ArrayAdapter<Contenido> {

        public  ListAdapter(){

            super(Consulta3.this, R.layout.item_consultas3, contenido);
            notifyDataSetChanged();
        }


        public View getView(int Position, View convertView, ViewGroup parent) {
            View itemView = convertView;
            if (itemView == null) {
                itemView = getLayoutInflater().inflate(R.layout.item_consultas3, parent, false);

            }
            Contenido Current = contenido.get(Position);
            TextView rfc, nombre, ciudad;

            rfc = itemView.findViewById(R.id.lblRfc);
            nombre = itemView.findViewById(R.id.lblNombre);
            ciudad = itemView.findViewById(R.id.lblCiudade);

            rfc.setText(Current.getAtributo(0));
            nombre.setText(Current.getAtributo(1));
            ciudad.setText(Current.getAtributo(2));


            return itemView;

        }

    }
}
