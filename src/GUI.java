import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

public class GUI extends JFrame {
    private JButton[][] guiTable;
    private Table table;

    private JPanel menu, game, gameOptions;
    private JButton[] gameSizeButtons;
    private JFrame gameFrame;
    private ImageIcon NORMAL_CELL, FLAG_CELL, MINE_CELL;

    public GUI() {
        super("Minesweeper");

        try {
            this.NORMAL_CELL = new ImageIcon(ImageIO.read(Objects.requireNonNull(GUI.class.getResource("resources/normal_cell.jpg"))));
            this.FLAG_CELL = new ImageIcon(ImageIO.read(Objects.requireNonNull(getClass().getResource("resources/flag.png"))));
            this.MINE_CELL = new ImageIcon(ImageIO.read(Objects.requireNonNull(getClass().getResource("resources/mine.png"))));
        } catch (IOException e) {
            e.printStackTrace();
        }

        JOptionPane.showMessageDialog(new JFrame("Welcome"), "Welcome to minesweeper!");
        showMenu();
    }

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
                                    JOptionPane.showMessageDialog(new JFrame(), "Congratulations!!! You won!!!");
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

    private void revealCell(int currentR, int currentC) {
        if (!table.isStarted()) {
            table.startGame(currentR, currentC);

        } else if (table.isMine(currentR, currentC)) {

            for (int i = 0; i < guiTable.length; i++) {
                for (int j = 0; j < guiTable[0].length; j++) {
                    if(table.isMine(i,j)){
                        guiTable[i][j].setIcon(MINE_CELL);
                    }
                }
            }

            JOptionPane.showMessageDialog(new JFrame(), "You lost! L");
            gameFrame.setVisible(false);
            setVisible(true);
            table.restart();

        } else if (!table.isRevealed(currentR, currentC)) {
            table.reveal(currentR, currentC);
        }
    }

    private void flagCell(int currentR, int currentC) {
        if (table.isFlagged(currentR, currentC)) {
            table.unflag(currentR, currentC);
        } else {
            table.flag(currentR, currentC);
        }
    }

    private void refreshFrame(JFrame frame) {
        frame.invalidate();
        frame.validate();
        frame.repaint();
    }

    private void refreshTable() {
        for (int k = 0; k < guiTable.length; k++) {
            for (int l = 0; l < guiTable[0].length; l++) {
                if (table.isRevealed(k, l)) {
                    try {
                        guiTable[k][l]
                                .setIcon(new ImageIcon(ImageIO.read(Objects.requireNonNull(getClass().getResource("resources/" + table.getMines(k, l) + ".png")))));
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }

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
