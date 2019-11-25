package com.example.calculadora;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    public static String operacion = "";
    public static String resultado = "";

    public static int contSuma = 0;
    public static int contResta = 0;
    public static int contMultiplicacion = 0;
    public static int contDivision = 0;
    public static int contPiz = 0;
    public static int contPde = 0;
    public static int contPunto = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void Entrar(View view){
        Intent intent =new Intent(this,Calculadora.class);
        startActivity(intent);
    }

    public void OnClickDatos(View view){
        Intent intent =new Intent(this,Datos.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        operacion = "";
        resultado = "";

        contSuma = 0;
        contResta = 0;
        contMultiplicacion = 0;
        contDivision = 0;
        contPiz = 0;
        contPde = 0;
        contPunto = 0;
    }
}
