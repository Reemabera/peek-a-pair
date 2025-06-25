import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class TimedMemoryChallenge {
private JFrame frame;
private JPanel boardPanel;
private JLabel timerLabel, moveLabel, starLabel, turnLabel, challengeLabel;
private Timer timer;
private int timeElapsed, moves, matchedPairs;
private JButton[] buttons;
private int[] values;
private JButton firstSelected, secondSelected;
private int gridSize;
private String playerName;
private int bestTime = Integer.MAX_VALUE;
private boolean isPaused = false;
private boolean isPlayerOneTurn = true;
private int playerOneScore = 0, playerTwoScore = 0;
private JButton pauseButton;
private boolean isDailyChallenge = false;
private String dailySeed;

public TimedMemoryChallenge() {
askPlayerName();
askGameMode();
if (!isDailyChallenge)
askGameLevel();
initializeUI();
}

private void askPlayerName() {
playerName = JOptionPane.showInputDialog("Enter your name:");
if (playerName == null || playerName.trim().isEmpty()) {
playerName = "Player";
}
}

private void askGameMode() {
String[] modes = { "Normal Mode", "Daily Challenge" };
int choice = JOptionPane.showOptionDialog(null, "Select Game Mode:", "Game Mode",
JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, modes, modes[0]);
isDailyChallenge = (choice == 1);
}

private void askGameLevel() {
String[] options = { "Easy (4x4)", "Medium (6x6)", "Difficult (8x8)" };
int choice = JOptionPane.showOptionDialog(null, "Select Game Difficulty:", "Choose Level",
JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

gridSize = (choice == 1) ? 6 : (choice == 2) ? 8 : 4;
}

private void initializeUI() {
frame = new JFrame("🧠 Timed Memory Challenge");
frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
frame.setSize(900, 1000);
frame.setLayout(new BorderLayout(10, 10));
frame.getContentPane().setBackground(new Color(173, 216, 230)); // Light Blue Background

JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
topPanel.setBackground(new Color(173, 216, 230)); // Light Blue
topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

timerLabel = createStyledLabel("⏱ Time: 0s", Color.BLACK);
moveLabel = createStyledLabel("🔁 Moves: 0", Color.BLACK);
starLabel = createStyledLabel("⭐ Stars: ★★★", new Color(255, 215, 0));
turnLabel = createStyledLabel("🎮 Turn: Player 1", Color.BLACK);
challengeLabel = createStyledLabel("", Color.BLACK);

if (isDailyChallenge) {
gridSize = 6;
challengeLabel.setText("📅 Daily Challenge Mode");
dailySeed = getCurrentDateSeed();
}

JButton restartButton = createStyledButton("🔄 Restart");
restartButton.addActionListener(e -> restartGame());

pauseButton = createStyledButton("⏸ Pause");
pauseButton.addActionListener(e -> togglePause());

topPanel.add(timerLabel);
topPanel.add(moveLabel);
topPanel.add(starLabel);
topPanel.add(turnLabel);
topPanel.add(challengeLabel);
topPanel.add(restartButton);
topPanel.add(pauseButton);

frame.add(topPanel, BorderLayout.NORTH);

boardPanel = new JPanel();
boardPanel.setBackground(new Color(173, 216, 230)); // Light Blue
frame.add(boardPanel, BorderLayout.CENTER);

JPanel bottomPanel = new JPanel();
bottomPanel.setBackground(new Color(173, 216, 230)); // Light Blue
JButton leaderboardButton = createStyledButton("🏆 Leaderboard");
leaderboardButton.addActionListener(e -> showLeaderboard());
bottomPanel.add(leaderboardButton);
frame.add(bottomPanel, BorderLayout.SOUTH);
setupGame();
frame.setVisible(true);
}

private JLabel createStyledLabel(String text, Color color) {
JLabel label = new JLabel(text);
label.setFont(new Font("Verdana", Font.BOLD, 16));
label.setForeground(color);
return label;
}

private JButton createStyledButton(String text) {
JButton button = new JButton(text);
button.setBackground(new Color(70, 130, 180));
button.setForeground(Color.WHITE);
button.setFont(new Font("Arial", Font.BOLD, 14));
button.setFocusPainted(false);
button.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
return button;
}

private void setupGame() {
boardPanel.removeAll();
boardPanel.setLayout(new GridLayout(gridSize, gridSize, 10, 10));
int totalCards = gridSize * gridSize;
values = new int[totalCards];
ArrayList<Integer> cardValues = new ArrayList<>();
for (int i = 0; i < totalCards / 2; i++) {
cardValues.add(i);
cardValues.add(i);
}
if (isDailyChallenge) {
Collections.shuffle(cardValues, new Random(dailySeed.hashCode()));
} else {
Collections.shuffle(cardValues);
}
for (int i = 0; i < totalCards; i++) {
values[i] = cardValues.get(i);
}

buttons = new JButton[totalCards];
for (int i = 0; i < totalCards; i++) {
JButton btn = new JButton();
btn.setFont(new Font("SansSerif", Font.BOLD, 28));
btn.setBackground(new Color(255, 255, 255));
btn.setForeground(Color.RED); // Card font color changed to red
btn.setFocusPainted(false);
btn.setBorder(BorderFactory.createLineBorder(new Color(100, 149, 237), 3));
int index = i;
btn.addActionListener(new CardClickListener(index));
buttons[i] = btn;
boardPanel.add(btn);
}
matchedPairs = 0;
moves = 0;
updateMoveLabel();
updateStars();
isPlayerOneTurn = true;
playerOneScore = 0;
playerTwoScore = 0;
startTimer();
boardPanel.revalidate();
boardPanel.repaint();
}

private String getCurrentDateSeed() {
return java.time.LocalDate.now().toString();
}

private void startTimer() {
timeElapsed = 0;
if (timer != null)
timer.stop();
timer = new Timer(1000, e -> {
if (!isPaused)
timerLabel.setText("⏱ Time: " + (++timeElapsed) + "s");
});
timer.start();
}

private void togglePause() {
isPaused = !isPaused;
pauseButton.setText(isPaused ? "▶ Resume" : "⏸ Pause");
}

private void updateMoveLabel() {
moveLabel.setText("🔁 Moves: " + moves);
updateStars();
}

private void updateStars() {
int total = gridSize * gridSize / 2;
int rating = 3;
if (moves > total * 2)
rating = 1;
else if (moves > total * 1.5)
rating = 2;
starLabel.setText("⭐ Stars: " + "★".repeat(rating));
}

private void restartGame() {
askGameMode();
if (!isDailyChallenge)
askGameLevel();
initializeUI();
}

private void showLeaderboard() {
String leaderboard = "Leaderboard:\n" +
"Best Time: " + (bestTime == Integer.MAX_VALUE ? "N/A" : bestTime + "s");
JOptionPane.showMessageDialog(frame, leaderboard, "🏆 Leaderboard", JOptionPane.INFORMATION_MESSAGE);
}

private class CardClickListener implements ActionListener {
private final int index;

public CardClickListener(int index) {
this.index = index;
}

@Override
public void actionPerformed(ActionEvent e) {
if (isPaused)
return;
JButton button = buttons[index];
if (button == firstSelected || button == secondSelected || !button.getText().isEmpty())
return;

button.setText(String.valueOf(values[index]));
button.setBackground(new Color(173, 216, 230)); // Reveal color

if (firstSelected == null) {
firstSelected = button;
} else if (secondSelected == null) {
secondSelected = button;
moves++;
updateMoveLabel();
if (firstSelected.getText().equals(secondSelected.getText())) {
matchedPairs++;
if (isPlayerOneTurn)
playerOneScore++;
else
playerTwoScore++;
firstSelected.setBackground(new Color(144, 238, 144));
secondSelected.setBackground(new Color(144, 238, 144));
firstSelected = null;
secondSelected = null;
if (matchedPairs == (gridSize * gridSize) / 2) {
timer.stop();
updateBestTime();
String winner = (playerOneScore == playerTwoScore) ? "It's a draw!"
: (playerOneScore > playerTwoScore ? "Player 1 wins!" : "Player 2 wins!");
JOptionPane.showMessageDialog(frame, "🎉 Congratulations, " + playerName +
"! Game completed in " + timeElapsed + "s.\n" + winner,
"🎊 Game Over", JOptionPane.INFORMATION_MESSAGE);
}
} else {
Timer delay = new Timer(700, ev -> {
firstSelected.setText("");
secondSelected.setText("");
firstSelected.setBackground(Color.WHITE);
secondSelected.setBackground(Color.WHITE);
firstSelected = null;
secondSelected = null;
isPlayerOneTurn = !isPlayerOneTurn;
turnLabel.setText("🎮 Turn: " + (isPlayerOneTurn ? "Player 1" : "Player 2"));
});
delay.setRepeats(false);
delay.start();
}
}
}
}

private void updateBestTime() {
if (timeElapsed < bestTime) {
bestTime = timeElapsed;
}
}

public static void main(String[] args) {
SwingUtilities.invokeLater(TimedMemoryChallenge::new);
}
}


