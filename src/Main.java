import javax.swing.*;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

public class Main {
    public static void main(String[] args) {

        var gui_obj = new GUI();

        gui_obj.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gui_obj.setSize(575, 200);
        gui_obj.setVisible(true);
    }
}
