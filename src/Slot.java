public class Slot {
    private int mines;
    private boolean revealed;
    private boolean flagged;
    private boolean isMine;

    public Slot(int mines) {
        this.mines = mines;
        isMine=this.mines==-1;
        this.flagged=false;
        this.revealed=false;
    }

    public void reveal(){
        this.flagged=false;
        this.revealed=true;
    }

    public void flag(){
        if(this.revealed){
            throw new IllegalStateException("The slot is already revealed!");
        }
        this.flagged=true;
    }

    public void unflag(){
        if(this.revealed || !this.flagged){
            throw new IllegalStateException("The slot is either revealed or unflagged");
        }
        this.flagged=false;
    }


    public int getMines() {
        return mines;
    }

    public Slot setMines(int mines) {
        this.mines = mines;
        this.isMine=this.mines==-1;
        return this;
    }

    public boolean isRevealed() {
        return revealed;
    }

    public boolean isFlagged() {
        return flagged;
    }

    public boolean isMine() {
        return isMine;
    }
}
