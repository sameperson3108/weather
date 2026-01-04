package com.example.weather;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class HelloController {

    @FXML
    private TextField latitude;

    @FXML
    private TextField longitude;

    @FXML
    private Button getData;

    @FXML
    private Text temp_info;

    private static String getUrlContent(String urlAddress) {
        StringBuilder content = new StringBuilder();

        try {
            URL url = new URL(urlAddress);
            URLConnection urlConn = url.openConnection();

            BufferedReader bufferedReader =
                    new BufferedReader(new InputStreamReader(urlConn.getInputStream()));

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                content.append(line).append("\n");
            }
            bufferedReader.close();

        } catch (Exception e) {
            return "Ошибка запроса: " + e.getMessage();
        }

        return content.toString();
    }

    @FXML
    void initialize() {
        getData.setOnAction(event -> loadWeather());
    }

    private void loadWeather() {
        String latText = latitude.getText().trim();
        String lonText = longitude.getText().trim();

        if (latText.isEmpty() || lonText.isEmpty()) {
            System.out.println("Введите координаты");
            return;
        }

        double lat;
        double lon;

        try {
            lat = Double.parseDouble(latText);
            lon = Double.parseDouble(lonText);
        } catch (NumberFormatException e) {
            System.out.println("Некорректные координаты");
            return;
        }

        new Thread(() -> {
            String url = "https://api.open-meteo.com/v1/forecast"
                    + "?latitude=" + lat
                    + "&longitude=" + lon
                    + "&hourly=temperature_2m";

            String output = getUrlContent(url);

            try {
                JSONObject obj = new JSONObject(output);
                JSONObject hourly = obj.getJSONObject("hourly");
                double temp = hourly.getJSONArray("temperature_2m").getDouble(0);

                Platform.runLater(() -> {
                    temp_info.setText("Температура: " + temp + " °C");
                });

            } catch (Exception e) {
                Platform.runLater(() -> {
                    temp_info.setText("Ошибка данных");
                });
            }
        }).start();
    }
}