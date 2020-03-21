package de.pauhull.paintwars.util;

// Project: paintwars
// Class created on 21.03.2020 by Paul
// Package de.pauhull.paintwars.util

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.pauhull.paintwars.PaintWars;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;
import java.util.function.BiConsumer;

public class UUIDFetcher {

    private PaintWars paintWars;

    public UUIDFetcher(PaintWars paintWars) {

        this.paintWars = paintWars;
    }

    public void fetchProfile(String identifier, BiConsumer<UUID, String> consumer) {

        paintWars.getExecutorService().execute(() -> {

            String url = String.format("https://api.mcstats.net/v2/player/%s/", identifier);
            HttpURLConnection connection = null;

            try {
                connection = (HttpURLConnection) new URL(url).openConnection();
            } catch (IOException e) {
                e.printStackTrace();
                consumer.accept(null, null);
                return;
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {

                JsonObject json = new JsonParser().parse(reader).getAsJsonObject();

                int status = json.getAsJsonObject("system").getAsJsonPrimitive("status").getAsInt();

                if (status != 200) {
                    consumer.accept(null, null);
                    return;
                }

                UUID uuid = UUID.fromString(json.getAsJsonObject("response").getAsJsonPrimitive("UUID").getAsString());
                String name = json.getAsJsonObject("response").getAsJsonPrimitive("name").getAsString();

                consumer.accept(uuid, name);

            } catch (IOException e) {
                e.printStackTrace();
                consumer.accept(null, null);
            }
        });
    }

}
