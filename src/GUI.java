import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

public class GUI extends JFrame {
    private JButton[][] guiTable;
    private Table table;

    private JPanel menu, game, gameOptions;
    private JButton[] gameSizeButtons;
    private JFrame gameFrame;

    public GUI() {
        super("Minesweeper");

        JOptionPane.showMessageDialog(new JFrame("Welcome"), "Welcome to minesweeper!");
        showMenu();
    }

    private void showMenu() {
        menu=new JPanel();
        menu.setLayout(new GridBagLayout());

        var gbc=new GridBagConstraints();
        gbc.insets=new Insets(6,0,6,0);

        var welcomeSign=new JLabel("Choose the mode you'd like to play: ");
        gbc.gridy=0;
        gbc.gridx=1;
        menu.add(welcomeSign, gbc);

        this.gameSizeButtons = new JButton[3];
        this.gameSizeButtons[0] = new JButton("Small (8x8)");
        this.gameSizeButtons[1] = new JButton("Medium (16x16)");
        this.gameSizeButtons[2] = new JButton("Large (25x25)");

        gbc.gridy=1;
        gbc.gridx=0;
        menu.add(gameSizeButtons[0],gbc);

        gbc.gridx=1;
        menu.add(gameSizeButtons[1],gbc);

        gbc.gridx=2;
        menu.add(gameSizeButtons[2],gbc);


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
        this.gameFrame=new JFrame("Minesweeper");

        this.game=new JPanel(new GridLayout(this.table.getRows(), this.table.getCols()));

        for (int i = 0; i < this.table.getRows(); i++) {
            for (int j = 0; j < this.table.getCols(); j++) {

                final var currentR=i;
                final var currentC=j;

                this.guiTable[i][j]=new JButton();

                this.guiTable[i][j].addActionListener(e->{
                    if(!this.table.isStarted()){
                        this.table.startGame(currentR,currentC);
                    }else if(this.table.isMine(currentR, currentC)){
                        JOptionPane.showMessageDialog(new JFrame(), "You lost! L");
                        this.gameFrame.setVisible(false);
                        this.setVisible(true);
                        this.table.restart();
                        this.restart();
                    }else{
                        this.table.reveal(currentR,currentC);
                    }

                    for (int k = 0; k < this.table.getRows(); k++) {
                        for (int l = 0; l < this.table.getCols(); l++) {
                            if(this.table.isRevealed(k, l)){
                                try {
                                    this.guiTable[k][l]
                                            .setIcon(new ImageIcon(ImageIO.read(getClass().getResource("resources/"+this.table.getMines(k,l)+".png"))));
                                } catch (IOException ex) {
                                    ex.printStackTrace();
                                }
                                this.guiTable[k][l].setEnabled(false);
                            }
                        }
                    }

                    game.repaint();
                    this.gameFrame.invalidate();
                    this.gameFrame.validate();
                    this.gameFrame.repaint();

                    if(this.table.checkForWin()){
                        JOptionPane.showMessageDialog(new JFrame(), "Congratulations!!! You won!!!");
                        this.gameFrame.setVisible(false);
                        this.setVisible(true);
                        this.table.restart();
                        this.restart();
                    }
                });

                this.guiTable[i][j].addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if(e.getButton()==MouseEvent.BUTTON3){
                            if(table.isFlagged(currentR, currentC)){
                                table.unflag(currentR, currentC);
                            }else{
                                table.flag(currentR, currentC);
                            }
                        }

                        for (int k = 0; k < table.getRows(); k++) {
                            for (int l = 0; l < table.getCols(); l++) {
                                if(table.isFlagged(k, l)){
                                    try {
                                        guiTable[k][l]
                                                .setIcon(new ImageIcon(ImageIO.read(getClass().getResource("resources/flag.png"))));
                                    } catch (IOException ex) {
                                        ex.printStackTrace();
                                    }
                                }
                            }
                        }

                        game.repaint();
                        gameFrame.invalidate();
                        gameFrame.validate();
                        gameFrame.repaint();
                    }
                });

                this.game.add(this.guiTable[i][j]);
            }
        }

        this.gameFrame.add(this.game);

        gameFrame.pack();
        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameFrame.setLocationRelativeTo(null);
        gameFrame.setSize(this.table.getRows()*30, this.table.getRows()*30);
        gameFrame.setResizable(false);
        gameFrame.setVisible(true);
    }

    private void restart() {
        for (JButton[] jButtons : this.guiTable) {
            Arrays.fill(jButtons, new JButton());
        }
    }
}
