import javax.swing.*;
import java.awt.*;						//Abstract Window Toolkit
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class HANGMAN {
    private JFrame frame;
    private JPanel panel;
    private JLabel welcomeLabel;
    private JLabel playingLabel;
    private JLabel wordLabel;
    private JTextField guessText;
    private JButton guessButton;
    private JLabel wrongGuessesLabel;
    private HangmanPanel hangmanPanel;

    private String playerName;
    private String randomWord;
    private Set<Character> wrongGuesses; //A Set is a collection in Java that does not 
    //allow duplicate elements, and Character represents a single Unicode character
    private int wrongAttempts;

    public HANGMAN() {
        frame = new JFrame("Hangman Game");
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        panel = new JPanel();
        frame.add(panel);
        initializeUI();

        // Center the frame on the screen
        //frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void initializeUI() {
        panel.setLayout(null);

        welcomeLabel = new JLabel("Welcome to Hangman Game");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 20));
        panel.add(welcomeLabel);
        
        //The welcomeLabel is set to be centered horizontally at the top of the frame
        // (y = 20), with a fixed width of 300 pixels and a height of 25 pixels.
        frame.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                welcomeLabel.setBounds((frame.getWidth() - 300) / 2, 20, 300, 25);
            }
        });

        // Add HangmanPanel
        hangmanPanel = new HangmanPanel();
        hangmanPanel.setBounds(10, 20, 380, 500); //setBounds(x, y, width, height)
        panel.add(hangmanPanel);

        JLabel nameLabel = new JLabel("Enter your name:");
        nameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        nameLabel.setBounds(400, 60, 150, 25);
        panel.add(nameLabel);

        JTextField nameText = new JTextField(20); //20=width of text field
        nameText.setBounds(550, 60, 150, 25);
        panel.add(nameText);

        JButton startButton = new JButton("Start Game");
        startButton.setBounds(500, 100, 100, 25);
        panel.add(startButton);

        startButton.addActionListener(e -> {
            playerName = nameText.getText();
            startGame();
        }); 
        nameText.addActionListener(e -> {
            playerName = nameText.getText();
            startGame();
        }); //enter
    }

    private void startGame() {
        // Choose a word randomly
        randomWord = getRandomWord();

        // Display player is playing
        playingLabel = new JLabel(playerName + " is playing...");
        playingLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        playingLabel.setBounds(400, 140, 300, 25);
        panel.add(playingLabel);

        // Display lines for each letter in the word
        wordLabel = new JLabel(generateHiddenWord());
        wordLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        wordLabel.setBounds(400, 180, 300, 25);
        panel.add(wordLabel);

        // Display "Guess the word" label
        JLabel guessLabel = new JLabel("Guess the word:");
        guessLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        guessLabel.setBounds(400, 220, 150, 25);
        panel.add(guessLabel);

        guessText = new JTextField(20);
        guessText.setBounds(550, 220, 100, 25);
        panel.add(guessText);

        guessButton = new JButton("Guess");
        guessButton.setBounds(660, 220, 80, 25);
        panel.add(guessButton);
        guessText.addActionListener(e -> processGuess());  

        guessButton.addActionListener(e -> processGuess());

        wrongGuessesLabel = new JLabel("Wrong Guesses: ");
        wrongGuessesLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        wrongGuessesLabel.setBounds(400, 260, 300, 25);
        panel.add(wrongGuessesLabel);

        wrongGuesses = new HashSet<>(); //A HashSet is a collection in Java that does not allow duplicate elements, and it does not guarantee the order of elements.
        wrongAttempts = 0;

        // Update the panel to repaint with new components
        panel.repaint();
    }

    private String getRandomWord() {
        String[] wordList = {"RVITM", "LINUX", "PROGRAMMING", "SWINGLIBRARY", "PYTHON", "COMPILER", "DEBUGGER", "JAVASCRIPT", "ANDROID", "SYNTAX", "DATABASE", "WINDOWS", "COMPUTER"};
        Random random = new Random();
        return wordList[random.nextInt(wordList.length)].toUpperCase();
    }

  //StringBuilder is a class used to efficiently build strings, especially when 
  //concatenating many strings in a loop.
    private String generateHiddenWord() {
        StringBuilder hiddenWord = new StringBuilder(); 
        for (int i = 0; i < randomWord.length(); i++) {
            hiddenWord.append("_ ");
        }
        return hiddenWord.toString().trim();
    }
    //Converts the hiddenWord StringBuilder 
    //to a String and trims any leading or trailing whitespaces.
    
    private void processGuess() {
        String guess = guessText.getText().toUpperCase();
        if (guess.length() == 1 && Character.isLetter(guess.charAt(0))) {
            char guessedLetter = guess.charAt(0);
            if (!wrongGuesses.contains(guessedLetter)) {
                if (randomWord.indexOf(guessedLetter) != -1) {
                    if (wordLabel.getText().indexOf(guessedLetter) != -1) {
                        JOptionPane.showMessageDialog(null, "You have already guessed '" + guessedLetter + "' correctly.", "Already Guessed", JOptionPane.ERROR_MESSAGE);
                    } else {
                        updateHiddenWord(guessedLetter);
                    }
                } else {
                    wrongGuesses.add(guessedLetter);
                    wrongAttempts++;

                    // Update wrongGuessesLabel with the new wrong guess
                    updateWrongGuessesLabel();

                    // Update HangmanPanel based on the number of wrong guesses
                    hangmanPanel.updateHangman(wrongAttempts);

                    if (wrongAttempts >= 8) {
                        gameOver(false);
                    }
                }

                if (!wordLabel.getText().contains("_")) {
                    gameOver(true);
                }

                // Clear the guessText for the next guess
                guessText.setText("");
            } else {
                JOptionPane.showMessageDialog(null, "You already guessed '" + guessedLetter + "'.", "Already Guessed", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Enter a valid single alphabet for guess.", "Invalid Guess", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateHiddenWord(char guessedLetter) {
        StringBuilder updatedWord = new StringBuilder(wordLabel.getText());
        for (int i = 0; i < randomWord.length(); i++) {
            if (randomWord.charAt(i) == guessedLetter) {
                updatedWord.setCharAt(i * 2, guessedLetter);
            }
        }
        wordLabel.setText(updatedWord.toString());

        if (!updatedWord.toString().contains("_")) {
            gameOver(true);
        }
    }

    private void updateWrongGuessesLabel() {
        if (wrongGuesses.size() > 0) {
            StringBuilder wrongGuessesText = new StringBuilder("Wrong Guesses: ");
            for (char wrongGuess : wrongGuesses) {
                wrongGuessesText.append(wrongGuess).append(", ");
            }
            wrongGuessesLabel.setText(wrongGuessesText.substring(0, wrongGuessesText.length() - 2));
        } else {
            wrongGuessesLabel.setText("Wrong Guesses: ");
        }
    }

    private boolean gameEnded = false;

    private void gameOver(boolean playerWins) {
        if (!gameEnded) {
            gameEnded = true;  // Set the flag to true to indicate that the game has ended

            if (playerWins) {
                JOptionPane.showMessageDialog(null, "Congratulations, " + playerName + "!\nYou guessed the word: " + randomWord, "Game Over", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Sorry, " + playerName + ". You lost.\nThe word was: " + randomWord, "Game Over", JOptionPane.ERROR_MESSAGE);
            }

            // Clear previous game components
            panel.remove(playingLabel);
            panel.remove(wordLabel);
            panel.remove(guessText);
            panel.remove(guessButton);
            panel.remove(wrongGuessesLabel);

            // Reset the UI for a new game
            initializeUI();
        }
    }


    private class HangmanPanel extends JPanel {
       /*public void reset() {
            // Reset the HangmanPanel
            repaint();
        }*/

        public void updateHangman(int wrongAttempts) {
            // Update HangmanPanel based on the number of wrong guesses
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);  //method in JPanel

            // Draw the hangman based on the number of wrong guesses
            for (int i = 1; i <= wrongAttempts; i++) {
                drawHangmanPart(g, i);
            }
        }

        private void drawHangmanPart(Graphics g, int partNumber) {
            // Draw the corresponding part based on partNumber
            switch (partNumber) {
                case 1:
                    g.setColor(Color.BLACK);
                    g.fillRect(145, 25, 10, 30);
                    g.setColor(Color.BLACK);
                    g.fillRect(145, 5, 150, 20);
                    g.setColor(Color.BLACK);
                    g.fillRect(295, 5, 10, 300);
                    g.setColor(Color.BLACK);
                    g.fillRect(225, 300, 150, 20);
                    break;
                case 2:
                    g.setColor(Color.RED);
                    g.fillOval(100, 50, 100, 100);
                    g.setColor(Color.BLACK);
                    drawEyes(g, 135, 90);
                    drawNose(g, 138, 100);
                    drawMouth(g, 140, 115);
                    break;
                case 3:
                    g.setColor(Color.BLUE);
                    g.fillRect(145, 150, 10, 100);
                    break;
                case 4:
                    g.setColor(Color.ORANGE);
                    int[] xPointsLeft = {150, 160, 195, 185};
                    int[] yPointsLeft = {250, 250, 350, 350};
                    g.fillPolygon(xPointsLeft, yPointsLeft, 4);
                    break;
                case 5:
                    g.setColor(Color.ORANGE);
                    int[] xPointsL = {140, 150, 125, 115};
                    int[] yPointsL = {250, 250, 350, 350};
                    g.fillPolygon(xPointsL, yPointsL, 4);
                    break;
                case 6:
                    g.setColor(Color.BLUE);
                    g.fillRect(145, 250, 10, 100);
                    break;
                case 7:
                    g.setColor(Color.ORANGE);
                    int[] xPoints = {150, 160, 195, 185};
                    int[] yPoints = {350, 350, 450, 450};
                    g.fillPolygon(xPoints, yPoints, 4);
                    break;
                case 8:
                    g.setColor(Color.ORANGE);
                    int[] xPointsLe = {140, 150, 125, 115};
                    int[] yPointsLe = {350, 350, 450, 450};
                    g.fillPolygon(xPointsLe, yPointsLe, 4);
                    break;
                default:
                    // Do nothing for unknown part numbers
                    break;
            }
        }

        private void drawEyes(Graphics g, int x, int y) {
            // Draw eyes
            g.setColor(Color.BLACK);
            g.fillOval(x, y, 10, 10);
            g.fillOval(x + 20, y, 10, 10);
        }

        private void drawNose(Graphics g, int x, int y) {
            // Draw nose
            g.setColor(Color.BLACK);
            int[] xPoints = {x + 8, x + 12, x + 16};
            int[] yPoints = {y + 16, y + 8, y + 16};
            g.drawPolyline(xPoints, yPoints, 3);
        }

        private void drawMouth(Graphics g, int x, int y) {
            // Draw mouth
            g.setColor(Color.BLACK);
            g.drawArc(x + 3, y + 7, 14, 8, 180, 180);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new HANGMAN());
    }
}