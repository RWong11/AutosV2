package com.example.autosv2;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class Consulta1 extends AppCompatActivity {

    ArrayList<Contenido> contenido;
    ArrayAdapter<Contenido> adapter;
    ListView listV;
    DecimalFormat df = new DecimalFormat("#.##");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consulta1);
        hacerLista();

    }

    protected  void onResume() {
        super.onResume();
        contenido.clear();
        hacerLista();
        adapter.notifyDataSetChanged();
    }

    private void hacerLista(){

        listV = (ListView) findViewById(R.id.lista1);
        BaseDeDatos bd = BaseDeDatos.getBD();
        SQLiteDatabase query = bd.getWritableDatabase();
        Cursor c = query.rawQuery("select per_ciudad, SUM(ser_precio), MIN(ser_precio), MAX(ser_precio), AVG(ser_precio) "+
                " FROM servicios s INNER JOIN personas p ON s.ser_rfc = p.per_rfc GROUP BY p.per_ciudad",null);

        while(c.moveToNext()){
            String atributos [] = new String[5];
            contenido = new ArrayList<>();
            atributos[0] = c.getString(0);
            atributos[1] = df.format(c.getDouble(1));
            atributos[2] = df.format(c.getDouble(2));
            atributos[3] = df.format(c.getDouble(3));
            atributos[4] = df.format(c.getDouble(4));
            contenido.add(new Contenido(null,atributos,true));
        }

        adapter = new ListAdapter();
        listV.setAdapter(adapter);
//        adapter.notifyDataSetChanged();
        query.close();

    }

    private class ListAdapter extends ArrayAdapter<Contenido>{

        public  ListAdapter(){

            super(Consulta1.this, R.layout.item_consulta1, contenido);
            notifyDataSetChanged();
        }


        public View getView(int Position, View convertView, ViewGroup parent) {
            View itemView = convertView;
            if (itemView == null) {
                itemView = getLayoutInflater().inflate(R.layout.item_consulta1, parent, false);

            }
            Contenido Current = contenido.get(Position);
            TextView ciudad, sum, max, min, prom;

            ciudad = itemView.findViewById(R.id.lblCiudad);
            sum = itemView.findViewById(R.id.lblTotal);
            max = itemView.findViewById(R.id.lblMax);
            min = itemView.findViewById(R.id.lblMin);
            prom = itemView.findViewById(R.id.lblProm);

            ciudad.setText(Current.getAtributo(0));
            sum.setText(Current.getAtributo(1));
            max.setText(Current.getAtributo(2));
            min.setText(Current.getAtributo(3));
            prom.setText(Current.getAtributo(4));

            return itemView;

        }

    }



}
