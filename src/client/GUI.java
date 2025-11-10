package client;

import model.*;
import utils.JSONLoader;

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

    private GameState gameState;
    private Team currentTeam;

    public GUI(GameState state, Team team) {
        this.gameState = state;
        this.currentTeam = team;

        frame = new JFrame("IsKahoot");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        questionLabel = new JLabel("", SwingConstants.CENTER);
        questionLabel.setFont(new Font("Arial", Font.BOLD, 16));

        optionsPanel = new JPanel(new GridLayout(0, 1));
        optionsGroup = new ButtonGroup();

        scoreLabel = new JLabel("Pontuação: 0", SwingConstants.CENTER);
        nextButton = new JButton("Enviar / Próxima");

        nextButton.addActionListener(e -> handleNext());

        frame.add(questionLabel, BorderLayout.NORTH);
        frame.add(optionsPanel, BorderLayout.CENTER);
        frame.add(scoreLabel, BorderLayout.SOUTH);
        frame.add(nextButton, BorderLayout.EAST);

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
            JOptionPane.showMessageDialog(frame, "Seleciona uma resposta!");
            return;
        }

        if (selected == q.getCorrect()) {
            currentTeam.addPlayer(new Player("Dummy", currentTeam.getName())); // só para simular
            gameState.addScore(currentTeam.getName(), q.getPoints());
            JOptionPane.showMessageDialog(frame, "Resposta certa! +" + q.getPoints() + " pontos.");
        } else {
            JOptionPane.showMessageDialog(frame, "Resposta errada!");
        }

        scoreLabel.setText("Pontuação: " + gameState.getTeamScores().get(currentTeam.getName()));

        if (!gameState.nextQuestion()) {
            questionLabel.setText("Fim do quiz!");
            nextButton.setEnabled(false);
        } else {
            loadQuestion();
        }
    }

    public static void main(String[] args) {
        QuizCollection qc = JSONLoader.load("resources/questions.json");
        Quiz quiz = qc.getQuizzes().get(0);

        GameState state = new GameState(quiz);
        Team team = new Team("Team A");
        state.addTeam(team);

        new GUI(state, team);
    }
}
