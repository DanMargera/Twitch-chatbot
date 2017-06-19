package irc;

import data.Database;
import data.VotingManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import javax.swing.Timer;
import org.jibble.pircbot.PircBot;
import org.jibble.pircbot.User;
import robot.Nintendo64Interpreter;
import robot.SnesInterpreter;
import twitch.plays.TwitchPlays;

/**
 * Bot que conecta no IRC da twitch e recebe as msgs do chat.
 * @author Dan
 */
public class TwitchBot extends PircBot {
    
    public static final String CHANNEL = "#twitchplaysyourgame";
    public static final long TIME_LIMIT = 29500000000L; // limite de tempo da twitch pra spam
    
    private int usersonline = 0, minuteCounter = 0;
    LinkedList<Long> sentMsgTimeStamps;
    Database database;
    VotingManager vm;
    ArrayList<String> operators;
    
    SnesInterpreter snesInter;
    Nintendo64Interpreter n64inter;
    public boolean interpreterOn = true, votingOn = false;
    public int currentInterpreter = 0;
    private static final int N64 = 0, SNES = 1;
    
    public TwitchBot() {
        setName("TwitchPlaysYourGame");
        n64inter = new Nintendo64Interpreter();
        snesInter = new SnesInterpreter();
        database = Database.load();
        operators = new ArrayList<>();
        operators.add("twitchplaysyourgame");
        operators.add("terenzi94");
        operators.add("newkrab");
        operators.add("ankrider");
        operators.add("xlusty");
        
        sentMsgTimeStamps = new LinkedList<>();
        
        ActionListener everyMinute = (ActionEvent evt) -> { // Registra no console a cada minuto quem está online.
            User[] users = getUsers(CHANNEL);
            StringBuilder strB = new StringBuilder();
            for (User u : users) {
                database.addPoints(u, 1);
                strB.append(u.toString());
                strB.append(", ");
            }
            usersonline = users.length;
            TwitchPlays.console.append(strB.toString()+users.length+" users.");
            minuteCounter++;
            if (minuteCounter > 59) {
                database.save();
                minuteCounter = minuteCounter%60;
            }
        };
        
        Timer t = new Timer(60000, everyMinute);
        t.start();
    }
    
    /**
     * Método que é chamado quando alguem entra no chat.
     */
    @Override
    public void onJoin(String channel, String sender, String login, String hostname) {
        database.putUser(sender);
    }
    
    /**
     * Método que é chamado quando uma mensagem é recebida no chat.
     */
    @Override
    public void onMessage(String channel, String sender, String login, String hostname, String message) {
        if (message.startsWith("!")) {
            channelCommand(sender, message);
            return;
        }
        
        if (votingOn && message.length() == 1) {
            vm.vote(sender, message);
        }
        
        if (!interpreterOn)
            return;
        
        if (currentInterpreter == N64) {
            if (message.startsWith("1"))
                n64inter.parsePlayerCommand(0, message.substring(1));

            else if (message.startsWith("2"))
                n64inter.parsePlayerCommand(1, message.substring(1));

            else if (message.startsWith("3"))
                n64inter.parsePlayerCommand(2, message.substring(1));

            else if (message.startsWith("4"))
                n64inter.parsePlayerCommand(3, message.substring(1));

            else
                n64inter.parsePlayerCommand(message);
        }
        else if (currentInterpreter == SNES) {
            if (message.startsWith("1"))
                snesInter.parsePlayerCommand(0, message.substring(1));

            else if (message.startsWith("2"))
                snesInter.parsePlayerCommand(1, message.substring(1));

            else
                snesInter.parsePlayerCommand(message);
        }
    }
    
    /**
     * Recebe mensagens do chat que começam com ! e tenta resolver o comando.
     * @param cmd a command like !help
     */
    private void channelCommand(String sender, String cmd) {
        boolean isMod = operators.contains(sender);
        
        switch (cmd) {
            case "!controls":
            case "!help":
                if (currentInterpreter == N64)
                    sendMsg("http://i.imgur.com/QyKkbxe.png");
                else if (currentInterpreter == SNES)
                    sendMsg("http://i.imgur.com/QyKkbxe.png");
                return;
            case "!commands":
                if (currentInterpreter == N64)
                    sendMsg("!help !savestate !loadstate !projects !request !points !givepoints !userpoints !rngsus !stars "
                            + "---- up down left right ---- jump (j) longjump (lj) hipdrop (hd) jumpattack (ja) ----");
                else if (currentInterpreter == SNES)
                    sendMsg("!help !savestate !loadstate !projects !request !points !givepoints !userpoints !rngsus !stars "
                            + "---- up down left right jump (j) ----");
                return;
            case "!points":
                sendMsg(sender+", you have "+database.getUserPoints(sender)+" points.");
                return;
            case "!projects":
                sendMsg("We are still defining the next project, give us suggestions and stay tuned! :D");
                return;
            case "!stars":
                sendMsg("https://docs.google.com/spreadsheets/d/1lCd0H-D6L0s1N7roGQ8OIJghexsZupdPOcalFkTEi1M/edit#gid=0");
                return;
            case "!savestate":
                if (interpreterOn && currentInterpreter == N64) {
                    if (isMod)
                        n64inter.saveState();
                    else if (database.spendPoints(sender, 50))
                        n64inter.saveState();
                    else
                        sendMsg(sender+", you don't have 50 points to save state.");
                }
                else if (interpreterOn && currentInterpreter == SNES) {
                    if (isMod)
                        snesInter.saveState();
                    else if (database.spendPoints(sender, 25))
                        snesInter.saveState();
                    else
                        sendMsg(sender+", you don't have 25 points to save state.");
                }
                return;
            case "!loadstate":
                if (interpreterOn && currentInterpreter == N64) {
                    if (isMod)
                        n64inter.loadState();
                    else if (database.spendPoints(sender, 20))
                        n64inter.loadState();
                    else
                        sendMsg(sender+", you don't have 20 points to load state.");
                }
                else if (interpreterOn && currentInterpreter == SNES) {
                    if (isMod)
                        snesInter.loadState();
                    else if (database.spendPoints(sender, 10))
                        snesInter.loadState();
                    else
                        sendMsg(sender+", you don't have 10 points to load state.");
                }
                return;
        }
        
        if (cmd.startsWith("!givepoints")) {
            String[] value = cmd.split(" ");
            if (value.length != 3) {
                sendMsg("Correct usage: !givepoints user quantity");
            }
            try {
                int points = Integer.parseInt(value[2]);
                if (points<0)
                    return;
                if (database.spendPoints(sender, points))
                    database.addPoints(value[1].toLowerCase(), points);
            } catch (NumberFormatException nFE) {
                System.out.println(nFE);
            }
            return;
        }
        else if (cmd.startsWith("!userpoints")) {
            String[] temp = cmd.split(" ");
            if (temp.length == 2) {
                sendMsg(temp[1]+" has "+database.getUserPoints(temp[1].toLowerCase())+" points.");
            }
        }
        else if (cmd.startsWith("!request")) {
            if (cmd.length()<10) {
                sendMsg("Correct usage: !request <your request here>");
                return;
            }
            String value = cmd.substring(9);
            sendMsg("/w ankrider "+sender+" requested "+value);
        }
        else if (cmd.startsWith("!rngsus")) {
            String[] temp = cmd.split(" ");
            if (temp.length == 2) {
                try {
                    int points = Integer.parseInt(temp[1]);
                    if (points<3)
                        return;
                    if (database.spendPoints(sender, points)) {
                        sendMsg("You have offered your points to RNGSUS!");
                        for (int i=0;i<points/3;i++) {
                            if (currentInterpreter == N64)
                                n64inter.randomCommand();
                            else if (currentInterpreter == SNES)
                                snesInter.randomCommand();
                        }
                    }
                } catch (Exception ex) {
                    System.out.println(ex);
                }
            }
            else
                sendMsg("Usage: !rngsus <points>");
        }
        
        if (isMod) {
            if (cmd.startsWith("!loadbackupstate")) {
                String[] temp = cmd.split(" ");
                if (temp.length == 2) {
                    try {
                        int backupNumber = Integer.parseInt(temp[1]);
                        if (backupNumber>0 && backupNumber<21 && currentInterpreter == N64)
                            n64inter.backupLoadState(backupNumber);
                    } catch (Exception ex) {
                        System.out.println(ex);
                    }
                }
            }
            else if (cmd.startsWith("!loadgame")) {
                interpreterOn = false;
                
                if (currentInterpreter == N64) {
                    n64inter.releaseAll();

                    if (cmd.contains("oot")) {
                        n64inter.changeGame("1");
                    }
                    else if (cmd.contains("majora")) {
                        n64inter.changeGame("2");
                    }
                    else if (cmd.contains("party")) {
                        n64inter.changeGame("5");
                    }
                    else if (cmd.contains("kart")) {
                        n64inter.changeGame("6");
                    }
                    else if (cmd.contains("mario")) {
                        n64inter.changeGame("3");
                    }
                    else if (cmd.contains("smash")) {
                        n64inter.changeGame("4");
                    }
                    else if (cmd.contains("pokemon")) {
                        n64inter.changeGame("7");
                    }
                }

                interpreterOn = true;
            }
            else if (cmd.startsWith("!addpoints")) {
                String[] value = cmd.split(" ");
                if (value.length != 3) {
                    sendMsg("/w "+sender+" Usage: !addpoints <user> <quantity>");
                }
                try {
                    int points = Integer.parseInt(value[2]);
                    database.addPoints(value[1].toLowerCase(), points);
                } catch (NumberFormatException nFE) {
                    System.out.println(nFE);
                }
            }
            else if (cmd.startsWith("!startvote")) {
                if (currentInterpreter == N64) {
                    interpreterOn = false;
                    n64inter.releaseAll();
                    vm = new VotingManager(this);
                    n64inter.getRobot().keyStroke(KeyEvent.VK_HOME);
                    vm.startVoting();
                    votingOn = true;
                    sendMsg("A voting has started, the results will be announced in 70 seconds! PogChamp");
                }
            }
            else if (cmd.startsWith("!status")) {
                sendMsg("/w "+sender+" "+usersonline+" users on. Interpreter is set to "+interpreterOn);
            }
            else if (cmd.startsWith("!interpreter")) {
                String[] value = cmd.split(" ");
                if (value.length != 2) {
                    sendMsg("Usage: !interpreter on/off");
                    return;
                }
                switch (value[1]) {
                    case "on":
                        interpreterOn = true;
                        break;
                    case "off":
                        interpreterOn = false;
                        break;
                }
            }
            else if (cmd.startsWith("!snes")) {
                if (currentInterpreter == N64) {
                    interpreterOn = false;
                    n64inter.releaseAll();
                    currentInterpreter = SNES;
                    interpreterOn = true;
                }
            }
            else if (cmd.startsWith("!n64")) {
                if (currentInterpreter == SNES) {
                    interpreterOn = false;
                    snesInter.releaseAll();
                    currentInterpreter = N64;
                    interpreterOn = true;
                }
            }
            else if (cmd.startsWith("!mod")) {
                String[] value = cmd.split(" ");
                if (value.length != 2) {
                    sendMsg("Usage: !mod <user>");
                    return;
                }
                sendMsg("/mod "+value[1]);
            }
            else if (cmd.startsWith("!unmod")) {
                String[] value = cmd.split(" ");
                if (value.length != 2) {
                    sendMsg("Usage: !mod <user>");
                    return;
                }
                sendMsg("/unmod "+value[1]);
            }
            else if (cmd.startsWith("!ban")) {
                String[] value = cmd.split(" ");
                if (value.length != 2) {
                    sendMsg("Usage: !ban <user>");
                    return;
                }
                sendMsg("/ban "+value[1]);
            }
            else if (cmd.startsWith("!unban")) {
                String[] value = cmd.split(" ");
                if (value.length != 2) {
                    sendMsg("Usage: !unban <user>");
                    return;
                }
                sendMsg("/unban "+value[1]);
            }
            else if (cmd.startsWith("!echo")) {
                String value = cmd.substring(6);
                sendMsg(value);
            }
            else if (cmd.startsWith("!addop")) {
                String[] value = cmd.split(" ");
                if (value.length != 2) {
                    sendMsg("Usage: !addop <user>");
                    return;
                }
                operators.add(value[1].toLowerCase());
            }
            else if (cmd.startsWith("!removeop")) {
                String[] value = cmd.split(" ");
                if (value.length != 2) {
                    sendMsg("Usage: !removeop <user>");
                    return;
                }
                operators.remove(value[1].toLowerCase());
            }
            else if (cmd.startsWith("!shutdown") && sender.equals("ankrider")) {
                if (cmd.contains("-s")) {
                    interpreterOn = false;
                    n64inter.getRobot().keyStroke(KeyEvent.VK_END);
                    shutdown();
               }
                else if (cmd.contains("-h")) {
                    Runtime runtime = Runtime.getRuntime();
                    try {
                        Process proc = runtime.exec("shutdown -s -t 0");
                    } catch (IOException ex) {
                        TwitchPlays.console.append(ex.toString());
                    }
                    System.exit(0);
                }
                else
                    sendMsg("/w ankrider Use -s to soft shutdown or -h to shutdown server hardware.");
            }
        }
    }
    
    /**
     * Faz o controle do limite de mensagens da Twitch
     * antes de usar o sendMessage.
     * @param message 
     */
    private void sendMsg(String message) {
        if (message.charAt(0) != '/' && sentMsgTimeStamps.size()>=98) {
            if (System.nanoTime()-sentMsgTimeStamps.getFirst() <= TIME_LIMIT) {
                return;
            }
        }
        
        sentMsgTimeStamps.addLast(System.nanoTime());
        if (sentMsgTimeStamps.size() > 98)
            sentMsgTimeStamps.removeFirst();
        sendMessage(CHANNEL, message);
    }
    
    /**
     * Método chamado quando o bot é desconectado.
     * Tenta reconectar a cada 2 minutos.
     */
    @Override
    public void onDisconnect() {
        boolean connected = false;
        while (!connected) {
            TwitchPlays.console.append("Disconnected, trying to reconnect in 2 minutes.");
            try {
                Thread.sleep(120000);
            } catch (Exception ex) {
                System.out.println(ex);
            }
            try {
                connect("irc.twitch.tv", 6667, "oauth:3r7b0i1ahps0lyxmj292apl6o8y2n3");
                sendRawLineViaQueue("CAP REQ :twitch.tv/membership");
                joinChannel("#twitchplaysyourgame");
                TwitchPlays.console.append("Reconnected.");
                connected = true;
            } catch (Exception ex) {
                TwitchPlays.console.append(ex.toString());
            }
        }
    }
    
    /**
     * Ativa/desativa o interpretador.
     */
    public void toggleInterpreter() {
        interpreterOn = !interpreterOn;
    }
    
    public void setInterpreterOn(boolean i) {
        interpreterOn = i;
    }
    
    /**
     * Termina o processo de votação.
     */
    public void closeVoting() {
        votingOn = false;
        int chosenGame = vm.getResults()+1;
        if ((vm.getTotalVotes()>usersonline/3) && (n64inter.currentGame != chosenGame)) {
            sendMsg("The voting has ended, and the winner is number "+chosenGame+"! PogChamp");
            n64inter.changeGame(""+chosenGame);
        }
        else {
            if (n64inter.currentGame == chosenGame)
                sendMsg("The voting has ended, and the winner is number "+chosenGame+"! Kappa");
            else
                sendMsg("There were not enough votes to change the game. FeelsBadMan");
            n64inter.getRobot().keyStroke(KeyEvent.VK_PAGE_UP);
        }
        interpreterOn = true;
    }
    
    /**
     * Método pra ser chamado ao fechar.
     */
    public void shutdown() {
        this.disconnect();
        n64inter.shutdown();
        database.clean();
        database.save();
    }
}
