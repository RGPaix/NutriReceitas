package com.example.nutrireceitas;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText editAlimento;
    private Button btnBuscar;
    private TextView textResultado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editAlimento = findViewById(R.id.editAlimento);
        btnBuscar = findViewById(R.id.btnBuscar);
        textResultado = findViewById(R.id.textResultado);


        btnBuscar.setOnClickListener(v -> {
            String alimento = editAlimento.getText().toString().trim();

            if (!alimento.isEmpty()) {
                CaloriaAPI.buscarCalorias(alimento, new CaloriaAPI.CaloriaCallback() {
                    @Override
                    public void onResult(String resultado) {
                        textResultado.setText(resultado);
                    }

                    @Override
                    public void onError(String erro) {
                        textResultado.setText("Erro: " + erro);
                    }
                });
            } else {
                textResultado.setText("Digite um alimento.");
            }
        });
    }
}