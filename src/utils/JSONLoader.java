package utils;

import com.google.gson.*;
import java.io.*;
import java.util.*;
import model.*;

public class JSONLoader {


    public QuizCollection loadQuestionsFromFile(String fileName) {
        QuizCollection quizCollection = new QuizCollection(); // Criar uma instância de QuizCollection

        try {
            Gson gson = new Gson();
            FileReader reader = new FileReader(fileName);
            JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
            JsonArray quizArray = jsonObject.getAsJsonArray("quizzes"); // Array de quizzes

            for (JsonElement quizElement : quizArray) {
                JsonObject quizObject = quizElement.getAsJsonObject();
                String quizName = quizObject.get("name").getAsString(); // Nome do quiz
                JsonArray questionArray = quizObject.getAsJsonArray("questions"); // Array de perguntas

                Quiz quiz = new Quiz(quizName); // Criar um novo Quiz com o nome carregado
                for (JsonElement questionElement : questionArray) {
                    JsonObject questionObject = questionElement.getAsJsonObject();
                    String questionText = questionObject.get("question").getAsString();
                    int points = questionObject.get("points").getAsInt();
                    int correctAnswer = questionObject.get("correct").getAsInt();
                    JsonArray options = questionObject.getAsJsonArray("options");

                    List<String> optionsList = new ArrayList<>();
                    for (JsonElement option : options) {
                        optionsList.add(option.getAsString()); // Adicionar opções à lista
                    }

                    // Criar a pergunta e adicioná-la ao quiz
                    Question question = new Question(questionText, points, correctAnswer, optionsList);
                    quiz.addQuestion(question); // Adiciona a pergunta ao quiz
                }

                quizCollection.addQuiz(quiz); // Adiciona o quiz ao QuizCollection
            }
        } catch (IOException e) {
            e.printStackTrace(); // Imprime erros de IO
        }

        return quizCollection; // Retorna o QuizCollection contendo todos os quizzes e perguntas
    }
}

