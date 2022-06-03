package components;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

//Class representation of the minesweeper board

public enum Table {
    SMALL(8, 8),
    MEDIUM(16, 16),
    LARGE(25, 25);

    private final Slot[][] table;
    private final int rows;
    private final int cols;
    private boolean started;
    private long timeStarted;

    Table(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.table = new Slot[this.rows][this.cols];
        this.timeStarted=0;
    }


    //Starts the game(initializes the board) by using a pseudo random algorithm to choose the mine cells
    public void startGame(int row, int col) {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if(i>=row-1 && i<=row+1 && j>=col-1 && j<=col+1) {
                    this.table[i][j]=new Slot(0);
                }else{
                    this.table[i][j] = ThreadLocalRandom.current().nextInt(-1, 5) == -1 ? new Slot(-1) : new Slot(0);
                }
            }
        }

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (!this.table[i][j].isMine()) {
                    fillSlot(i, j);
                }
            }
        }

        this.started = true;
        this.reveal(row, col);
        this.timeStarted=System.currentTimeMillis();
    }

//    Reveals a cell on the board
    public void reveal(int row, int col) {

        if (this.table[row][col].isMine()) {
            return;
        }

        this.table[row][col].reveal();

        if (this.table[row][col].getMines() == 0) {
            revealAround0(row, col);
        }
    }

//    If a cell has zero mines around it, its reveal automatically reveals the ones around it. That's what this method does
    private void revealAround0(int row, int col) {
        for (int i = row - 1; i <= row + 1; i++) {
            for (int j = col - 1; j <= col + 1; j++) {
                if (checkIndices(i, j) && !this.table[i][j].isMine() && !this.table[i][j].isRevealed()) {
                    this.reveal(i, j);
                }
            }
        }
    }

//    Resets the board so that a new game could be started without restarting the whole application
    public void restart() {
        this.started = false;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                this.table[i][j] = new Slot(0);
            }
        }
    }

//    Checks whether the player has won the game i.e. all the cell NOT containing a mine are revealed
    public boolean checkForWin() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if(this.table[i][j].getMines()>=0 && !this.table[i][j].isRevealed()){
                    return false;
                }
            }
        }

        return true;
    }

//    Sets the mines count of the cell(scans the surrounding slots for mines)
    private void fillSlot(int row, int col) {

        var mines = 0;

        for (int i = row - 1; i <= row + 1; i++) {
            for (int j = col - 1; j <= col + 1; j++) {
                if ((i != row || j != col) && checkIndices(i, j) && this.table[i][j].isMine()) {
                    mines++;
                }
            }
        }

        this.table[row][col].setMines(mines);
    }

//    Checks whether an index is valid given the table dimensions
    private boolean checkIndices(int i, int j) {
        return i >= 0 && j >= 0 && i <= this.table.length - 1 && j <= this.table[0].length - 1;
    }

//    Trivial methods(getters, setters, toString etc.)

    public int getRows() {
        return this.rows;
    }

    public int getCols() {
        return this.cols;
    }

    public boolean isRevealed(int row, int col) {
        return this.table[row][col].isRevealed();
    }

    public boolean isMine(int row, int col) {
        return this.table[row][col].isMine();
    }

    public void flag(int row, int col) {
        this.table[row][col].flag();
    }

    public void unflag(int row, int col) {
        this.table[row][col].unflag();
    }

    public boolean isFlagged(int row, int col) {
        return this.table[row][col].isFlagged();
    }

    public boolean isStarted() {
        return started;
    }

    public int getMines(int row, int col) {
        return this.table[row][col].getMines();
    }

    public long getTimeStarted() {
        return timeStarted;
    }

    public Table setTimeStarted(long timeStarted) {
        this.timeStarted = timeStarted;
        return this;
    }

    @Override
    public String toString() {
        var sb = new StringBuilder();

        for (int i = 0; i < this.rows; i++) {
            sb.append(Arrays.stream(this.table[i]).map(e -> String.valueOf(e.getMines())).collect(Collectors.joining(" ")))
                    .append(System.lineSeparator());
        }

        return sb.toString();
    }
}
