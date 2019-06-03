package com.example.autosv2;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

public class BaseDeDatos extends SQLiteOpenHelper {
    static final String NOMBRE = "NISSON";
    static final int version = 1;

    final String TABLA_AUTOS = "CREATE TABLE autos(aut_placa TEXT PRIMARY KEY, " +
                                "aut_marca TEXT, aut_modelo TEXT, aut_imagen BLOB, " +
            "                    aut_ano INTEGER, aut_estado INTEGER)";
    final String TABLA_PERSONAS = "CREATE TABLE personas(per_rfc TEXT PRIMARY KEY, per_nombre TEXT" +
                                  ", per_ciudad TEXT, per_estado INTEGER)";
    final String TABLA_SERVICIOS = "CREATE TABLE servicios(ser_orden INTEGER PRIMARY KEY AUTOINCREMENT" +
                                   ", ser_placa TEXT, ser_rfc TEXT, ser_km INTEGER, ser_precio REAL, ser_fecha TEXT)";

    private SQLiteDatabase db;
    private Context contexto;
    private static BaseDeDatos baseDeDatos;

    public static boolean iniciarBD(Context contexto) {
        baseDeDatos = new BaseDeDatos(contexto, null);
        return (baseDeDatos != null);
    }

    public static BaseDeDatos getBD() {
        return baseDeDatos;
    }

    private BaseDeDatos(Context contexto, SQLiteDatabase.CursorFactory factory) {
        super(contexto, NOMBRE, factory, version);
        this.contexto = contexto;
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLA_AUTOS);
        db.execSQL(TABLA_PERSONAS);
        db.execSQL(TABLA_SERVICIOS);
    }

    public void onUpgrade(SQLiteDatabase db, int versionAnterior, int versionNueva) {
        db.execSQL("DROP TABLE IF EXISTS autos");
        db.execSQL("DROP TABLE IF EXISTS personas");
        db.execSQL("DROP TABLE IF EXISTS servicios");
        onCreate(db);
    }

    public boolean insertarAuto(String placa, String marca, String modelo, byte[] imagen, int ano) {
        ContentValues cv = new ContentValues();
        cv.put("aut_placa", placa);
        cv.put("aut_marca", marca);
        cv.put("aut_modelo", modelo);
        cv.put("aut_imagen", imagen);
        cv.put("aut_ano", ano);

        db = getWritableDatabase();
        try {
            db.insertOrThrow("autos", null, cv);
        } catch (SQLiteException e) {
            Log.d("DB", e.toString());
            return false;
        }
        finally {
            db.close();
        }

        Toast.makeText(contexto, "Auto (" +placa +") agregado correctamente!", Toast.LENGTH_LONG).show();
        return true;
    }

    public boolean insertarPersona(String rfc, String nombre, String ciudad) {
        ContentValues cv = new ContentValues();
        cv.put("per_rfc", rfc);
        cv.put("per_nombre", nombre);
        cv.put("per_ciudad", ciudad);

        db = getWritableDatabase();
        try {
            db.insertOrThrow("personas", null, cv);
        } catch (SQLiteException e) {
            Log.d("DB", e.toString());
            return false;
        }
        finally {
            db.close();
        }
        Toast.makeText(contexto, "Persona (" +nombre +") agregada correctamente!", Toast.LENGTH_LONG).show();
        return true;
    }

    public long insertarServicio(String placa, String rfc, int km, double precio, String fecha){
        ContentValues cv = new ContentValues();
        cv.put("ser_placa",placa);
        cv.put("ser_rfc",rfc);
        cv.put("ser_km",km);
        cv.put("ser_precio",precio);
        cv.put("ser_fecha",fecha);

        db = getWritableDatabase();
        long id = db.insert("servicios", null, cv);
        db.close();

        Toast.makeText(contexto,"Servicio (#" +id +") registrado correctamente",Toast.LENGTH_LONG).show();
        return id;
    }
}
