package robot;

import com.sun.glass.events.KeyEvent;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import twitch.plays.TwitchPlays;

/**
 * Classe usada para interpretar os comandos(strings) do chat
 * e converter pra ações no mario party usando o robot.
 * @author Dan
 */
public class Nintendo64Interpreter {
    
    /**
     * Jogos de N64.
     */
    public static final int OOT = 1, MAJORAS = 2, MARIO = 3, SMASH = 4, MARIOPARTY = 5, MARIOKART = 6, POKESTADIUM = 7;
    public int currentGame = 3;
    
    /**
     * Botões do controle de nintendo64.
     */
    private static final int A=0, B=1, Z=2, R=3, L=4, UP=5, DOWN=6, LEFT=7, RIGHT=8, C_UP=9, C_DOWN=10, C_LEFT=11, C_RIGHT=12, START=13;
    
    /**
     * Usado pra salvar backups dos savestates.
     */
    int saveCounter = 0;
    public static final String SAVESTATE_DIR = "C:\\Users\\DAN\\Desktop\\TwitchPlays\\Project64\\Save\\";
    public static final String BACKUP_DIR = "C:\\Users\\DAN\\Desktop\\TwitchPlays\\SaveBackup\\";
    
    /**
     * Robot utilizado pra controlar mouse/teclado.
     */
    SmartRobot robot;
    
    /**
     * Serviço usado pra executar as ordens do robot em paralelo com uma Thread Pool.
     */
    ExecutorService exeService;
    
    /**
     * Mapa de teclas usadas como controles no jogo.
     * Uso: keymap[player][action].
     */
    int[][] keymap;
    
    boolean[] isSpinning; // quais controles tão fazendo um spin
    boolean[][] isHeld; // quais botões tão sendo segurados
    
    public Nintendo64Interpreter() {
        robot = new SmartRobot();
        isSpinning = new boolean[4];
        keymap = new int[4][];
        isHeld = new boolean[4][14];
        
        // Cria uma thread pool com 3 threads.
        exeService = Executors.newFixedThreadPool(3);
        
        // define as teclas de 1 a 9 como controle do player1
        int[] p1keys = {KeyEvent.VK_1, KeyEvent.VK_2, KeyEvent.VK_3, KeyEvent.VK_4,
            KeyEvent.VK_5, KeyEvent.VK_6, KeyEvent.VK_7, KeyEvent.VK_8, KeyEvent.VK_9,
            KeyEvent.VK_EQUALS, KeyEvent.VK_BACKSPACE, KeyEvent.VK_P, KeyEvent.VK_SUBTRACT, KeyEvent.VK_ENTER};
        keymap[0] = p1keys;
        
        // define as teclas de Q a O como controle do player2
        int[] p2keys = {KeyEvent.VK_Q, KeyEvent.VK_W, KeyEvent.VK_E, KeyEvent.VK_R,
            KeyEvent.VK_T, KeyEvent.VK_Y, KeyEvent.VK_U, KeyEvent.VK_I, KeyEvent.VK_O,
            KeyEvent.VK_0, KeyEvent.VK_MINUS, KeyEvent.VK_ADD, KeyEvent.VK_QUOTE, KeyEvent.VK_NUMPAD1};
        keymap[1] = p2keys;
        
        // define as teclas de A a L como controle do player3
        int[] p3keys = {KeyEvent.VK_A, KeyEvent.VK_S, KeyEvent.VK_D, KeyEvent.VK_F,
            KeyEvent.VK_G, KeyEvent.VK_H, KeyEvent.VK_J, KeyEvent.VK_K, KeyEvent.VK_L,
            KeyEvent.VK_NUMPAD5, KeyEvent.VK_NUMPAD6, KeyEvent.VK_NUMPAD7, KeyEvent.VK_NUMPAD8, KeyEvent.VK_NUMPAD2};
        keymap[2] = p3keys;
        
        // define as teclas abaixo como controle do player4
        int[] p4keys = {KeyEvent.VK_Z, KeyEvent.VK_X, KeyEvent.VK_C, KeyEvent.VK_V,
            KeyEvent.VK_B, KeyEvent.VK_N, KeyEvent.VK_M, KeyEvent.VK_SPACE, KeyEvent.VK_NUMPAD4,
            KeyEvent.VK_NUMPAD0, KeyEvent.VK_NUMPAD9, KeyEvent.VK_PERIOD, KeyEvent.VK_DIVIDE, KeyEvent.VK_NUMPAD3};
        keymap[3] = p4keys;
    }
    
    /**
     * Comandos com indicação de um player e uma ação.
     * @param player
     * @param command 
     */
    public void parsePlayerCommand(int player, String command) {
        if (command.equals("spin")) {
            playerAnalog360(player);
            return;
        }
        else if (command.equals("start")) {
            playerButtonTap(player, START);
            return;
        }
        for (int i=0;i<command.length();i++) {
            switch (command.charAt(i)) {
                case 'h':
                    i++;
                    if (i<command.length())
                        switch (command.charAt(i)) {
                            case 'c':
                                i++;
                                if (i<command.length())
                                    switch (command.charAt(i)) {
                                        case '8':
                                            playerButtonToggleHold(player, C_UP);
                                            break;
                                        case '2':
                                        case '5':
                                            playerButtonToggleHold(player, C_DOWN);
                                            break;
                                        case '4':
                                            playerButtonToggleHold(player, C_LEFT);
                                            break;
                                        case '6':
                                            playerButtonToggleHold(player, C_RIGHT);
                                            break;
                                    }
                                break;
                            case 'a':
                                playerButtonToggleHold(player, A);
                                break;
                            case 'b':
                                playerButtonToggleHold(player, B);
                                break;
                            case 'z':
                                playerButtonToggleHold(player, Z);
                                break;
                            case 'r':
                                playerButtonToggleHold(player, R);
                                break;
                            case 'l':
                                playerButtonToggleHold(player, L);
                                break;
                            case '8':
                                playerButtonToggleHold(player, UP);
                                break;
                            case '2':
                            case '5':
                                playerButtonToggleHold(player, DOWN);
                                break;
                            case '4':
                                playerButtonToggleHold(player, LEFT);
                                break;
                            case '6':
                                playerButtonToggleHold(player, RIGHT);
                                break;
                        }
                    break;
                case 'c':
                    i++;
                    if (i<command.length())
                        switch (command.charAt(i)) {
                            case '8':
                                playerButtonTap(player, C_UP);
                                break;
                            case '2':
                            case '5':
                                playerButtonTap(player, C_DOWN);
                                break;
                            case '4':
                                playerButtonTap(player, C_LEFT);
                                break;
                            case '6':
                                playerButtonTap(player, C_RIGHT);
                                break;
                        }
                    break;
                case 'j':
                    playerButtonHold(player, A, 5);
                case 'a':
                    playerButtonTap(player, A);
                    break;
                case 'b':
                    playerButtonTap(player, B);
                    break;
                case 'z':
                    playerButtonTap(player, Z);
                    break;
                case 'r':
                    playerButtonTap(player, R);
                    break;
                case 'l':
                    playerButtonTap(player, L);
                    break;
                case 'f':
                case 'u':
                case '8':
                    if (i>1)
                        playerButtonLongTap(player, UP);
                    else
                        playerButtonTap(player, UP);
                    break;
                case 'd':
                case '2':
                case '5':
                    if (i>1)
                        playerButtonLongTap(player, DOWN);
                    else
                        playerButtonTap(player, DOWN);
                    break;
                case '4':
                    if (i>1)
                        playerButtonLongTap(player, LEFT);
                    else
                        playerButtonTap(player, LEFT);
                    break;
                case '6':
                    if (i>1)
                        playerButtonLongTap(player, RIGHT);
                    else
                        playerButtonTap(player, RIGHT);
                    break;
            }
        }
    }
    
    /**
     * Comandos pra jogos single player em geral,
     * que são passados sem indicação de qual player.
     * @param command 
     */
    public void parsePlayerCommand(String command) {
        int p = 0;
        switch (currentGame) {
            case OOT:
                switch (command) {
                    case "ja":
                    case "jumpattack":
                        playerButtonSequence(p, 150, Z, A);
                        break;
                    case "forward":
                    case "u":
                    case "up":
                        playerButtonHold(p, UP, 4);
                        break;
                    case "back":
                    case "d":
                    case "down":
                        playerButtonHold(p, DOWN, 4);
                        break;
                    case "left":
                        playerButtonHold(p, LEFT, 4);
                        break;
                    case "right":
                        playerButtonHold(p, RIGHT, 4);
                        break;
                }
                break;
            case MAJORAS:
                switch (command) {
                    case "ja":
                    case "jumpattack":
                        playerButtonSequence(p, 150, Z, A);
                        break;
                    case "forward":
                    case "u":
                    case "up":
                        playerButtonHold(p, UP, 4);
                        break;
                    case "back":
                    case "d":
                    case "down":
                        playerButtonHold(p, DOWN, 4);
                        break;
                    case "left":
                        playerButtonHold(p, LEFT, 4);
                        break;
                    case "right":
                        playerButtonHold(p, RIGHT, 4);
                        break;
                }
                break;
            case MARIO:
                switch (command) {
                    case "j":
                    case "jump":
                        playerButtonHold(p, A, 5);
                        break;
                    case "atk":
                    case "attack":
                        playerButtonSequence(p, 350, B, B, B);
                        break;
                    case "forward":
                    case "u":
                    case "up":
                        playerButtonHold(p, UP, 4);
                        break;
                    case "back":
                    case "d":
                    case "down":
                        playerButtonHold(p, DOWN, 4);
                        break;
                    case "left":
                        playerButtonHold(p, LEFT, 4);
                        break;
                    case "right":
                        playerButtonHold(p, RIGHT, 4);
                        break;
                    case "hd":
                    case "hipdrop":
                        playerButtonSequence(p, 350, A, Z);
                        break;
                    case "lj":
                    case "longjump":
                        playerButtonSequence(p, 150, Z, A);
                        break;
                }
                break;
            case SMASH:
                return;
            case MARIOPARTY:
                return;
            case MARIOKART:
                switch (command) {
                    case "go":
                        break;
                }
                break;
            case POKESTADIUM:
                int key;
                switch (command) {
                    case "up":
                        key = KeyEvent.VK_F6;
                        break;
                    case "down":
                        key = KeyEvent.VK_F8;
                        break;
                    case "left":
                        key = KeyEvent.VK_F10;
                        break;
                    case "right":
                        key = KeyEvent.VK_F11;
                        break;
                    default:
                        key = -1;
                }
                if (key != -1) {
                    exeService.execute(() -> { // expressão lambda p/ n usar instancia anonima de um Runnable.
                        try {
                            robot.keyHold(key);
                            Thread.sleep(75);
                            robot.keyRelease(key);
                        }catch(Exception ex) {
                            System.out.println(ex);
                        }
                    });
                }
                break;
        }
        if (command.length() == 1) {
            parsePlayerCommand(p, command.toLowerCase());
        }
    }
    
    /**
     * Realiza uma ação aleatória pro player 1.
     */
    public void randomCommand() {
        double rdm = Math.random();
        double button = Math.random()*(keymap[0].length-5);
        if ((int)button == R) {
            return;
        }
        if (rdm<0.98) {
            playerButtonTap(0, (int)button);
        }
        else {
            playerAnalog360(0);
        }
    }
    
    /**
     * Aperta um botão pra um player.
     * @param player
     * @param button 
     */
    private void playerButtonTap(int player, int button) {
        exeService.execute(() -> { // expressão lambda p/ n usar instancia anonima de um Runnable.
            try {
                robot.keyHold(keymap[player][button]);
                Thread.sleep(60);
                robot.keyRelease(keymap[player][button]);
                isHeld[player][button] = false;
            } catch (InterruptedException ex) {
                System.out.println(ex);
            }
        });
    }
    
    /**
     * Segura um botão por 300ms pra um player.
     * @param player
     * @param button 
     */
    private void playerButtonLongTap(int player, int button) {
        exeService.execute(() -> { // expressão lambda p/ n usar instancia anonima de um Runnable.
            try {
                robot.keyHold(keymap[player][button]);
                Thread.sleep(300);
                robot.keyRelease(keymap[player][button]);
                isHeld[player][button] = false;
            } catch (InterruptedException ex) {
                System.out.println(ex);
            }
        });
    }
    
    /**
     * Segura um botão pelo tempo determinado no parâmetro.
     * @param player
     * @param button
     * @param time time to hold (1 is 100ms)
     */
    private void playerButtonHold(int player, int button, int time) {
        exeService.execute(() -> { // expressão lambda p/ n usar instancia anonima de um Runnable.
            try {
                robot.keyHold(keymap[player][button]);
                Thread.sleep(Math.min(time, 30)*100);
                robot.keyRelease(keymap[player][button]);
                isHeld[player][button] = false;
            } catch (InterruptedException ex) {
                System.out.println(ex);
            }
        });
    }
    
    /**
     * Ativa ou desativa segurar um botão por tempo indeterminado.
     * @param player
     * @param button 
     */
    private void playerButtonToggleHold(int player, int button) {
        if (!isHeld[player][button]) {
            exeService.execute(() -> { // expressão lambda p/ n usar instancia anonima de um Runnable.
                robot.keyHold(keymap[player][button]);
                isHeld[player][button] = true;
            });
        }
        else {
            exeService.execute(() -> { // expressão lambda p/ n usar instancia anonima de um Runnable.
                robot.keyRelease(keymap[player][button]);
                isHeld[player][button] = false;
            });
        }
    }
    
    /**
     * Pressiona botões em sequência, o tempo que cada botão
     * fica pressionado é determinado por interval.
     * @param player
     * @param interval intervalo que cada botão fica pressionado, em ms.
     * @param buttons 
     */
    private void playerButtonSequence(int player, int interval, int... buttons) {
        doButtonSequence(player, interval, buttons, 0, buttons.length);
    }
    
    /**
     * Método recursivo chamado por playerButtonSequence,
     * não chamar esse método diretamente.
     */
    private void doButtonSequence(int player, int interval, int[] buttons, int offset, int length) {
        if (length <= 0) {
            return;
        }
        exeService.execute(() -> { // expressão lambda p/ n usar instancia anonima de um Runnable.
            try {
                robot.keyHold(keymap[player][buttons[offset]]);
                Thread.sleep(interval);
                robot.keyRelease(keymap[player][buttons[offset]]);
                isHeld[player][buttons[offset]] = false;
                doButtonSequence(player, interval, buttons, offset+1, length-1);
            } catch (InterruptedException ex) {
                System.out.println(ex);
            }
        });
    }
    
    /**
     * Gira em 360º o analógico do player.
     */
    private void playerAnalog360(int player) {
        if (!isSpinning[player]) // se ja ta girando ignora o comando
            exeService.execute(() -> {
                try {
                    isSpinning[player] = true;
                    robot.keyHold(keymap[player][UP]);
                    Thread.sleep(30);
                    robot.keyHold(keymap[player][RIGHT]);
                    Thread.sleep(30);
                    robot.keyRelease(keymap[player][UP]);
                    Thread.sleep(30);
                    robot.keyHold(keymap[player][DOWN]);
                    Thread.sleep(30);
                    robot.keyRelease(keymap[player][RIGHT]);
                    Thread.sleep(30);
                    robot.keyHold(keymap[player][LEFT]);
                    Thread.sleep(30);
                    robot.keyRelease(keymap[player][DOWN]);
                    Thread.sleep(30);
                    robot.keyHold(keymap[player][UP]);
                    Thread.sleep(30);
                    robot.keyRelease(keymap[player][LEFT]);
                    Thread.sleep(30);
                    robot.keyRelease(keymap[player][UP]);
                    isSpinning[player] = false;
                } catch (InterruptedException ex) {
                    System.out.println(ex);
                }
            });
    }
    
    public void saveState() {
        exeService.execute(() -> { // expressão lambda p/ n usar instancia anonima de um Runnable.
            try {
                robot.keyHold(KeyEvent.VK_F5);
                Thread.sleep(150);
                robot.keyRelease(KeyEvent.VK_F5);
                Thread.sleep(1500);
                Path target = Paths.get(BACKUP_DIR+saveCounter+""+currentGame);
                Files.copy(getSaveFile(), target, StandardCopyOption.REPLACE_EXISTING);
                saveCounter = (saveCounter+1)%20;
            } catch (Exception ex) {
                System.out.println(ex);
            }
        });
    }
    
    private Path getSaveFile() {
        String fname = "";
        switch (currentGame) {
            case OOT:
                fname = "The Legend of Zelda - Ocarina of Time (U) (V1.0).pj.zip";
                break;
            case MAJORAS:
                fname = "The Legend of Zelda - Majora's Mask (U).pj.zip";
                break;
            case MARIO:
                fname = "Super Mario 64 (U).pj.zip";
                break;
            case SMASH:
                fname = "Super Smash Bros. (U).pj.zip";
                break;
            case MARIOPARTY:
                fname = "Mario Party (U).pj.zip";
                break;
            case MARIOKART:
                fname = "Mario Kart 64 (U).pj.zip";
                break;
            case POKESTADIUM:
                fname = "Pokemon Stadium 2 (U).pj.zip";
                break;
        }
        Path result = Paths.get(SAVESTATE_DIR+fname);
        return result;
    }
    
    public void backupLoadState(int bs) {
        int save = (saveCounter-bs)<0?(saveCounter-bs+20):(saveCounter-bs);
        Path source = Paths.get(BACKUP_DIR+save+""+currentGame);
        try {
            Files.copy(source, getSaveFile(), StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception ex) {
            System.out.println(ex);
        }
        loadState();
    }
    
    public void loadState() {
        exeService.execute(() -> { // expressão lambda p/ n usar instancia anonima de um Runnable.
            try {
                robot.keyHold(KeyEvent.VK_F7);
                Thread.sleep(150);
                robot.keyRelease(KeyEvent.VK_F7);
            } catch (InterruptedException ex) {
                System.out.println(ex);
            }
        });
    }
    
    public void changeGame(String game) {
        try {
            robot.keyHold(KeyEvent.VK_PAGE_DOWN);
            Thread.sleep(350);
            robot.keyRelease(KeyEvent.VK_PAGE_DOWN);
            Thread.sleep(350);
            robot.keyHold(KeyEvent.VK_CONTROL);
            robot.keyHold(KeyEvent.VK_O);
            Thread.sleep(350);
            robot.keyRelease(KeyEvent.VK_O);
            Thread.sleep(100);
            robot.keyRelease(KeyEvent.VK_CONTROL);
            Thread.sleep(730);
            robot.type(game);
            Thread.sleep(730);
            robot.keyHold(KeyEvent.VK_DOWN);
            Thread.sleep(150);
            robot.keyRelease(KeyEvent.VK_DOWN);
            Thread.sleep(230);
            robot.keyHold(KeyEvent.VK_ENTER);
            Thread.sleep(230);
            robot.keyRelease(KeyEvent.VK_ENTER);
            Thread.sleep(12030);
            robot.keyHold(KeyEvent.VK_PAGE_UP);
            Thread.sleep(250);
            robot.keyRelease(KeyEvent.VK_PAGE_UP);
            currentGame = Integer.parseInt(game);
        } catch (InterruptedException ex) {
            TwitchPlays.console.append(ex.toString());
        }
    }
    
    public SmartRobot getRobot() {
        return robot;
    }
    
    /**
     * Solta todas as keys que o interpretador pode manter pressionadas.
     */
    public void releaseAll() {
        try {
            Thread.sleep(1500);
        } catch (InterruptedException ex) {
            TwitchPlays.console.append(ex.toString());
        }
        
        for (int i=0;i<keymap.length;i++) {
            for (int j=0;j<keymap[0].length;j++) {
                robot.keyRelease(keymap[i][j]);
            }
        }
    }
    
    /**
     * Fecha a thread pool do service executor.
     * Chamado ao encerrar o programa.
     */
    public void shutdown() {
        exeService.shutdown();
        releaseAll();
    }
}
