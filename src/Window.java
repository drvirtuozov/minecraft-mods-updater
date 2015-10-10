import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by vlad on 05.10.15.
 */


public class Window extends JFrame {

    public static JButton button;
    public static JLabel label;
    EventHandler handler = new EventHandler();
    Updater updater = new Updater();

    public Window(String s){
        setTitle(s);
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
        if (Main.OSNAME.contains("windows")) {
            label.setBounds(2, 60, 250, 11);
        }
        else {
            label.setBounds(3, 61, 250, 11);
        }
        label.setFont(new Font("Arial", Font.PLAIN, 11));

        add(button);
        add(label);
    }

    public class EventHandler implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if(e.getSource() == button) {
                Thread t1 = new Thread(updater.updateMods);
                t1.start();
            }
        }
    }
}
