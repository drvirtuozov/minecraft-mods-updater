import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by vlad on 10.10.15.
 */


public class Updater {

    Runnable updateMods = () -> {
        Window.button.setEnabled(false);
        File minecraft = new File(Main.minePath);

        if (minecraft.exists()) {
            File folder = new File(Main.path);

            if (folder.exists()) {
                rmdir(folder);
            }
            else {
                folder.mkdir();
            }

            try {
                try {
                    URL url = new URL(Main.LINK);
                    HttpURLConnection httpConn = null;
                    httpConn = (HttpURLConnection) url.openConnection();
                    int responseCode = httpConn.getResponseCode();

                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        try {
                            download(url, Main.path + "mods.zip");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        try {
                            unzip(Main.path + "mods.zip", Main.path);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        File file = new File(Main.path + "mods.zip");
                        file.delete();
                        Window.button.setText("Done!");
                        Window.label.setText("");
                    }
                    else {
                        Window.label.setText("Error. Server replied HTTP code: " + responseCode);
                        Window.button.setEnabled(true);
                        Window.button.setText("Update mods");
                    }
                    httpConn.disconnect();
                } catch (UnknownHostException e) {
                    Window.label.setText("Error. Check your internet connection.");
                    Window.button.setEnabled(true);
                    Window.button.setText("Update mods");
                } catch (SocketException e) {
                    Window.label.setText("Error. Check your internet connection.");
                    Window.button.setEnabled(true);
                    Window.button.setText("Update mods");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            Window.button.setText("Minecraft is not installed!");
            Window.button.setEnabled(true);
        }
    };

    public void rmdir(File folder) {
        Window.label.setText("Deleting old mods...");
        Window.button.setText("Please wait...");
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
        Window.label.setText("Downloading new mods...");
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
        Window.label.setText("Extracting...");
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

    public void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
        byte[] bytesIn = new byte[2048];
        int read = 0;

        while ((read = zipIn.read(bytesIn)) != -1) {
            bos.write(bytesIn, 0, read);
        }
        bos.close();
    }
}
