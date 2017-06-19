package robot;

import java.awt.AWTException;
import java.awt.Robot;
import static java.awt.event.KeyEvent.*;
import java.awt.event.MouseEvent;

/**
 * Implementação das funções da classe Robot reunidas.
 * @author Dan
 */
public class SmartRobot {
    
    /**
     * Constante usada como delay(em milisegundos) do robot para todos os comandos.
     */
    private static final int DELAY = 5;
    
    public Robot robot;
    
    public SmartRobot() {
        try {
            robot = new Robot();
        }
        catch (AWTException e) {
            System.out.println(e);
        }
    }
    
    /**
     * Move o mouse para a coordenada (x,y) na tela.
     * @param x Coordenada x da tela;
     * @param y Coordenada y da tela;
     */
    public void mouseMove(int x, int y) {
        robot.mouseMove(x,y);
    }
    
    /**
     * Move o mouse para a coordenada (x,y) e clica com o botão esquerdo do mouse.
     * @param x Coordenada x da tela;
     * @param y Coordenada y da tela;
     */
    public void mouseLeftClick(int x, int y) {
        robot.mouseMove(x, y);
        delay();
        robot.mousePress(MouseEvent.BUTTON1_MASK);
        robot.mouseRelease(MouseEvent.BUTTON1_MASK);
        delay();
    }
    
    /**
     * Clica com o botao esquerdo do mouse, na posição atual do ponteiro.
     */
    public void mouseLeftClick() {
        delay();
        robot.mousePress(MouseEvent.BUTTON1_MASK);
        robot.mouseRelease(MouseEvent.BUTTON1_MASK);
        delay();
    }
    
    /**
     * Move o mouse para a coordenada (x,y) e clica com o botão direito do mouse.
     * @param x Coordenada x da tela;
     * @param y Coordenada y da tela;
     */
    public void mouseRightClick(int x, int y) {
        robot.mouseMove(x, y);
        delay();
        robot.mousePress(MouseEvent.BUTTON3_MASK);
        robot.mouseRelease(MouseEvent.BUTTON3_MASK);
        delay();
    }
    
    /**
     * Clica com o botao direito do mouse, na posição atual do ponteiro.
     */
    public void mouseRightClick() {
        delay();
        robot.mousePress(MouseEvent.BUTTON3_MASK);
        robot.mouseRelease(MouseEvent.BUTTON3_MASK);
        delay();
    }
    
    /**
     * Segura o botão esquerdo do mouse.
     */
    public void mouseHoldLeftButton() {
        delay();
        robot.mousePress(MouseEvent.BUTTON1_MASK);
        delay();
    }
    
    /**
     * Segura o botão direito do mouse.
     */
    public void mouseHoldRightButton() {
        delay();
        robot.mousePress(MouseEvent.BUTTON3_MASK);
        delay();
    }
    
    /**
     * Solta o botão esquerdo do mouse.
     */
    public void mouseReleaseLeftButton() {
        delay();
        robot.mouseRelease(MouseEvent.BUTTON1_MASK);
        delay();
    }
    
    /**
     * Solta o botão direito do mouse.
     */
    public void mouseReleaseRightButton() {
        delay();
        robot.mouseRelease(MouseEvent.BUTTON3_MASK);
        delay();
    }
    
    /**
     * Pressiona e solta imediatamente uma(ou mais) tecla definida por um KeyEvent.
     * @param keyCodes Exemplos: KeyEvent.VK_SPACE ou {KeyEvent.VK_SHIFT, KeyEvent.VK_1}
     */
    public void keyStroke(int... keyCodes) {
        delay();
        for (int i=0;i<keyCodes.length;i++)
            robot.keyPress(keyCodes[i]);
        delay(75); // delay estimado que um ser humano segura uma tecla normalmente.
        for (int i=0;i<keyCodes.length;i++)
            robot.keyRelease(keyCodes[i]);
        delay();
    }
    
    /**
     * Segura uma tecla definida por um KeyEvent.
     * Nota: A tecla não é solta ao fim do método,
     * é necessário chamar releaseKey para parar de pressionar.
     * @param keyCode Tecla a ser pressionada. Exemplo: KeyEvent.VK_SPACE.
     */
    public void keyHold(int keyCode) {
        delay();
        robot.keyPress(keyCode);
        delay();
    }
    
    /**
     * Solta uma tecla definida por um KeyEvent.
     * @param keyCode Tecla a ser solta. Exemplo: KeyEvent.VK_SPACE.
     */
    public void keyRelease(int keyCode) {
        delay();
        robot.keyRelease(keyCode);
        delay();
    }
    
    /**
     * Digita uma sequência de caracteres ou string.
     * @param characters 
     */
    public void type(CharSequence characters) {
        int length = characters.length();
        for (int i = 0; i < length; i++) {
            char character = characters.charAt(i);
            type(character);
        }
    }

    /**
     * Digita um caractere. (Pressiona e solta a(s) tecla(s) necessária(s) p/ digitá-lo)
     * @param character 
     */
    private void type(char character) {
        switch (character) {
        case 'a': doType(VK_A); break;
        case 'b': doType(VK_B); break;
        case 'c': doType(VK_C); break;
        case 'd': doType(VK_D); break;
        case 'e': doType(VK_E); break;
        case 'f': doType(VK_F); break;
        case 'g': doType(VK_G); break;
        case 'h': doType(VK_H); break;
        case 'i': doType(VK_I); break;
        case 'j': doType(VK_J); break;
        case 'k': doType(VK_K); break;
        case 'l': doType(VK_L); break;
        case 'm': doType(VK_M); break;
        case 'n': doType(VK_N); break;
        case 'o': doType(VK_O); break;
        case 'p': doType(VK_P); break;
        case 'q': doType(VK_Q); break;
        case 'r': doType(VK_R); break;
        case 's': doType(VK_S); break;
        case 't': doType(VK_T); break;
        case 'u': doType(VK_U); break;
        case 'v': doType(VK_V); break;
        case 'w': doType(VK_W); break;
        case 'x': doType(VK_X); break;
        case 'y': doType(VK_Y); break;
        case 'z': doType(VK_Z); break;
        case 'A': doType(VK_SHIFT, VK_A); break;
        case 'B': doType(VK_SHIFT, VK_B); break;
        case 'C': doType(VK_SHIFT, VK_C); break;
        case 'D': doType(VK_SHIFT, VK_D); break;
        case 'E': doType(VK_SHIFT, VK_E); break;
        case 'F': doType(VK_SHIFT, VK_F); break;
        case 'G': doType(VK_SHIFT, VK_G); break;
        case 'H': doType(VK_SHIFT, VK_H); break;
        case 'I': doType(VK_SHIFT, VK_I); break;
        case 'J': doType(VK_SHIFT, VK_J); break;
        case 'K': doType(VK_SHIFT, VK_K); break;
        case 'L': doType(VK_SHIFT, VK_L); break;
        case 'M': doType(VK_SHIFT, VK_M); break;
        case 'N': doType(VK_SHIFT, VK_N); break;
        case 'O': doType(VK_SHIFT, VK_O); break;
        case 'P': doType(VK_SHIFT, VK_P); break;
        case 'Q': doType(VK_SHIFT, VK_Q); break;
        case 'R': doType(VK_SHIFT, VK_R); break;
        case 'S': doType(VK_SHIFT, VK_S); break;
        case 'T': doType(VK_SHIFT, VK_T); break;
        case 'U': doType(VK_SHIFT, VK_U); break;
        case 'V': doType(VK_SHIFT, VK_V); break;
        case 'W': doType(VK_SHIFT, VK_W); break;
        case 'X': doType(VK_SHIFT, VK_X); break;
        case 'Y': doType(VK_SHIFT, VK_Y); break;
        case 'Z': doType(VK_SHIFT, VK_Z); break;
        case '`': doType(VK_BACK_QUOTE); break;
        case '0': doType(VK_0); break;
        case '1': doType(VK_1); break;
        case '2': doType(VK_2); break;
        case '3': doType(VK_3); break;
        case '4': doType(VK_4); break;
        case '5': doType(VK_5); break;
        case '6': doType(VK_6); break;
        case '7': doType(VK_7); break;
        case '8': doType(VK_8); break;
        case '9': doType(VK_9); break;
        case '-': doType(VK_MINUS); break;
        case '=': doType(VK_EQUALS); break;
        case '~': doType(VK_SHIFT, VK_BACK_QUOTE); break;
        case '!': doType(VK_EXCLAMATION_MARK); break;
        case '@': doType(VK_AT); break;
        case '#': doType(VK_NUMBER_SIGN); break;
        case '$': doType(VK_DOLLAR); break;
        case '%': doType(VK_SHIFT, VK_5); break;
        case '^': doType(VK_CIRCUMFLEX); break;
        case '&': doType(VK_AMPERSAND); break;
        case '*': doType(VK_ASTERISK); break;
        case '(': doType(VK_LEFT_PARENTHESIS); break;
        case ')': doType(VK_RIGHT_PARENTHESIS); break;
        case '_': doType(VK_UNDERSCORE); break;
        case '+': doType(VK_PLUS); break;
        case '\t': doType(VK_TAB); break;
        case '\n': doType(VK_ENTER); break;
        case '[': doType(VK_OPEN_BRACKET); break;
        case ']': doType(VK_CLOSE_BRACKET); break;
        case '\\': doType(VK_BACK_SLASH); break;
        case '{': doType(VK_SHIFT, VK_OPEN_BRACKET); break;
        case '}': doType(VK_SHIFT, VK_CLOSE_BRACKET); break;
        case '|': doType(VK_SHIFT, VK_BACK_SLASH); break;
        case ';': doType(VK_SEMICOLON); break;
        case ':': doType(VK_COLON); break;
        case '\'': doType(VK_QUOTE); break;
        case '"': doType(VK_QUOTEDBL); break;
        case ',': doType(VK_COMMA); break;
        case '<': doType(VK_LESS); break;
        case '.': doType(VK_PERIOD); break;
        case '>': doType(VK_GREATER); break;
        case '/': doType(VK_SLASH); break;
        case '?': doType(VK_SHIFT, VK_SLASH); break;
        case ' ': doType(VK_SPACE); break;
        default:
            throw new IllegalArgumentException("Cannot type character " + character);
        }
    }

    /**
     * Chamada  do método recursivo para pressionar múltiplas teclas simultaneamente e soltá-las após o procedimento.
     * @param keyCodes Códigos de teclas a serem pressionadas.
     */
    private void doType(int... keyCodes) {
        doType(keyCodes, 0, keyCodes.length);
    }

    /**
     * Método recursivo para pressionar múltiplas teclas simultaneamente e soltá-las após o procedimento.
     * @param keyCodes Vetor contendo os códigos de teclas a serem pressionadas.
     */
    private void doType(int[] keyCodes, int offset, int length) {
        if (length == 0) {
            return;
        }

        delay();
        robot.keyPress(keyCodes[offset]);
        delay();
        doType(keyCodes, offset + 1, length - 1);
        delay();
        robot.keyRelease(keyCodes[offset]);
        delay();
    }
    
    /**
     * Pausa o robot @DELAY milisegundos.
     */
    private void delay() {
        robot.delay(DELAY);
    }
    
    /**
     * Pausa o robot pelo tempo determinado no parametro.
     * @param ms 
     */
    private void delay(int ms) {
        robot.delay(ms);
    }
}
