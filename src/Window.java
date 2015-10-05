import sun.security.util.ObjectIdentifier;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

/**
 * Created by vlad on 05.10.15.
 */
public class Window extends JFrame{
    String path;
    String username = System.getProperty("user.name");
    String link = "https://github.com/drvirtuozov/minecraft-client-mods-1710/archive/master.zip";

    JButton b1;
    EventHandler handler = new EventHandler();

    public Window(String s){
        super(s);
        setLayout(new FlowLayout());
        b1 = new JButton("Update mods");
        add(b1);
        b1.addActionListener(handler);
    }

    public class EventHandler implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            if(e.getSource() == b1) {
                updateMods();
            }
        }

    }

    public void updateMods() {
        if (System.getProperty("os.name").toLowerCase() == "windows") {
            path = "C:\\Users\\" + username + "\\AppData\\Roaming\\.minecraft\\mods\\";
        }
        else {
            path = "/home/" + username + "/.minecraft/mods/";
        }

        b1.setEnabled(false);
        File folder = new File(path);
        rmdir(folder);

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

    public void rmdir(File folder) {
        if (folder.exists()) {
            b1.setText("Deleting old mods...");
            File[] list = folder.listFiles();
            for (int i = 0; i < list.length; i++) {
                File tmpF = list[i];
                if (tmpF.isDirectory()) {
                    rmdir(tmpF);
                }
                list[i].delete();
            }
        }
        else {
            b1.setText("Minecraft not found.");
        }
    }

    public void download(String destinationFile) throws IOException {
        b1.setText("Downloading new mods...");
        URL url = new URL(link);
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
            String entryname = entry.getName();
            String filePath = destDirectory + entryname.substring(entryname.lastIndexOf("/") + 1, entryname.length());
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
        b1.setEnabled(true);
        b1.setText("Done!");
    }
}
