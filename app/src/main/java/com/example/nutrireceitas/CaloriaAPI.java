package com.example.nutrireceitas;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import android.os.Handler;
import android.os.Looper;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

public class CaloriaAPI {

    public interface CaloriaCallback {
        void onResult(String resultado);
        void onError(String erro);
    }

    public static void buscarCalorias(String alimento, CaloriaCallback callback) {
        OkHttpClient client = new OkHttpClient();

        String url = "https://caloriasporalimentoapi.herokuapp.com/api/calorias/?descricao=" + alimento;

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            final Handler mainHandler = new Handler(Looper.getMainLooper());

            @Override
            public void onFailure(Call call, IOException e) {
                mainHandler.post(() -> callback.onError("Erro de rede: " + e.getMessage()));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    mainHandler.post(() -> callback.onError("Erro: " + response.code()));
                    return;
                }

                String json = response.body().string();

                try {
                    JSONArray array = new JSONArray(json);
                    if (array.length() > 0) {
                        JSONObject item = array.getJSONObject(0);
                        String descricao = item.getString("descricao");
                        String quantidade = item.getString("quantidade");
                        String calorias = item.getString("calorias");

                        String resultado = descricao + "\n" +
                                "Quantidade: " + quantidade + "\n" +
                                "Calorias: " + calorias + " kcal";

                        mainHandler.post(() -> callback.onResult(resultado));
                    } else {
                        mainHandler.post(() -> callback.onError("Alimento não encontrado."));
                    }
                } catch (Exception e) {
                    mainHandler.post(() -> callback.onError("Erro ao processar JSON: " + e.getMessage()));
                }
            }
        });
    }
}