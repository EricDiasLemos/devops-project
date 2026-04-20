package com.Estudo;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpServer;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.file.*;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.MediaType;

public class Servidor {
    public static void main(String[] args) throws Exception{
        HttpServer server = HttpServer.create(new InetSocketAddress(8000) , 0);

        server.createContext("/", exchange -> {
            byte[] resposta = Files.readAllBytes(Path.of("public/index.html"));

            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");

            exchange.sendResponseHeaders(200, resposta.length);

            exchange.getResponseBody().write(resposta);

            exchange.getResponseBody().close();
        });


        server.createContext("/pessoas", exchange -> {
            if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                exchange.sendResponseHeaders(405,-1);
                return;
            }

            String corpo = new String(exchange.getRequestBody().readAllBytes());

            JsonObject json = JsonParser.parseString(corpo).getAsJsonObject();
            String nome = json.get("nome").getAsString();
            System.out.println("Nome recebido: " + nome);

            OkHttpClient client = new OkHttpClient();

            String json_supabase = "{\"name\": \"" + nome + "\"}";

            RequestBody body = RequestBody.create(
                json_supabase,
                MediaType.get("application/json")
            );

            Request request = new Request.Builder()
                .url("https://mglwglpcoenwwlpzwhfk.supabase.co/rest/v1/name")
                .post(body)
                .addHeader("apikey", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im1nbHdnbHBjb2Vud3dscHp3aGZrIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzY1MzM4MDgsImV4cCI6MjA5MjEwOTgwOH0.APd2ie53o2eXr-coV3SaSDcVdmP_0YUfRKRdUztg3eo")
                .addHeader("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im1nbHdnbHBjb2Vud3dscHp3aGZrIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzY1MzM4MDgsImV4cCI6MjA5MjEwOTgwOH0.APd2ie53o2eXr-coV3SaSDcVdmP_0YUfRKRdUztg3eo")
                .addHeader("Content-type", "application/json")
                .build();

            client.newCall(request).execute();

            okhttp3.Response response = client.newCall(request).execute();
            System.out.println("Status Supabase: " + response.code());
            System.out.println("Resposta Supabase: " + response.body().string());
            
            String resposta = "{\"status\": \"salvo com sucesso\"}";
            byte[] bytes = resposta.getBytes();
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, bytes.length);
            exchange.getResponseBody().write(bytes);
            exchange.getResponseBody().close();

            });

        server.start();
        System.out.println("Servidor iniciado na porta: 8000");
    }
    
}
