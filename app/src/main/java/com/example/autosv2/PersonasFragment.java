package com.example.autosv2;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

public class PersonasFragment extends Fragment implements View.OnClickListener{
    BaseDeDatos bd;
    EditText tRfc, tNombre, tCiudad;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle("Nisson - Personas");
        return inflater.inflate(R.layout.fragment_personas, container, false);
    }


    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bd = BaseDeDatos.getBD();

        tRfc = view.findViewById(R.id.textoRFC);
        tNombre = view.findViewById(R.id.textoNombre);
        tCiudad = view.findViewById(R.id.textoCiudad);
        (view.findViewById(R.id.botonInsertar)).setOnClickListener(this);
        (view.findViewById(R.id.botonActualizar)).setOnClickListener(this);
        (view.findViewById(R.id.botonConsultar)).setOnClickListener(this);
        (view.findViewById(R.id.botonEliminar)).setOnClickListener(this);
    }

    public void onClick(View v){

        SQLiteDatabase query = null;
        String sql,rfc,nombre,ciudad;
        rfc = tRfc.getText().toString();

        switch(v.getId()){
            case R.id.botonInsertar:

                nombre = tNombre.getText().toString();
                ciudad = tCiudad.getText().toString();

                if(rfc.isEmpty() || nombre.isEmpty() || ciudad.isEmpty()){
                    alerta("Error", "Algunos campos estan vacíos");
                    return;
                }

                if(!bd.insertarPersona(rfc,nombre,ciudad))
                    alerta("BD Error", "Ya existe una persona con el RFC: " + rfc);
                else
                    vaciarCampos();

                break;

            case R.id.botonConsultar:
                if(rfc.isEmpty()){
                    alerta("Error", "No ha especificado ningún RFC");
                    return;
                }

                query = bd.getReadableDatabase();
                sql = "SELECT * FROM personas WHERE per_rfc=?";
                Cursor c = query.rawQuery(sql, new String[]{rfc});
                if(c.moveToFirst()) {
                    tRfc.setText(c.getString(0));
                    tNombre.setText(c.getString(1));
                    tCiudad.setText(c.getString(2));

                    if(c.getInt(3) == 1)
                        alerta("Alerta", "El RFC que se muestra está dado de baja");
                }
                else
                    Toast.makeText(getActivity(), "RFC no encontrado", Toast.LENGTH_LONG).show();

                query.close();
                break;

            case R.id.botonActualizar:
                rfc = tRfc.getText().toString();
                nombre = tNombre.getText().toString();
                ciudad = tCiudad.getText().toString();

                if(rfc.isEmpty() || nombre.isEmpty() || ciudad.isEmpty()){
                    alerta("Error", "Algunos campos están vacíos");
                    return;
                }

                query = bd.getWritableDatabase();
                ContentValues cv = new ContentValues();
                cv.put("per_rfc", rfc);
                cv.put("per_nombre", nombre);
                cv.put("per_ciudad", ciudad);

                if(query.update("personas", cv, "per_rfc=?", new String[]{rfc}) > 0) {
                    Toast.makeText(getActivity(), "Se ha actualizado el RFC " + rfc, Toast.LENGTH_LONG).show();
                    vaciarCampos();
                }
                else
                    alerta("Error", "El número de RFC ingresado no existe");

                query.close();
                break;

            case R.id.botonEliminar:
                if(rfc.isEmpty()) {
                    alerta("Error", "No ha especificado ningún RFC");
                    return;
                }

                query = bd.getWritableDatabase();
                ContentValues contenedor = new ContentValues();
                contenedor.put("per_estado", 1);

                if(query.update("personas", contenedor, "per_rfc=?", new String[]{rfc}) > 0) {
                    Toast.makeText(getActivity(), "Se ha dado de baja el RFC: " + rfc, Toast.LENGTH_LONG).show();
                    vaciarCampos();
                }
                else
                    alerta("Error", "El número de RFC ingresado no existe");

                query.close();
                break;
        }


    }


    public void vaciarCampos(){
        tRfc.setText("");
        tNombre.setText("");
        tCiudad.setText("");
        tRfc.requestFocus();
    }

    public void alerta(String titulo, String mensaje) {
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setTitle(titulo);
        alertDialog.setMessage(mensaje);
        alertDialog.show();
    }
}
