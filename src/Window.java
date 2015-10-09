import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
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
    JButton button;
    JLabel label;
    EventHandler handler = new EventHandler();

    public Window(String s){
        super(s);
        setLayout(null);
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        button = new JButton("Update mods");
        button.setFocusPainted(false);
        button.setBounds(50, 10, 150, 50);
        button.addActionListener(handler);

        label = new JLabel();
        if (OSNAME.contains("windows")) {
            label.setBounds(2, 60, 250, 11);
        }
        else {
            label.setBounds(3, 61, 250, 11);
        }
        label.setFont(new Font("Arial", Font.PLAIN, 11));

        add(button);
        add(label);

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
    }

    public class EventHandler implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if(e.getSource() == button) {
                Thread t1 = new Thread(updateMods);
                t1.start();
            }
        }
    }

    Runnable updateMods = new Runnable() {
        public void run() {
            button.setEnabled(false);
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
                    try {
                        URL url = new URL(LINK);
                        HttpURLConnection httpConn = null;
                        httpConn = (HttpURLConnection) url.openConnection();
                        int responseCode = httpConn.getResponseCode();

                        if (responseCode == HttpURLConnection.HTTP_OK) {
                            try {
                                download(url, path + "mods.zip");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            try {
                                unzip(path + "mods.zip", path);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            File file = new File(path + "mods.zip");
                            file.delete();
                            button.setText("Done!");
                            label.setText("");
                        } else {
                            label.setText("Error. Server replied HTTP code: " + responseCode);
                            button.setEnabled(true);
                            button.setText("Update mods");
                        }
                        httpConn.disconnect();
                    } catch (UnknownHostException e) {
                        label.setText("Error. Check your internet connection.");
                        button.setEnabled(true);
                        button.setText("Update mods");
                    } catch (SocketException e) {
                        label.setText("Error. Check your internet connection.");
                        button.setEnabled(true);
                        button.setText("Update mods");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else {
                button.setText("Minecraft is not installed!");
                button.setEnabled(true);
            }
        }
    };

    public void rmdir(File folder) {
        label.setText("Deleting old mods...");
        button.setText("Please wait...");
        File[] list = folder.listFiles();
        for (int i = 0; i < list.length; i++) {
            File tempFile = list[i];
            if (tempFile.isDirectory()) {
                rmdir(tempFile);
            }
            list[i].delete();
        }
    }

    public void download(URL url, String destinationFile) throws IOException {
        label.setText("Downloading new mods...");
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
        label.setText("Extracting...");
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
    }

}
