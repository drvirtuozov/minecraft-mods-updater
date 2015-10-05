import javax.swing.*;

public class Main {

    public static void main(String args[]) {
        Window w = new Window("Minecraft Mods Updater");
        w.setVisible(true);
        w.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        w.setSize(250, 100);
        w.setResizable(false);
        w.setLocationRelativeTo(null);
    }

}
