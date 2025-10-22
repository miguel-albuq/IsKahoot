package client;

import javax.swing.*;

public class GUI {
    private JFrame frame;
    private JPanel panel;
    private JButton button;
    private JLabel label;

    public GUI() {
        frame = new JFrame("IsKahoot");
        panel = new JPanel();
        button = new JButton("Enviar Respostaaa");
        label = new JLabel("Pergunta aparecerá aqui");

        panel.add(label);
        panel.add(button);
        frame.add(panel);

        frame.setSize(400, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        new GUI();
    }
}
