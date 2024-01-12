import javax.swing.*;
import java.awt.*;

// Represents a single 3x3 Tic Tac Toe board
class TicTacToeBoard {
    private char[][] board;
    private char winner;
    private boolean isFinished;

    public TicTacToeBoard() {
        board = new char[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = '-';
            }
        }
        winner = '-';
        isFinished = false;
    }

    public boolean makeMove(int row, int col, char player) {
        if (row < 0 || row >= 3 || col < 0 || col >= 3 || board[row][col] != '-') {
            return false;
        }
        board[row][col] = player;
        checkWinner(row, col, player);
        return true;
    }

    public void checkWinner(int row, int col, char player) {
        // Check row, column, and diagonals for a win
        if ((board[row][0] == player && board[row][1] == player && board[row][2] == player) ||
                (board[0][col] == player && board[1][col] == player && board[2][col] == player) ||
                (board[0][0] == player && board[1][1] == player && board[2][2] == player) ||
                (board[0][2] == player && board[1][1] == player && board[2][0] == player)) {
            winner = player;
            isFinished = true;
        }
    }

    public char getWinner() {
        return winner;
    }

    public boolean isFinished() {
        return isFinished;
    }
}

// Represents the entire 3x3 grid of Tic Tac Toe boards
class UltimateBoard extends TicTacToeBoard {
    private TicTacToeBoard[][] ultimateBoard;

    public UltimateBoard() {
        ultimateBoard = new TicTacToeBoard[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                ultimateBoard[i][j] = new TicTacToeBoard();
            }
        }
    }

    @Override
    public boolean makeMove(int row, int col, char player) {
        int boardRow = row / 3;
        int boardCol = col / 3;
        int cellRow = row % 3;
        int cellCol = col % 3;

        if (!ultimateBoard[boardRow][boardCol].isFinished()
                && ultimateBoard[boardRow][boardCol].makeMove(cellRow, cellCol, player)) {
            super.checkWinner(boardRow, boardCol, player);
            return true;
        }
        return false;
    }

    @Override
    public void checkWinner(int row, int col, char player) {
        // Check if the player has won any of the small boards
        if (ultimateBoard[row][col].getWinner() == player) {
            // Check row, column, and diagonals for a win in the ultimate board
            if ((ultimateBoard[row][0].getWinner() == player && ultimateBoard[row][1].getWinner() == player
                    && ultimateBoard[row][2].getWinner() == player) ||
                    (ultimateBoard[0][col].getWinner() == player && ultimateBoard[1][col].getWinner() == player
                            && ultimateBoard[2][col].getWinner() == player)
                    ||
                    (ultimateBoard[0][0].getWinner() == player && ultimateBoard[1][1].getWinner() == player
                            && ultimateBoard[2][2].getWinner() == player)
                    ||
                    (ultimateBoard[0][2].getWinner() == player && ultimateBoard[1][1].getWinner() == player
                            && ultimateBoard[2][0].getWinner() == player)) {
                winner = player;
                isFinished = true;
            }
            
        }
    }

    public TicTacToeBoard getBoard(int row, int col) {
        return ultimateBoard[row][col];
    }
}

// Manages the game's rules and player turns
class Game {
    private UltimateBoard ultimateBoard;
    private char currentPlayer;
    private int lastRow, lastCol;
    private UltimateTicTacToeGUI gui;

    public Game(UltimateTicTacToeGUI gui) {
        this.gui = gui;
        ultimateBoard = new UltimateBoard();
        currentPlayer = 'X';
        lastRow = -1;
        lastCol = -1;
    }

    public boolean makeMove(int row, int col) {
        if (!isValidMove(row, col)) {
            return false;
        }

        if (ultimateBoard.makeMove(row, col, currentPlayer)) {
            lastRow = row % 3;
            lastCol = col % 3;

            if (ultimateBoard.isFinished()) {
                gui.displayWinner(currentPlayer);
            }
            return true;
        }
        return false;
    }

    private boolean isValidMove(int row, int col) {
        if (lastRow != -1 && (row / 3 != lastRow || col / 3 != lastCol)
                && !ultimateBoard.getBoard(lastRow, lastCol).isFinished()) {
            JOptionPane.showMessageDialog(null,
                    "Invalid move. You must play in the board at (" + lastRow + ", " + lastCol + ").", "Invalid Move",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    public void switchPlayer() {
        currentPlayer = (currentPlayer == 'X') ? 'O' : 'X';
    }

    public char getCurrentPlayer() {
        return currentPlayer;
    }
}

// GUI for the Ultimate Tic Tac Toe game
class UltimateTicTacToeGUI {
    private JFrame frame;
    private JButton[][] buttons;
    private Game game;

    public UltimateTicTacToeGUI() {
        game = new Game(this);
        initializeUI();
    }

    private void initializeUI() {
    frame = new JFrame("Ultimate Tic Tac Toe");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setLayout(new GridLayout(3, 3, 5, 5)); // 3x3 grid with some padding

    buttons = new JButton[9][9];

    for (int i = 0; i < 9; i++) {
        for (int j = 0; j < 9; j++) {
            JButton button = new JButton();
            button.setFont(new Font("Arial", Font.BOLD, 20));
            button.setFocusable(false);
            int row = i;
            int col = j;
            button.addActionListener(e -> buttonClicked(row, col));
            buttons[i][j] = button;

            int panelIndex = (i / 3) * 3 + (j / 3);
            Container panel = frame.getContentPane();
            if (panelIndex >= panel.getComponentCount()) {
                JPanel subPanel = new JPanel(new GridLayout(3, 3));
                panel.add(subPanel);
            }
            JPanel subPanel = (JPanel) panel.getComponent(panelIndex);
            subPanel.add(button);
        }
    }

    frame.setSize(600, 600);
    frame.setVisible(true);
}


    private void buttonClicked(int row, int col) {
        if (game.makeMove(row, col)) {
            buttons[row][col].setText(String.valueOf(game.getCurrentPlayer()));
            game.switchPlayer();
        }
    }

    public void displayWinner(char winner) {
        JOptionPane.showMessageDialog(frame, "Player " + winner + " wins!", "Game Over",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(UltimateTicTacToeGUI::new);
    }
}
