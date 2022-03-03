import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

public class Main {
    public static void main(String[] args) {
        var game=Table.SMALL;
        game.startGame();
        System.out.println(game);
    }
}
