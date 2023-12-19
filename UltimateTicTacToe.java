import javax.swing.*;
import java.awt.*;

public class UltimateTicTacToe {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Ultimate Tic Tac Toe");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            GameController gameController = new GameController();
            frame.add(gameController.getBoard(), BorderLayout.CENTER);
            frame.add(gameController.getStatusLabel(), BorderLayout.SOUTH);
            frame.pack();
            frame.setVisible(true);
        });
    }
}

class GameController {
    private Board board;
    private JLabel statusLabel;
    private char currentPlayer;

    public GameController() {
        currentPlayer = 'X';
        statusLabel = new JLabel("Player X's turn");
        board = new Board(this);
    }

    public Board getBoard() {
        return board;
    }

    public JLabel getStatusLabel() {
        return statusLabel;
    }

    public void cellClicked(Cell cell) {
        if (cell.isEmpty()) {
            cell.setSymbol(currentPlayer);
            if (board.checkWin()) {
                statusLabel.setText("Player " + currentPlayer + " wins!");
                board.setEnabled(false);
            } else if (board.isFull()) {
                statusLabel.setText("Draw!");
                board.setEnabled(false);
            } else {
                switchPlayer();
            }
        }
    }

    public void switchPlayer() {
        currentPlayer = (currentPlayer == 'X') ? 'O' : 'X';
        statusLabel.setText("Player " + currentPlayer + "'s turn");
    }

    public char getCurrentPlayer() {
        return currentPlayer;
    }
}

class Board extends JPanel {
    private SmallBoard[][] boards = new SmallBoard[3][3];
    private GameController gameController;

    public Board(GameController gameController) {
        this.gameController = gameController;
        setLayout(new GridLayout(3, 3));
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                boards[i][j] = new SmallBoard(gameController);
                add(boards[i][j]);
            }
        }
    }

    public boolean checkWin() {
        for (int i = 0; i < 3; i++) {
            if (checkRowCol(boards[i][0], boards[i][1], boards[i][2]) || // check rows
                    checkRowCol(boards[0][i], boards[1][i], boards[2][i])) { // check columns
                return true;
            }
        }
        return checkRowCol(boards[0][0], boards[1][1], boards[2][2]) || // check diagonal
                checkRowCol(boards[0][2], boards[1][1], boards[2][0]); // check reverse diagonal
    }

    private boolean checkRowCol(SmallBoard a, SmallBoard b, SmallBoard c) {
        char winner = gameController.getCurrentPlayer();
        return a.getWinner() == winner && b.getWinner() == winner && c.getWinner() == winner;
    }

    public boolean isFull() {
        for (SmallBoard[] row : boards) {
            for (SmallBoard board : row) {
                if (!board.isFull()) {
                    return false;
                }
            }
        }
        return true;
    }

    // Set enabled state of all SmallBoards
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        for (SmallBoard[] row : boards) {
            for (SmallBoard board : row) {
                board.setEnabled(enabled);
            }
        }
    }
}

class SmallBoard extends JPanel {
    private Cell[][] cells = new Cell[3][3];
    private GameController gameController;
    private char winner = ' ';

    public SmallBoard(GameController gameController) {
        this.gameController = gameController;
        setLayout(new GridLayout(3, 3));
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                cells[i][j] = new Cell(this, gameController);
                add(cells[i][j]);
            }
        }
    }

    public boolean checkWin() {
        for (int i = 0; i < 3; i++) {
            if (checkRowCol(cells[i][0], cells[i][1], cells[i][2]) || // check rows
                    checkRowCol(cells[0][i], cells[1][i], cells[2][i])) { // check columns
                winner = gameController.getCurrentPlayer();
                return true;
            }
        }
        if (checkRowCol(cells[0][0], cells[1][1], cells[2][2]) || // check diagonal
                checkRowCol(cells[0][2], cells[1][1], cells[2][0])) { // check reverse diagonal
            winner = gameController.getCurrentPlayer();
            return true;
        }
        return false;
    }

    private boolean checkRowCol(Cell a, Cell b, Cell c) {
        char symbol = gameController.getCurrentPlayer();
        return a.getSymbol() == symbol && b.getSymbol() == symbol && c.getSymbol() == symbol;
    }

    public boolean isFull() {
        for (Cell[] row : cells) {
            for (Cell cell : row) {
                if (cell.isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }

    public char getWinner() {
        return winner;
    }

    // Set enabled state of all Cells
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        for (Cell[] row : cells) {
            for (Cell cell : row) {
                cell.setEnabled(enabled);
            }
        }
    }
}

class Cell extends JButton {
    private char symbol = ' ';
    private SmallBoard smallBoard;
    private GameController gameController;

    public Cell(SmallBoard smallBoard, GameController gameController) {
        this.smallBoard = smallBoard;
        this.gameController = gameController;
        addActionListener(e -> {
            if (isEmpty()) {
                setSymbol(gameController.getCurrentPlayer()); // Set symbol based on the current player
                gameController.cellClicked(this); // Delegate to GameController to handle the action
                gameController.switchPlayer(); // Switch player after every valid move
            }
        });
    }

    public boolean isEmpty() {
        return symbol == ' ';
    }

    public void setSymbol(char symbol) {
        this.symbol = symbol;
        setText(String.valueOf(symbol));
        // Check if the small board or the entire board is won or full after setting the
        // symbol
        checkBoardStates();
    }

    private void checkBoardStates() {
        if (smallBoard.checkWin() || smallBoard.isFull()) {
            smallBoard.setEnabled(false);
            // Check if the entire game is won or if it's a draw
            if (gameController.getBoard().checkWin()) {
                gameController.getStatusLabel()
                        .setText("Player " + (gameController.getCurrentPlayer() == 'X' ? 'O' : 'X') + " wins!");
                gameController.getBoard().setEnabled(false);
            } else if (gameController.getBoard().isFull()) {
                gameController.getStatusLabel().setText("Draw!");
                gameController.getBoard().setEnabled(false);
            }
        }
    }

    public char getSymbol() {
        return symbol;
    }
}
