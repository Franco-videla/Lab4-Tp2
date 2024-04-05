package org.example;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.*;

public class Main {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/tp2_db";
        String user = "root";
        String password = "root";
        for (int codigo = 0; codigo < 300; codigo++) {
            try {
                String apiUrl = "https://restcountries.com/v2/callingcode/" + codigo;
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(apiUrl))
                        .build();

                HttpClient client = HttpClient.newHttpClient();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {

                    String responseBody = response.body();
                    JSONArray jsonArray = new JSONArray(responseBody);
                    JSONObject json = jsonArray.getJSONObject(0);
                    String nombrePais = json.getString("name");
                    String capital = json.optString("capital", null);
                    String region = json.optString("region", null);
                    int poblacion = json.optInt("population", 0);
                    double latitud = json.getJSONArray("latlng").optDouble(0, 0);
                    double longitud = json.getJSONArray("latlng").optDouble(1, 0);
                    JSONArray callingCodesArray = json.getJSONArray("callingCodes");
                    String codigoPais = callingCodesArray.getString(0);
                    try (Connection connection = DriverManager.getConnection(url, user, password)) {

                        String query = "SELECT * FROM pais WHERE codigoPais = ?";
                        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                            preparedStatement.setString(1, codigoPais);
                            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                                if (resultSet.next()) {

                                    query = "UPDATE pais SET nombrePais = ?, capitalPais = ?, region = ?, poblacion = ?, latitud = ?, longitud = ? WHERE codigoPais = ?";
                                    try (PreparedStatement updateStatement = connection.prepareStatement(query)) {
                                        updateStatement.setString(1, nombrePais);
                                        updateStatement.setString(2, capital);
                                        updateStatement.setString(3, region);
                                        updateStatement.setInt(4, poblacion);
                                        updateStatement.setDouble(5, latitud);
                                        updateStatement.setDouble(6, longitud);
                                        updateStatement.setString(7, codigoPais);
                                        updateStatement.executeUpdate();
                                    }
                                } else {

                                    query = "INSERT INTO pais(nombrePais, capitalPais, region, poblacion, latitud, longitud, codigoPais) VALUES (?, ?, ?, ?, ?, ?, ?)";
                                    try (PreparedStatement insertStatement = connection.prepareStatement(query)) {
                                        insertStatement.setString(1, nombrePais);
                                        insertStatement.setString(2, capital);
                                        insertStatement.setString(3, region);
                                        insertStatement.setInt(4, poblacion);
                                        insertStatement.setDouble(5, latitud);
                                        insertStatement.setDouble(6, longitud);
                                        insertStatement.setString(7, codigoPais);
                                        insertStatement.executeUpdate();
                                    }
                                }
                            }
                        }
                    }
                    System.out.println("Datos del país " + nombrePais + " insertados o actualizados correctamente.");
                } else {
                    System.out.println("No se encontraron datos para el código de país " + codigo );
                }
            } catch (IOException | InterruptedException | SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
