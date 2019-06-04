package com.example.autosv2;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    BaseDeDatos bd;
    private DrawerLayout drawer;
    private Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(!BaseDeDatos.iniciarBD(this)) {
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle("Base de datos ");
            alertDialog.setMessage("la conexión a la base de datos no se realizó");
            alertDialog.show();
            return;
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.abrir_nav, R.string.cerrar_nav);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        if(savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new InicioFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_inicio);
        }
    }

    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START))
            drawer.closeDrawer(GravityCompat.START);
        else
            super.onBackPressed();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        switch(item.getItemId()) {
            case R.id.nav_inicio:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new InicioFragment()).commit();
                break;
            case R.id.nav_autos:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AutosFragment()).commit();
                break;
            case R.id.nav_personas:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new PersonasFragment()).commit();
                break;
            case R.id.nav_servicios:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ServiciosFragment()).commit();
                break;
            case R.id.nav_consulta_ingresos:
                Toast.makeText(getApplicationContext(),"Consulta ingresos", Toast.LENGTH_SHORT).show();
                intent = new Intent(this,Consulta1.class);
                startActivityForResult(intent,0);
                break;
            case R.id.nav_consulta_servicios:
                Toast.makeText(getApplicationContext(),"Consulta Servicios", Toast.LENGTH_SHORT).show();
                intent = new Intent(this,Consulta2.class);
                startActivityForResult(intent,0);
                break;
            case R.id.nav_consulta_personas:
                Toast.makeText(getApplicationContext(),"Consulta Personas", Toast.LENGTH_SHORT).show();
                intent = new Intent(this,Consulta3.class);
                startActivityForResult(intent,0);
                break;
        }



        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
