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

public class ServiciosFragment extends Fragment implements View.OnClickListener {
    BaseDeDatos bd;
    EditText tPlaca, tRfc, tKm, tPrecio, tFecha;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle("Nisson - Servicios");
        return inflater.inflate(R.layout.fragment_servicios, container, false);


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bd = BaseDeDatos.getBD();


        tPlaca = view.findViewById(R.id.txtPlaca2);
        tRfc = view.findViewById(R.id.txtRfc2);
        tKm = view.findViewById(R.id.txtKm);
        tPrecio = view.findViewById(R.id.txtPrecio);
        tFecha = view.findViewById(R.id.txtFecha);

        (view.findViewById(R.id.botonInsertar)).setOnClickListener(this);
        (view.findViewById(R.id.botonActualizar)).setOnClickListener(this);
        (view.findViewById(R.id.botonConsultar)).setOnClickListener(this);
        (view.findViewById(R.id.botonEliminar)).setOnClickListener(this);
    }


    public void alerta(String titulo, String mensaje) {
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setTitle(titulo);
        alertDialog.setMessage(mensaje);
        alertDialog.show();
    }

    @Override
    public void onClick(View v) {

        SQLiteDatabase query = null;
        String sql, placa,rfc,km,precio,fecha;
        placa = tPlaca.getText().toString();
        rfc = tRfc.getText().toString();

        switch(v.getId()) {
            case R.id.botonInsertar:
                km = tKm.getText().toString();
                precio = tPrecio.getText().toString();
                fecha = tFecha.getText().toString();

                if(placa.isEmpty() || rfc.isEmpty() || km.isEmpty() || precio.isEmpty() || fecha.isEmpty()) {
                    alerta("Error", "Algunos campos estan vacios");
                    return;
                }

                if(!bd.i)
                    alerta("BD Error", "Ya existe un auto con la placa: " + placa);
                else
                    vaciarCampos();

                break;

            case R.id.botonConsultar:
                if(placa.isEmpty()) {
                    alerta("Error", "No ha especificado ninguna placa.");
                    return;
                }

                query = bd.getReadableDatabase();
                sql = "SELECT * FROM autos WHERE aut_placa=?";
                Cursor c = query.rawQuery(sql, new String[]{placa});
                if(c.moveToFirst()) {
                    textoMarca.setText(c.getString(1));
                    textoModelo.setText(c.getString(2));
                    byteArrayToImagen(imagenAuto, c.getBlob(3));
                    textoAno.setText(c.getString(4));

                    if(c.getInt(5) == 1)
                        alerta("Alerta", "El auto que se muestra está dado de baja");
                }
                else
                    Toast.makeText(getActivity(), "Auto no encontrado", Toast.LENGTH_LONG).show();

                query.close();
                break;
            case R.id.botonActualizar:
                marca = textoMarca.getText().toString();
                modelo = textoModelo.getText().toString();
                ano = textoAno.getText().toString();

                if(placa.isEmpty() || marca.isEmpty() || modelo.isEmpty() || ano.isEmpty()) {
                    alerta("Error", "Algunos campos están vacíos");
                    return;
                }

                query = bd.getWritableDatabase();
                ContentValues cv = new ContentValues();
                cv.put("aut_marca", marca);
                cv.put("aut_modelo", modelo);
                cv.put("aut_imagen", imagenToByteArray(imagenAuto));
                cv.put("aut_ano", ano);

                if(query.update("autos", cv, "aut_placa=?", new String[]{placa}) > 0) {
                    Toast.makeText(getActivity(), "Se ha actualizado el auto " + placa, Toast.LENGTH_LONG).show();
                    vaciarCampos();
                }
                else
                    alerta("Error", "El número de placa ingresado no existe");

                query.close();
                break;
            case R.id.botonEliminar:
                if(placa.isEmpty()) {
                    alerta("Error", "No ha especificado ninguna placa.");
                    return;
                }

                query = bd.getWritableDatabase();
                ContentValues contenedor = new ContentValues();
                contenedor.put("aut_estado", 1);

                if(query.update("autos", contenedor, "aut_placa=?", new String[]{placa}) > 0) {
                    Toast.makeText(getActivity(), "Se ha dado de baja el auto: " + placa, Toast.LENGTH_LONG).show();
                    vaciarCampos();
                }
                else
                    alerta("Error", "El número de placa ingresado no existe");

                query.close();
                break;
        }

    }

    public void vaciarCampos() {
        tPlaca.setText("");
        tRfc.setText("");
        tKm.setText("");
        tPrecio.setText("");
        tFecha.setText("");
        tPlaca.requestFocus();
    }

}
