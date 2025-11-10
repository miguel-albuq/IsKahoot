package utils;

import com.google.gson.Gson;
import model.QuizCollection;
import java.io.FileReader;

public class JSONLoader {
    public static QuizCollection load(String path) {
        try (FileReader reader = new FileReader(path)) {
            Gson gson = new Gson();
            return gson.fromJson(reader, QuizCollection.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
