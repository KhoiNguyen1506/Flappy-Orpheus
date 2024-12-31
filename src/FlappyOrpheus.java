import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import java.io.*;
import javax.sound.sampled.*;
import javax.swing.*;


public class FlappyOrpheus extends JPanel implements ActionListener, KeyListener {
    int boardWidth = 410;
    int boardHeight = 670;

    // Images
    Image backgroundImg;
    Image OrpheusImg;
    Image topPipeImg;
    Image bottomPipeImg;

    //Orpheus
    int OrpheX = boardWidth / 8;
    int OrpheY = boardHeight / 2;
    int orpheWidth = 60;
    int orpheHeight = 50;

    class Orphe{
        int x = OrpheX;
        int y = OrpheY;
        int width = orpheWidth;
        int height = orpheHeight;
        Image img;

        Orphe(Image img){
            this.img = img;
        }
    }

    //Pipes
    int pipeX = boardWidth;
    int pipeY = 0;
    int pipeWidth = 64;
    int pipeHeight = 512;

    class Pipe{
        int x = pipeX;
        int y = pipeY;
        int width = pipeWidth;
        int height = pipeHeight;
        Image img;
        boolean passed = false;

        Pipe(Image img){
            this.img = img;
        }
    }


    //Game Logic
    Orphe orpheus;
    int velocityX = -4;
    int velocityY = 0;
    int gravity = 1;
    
    ArrayList<Pipe> pipes;
    Random random = new Random();

    boolean gameOver = false;
    double score = 0;

    //Timers
    Timer gameLoop;
    Timer placePipesTimer;

    FlappyOrpheus() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        //setBackground(Color.blue);
        setFocusable(true);
        addKeyListener(this);

        //Load images
        backgroundImg = new ImageIcon(getClass().getResource("./flappyorpheusbg.png")).getImage();
        OrpheusImg = new ImageIcon(getClass().getResource("./Orpheus Flapping.png")).getImage();
        topPipeImg = new ImageIcon(getClass().getResource("./toppipe.png")).getImage();
        bottomPipeImg = new ImageIcon(getClass().getResource("./bottompipe.png")).getImage();

        //Loading soundtrack
        

        //Orpheus
        orpheus = new Orphe(OrpheusImg);
        pipes = new ArrayList<Pipe>();

        //Place pipes timer
        placePipesTimer = new Timer(2000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                placePipes();
            }
        });
        placePipesTimer.start();

        //Game Timer
        gameLoop = new Timer(1300/60, this);
        gameLoop.start();
    }



    public void placePipes() {
        int randomPipeY = (int) (pipeY - pipeHeight / 4 - Math.random()*(pipeHeight/2));
        int openingSpace = boardHeight / 4;


        Pipe topPipe = new Pipe(topPipeImg);
        topPipe.y = randomPipeY;
        pipes.add(topPipe);

        Pipe bottomPipe = new Pipe(bottomPipeImg);
        bottomPipe.y = topPipe.y + pipeHeight + openingSpace;
        pipes.add(bottomPipe);
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g){
        //Drawing Background
        g.drawImage(backgroundImg, 0, 0, boardWidth, boardHeight, null);

        //Drawing Orpheus
        g.drawImage(orpheus.img, orpheus.x, orpheus.y, orpheus.width, orpheus.height, null);

        //Drawing pipes
        for (int i = 0; i < pipes.size(); i++){
            Pipe pipe = pipes.get(i);
            g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null);
        }

        //Scores
        g.setColor(Color.white);
        g.setFont(new Font("Aerial", Font.PLAIN, 32));
        if(gameOver){
            g.drawString("Game Over: " + String.valueOf((int)score), 100, 220);
            g.drawString("Press space to restart",50, 280);
        }
        else{
            g.drawString(String.valueOf((int) score), 10, 35);
        }
    }

    public void move() {
        //Update the Orpheus' Y coordinates and velocity
        velocityY += gravity;
        orpheus.y += velocityY;
        orpheus.y = Math.max(orpheus.y, 0);

        //Pipes
        for (int i = 0; i < pipes.size(); i++){
            Pipe pipe = pipes.get(i);
            pipe.x += velocityX;

            if(!pipe.passed && orpheus.x > pipe.x + pipe.width){
                pipe.passed = true;
                score += 0.5;
                playSound("src/assets/point.wav");
            }

            if (collision(orpheus, pipe)){
                gameOver = true;
            }
        }

        if (orpheus.y > boardHeight){
            gameOver = true;
        }
    }

    public boolean collision(Orphe a, Pipe b){
        return a.x < b.x + b.width &&
               a.x + a.width > b.x &&
               a.y < b.y + b.height && 
               a.y + a.height > b.y;
    }

    //Sounds
    private void playSound(String filePath) {
        try {
            File audioFile = new File(filePath);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);

            Clip sound = AudioSystem.getClip();
            sound.open(audioStream);

            sound.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();}
        }
    
    

    @Override
    public void actionPerformed(ActionEvent e) {
       move();
       repaint();
       if (gameOver){
        placePipesTimer.stop();
        gameLoop.stop();
       }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            velocityY = -12;
            playSound("src/assets/jump.wav");
            if(gameOver){
                orpheus.y = OrpheY;
                velocityY = 0;
                pipes.clear();
                score = 0;
                gameOver = false;
                gameLoop.start();
                placePipesTimer.start();
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        
    }

    @Override
    public void keyReleased(KeyEvent e) {
        
    }

}
