package client;

import shared.Messages.*;
import model.*;
import utils.JSONLoader;
import server.*;

import javax.swing.SwingUtilities;
import java.net.*;
import java.io.*;

public class MainClient {

    public static void main(String[] args) throws Exception {
        String host = args[0];
        int port    = Integer.parseInt(args[1]);
        int teamNo  = Integer.parseInt(args[2]);
        String username = args[3];

        try (Socket s = new Socket(host, port)) {
            ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
            ObjectInputStream  in  = new ObjectInputStream(s.getInputStream());

            out.writeObject(Envelope.of(MsgType.JOIN_REQ, new JoinReq(username, teamNo)));
            out.flush();

            String gameCode = null; // vamos guardar para log/GUI se quiseres

            while (true) {
                Envelope env = (Envelope) in.readObject();

                switch (env.type()) {
                    case JOIN_ERR -> {
                        String reason = ((JoinErr) env.body()).reason();
                        System.out.println("[ERRO] " + reason);
                        return;
                    }
                    case JOIN_OK -> {
                        gameCode = ((JoinOk) env.body()).gameCode();
                        System.out.println("[OK] Código do jogo: " + gameCode);
                        System.out.println("Aguardando mais jogadores...");
                    }
                    case LOBBY -> {
                        Lobby l = (Lobby) env.body();
                        System.out.println("[LOBBY] Equipas prontas: " + l.teamsReady()
                                + " / " + l.expectedTeams());
                    }
                    case START -> {
                        String gc = ((Start) env.body()).gameCode();
                        System.out.println("[START] Começou! game=" + gc);

                        // 1) Carregar o quiz do JSON
                        JSONLoader loader = new JSONLoader();
                        QuizCollection qc = loader.loadQuestionsFromFile("resources/questions.json");
                        Quiz quiz = qc.getQuizzes().get(0); // usa o primeiro (ou escolhe por nome)

                        // 2) Criar GameState e Team deste cliente
                        GameState state = new GameState(quiz);
                        Team team = new Team("Team " + teamNo); // ou "Team " + teamNo + " (" + username + ")"
                        state.addTeam(team);

                        // 3) Lançar a GUI no Event Dispatch Thread (Swing)
                        final GameState st = state;
                        final Team tm = team;
                        final String user = username;
                        SwingUtilities.invokeLater(() -> new GUI(st, tm, username));

                        // Se por agora a lógica de jogo é toda local na GUI, podemos sair do loop
                        // (ou manter a ligação para futura sincronização com o servidor).
                        return;
                    }
                    default -> { /* ignora outros tipos */ }
                }
            }
        }
    }
}
