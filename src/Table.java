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
        this.table=new Slot[this.rows][this.cols];
    }

    public void startGame(int row, int col){
        this.table[row][col]=new Slot(0);

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if(i!=row || j!=col) {
                    this.table[i][j] = ThreadLocalRandom.current().nextInt(-1, 5) == -1 ? new Slot(-1) : new Slot(0);
                }
            }
        }

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if(!this.table[i][j].isMine()){
                    fillSlot(i, j);
                }
            }
        }

        this.reveal(row,col);
    }

    public boolean reveal(int row, int col){

        if(this.table[row][col].isMine()){
            return false;
        }

        this.table[row][col].reveal();

        if(this.table[row][col].getMines()==0) {
            for (int i = row - 1; i <= row + 1; i++) {
                for (int j = col - 1; j <= col + 1; j++) {
                    if ((i != row || j != col) && checkIndices(i, j) && !this.table[i][j].isMine()) {
                        this.table[i][j].reveal();
                        if (this.table[i][j].getMines() == 0) {
                            this.reveal(i, j);
                        }
                    }
                }
            }
        }

        this.started=true;
        return true;
    }

    public int getRows() {
        return this.rows;
    }

    public int getCols() {
        return this.cols;
    }

    public boolean isRevealed(int row, int col){
        return this.table[row][col].isRevealed();
    }

    public boolean isStarted() {
        return started;
    }

    private void fillSlot(int row, int col) {

        var mines=0;

        for (int i = row-1; i <=row+1 ; i++) {
            for (int j = col-1; j <=col+1 ; j++) {
                if((i!=row || j!=col) && checkIndices(i,j) && this.table[i][j].isMine()){
                    mines++;
                }
            }
        }

        this.table[row][col].setMines(mines);
    }

    private boolean checkIndices(int i, int j) {
        return i >= 0 && j >= 0 && i <= this.table.length - 1 && j <= this.table[0].length - 1;
    }

    @Override
    public String toString() {
        var sb=new StringBuilder();

        for (int i = 0; i < this.rows; i++) {
            sb.append(Arrays.stream(this.table[i]).map(e->String.valueOf(e.getMines())).collect(Collectors.joining(" ")))
                    .append(System.lineSeparator());
        }

        return sb.toString();
    }
}
