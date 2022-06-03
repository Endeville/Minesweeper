package GUI;

import components.Table;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

public class GUI extends JFrame {
//    Uses JButton matrix for swing representation that would be visible for the end-user
    private JButton[][] guiTable;
//    Uses Table for class representation for the game board and its underlying logic
    private Table table;

    private JPanel menu, game, gameOptions;
    private JButton[] gameSizeButtons;
    private JFrame gameFrame;
    private ImageIcon NORMAL_CELL, FLAG_CELL, MINE_CELL;

    private final HashMap<Integer, ImageIcon> minesToPictures = new HashMap<>();

    public GUI() {
        super("Minesweeper");
    }

//    The place where the application starts. It's main function is to get the resources
    public void run() {
        try {
            this.NORMAL_CELL = new ImageIcon(ImageIO.read(Objects.requireNonNull(getClass().getResource("../resources/normal_cell.jpg"))));
            this.FLAG_CELL = new ImageIcon(ImageIO.read(Objects.requireNonNull(getClass().getResource("../resources/flag.png"))));
            this.MINE_CELL = new ImageIcon(ImageIO.read(Objects.requireNonNull(getClass().getResource("../resources/mine.png"))));

            for (int i = 0; i < 9; i++) {
                minesToPictures.put(i, new ImageIcon(ImageIO.read(Objects.requireNonNull(GUI.class.getResource("../resources/" + i + ".png")))));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        JOptionPane.showMessageDialog(new JFrame("Welcome"), "Welcome to minesweeper!");
        showMenu();
    }


//    The game menu interface elements
    private void showMenu() {
        menu = new JPanel();
        menu.setLayout(new GridBagLayout());

        var gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 0, 6, 0);

        var welcomeSign = new JLabel("Choose the mode you'd like to play: ");
        gbc.gridy = 0;
        gbc.gridx = 1;
        menu.add(welcomeSign, gbc);

        this.gameSizeButtons = new JButton[3];
        this.gameSizeButtons[0] = new JButton("Small (8x8)");
        this.gameSizeButtons[1] = new JButton("Medium (16x16)");
        this.gameSizeButtons[2] = new JButton("Large (25x25)");

        gbc.gridy = 1;
        gbc.gridx = 0;
        menu.add(gameSizeButtons[0], gbc);

        gbc.gridx = 1;
        menu.add(gameSizeButtons[1], gbc);

        gbc.gridx = 2;
        menu.add(gameSizeButtons[2], gbc);


        for (JButton sizeButton : this.gameSizeButtons) {
            sizeButton.addActionListener(e -> {
                var game = ((JButton) e.getSource()).getText().split("\\s+")[0].toUpperCase();
                this.table = Arrays.stream(Table.values()).filter(t -> t.name().equals(game)).toList().get(0);
                this.guiTable = new JButton[this.table.getRows()][this.table.getCols()];

                this.setVisible(false);
                showGame();
            });
        }

        this.add(this.menu);
        this.pack();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setSize(575, 200);
        this.setVisible(true);
    }

//    After choosing the game type, this method shows the
    private void showGame() {
        this.gameFrame = new JFrame("Minesweeper");

        this.game = new JPanel(new GridLayout(this.table.getRows(), this.table.getCols()));

        for (int i = 0; i < this.table.getRows(); i++) {
            for (int j = 0; j < this.table.getCols(); j++) {

                final var currentR = i;
                final var currentC = j;

                this.guiTable[i][j] = new JButton();
                this.guiTable[i][j].setIcon(NORMAL_CELL);

                this.guiTable[i][j].addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if ((e.getButton() == MouseEvent.BUTTON3 || e.getButton() == MouseEvent.BUTTON1)) {
                            if (e.getButton() == MouseEvent.BUTTON1) {
                                revealCell(currentR, currentC);
                            } else if (e.getButton() == MouseEvent.BUTTON3 && table.isStarted() && !table.isRevealed(currentR, currentC)) {
                                flagCell(currentR, currentC);
                            }

                            if (table.isStarted()) {
                                refreshTable();
                                refreshFrame(gameFrame);

                                if (table.checkForWin()) {
                                    var time=System.currentTimeMillis();
                                    var minutes=(time-table.getTimeStarted())/60000;
                                    var seconds=(time-table.getTimeStarted()-minutes*60000)/1000;

                                    JOptionPane.showMessageDialog(new JFrame(),
                                            String.format("Congratulations!!! You won!!!%nYou finished in: %s : %s",minutes, seconds<10 ? "0"+seconds: seconds));

                                    try {
                                        var fw=new FileWriter("src/data.txt", true);
                                        fw.write(String.format("Game(%dx%d) finished in: %s : %s%n",
                                                table.getRows(), table.getCols(), minutes, seconds<10 ? "0"+seconds: seconds));
                                        fw.close();
                                    } catch (IOException ex) {
                                        ex.printStackTrace();
                                    }
                                    gameFrame.setVisible(false);
                                    setVisible(true);
                                    table.restart();
                                }
                            }
                        }
                    }
                });

                this.game.add(this.guiTable[i][j]);
            }
        }

        this.gameFrame.add(this.game);

        gameFrame.pack();
        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameFrame.setLocationRelativeTo(null);
        gameFrame.setSize(this.table.getRows() * 30, this.table.getRows() * 30);
        gameFrame.setResizable(false);
        gameFrame.setVisible(true);
    }

//    Reveals a cell at the click of a button. Checks whether it is a mine and acts accordingly
    private void revealCell(int currentR, int currentC) {
        if (!table.isStarted()) {
            table.startGame(currentR, currentC);

        } else if (table.isMine(currentR, currentC)) {

            for (int i = 0; i < guiTable.length; i++) {
                for (int j = 0; j < guiTable[0].length; j++) {
                    if (table.isMine(i, j)) {
                        guiTable[i][j].setIcon(MINE_CELL);
                    }
                }
            }


            var time=System.currentTimeMillis();
            var minutes=(time-table.getTimeStarted())/60000;
            var seconds=(time-table.getTimeStarted()-minutes*60000)/1000;

            JOptionPane.showMessageDialog(new JFrame(), String.format("You lost! Take the L XD%n You failed in:%s : %s",minutes, seconds<10 ? "0"+seconds: seconds));

            try {
                var fw=new FileWriter("src/data.txt", true);
                fw.write(String.format("Game(%dx%d) failed in: %s : %s%n",
                        table.getRows(), table.getCols(), minutes, seconds<10 ? "0"+seconds: seconds));
                fw.close();
            }catch (IOException e) {
                e.printStackTrace();
            }
            gameFrame.setVisible(false);
            setVisible(true);
            table.restart();

        } else if (!table.isRevealed(currentR, currentC)) {
            table.reveal(currentR, currentC);
        }
    }

//    Flags a cell when the right mouse button is clicked on it
    private void flagCell(int currentR, int currentC) {
        if (table.isFlagged(currentR, currentC)) {
            table.unflag(currentR, currentC);
        } else {
            table.flag(currentR, currentC);
        }
    }

//    refreshes the frame and therefore - the gui
    private void refreshFrame(JFrame frame) {
        frame.invalidate();
        frame.validate();
        frame.repaint();
    }

//    Adjusts the gui table based on the changes made
    private void refreshTable() {
        for (int k = 0; k < guiTable.length; k++) {
            for (int l = 0; l < guiTable[0].length; l++) {
                if (table.isRevealed(k, l)) {
                    guiTable[k][l]
                            .setIcon(minesToPictures.get(table.getMines(k, l)));

                    guiTable[k][l].setEnabled(false);

                } else if (table.isFlagged(k, l)) {
                    guiTable[k][l].setIcon(FLAG_CELL);

                } else if (!table.isFlagged(k, l) && !table.isRevealed(k, l)) {
                    guiTable[k][l].setIcon(NORMAL_CELL);
                }
            }
        }
    }
}
