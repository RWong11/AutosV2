package com.example.autosv2;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;

import static android.app.Activity.RESULT_OK;

public class AutosFragment extends Fragment implements View.OnClickListener {
    BaseDeDatos bd;
    EditText textoPlaca, textoMarca, textoModelo, textoAno;

    private static final int ESCOGER_IMAGEN = 100;
    ImageView imagenAuto;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle("Nisson - Autos");
        return inflater.inflate(R.layout.fragment_autos, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bd = BaseDeDatos.getBD();

        imagenAuto = view.findViewById(R.id.imagenAuto);
        textoPlaca = view.findViewById(R.id.textoPlaca);
        textoMarca = view.findViewById(R.id.textoMarca);
        textoModelo = view.findViewById(R.id.textoModelo);
        textoAno = view.findViewById(R.id.textoAno);

        imagenAuto.setOnClickListener(this);
        (view.findViewById(R.id.botonInsertar)).setOnClickListener(this);
        (view.findViewById(R.id.botonActualizar)).setOnClickListener(this);
        (view.findViewById(R.id.botonConsultar)).setOnClickListener(this);
        (view.findViewById(R.id.botonEliminar)).setOnClickListener(this);
    }

    private void abrirGaleria(){
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        gallery.setType("image/*");
        startActivityForResult(gallery, ESCOGER_IMAGEN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode == RESULT_OK && requestCode == ESCOGER_IMAGEN)
            imagenAuto.setImageURI(data.getData());
    }

    @Override
    public void onClick(View v) {
        if(v == imagenAuto) {
            abrirGaleria();
            return;
        }

        SQLiteDatabase query = null;
        String sql, placa, marca, modelo, ano;
        placa = textoPlaca.getText().toString();

        switch(v.getId()) {
            case R.id.botonInsertar:
                marca = textoMarca.getText().toString();
                modelo = textoModelo.getText().toString();
                ano = textoAno.getText().toString();

                if(placa.isEmpty() || marca.isEmpty() || modelo.isEmpty() || ano.isEmpty()) {
                    alerta("Error", "Algunos campos estan vacios");
                    return;
                }

                if(!bd.insertarAuto(placa, marca, modelo, imagenToByteArray(imagenAuto), Integer.parseInt(ano)))
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
        textoPlaca.setText("");
        textoMarca.setText("");
        textoModelo.setText("");
        textoAno.setText("");
        imagenAuto.setImageResource(getResources().getIdentifier("ic_menu_gallery" , "android:drawable", getActivity().getPackageName()));
        textoPlaca.requestFocus();
    }

    public byte[] imagenToByteArray(ImageView imagen) {
        Drawable d = imagen.getDrawable();
        Bitmap bitmap = ((BitmapDrawable)d).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }

    public void byteArrayToImagen(ImageView imagen, byte[] byteArray) {
        Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        imagen.setImageBitmap(Bitmap.createScaledBitmap(bmp, imagen.getWidth(), imagen.getHeight(),false));
    }

    public void alerta(String titulo, String mensaje) {
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setTitle(titulo);
        alertDialog.setMessage(mensaje);
        alertDialog.show();
    }
}