package twitch.plays;

import irc.TwitchBot;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * 
 * @author Dan
 */
public class TwitchPlays extends JFrame implements WindowListener {
    
    public static JTextArea console;
    TwitchBot bot;
    
    public TwitchPlays(String key) {
        // Configurando uma interface simples só para o console
        super("Twitch Plays");
        setPreferredSize(new Dimension(300, 200));
        setLocationRelativeTo(null);
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        JPanel pan = new JPanel();
        JButton intButton = new JButton("Interpreter: On");
        intButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                bot.toggleInterpreter();
                if (bot.interpreterOn)
                    intButton.setText("Interpreter: On");
                else
                    intButton.setText("Interpreter: Off");
            }
        });
        console = new JTextArea(){
            @Override
            public void append(String str) {
                super.append(str+"\n");
                if (getLineCount()>100)
                    replaceRange("", 0, getText().indexOf("\n"));
                setCaretPosition(getText().length() - 2);
            }
        };
        console.setEditable(false);
        console.setWrapStyleWord(true);
        JTextField jtf = new JTextField();
        jtf.setPreferredSize(new Dimension(300, 20));
        jtf.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                bot.onMessage("#twitchplaysyourgame", "twitchplaysyourgame", "", "", jtf.getText());
                jtf.setText("");
            }
        });
        pan.setPreferredSize(new Dimension(320,265));
        setPreferredSize(new Dimension(320,265));
        JScrollPane jsp = new JScrollPane(console);
        jsp.setPreferredSize(new Dimension(300,160));
        pan.add(jsp);
        pan.add(intButton);
        pan.add(jtf);
        add(pan);
        addWindowListener(this);
        pack();
        
        // Configura o bot pra conectar no IRC
        bot = new TwitchBot();
        bot.setVerbose(true);
        try {
            bot.connect("irc.twitch.tv", 6667, key);
        } catch (Exception ex) {
            System.out.println(ex);
            return;
        }
        bot.sendRawLineViaQueue("CAP REQ :twitch.tv/membership");
        bot.joinChannel("#twitchplaysyourgame");
    }

    public static void main(String[] args) throws InterruptedException {
        String key = JOptionPane.showInputDialog("Input Auth Key:");
        new TwitchPlays(key);
    }

    @Override
    public void windowOpened(WindowEvent e) {
    }

    @Override
    public void windowClosing(WindowEvent e) {
        bot.shutdown();
    }

    @Override
    public void windowClosed(WindowEvent e) {
    }

    @Override
    public void windowIconified(WindowEvent e) {
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
    }

    @Override
    public void windowActivated(WindowEvent e) {
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
    }
}