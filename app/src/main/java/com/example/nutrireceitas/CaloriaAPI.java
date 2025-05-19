package com.example.nutrireceitas;

import android.content.Context;
import android.net.Uri;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CaloriaAPI {

    public interface Callback {
        void onSuccess(String resultado);

        void onError(String erro);
    }

    public static void buscarCalorias(Context context, String alimentoPT, int gramas, Callback callback) {
        traduzirParaIngles(context, alimentoPT, new Callback() {
            @Override
            public void onSuccess(String alimentoEN) {
                buscarNaFDA(context, alimentoEN, gramas, callback);
            }

            @Override
            public void onError(String erro) {
                callback.onError("Erro na tradução: " + erro);
            }
        });
    }

    private static void traduzirParaIngles(Context context, String texto, Callback callback) {
        // Monta a URL codificando o texto
        String url = "https://api.mymemory.translated.net/get?q=" + Uri.encode(texto) + "&langpair=pt|en";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONObject responseData = response.getJSONObject("responseData");
                        String traducao = responseData.getString("translatedText");
                        callback.onSuccess(traducao);
                    } catch (JSONException e) {
                        callback.onError("Erro ao processar resposta da tradução: " + e.getMessage());
                    }
                },
                error -> callback.onError("Erro ao traduzir: " + error.toString())
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }

    private static void buscarNaFDA(Context context, String alimento, int gramas, Callback callback) {
        String apiKey = "zfUoT2e4YdY3Ib0YfDMnS4qlqmnc3iBTedy0xO39";
        String url = "https://api.nal.usda.gov/fdc/v1/foods/search?query=" + Uri.encode(alimento) + "&api_key=" + apiKey;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray foods = response.getJSONArray("foods");
                        if (foods.length() > 0) {
                            JSONObject food = foods.getJSONObject(0);
                            JSONArray nutrients = food.getJSONArray("foodNutrients");

                            StringBuilder resultado = new StringBuilder();
                            resultado.append("Alimento: ").append(food.optString("description", "Descrição não disponível")).append("\n");

                            for (int i = 0; i < nutrients.length(); i++) {
                                JSONObject nutriente = nutrients.getJSONObject(i);
                                String nome = nutriente.optString("nutrientName", "Nutriente desconhecido");
                                double valorPor100g = nutriente.optDouble("value", 0);
                                String unidade = nutriente.optString("unitName", "");

                                double valor = valorPor100g * gramas / 100.0;
                                resultado.append(nome).append(": ").append(String.format("%.2f", valor)).append(" ").append(unidade).append("\n");
                            }

                            callback.onSuccess(resultado.toString());
                        } else {
                            callback.onError("Alimento não encontrado.");
                        }
                    } catch (JSONException e) {
                        callback.onError("Erro ao processar resposta FDA: " + e.getMessage());
                    }
                },
                error -> callback.onError("Erro ao buscar na FDA: " + error.toString())
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }
}