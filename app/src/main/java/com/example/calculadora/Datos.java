package com.example.calculadora;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class Datos extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datos);
    }

    @Override
    public void onClick(View v) {
        Toast.makeText(this, "Envíe un email para solicitarla a 'Marlonjomar07@gmail.com'", Toast.LENGTH_SHORT).show();
    }
}
