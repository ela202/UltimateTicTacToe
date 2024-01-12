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
    private SmallBoard activeBoard;
    private int lastPlayedRow;
    private int lastPlayedCol;

    public GameController() {
        currentPlayer = 'X';
        statusLabel = new JLabel("Player X's turn");
        board = new Board(this);
        activeBoard = board.getBoards()[1][1];
        activeBoard.setActive(true);
        lastPlayedRow = -1;
        lastPlayedCol = -1;
    }

    public Board getBoard() {
        return board;
    }

    public JLabel getStatusLabel() {
        return statusLabel;
    }

    public void cellClicked(Cell cell) {
        if (cell.isEmpty() && activeBoard.isEnabled()) {
            if (lastPlayedRow == -1 || (lastPlayedRow == cell.getRow() && lastPlayedCol == cell.getCol())) {
                cell.setSymbol(currentPlayer);
                if (activeBoard.checkWin()) {
                    statusLabel.setText("Player " + currentPlayer + " wins!");
                    activeBoard.setEnabled(false);
                } else if (activeBoard.isFull()) {
                    statusLabel.setText("Draw!");
                    activeBoard.setEnabled(false);
                } else {
                    switchPlayer();
                    updateActiveBoard(cell);
                    highlightNextActiveBoard(); // Highlight the next active small board
                }
            }
        }
    }

    private void updateActiveBoard(Cell cell) {
        lastPlayedRow = cell.getRow();
        lastPlayedCol = cell.getCol();
        activeBoard.setActive(false);
        activeBoard = board.getBoards()[lastPlayedRow][lastPlayedCol];
        activeBoard.setActive(true);
    }

    private void highlightNextActiveBoard() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board.getBoards()[i][j].setNextActive(false);
            }
        }
        SmallBoard nextActiveBoard = board.getBoards()[lastPlayedRow][lastPlayedCol];
        nextActiveBoard.setNextActive(true);
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

    public SmallBoard[][] getBoards() {
        return boards;
    }

    public boolean checkWin() {
        for (int i = 0; i < 3; i++) {
            if (checkRowCol(boards[i][0], boards[i][1], boards[i][2]) ||
                    checkRowCol(boards[0][i], boards[1][i], boards[2][i])) {
                return true;
            }
        }
        return checkRowCol(boards[0][0], boards[1][1], boards[2][2]) ||
                checkRowCol(boards[0][2], boards[1][1], boards[2][0]);
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
    private boolean isActive;
    private boolean isNextActive; // New variable to track if the small board is the next active small board

    public SmallBoard(GameController gameController) {
        this.gameController = gameController;
        setLayout(new GridLayout(3, 3));
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                cells[i][j] = new Cell(this, gameController, i, j);
                add(cells[i][j]);
            }
        }
        isActive = false;
        isNextActive = false;
    }

    public boolean checkWin() {
        for (int i = 0; i < 3; i++) {
            if (checkRowCol(cells[i][0], cells[i][1], cells[i][2]) ||
                    checkRowCol(cells[0][i], cells[1][i], cells[2][i])) {
                winner = gameController.getCurrentPlayer();
                return true;
            }
        }
        if (checkRowCol(cells[0][0], cells[1][1], cells[2][2]) ||
                checkRowCol(cells[0][2], cells[1][1], cells[2][0])) {
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

    public void setActive(boolean isActive) {
        this.isActive = isActive;
        if (isActive) {
            setBackground(Color.YELLOW);
        } else {
            setBackground(null);
        }
    }

    public void setNextActive(boolean isNextActive) {
        this.isNextActive = isNextActive;
        if (isNextActive) {
            setBackground(Color.GREEN); // Highlight the next active small board with a green background
        } else {
            setBackground(null);
        }
    }

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
    private int row;
    private int col;

    public Cell(SmallBoard smallBoard, GameController gameController, int row, int col) {
        this.smallBoard = smallBoard;
        this.gameController = gameController;
        this.row = row;
        this.col = col;
        addActionListener(e -> {
            if (isEmpty()) {
                setSymbol(gameController.getCurrentPlayer());
                gameController.cellClicked(this);
                gameController.switchPlayer();
            }
        });
    }

    public boolean isEmpty() {
        return symbol == ' ';
    }

    public void setSymbol(char symbol) {
        this.symbol = symbol;
        setText(String.valueOf(symbol));
        checkBoardStates();
    }

    private void checkBoardStates() {
        if (smallBoard.checkWin() || smallBoard.isFull()) {
            smallBoard.setEnabled(false);
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

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }
}
