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

import java.util.ArrayList;

public class Consulta2 extends AppCompatActivity {

    ArrayList<Contenido> contenido;
    ArrayAdapter<Contenido> adapter;
    ListView listV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consulta2);

        hacerLista();
    }

    private void hacerLista(){

        listV = (ListView) findViewById(R.id.lista2);
        BaseDeDatos bd = BaseDeDatos.getBD();
        SQLiteDatabase query = bd.getWritableDatabase();
        Cursor c = query.rawQuery("select substr(ser_fecha,1,4), per_ciudad, aut_marca, COUNT(*), SUM(ser_precio)" +
                "FROM servicios S INNER JOIN personas P on (ser_rfc = per_rfc) INNER JOIN autos A on" +
                "(ser_placa = aut_placa) GROUP BY substr(ser_fecha,1,4), per_ciudad, aut_marca",null);

        contenido = new ArrayList<>();
        while(c.moveToNext()){
            String atributos [] = new String[5];

            atributos[0] = c.getString(0);
            atributos[1] = c.getString(1);
            atributos[2] = c.getString(2);
            atributos[3] = c.getInt(3) + "";
            atributos[4] = c.getDouble(4) + "";
            contenido.add(new Contenido(null,atributos,true));
        }

        adapter = new Consulta2.ListAdapter();
        listV.setAdapter(adapter);

        query.close();

    }

    private class ListAdapter extends ArrayAdapter<Contenido> {

        public  ListAdapter(){
            super(Consulta2.this, R.layout.item_consulta2, contenido);
            notifyDataSetChanged();
        }


        public View getView(int Position, View convertView, ViewGroup parent) {
            View itemView = convertView;
            if (itemView == null) {
                itemView = getLayoutInflater().inflate(R.layout.item_consulta2, parent, false);

            }
            Contenido Current = contenido.get(Position);
            TextView fecha, ciudad, marca, ns, precio;

            fecha = itemView.findViewById(R.id.lblAnno);
            ciudad = itemView.findViewById(R.id.lblCity);
            marca = itemView.findViewById(R.id.lblMarca);
            ns = itemView.findViewById(R.id.lblNs);
            precio = itemView.findViewById(R.id.lblImporte);

            fecha.setText(Current.getAtributo(0));
            ciudad.setText(Current.getAtributo(1));
            marca.setText(Current.getAtributo(2));
            ns.setText(Current.getAtributo(3));
            precio.setText(Current.getAtributo(4));

            return itemView;

        }

    }

}
