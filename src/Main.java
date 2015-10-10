import javax.swing.JFrame;


public class Main {

    public static String path, minePath;
    private static final String USERNAME = System.getProperty("user.name");
    protected static final String OSNAME = System.getProperty("os.name").toLowerCase();
    public static final String LINK = "https://github.com/drvirtuozov/minecraft-client-mods-1710/archive/master.zip";

    public static void main(String args[]) {
        if (OSNAME.contains("linux")) {
            path = "/home/" + USERNAME + "/.minecraft/mods/";
            minePath = "/home/" + USERNAME + "/.minecraft/";
        }
        if (OSNAME.contains("windows")) {
            path = "C:\\Users\\" + USERNAME + "\\AppData\\Roaming\\.minecraft\\mods\\";
            minePath = "C:\\Users\\" + USERNAME + "\\AppData\\Roaming\\.minecraft\\";
        }
        if (OSNAME.contains("mac")) {
            path = "/Users/" + USERNAME + "/Library/Application Support/minecraft/mods/";
            minePath = "/Users/" + USERNAME + "/Library/Application Support/minecraft/";
        }

        Window w = new Window("Minecraft Mods Updater");
        w.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        w.setSize(250, 100);
        w.setResizable(false);
        w.setLocationRelativeTo(null);
        w.setVisible(true);
    }
}
