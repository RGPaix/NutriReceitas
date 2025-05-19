package com.example.nutrireceitas;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText editAlimento;
    private EditText editGramas;
    private Button btnBuscar;
    private TextView textResultado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editAlimento = findViewById(R.id.editAlimento);
        editGramas = findViewById(R.id.editGramas);
        btnBuscar = findViewById(R.id.btnBuscar);
        textResultado = findViewById(R.id.textResultado);

        btnBuscar.setOnClickListener(v -> {
            String alimento = editAlimento.getText().toString().trim();
            int gramas = Integer.parseInt(editGramas.getText().toString());

            if (!alimento.isEmpty() && gramas > 0) {
                CaloriaAPI.buscarCalorias(this, alimento, gramas, new CaloriaAPI.Callback() {
                    @Override
                    public void onSuccess(String resultado) {
                        textResultado.setText(resultado);
                    }

                    @Override
                    public void onError(String erro) {
                        textResultado.setText("Erro: " + erro);
                    }
                });
            } else {
                textResultado.setText("Digite um alimento e uma quantidade válida.");
            }
        });
    }
}