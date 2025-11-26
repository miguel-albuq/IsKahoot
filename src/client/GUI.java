package client;

import model.*;
import server.*;

import javax.swing.*;
import java.awt.*;
import java.util.Enumeration;

public class GUI {
    private JFrame frame;
    private JLabel questionLabel;
    private JPanel optionsPanel;
    private ButtonGroup optionsGroup;
    private JLabel scoreLabel;
    private JButton nextButton;
    private JLabel timerLabel;

    private GameState gameState;
    private Team currentTeam;
    private String username;   // <- novo


    public GUI(GameState state, Team team, String username) {
        this.gameState = state;
        this.currentTeam = team;
        this.username = username;


        frame = new JFrame("IsKahoot" + " | " + this.username);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        questionLabel = new JLabel("", SwingConstants.CENTER);
        questionLabel.setFont(new Font("Arial", Font.BOLD, 16));

        timerLabel = new JLabel("Tempo: --", SwingConstants.CENTER);
        timerLabel.setFont(new Font("Arial", Font.BOLD, 14));

        optionsPanel = new JPanel(new GridLayout(0, 1));
        optionsGroup = new ButtonGroup();

        scoreLabel = new JLabel("Pontuação: 0", SwingConstants.CENTER);
        nextButton = new JButton("Enviar / Próxima");

        nextButton.addActionListener(e -> handleNext());

        frame.add(questionLabel, BorderLayout.NORTH);
        frame.add(optionsPanel, BorderLayout.CENTER);
        frame.add(scoreLabel, BorderLayout.SOUTH);
        frame.add(nextButton, BorderLayout.EAST);
        frame.add(timerLabel, BorderLayout.WEST);

        // REGISTAR GUI como listener do temporizador
        gameState.setTimerListener(new GameState.TimerUpdateListener() {
            @Override
            public void onTimerUpdate(int secondsRemaining) {
                SwingUtilities.invokeLater(() -> timerLabel.setText("Tempo: " + Math.max(0, secondsRemaining) + "s"));
            }

            @Override
            public void onTimerFinished() {
                // note: esta rotina é chamada UMA vez por GameState devido ao timerFired flag
                SwingUtilities.invokeLater(() -> {
                    timerLabel.setText("Tempo: 0s");
                    JOptionPane.showMessageDialog(frame, "Terminou o tempo!");

                    // Passa automaticamente para a próxima pergunta.
                    // Usar o nextQuestion do GameState (que já cancela timers)
                    if (!gameState.nextQuestion()) {
                        questionLabel.setText("Fim do jogo!");
                        nextButton.setEnabled(false);
                        // assegura que não há timer activo
                        // (GameState.nextQuestion já cancela timer, mas por segurança:)
                        // gameState.cancelRoundTimer(); // método privado — não chamamos aqui
                    } else {
                        loadQuestion();
                    }
                });
            }
        });

        loadQuestion();
        frame.pack();
        frame.setSize(500, 300);
        frame.setVisible(true);
    }

    private void loadQuestion() {
        optionsPanel.removeAll();
        optionsGroup = new ButtonGroup();

        Question q = gameState.getCurrentQuestion();
        if (q == null) {
            questionLabel.setText("Fim do Quiz!");
            nextButton.setEnabled(false);
            timerLabel.setText("Tempo: --");
            return;
        }

        questionLabel.setText(q.getQuestion());
        for (int i = 0; i < q.getOptions().size(); i++) {
            String opt = q.getOptions().get(i);
            JRadioButton btn = new JRadioButton(opt);
            btn.setActionCommand(String.valueOf(i));
            optionsGroup.add(btn);
            optionsPanel.add(btn);
        }

        optionsPanel.revalidate();
        optionsPanel.repaint();

        // começa o contador (ex.: 30 segundos)
        gameState.startRoundTimer(30);
    }

    private void handleNext() {
        Question q = gameState.getCurrentQuestion();
        if (q == null) return;

        Enumeration<AbstractButton> buttons = optionsGroup.getElements();
        int selected = -1;
        while (buttons.hasMoreElements()) {
            AbstractButton b = buttons.nextElement();
            if (b.isSelected()) selected = Integer.parseInt(b.getActionCommand());
        }

        if (selected == -1) {
            JOptionPane.showMessageDialog(frame, "Seleciona uma respostaa!");
            return;
        }

        // Ao responder manualmente, queremos também garantir que o timer pára e que
        // não dispare a rotina de "tempo esgotado". nextQuestion() cancela timers.
        if (selected == q.getCorrectAnswer()) {
            gameState.addScore(currentTeam.getName(), q.getPoints());
            JOptionPane.showMessageDialog(frame, "Resposta certa! +" + q.getPoints() + " pontos.");
        } else {
            JOptionPane.showMessageDialog(frame, "Resposta errada!");
        }

        scoreLabel.setText("Pontuação: " + gameState.getTeamScores().getOrDefault(currentTeam.getName(), 0));

        // avançar para a próxima pergunta (nextQuestion cancela timer)
        if (!gameState.nextQuestion()) {
            questionLabel.setText("Fim do quiz!");
            nextButton.setEnabled(false);
            timerLabel.setText("Tempo: --");
        } else {
            loadQuestion();
        }
    }
    public static void main(String[] args) {

    }

}
