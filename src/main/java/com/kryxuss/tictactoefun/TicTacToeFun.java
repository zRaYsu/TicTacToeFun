package com.kryxuss.tictactoefun;

import java.util.*;
import java.util.stream.Collectors;

public class TicTacToeFun {
    private static final boolean USE_COLOR = true;
    private static final String HUMAN_MARK = "X";
    private static final String AI_MARK = "O";
    private static final Random RNG = new Random();
    private static final Scanner SC = new Scanner(System.in);

    private static final String RESET = c("");
    private static final String RED = c("\u001B[31m");
    private static final String GREEN = c("\u001B[32m");
    private static final String CYAN = c("\u001B[36m");
    private static final String YELLOW = c("\u001B[33m");
    private static final String PURPLE = c("\u001B[35m");
    private static final String BLUE = c("\u001B[34m");
    private static final String BOLD = c("\u001B[1m");

    private enum Difficulty {EASY, NORMAL, IMPOSSIBLE}

    private static String c(String code) { return USE_COLOR ? code : ""; }

    private final String[] board = new String[9];
    private final Difficulty difficulty;

    private TicTacToeFun(Difficulty difficulty) {
        Arrays.fill(board, " ");
        this.difficulty = difficulty;
    }

    public static void main(String[] args) {
        banner();
        do {
            Difficulty d = askDifficulty();
            TicTacToeFun game = new TicTacToeFun(d);
            game.loop();
        } while (askYesNo(CYAN + "¿Jugar otra partida? (s/n): " + RESET));
        println(GREEN + "¡Gracias por jugar! Adiós." + RESET);
    }

    private void loop() {
        boolean humanTurn = askYesNo("¿Quieres empezar tú? (s/n): ");
        showBoard();
        while (true) {
            if (humanTurn) {
                humanMove();
                if (isWinner(HUMAN_MARK)) { showBoard(); celebrateWin(true); return; }
            } else {
                aiMove();
                if (isWinner(AI_MARK)) { showBoard(); celebrateWin(false); return; }
            }
            if (isFull()) { showBoard(); println(YELLOW + "Empate ⚖️" + RESET); return; }
            humanTurn = !humanTurn;
            showBoard();
        }
    }

    private static Difficulty askDifficulty() {
        println(BOLD + "Selecciona dificultad:" + RESET);
        println("1) " + GREEN + "EASY" + RESET + "  (azar y comentarios simpáticos)");
        println("2) " + YELLOW + "NORMAL" + RESET + " (elige ganar/bloquear)");
        println("3) " + RED + "IMPOSSIBLE" + RESET + " (minimax perfecto)");
        while (true) {
            print("Opción (1/2/3): ");
            String s = SC.nextLine().trim();
            switch (s) {
                case "1": return Difficulty.EASY;
                case "2": return Difficulty.NORMAL;
                case "3": return Difficulty.IMPOSSIBLE;
            }
            println(RED + "Entrada inválida." + RESET);
        }
    }

    private void humanMove() {
        while (true) {
            print(CYAN + "Tu jugada (1-9, numpad style): " + RESET);
            String s = SC.nextLine().trim();
            if (!s.matches("[1-9]")) { println(RED + "Ingresa un número del 1 al 9." + RESET); continue; }
            int pos = mapHumanPosition(Integer.parseInt(s));
            if (!board[pos].equals(" ")) { println(YELLOW + "Esa casilla ya está ocupada." + RESET); continue; }
            board[pos] = HUMAN_MARK; break;
        }
    }

    private static int mapHumanPosition(int human) {
        switch (human) {
            case 7: return 0; case 8: return 1; case 9: return 2;
            case 4: return 3; case 5: return 4; case 6: return 5;
            case 1: return 6; case 2: return 7; case 3: return 8;
        }
        return -1;
    }

    private void aiMove() {
        thinking();
        int move;
        switch (difficulty) {
            case EASY: move = aiEasy(); taunt(EASY_TAUNTS); break;
            case NORMAL: move = aiNormal(); taunt(NORMAL_TAUNTS); break;
            case IMPOSSIBLE: move = aiImpossible(); taunt(IMPOSSIBLE_TAUNTS); break;
            default: move = aiEasy();
        }
        board[move] = AI_MARK;
        println(PURPLE + "La IA juega en " + (inverseMap(move)) + RESET);
    }

    private int inverseMap(int idx) { int[] rev = {7,8,9,4,5,6,1,2,3}; return rev[idx]; }

    private int aiEasy() { List<Integer> free = freeCells(); return free.get(RNG.nextInt(free.size())); }

    private int aiNormal() {
        for (int cell : freeCells()) { board[cell] = AI_MARK; if (isWinner(AI_MARK)) { board[cell] = " "; return cell; } board[cell] = " "; }
        for (int cell : freeCells()) { board[cell] = HUMAN_MARK; if (isWinner(HUMAN_MARK)) { board[cell] = " "; return cell; } board[cell] = " "; }
        if (board[4].equals(" ")) return 4;
        int[] corners = {0,2,6,8};
        List<Integer> availCorners = Arrays.stream(corners).filter(i->board[i].equals(" ")).boxed().collect(Collectors.toList());
        if (!availCorners.isEmpty()) return availCorners.get(RNG.nextInt(availCorners.size()));
        return aiEasy();
    }

    private int aiImpossible() {
        int bestScore = Integer.MIN_VALUE; int bestMove = -1;
        for (int cell : freeCells()) {
            board[cell] = AI_MARK;
            int score = minimax(false, 0, Integer.MIN_VALUE, Integer.MAX_VALUE);
            board[cell] = " ";
            if (score > bestScore) { bestScore = score; bestMove = cell; }
        }
        return bestMove;
    }

    private int minimax(boolean maximizing, int depth, int alpha, int beta) {
        if (isWinner(AI_MARK)) return 10 - depth;
        if (isWinner(HUMAN_MARK)) return depth - 10;
        if (isFull()) return 0;
        if (maximizing) {
            int best = Integer.MIN_VALUE;
            for (int cell : freeCells()) { board[cell] = AI_MARK; int val = minimax(false, depth + 1, alpha, beta); board[cell] = " "; best = Math.max(best, val); alpha = Math.max(alpha, best); if (beta <= alpha) break; }
            return best;
        } else {
            int best = Integer.MAX_VALUE;
            for (int cell : freeCells()) { board[cell] = HUMAN_MARK; int val = minimax(true, depth + 1, alpha, beta); board[cell] = " "; best = Math.min(best, val); beta = Math.min(beta, best); if (beta <= alpha) break; }
            return best;
        }
    }

    private boolean isFull() { for (String s : board) if (s.equals(" ")) return false; return true; }

    private boolean isWinner(String mark) {
        int[][] wins = {{0,1,2},{3,4,5},{6,7,8},{0,3,6},{1,4,7},{2,5,8},{0,4,8},{2,4,6}};
        for (int[] w : wins) if (board[w[0]].equals(mark) && board[w[1]].equals(mark) && board[w[2]].equals(mark)) return true; return false;
    }

    private List<Integer> freeCells() { List<Integer> f = new ArrayList<>(); for (int i=0;i<9;i++) if (board[i].equals(" ")) f.add(i); return f; }

    private void showBoard() {
        clearScreen();
        println("");
        println(BOLD + " Estado del tablero:" + RESET);
        String[] disp = new String[9];
        for (int i=0;i<9;i++) {
            if (board[i].equals(HUMAN_MARK)) disp[i] = GREEN + HUMAN_MARK + RESET;
            else if (board[i].equals(AI_MARK)) disp[i] = RED + AI_MARK + RESET;
            else disp[i] = BLUE + Integer.toString(inverseMap(i)) + RESET;
        }
        String hTop = BOLD + "┏━━━━━━━━━┳━━━━━━━━━┳━━━━━━━━━┓" + RESET;
        String hMid = BOLD + "┠─────────┼─────────┼─────────┨" + RESET;
        String hBot = BOLD + "┗━━━━━━━━━┻━━━━━━━━━┻━━━━━━━━━┛" + RESET;
        println(hTop);
        println(bigRow(disp[0], disp[1], disp[2]));
        println(hMid);
        println(bigRow(disp[3], disp[4], disp[5]));
        println(hMid);
        println(bigRow(disp[6], disp[7], disp[8]));
        println(hBot);
        println("");
    }

    private String bigRow(String a, String b, String c) { return BOLD + "┃" + RESET + center(a,9) + BOLD + "┃" + RESET + center(b,9) + BOLD + "┃" + RESET + center(c,9) + BOLD + "┃" + RESET; }
    private String center(String content, int width) { int visibleLength = content.replaceAll("\u001B\\[[;\\d]*m", "").length(); int pad = Math.max(0, width - visibleLength); int left = pad/2; int right = pad - left; return repeat(" ", left) + content + repeat(" ", right); }
    private String repeat(String s, int n) { StringBuilder sb = new StringBuilder(); for (int i=0;i<n;i++) sb.append(s); return sb.toString(); }

    private void thinking() { String[] dots = {".  ",".. ","..."}; print(PURPLE + "La IA está pensando"); for (String d : dots) { sleep(280); print("\r" + PURPLE + "La IA está pensando" + d + RESET); } println("\r" + PURPLE + "La IA decide su jugada!   " + RESET); sleep(200); }

    private static final String[] EASY_TAUNTS = {"Puro azar... o ¿no? 🤪","Elegí esa porque brillaba.","¿Qué podría salir mal?","Estoy improvisando jazz con tus casillas."};
    private static final String[] NORMAL_TAUNTS = {"Bloqueo estratégico activado.","No ganarás tan fácil.","Te vigilo 👀","Cálculos moderadamente serios..."};
    private static final String[] IMPOSSIBLE_TAUNTS = {"Destino sellado.","Minimax manda.","La perfección es fría, pero efectiva.","Cada movimiento reduce tus futuros... ♟️"};
    private void taunt(String[] list) { if (RNG.nextDouble() < 0.55) println(YELLOW + "[IA] " + list[RNG.nextInt(list.length)] + RESET); }
    private void celebrateWin(boolean human) { if (human) { println(GREEN + BOLD + "¡Ganaste! 🎉" + RESET); println(CYAN + "La máquina contempla su derrota." + RESET);} else { println(RED + BOLD + "La IA gana. ☠️" + RESET); println(PURPLE + "No te rindas, cada derrota afila tu mente." + RESET);} }

    private static void banner() {
        println(BOLD + CYAN +
                "╔═══════════════════════════════╗\n" +
                "║        TIC TAC TOE FUN        ║\n" +
                "║   Humano (X) vs IA (O)         ║\n" +
                "╚═══════════════════════════════╝" + RESET);
        println("Formato de entrada: usa números como el keypad:");
        println(BLUE +
                " 7 | 8 | 9\n" +
                " 4 | 5 | 6\n" +
                " 1 | 2 | 3" + RESET);
        println("");
    }

    private static boolean askYesNo(String prompt) {
        while (true) {
            print(prompt);
            String s = SC.nextLine().trim().toLowerCase(Locale.ROOT);
            if (s.matches("s(i)?|y|yes")) return true;
            if (s.matches("n(o)?")) return false;
            println(RED + "Responde s/n." + RESET);
        }
    }

    private static void sleep(long ms) { try { Thread.sleep(ms); } catch (InterruptedException ignored) {} }
    private static void print(String s) { System.out.print(s); }
    private static void println(String s) { System.out.println(s); }
    private static void clearScreen() { try { System.out.print("\033[H\033[2J"); System.out.flush(); } catch (Exception e) { for (int i=0;i<50;i++) System.out.println(); } }
}
