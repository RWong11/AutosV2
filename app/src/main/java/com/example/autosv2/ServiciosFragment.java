package com.example.autosv2;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class ServiciosFragment extends Fragment implements View.OnClickListener {
    BaseDeDatos bd;
    EditText tPlaca, tRfc, tKm, tPrecio, tFecha;
    ImageView imagenAuto;

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

        imagenAuto = view.findViewById(R.id.imagen);
        tPlaca = view.findViewById(R.id.txtPlaca);
        tRfc = view.findViewById(R.id.txtRfc);
        tKm = view.findViewById(R.id.txtKm);
        tPrecio = view.findViewById(R.id.txtPrecio);
        tFecha = view.findViewById(R.id.txtFecha);

        (view.findViewById(R.id.botonInsertar)).setOnClickListener(this);
        (view.findViewById(R.id.botonConsultar)).setOnClickListener(this);
        tFecha.setOnClickListener(this);
    }


    public void alerta(String titulo, String mensaje) {
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setTitle(titulo);
        alertDialog.setMessage(mensaje);
        alertDialog.show();
    }

    @Override
    public void onClick(View v) {
        if(v == tFecha)
            showDatePickerDialog();

        else if(v.getId() == R.id.botonConsultar)
            abrirAlertConsulta();
        else {
            String sql, placa, rfc, km, precio, fecha;
            placa = tPlaca.getText().toString();
            rfc = tRfc.getText().toString();
            km = tKm.getText().toString();
            precio = tPrecio.getText().toString();
            fecha = tFecha.getText().toString();

            if (placa.isEmpty() || rfc.isEmpty() || km.isEmpty() || precio.isEmpty() || fecha.isEmpty()) {
                alerta("Error", "Algunos campos estan vacios");
                return;
            }

            SQLiteDatabase query = bd.getWritableDatabase();
            Cursor c;

            sql = "SELECT aut_estado FROM autos WHERE aut_placa=?";
            c = query.rawQuery(sql, new String[] {placa});
            if(!c.moveToFirst()) {
                alerta("Placa", "La placa ingresada no existe");
                return;
            }

            if(c.getInt(0) == 1) {
                alerta("Placa", "La placa ingresada está dada de baja");
                return;
            }

            sql = "SELECT per_estado FROM personas WHERE per_rfc=?";
            c = query.rawQuery(sql, new String[]{rfc});
            if(!c.moveToFirst()) {
                alerta("RFC", "El RFC ingresado no existe");
                return;
            }

            if(c.getInt(0) == 1) {
                alerta("RFC", "El RFC ingresado está dado de baja");
                return;
            }

            query.close();
            bd.insertarServicio(placa, rfc, Integer.parseInt(km), Double.parseDouble(precio), fechaToField(fecha));
            vaciarCampos();
        }
    }

    public String fechaToField(String fecha) {
        String[] valores = fecha.split("/");
        return valores[2]+valores[1]+valores[0];
    }

    public String fieldToFecha(String fecha) {
        return fecha.substring(6)+"/"+fecha.substring(4, 6)+"/"+fecha.substring(0, 4);
    }

    private void showDatePickerDialog() {
        DatePickerFragment newFragment = DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                // +1 because january is zero
                String szDay = (day < 10 ? "0" : "") +day;
                String szMonth = ((month < 9) ? "0" : "") +(month+1);
                final String selectedDate =  szDay +"/" +szMonth  +"/" +year;
                tFecha.setText(selectedDate);
            }
        });
        newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
    }

    public void abrirAlertConsulta() {
        AlertDialog.Builder alertConsulta;
        final EditText editText = new EditText(getActivity());
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        alertConsulta = new AlertDialog.Builder(getActivity());
        alertConsulta.setMessage("Número de orden: ");
        alertConsulta.setTitle("Consulta");
        alertConsulta.setView(editText);
        alertConsulta.setPositiveButton("Consultar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String orden = editText.getText().toString();
                if(!orden.isEmpty())
                    consultarServicio(Integer.parseInt(orden));
                else
                    alerta("Error", "No escribió ninguna orden");
            }
        });

        alertConsulta.setNegativeButton("Cancelar", null);
        alertConsulta.show();
    }

    public void consultarServicio(int orden) {
        SQLiteDatabase query = bd.getReadableDatabase();
        String sql = "SELECT * FROM servicios WHERE ser_orden=" +orden;
        Cursor c = query.rawQuery(sql, null);
        if(c.moveToFirst()) {
            String placa = c.getString(1);
            tPlaca.setText(placa);
            tRfc.setText(c.getString(2));
            tKm.setText("" +c.getInt(3));
            tPrecio.setText("" +c.getDouble(4));
            tFecha.setText(fieldToFecha(c.getString(5)));

            sql = "SELECT aut_imagen FROM autos WHERE aut_placa=?";
            c = query.rawQuery(sql, new String[] {placa});
            c.moveToFirst();
            byteArrayToImagen(imagenAuto, c.getBlob(0));
        }
        else
            Toast.makeText(getActivity(), "Número de orden no encontrado", Toast.LENGTH_LONG).show();

        query.close();
    }

    public void vaciarCampos() {
        tPlaca.setText("");
        tRfc.setText("");
        tKm.setText("");
        tPrecio.setText("");
        tFecha.setText("");
        tPlaca.requestFocus();
    }

    public void byteArrayToImagen(ImageView imagen, byte[] byteArray) {
        Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        imagen.setImageBitmap(Bitmap.createScaledBitmap(bmp, imagen.getWidth(), imagen.getHeight(),false));
    }
}