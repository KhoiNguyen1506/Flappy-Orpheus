import javax.swing.*;

public class App {
    public static void main(String[] args) throws Exception {
        int boardWidth = 360;
        int boardHeight = 640;

        JFrame frame = new JFrame("Flappy Orpheus");
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        FlappyOrpheus flappyOrpheus = new FlappyOrpheus();
        frame.add(flappyOrpheus);
        frame.pack();
        flappyOrpheus.requestFocus();
        frame.setVisible(true);
        
    }
}
