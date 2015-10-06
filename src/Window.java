import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by vlad on 05.10.15.
 */

public class Window extends JFrame{
    String path, minePath;
    static String USERNAME = System.getProperty("user.name");
    static String OSNAME = System.getProperty("os.name").toLowerCase();
    static String LINK = "https://github.com/drvirtuozov/minecraft-client-mods-1710/archive/master.zip";

    JButton b1;
    EventHandler handler = new EventHandler();

    public Window(String s){
        super(s);
        setLayout(new FlowLayout());
        b1 = new JButton("Update mods");
        add(b1);
        b1.addActionListener(handler);


        if (OSNAME == "windows") {
            path = "C:\\Users\\" + USERNAME + "\\AppData\\Roaming\\.minecraft\\mods\\";
            minePath = "C:\\Users\\" + USERNAME + "\\AppData\\Roaming\\.minecraft\\";
        }
        else {
            path = "/home/" + USERNAME + "/.minecraft/mods/";
            minePath = "/home/" + USERNAME + "/.minecraft/";
        }

    }

    public class EventHandler implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            if(e.getSource() == b1) {
                updateMods();
            }
        }

    }

    public void updateMods() {
        b1.setEnabled(false);
        File minecraft = new File(minePath);

        if (minecraft.exists()) {
            File folder = new File(path);

            if (folder.exists()) {
                rmdir(folder);
            }
            else {
                folder.mkdir();
            }

            try {
                download(path + "mods.zip");
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                unzip(path + "mods.zip", path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            b1.setText("Minecraft is not installed!");
            b1.setEnabled(true);
        }
    }

    public void rmdir(File folder) {
        b1.setText("Deleting old mods...");
        File[] list = folder.listFiles();
        for (int i = 0; i < list.length; i++) {
            File tempFile = list[i];
            if (tempFile.isDirectory()) {
                rmdir(tempFile);
            }
            list[i].delete();
        }
    }

    public void download(String destinationFile) throws IOException {
        b1.setText("Downloading new mods...");
        URL url = new URL(LINK);
        InputStream is = url.openStream();
        OutputStream os = new FileOutputStream(destinationFile);

        byte[] b = new byte[2048];
        int length;

        while ((length = is.read(b)) != -1) {
            os.write(b, 0, length);
        }

        is.close();
        os.close();
    }

    public void unzip(String zipFilePath, String destDirectory) throws IOException {
        ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFilePath));
        ZipEntry entry = zipIn.getNextEntry();

        while (entry != null) {
            String entryName = entry.getName();
            String filePath = destDirectory + entryName.substring(entryName.lastIndexOf("/") + 1, entryName.length());
            if (!entry.isDirectory()) {
                extractFile(zipIn, filePath);
            }
            zipIn.closeEntry();
            entry = zipIn.getNextEntry();
        }
        zipIn.close();
    }

    private void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
        byte[] bytesIn = new byte[2048];
        int read = 0;

        while ((read = zipIn.read(bytesIn)) != -1) {
            bos.write(bytesIn, 0, read);
        }
        bos.close();

        File file = new File(path + "mods.zip");
        file.delete();
        b1.setText("Done!");
    }


}
