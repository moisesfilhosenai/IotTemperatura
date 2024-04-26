package com.example.iottemperatura;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private final MQTT mqtt = new MQTT();
    private TextView textViewTemperatura;
    private TextView textViewUmidade;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        textViewTemperatura = findViewById(R.id.textViewTemperaturaValor);
        textViewUmidade = findViewById(R.id.textViewUmidadeValor);

        receberDados();
    }

    private void receberDados() {
        mqtt.conectar(this, textViewTemperatura, textViewUmidade);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mqtt.desconectar(this);
    }
}