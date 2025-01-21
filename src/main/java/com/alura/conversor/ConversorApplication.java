package com.alura.conversor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

@SpringBootApplication
public class ConversorApplication implements CommandLineRunner {
    public static void main(String[] args) {
        SpringApplication.run(ConversorApplication.class, args);
    }

    private final Scanner scanner;
    private final Map<String, String> currencyNames = new HashMap<>(Map.of(
        "USD", "Dólar",
        "ARS", "Peso argentino",
        "BRL", "Real brasileño",
        "COP", "Peso colombiano"
    ));
    @Value("${exchangerate.api.url}")
    private String exchangeRateApiUrl = "<URL>";

    public ConversorApplication() {
        scanner = new Scanner(System.in);
    }

    @Override
    public void run(String... args) throws Exception {
        boolean repeat = true;

        while (repeat) {
            System.out.println("Sea bienvenido/a al Conversor de Monedas =]");
            System.out.println();
            System.out.println("1) Dólar =>> Peso argentino");
            System.out.println("2) Peso argentino =>> Dólar");
            System.out.println("3) Dólar =>> Real brasileño");
            System.out.println("4) Real brasileño =>> Dólar");
            System.out.println("5) Dólar =>> Peso colombiano");
            System.out.println("6) Peso colombiano =>> Dólar");
            System.out.println("7) Salir");

            int option = readInt("Elija una opción válida: ");

            switch (option) {
                case 1:
                    convertCurrency("USD", "ARS");
                    break;
                case 2:
                    convertCurrency("ARS", "USD");
                    break;
                case 3:
                    convertCurrency("USD", "BRL");
                    break;
                case 4:
                    convertCurrency("BRL", "USD");
                    break;
                case 5:
                    convertCurrency("USD", "COP");
                    break;
                case 6:
                    convertCurrency("COP", "USD");
                    break;
                case 7:
                    System.out.println("Gracias por utilizar el Conversor de Moneda =]");
                    repeat = false;
                    break;
                default:
                    System.out.println("Opcion inválida");
                    break;
            }
        }
    }

    public int readInt(String message) {
        int value = 0;
        boolean valid = false;

        while (!valid) {
            System.out.print(message);

            if (scanner.hasNextInt()) {
                value = scanner.nextInt();
                valid = true;
            } else {
                System.out.println("El valor introducido no es válido");
                scanner.next();
            }
        }

        return value;
    }

    public double readDouble(String message) {
        double value = 0;
        boolean valid = false;

        while (!valid) {
            System.out.print(message);

            if (scanner.hasNextDouble()) {
                value = scanner.nextDouble();
                valid = true;
            } else {
                System.out.println("El valor introducido no es válido");
                scanner.next();
            }
        }

        return value;
    }

    public void convertCurrency(String fromCurrencyCode, String toCurrencyCode) throws IOException, InterruptedException {
        String fromCurrencyName = currencyNames.get(fromCurrencyCode);
        String toCurrencyName = currencyNames.get(toCurrencyCode);
        double value = readDouble("Ingrese el valor a convertir: ");

        HttpClient httpClient = HttpClient.newHttpClient();
        ObjectMapper objectMapper = new ObjectMapper();
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(java.net.URI.create(exchangeRateApiUrl + "/pair/" + fromCurrencyCode + "/" + toCurrencyCode + "/" + value))
                .build();
        HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        JsonNode jsonNode = objectMapper.readTree(httpResponse.body());

        double convertedValue = jsonNode.get("conversion_result").doubleValue();

        System.out.printf("%.2f %s = %.2f %s%n", value, fromCurrencyName, convertedValue, toCurrencyName);
    }
}
