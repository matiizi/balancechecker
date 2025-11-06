package org.example;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class FileService {
    private static final String FILE_NAME = "src/main/java/data/data.json";
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().registerTypeAdapter(Transaction.class, new TransactionAdapter()).create();

    public static void saveData(DataStorage data) {
        try (FileWriter writer = new FileWriter(FILE_NAME)) {
            gson.toJson(data, writer);
        } catch (IOException e) {
            System.out.println("Ошибка при сохранении данных: " + e.getMessage());
        }
    }

    public static DataStorage loadData() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            return new DataStorage();
        }

        try (FileReader reader = new FileReader(FILE_NAME)) {
            return gson.fromJson(reader, DataStorage.class);
        } catch (IOException e) {
            System.out.println("Ошибка при загрузке данных: " + e.getMessage());
            return new DataStorage();
        }
    }

    private static class TransactionAdapter extends TypeAdapter<Transaction> {
        private final Gson gson = new Gson();
        private final Type type = new TypeToken<Map<String, Object>>(){}.getType();

        @Override
        public void write(JsonWriter out, Transaction transaction) throws IOException {
            if (transaction == null) {
                out.nullValue();
                return;
            }

            Map<String, Object> map = new HashMap<>();
            map.put("type", transaction instanceof Income ? "INCOME" : "EXPENSE");
            map.put("amount", transaction.getAmount());
            map.put("category", transaction.getCategory());
            map.put("description", transaction.getDescription());

            gson.toJson(map, type, out);
        }

        @Override
        public Transaction read(JsonReader in) throws IOException {
            Map<String, Object> map = gson.fromJson(in, type);

            String type = (String) map.get("type");
            double amount = ((Number) map.get("amount")).doubleValue();
            String category = (String) map.get("category");
            String description = (String) map.get("description");

            if ("INCOME".equals(type)) {
                return new Income(amount, category, description);
            } else {
                return new Expense(amount, category, description);
            }
        }
    }
}
