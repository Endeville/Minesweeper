package components;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public enum Table {
    SMALL(8, 8),
    MEDIUM(16, 16),
    LARGE(25, 25);

    private final Slot[][] table;
    private final int rows;
    private final int cols;
    private boolean started;

    Table(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.table = new Slot[this.rows][this.cols];
    }

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
    }

    public void reveal(int row, int col) {

        if (this.table[row][col].isMine()) {
            return;
        }

        this.table[row][col].reveal();

        if (this.table[row][col].getMines() == 0) {
            revealAround0(row, col);
        }
    }

    private void revealAround0(int row, int col) {
        for (int i = row - 1; i <= row + 1; i++) {
            for (int j = col - 1; j <= col + 1; j++) {
                if (checkIndices(i, j) && !this.table[i][j].isMine() && !this.table[i][j].isRevealed()) {
                    this.reveal(i, j);
                }
            }
        }
    }

    public void restart() {
        this.started = false;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                this.table[i][j] = new Slot(0);
            }
        }
    }

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

    private boolean checkIndices(int i, int j) {
        return i >= 0 && j >= 0 && i <= this.table.length - 1 && j <= this.table[0].length - 1;
    }

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
