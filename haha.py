class UltimateTicTacToeFixed:
    def __init__(self):
        self.board = [[self.init_board() for _ in range(3)] for _ in range(3)]
        self.current_board = (1, 1)  # Start in the middle board
        self.player_turn = 'X'  # X starts

    @staticmethod
    def init_board():
        return [[' ' for _ in range(3)] for _ in range(3)]

    def print_board(self):
        for row in range(3):
            for sub_row in range(3):
                for col in range(3):
                    print(' | '.join(self.board[row][col][sub_row]), end='   ')
                print()
            if row < 2:
                print('---------------------   ---------------------   ---------------------')

    def move(self, board_row, board_col, row, col):
        # Check if the move is to the correct board and the cell is empty
        if (self.current_board == (board_row, board_col) or self.is_board_full(self.current_board)) \
                and self.board[board_row][board_col][row][col] == ' ':
            self.board[board_row][board_col][row][col] = self.player_turn
            self.current_board = (row, col)  # Next player must play in the corresponding board
            self.player_turn = 'O' if self.player_turn == 'X' else 'X'
            return True
        else:
            return False

    def is_board_full(self, board_pos):
        # Check if a 3x3 board is full
        board = self.board[board_pos[0]][board_pos[1]]
        return all(cell != ' ' for row in board for cell in row)

    def check_win(self, board):
        for i in range(3):
            if board[i][0] == board[i][1] == board[i][2] != ' ' or \
               board[0][i] == board[1][i] == board[2][i] != ' ':
                return board[i][0]

        if board[0][0] == board[1][1] == board[2][2] != ' ' or \
           board[0][2] == board[1][1] == board[2][0] != ' ':
            return board[1][1]

        return None

    def check_global_win(self):
        global_board = [[self.check_win(self.board[i][j]) for j in range(3)] for i in range(3)]
        return self.check_win(global_board)

# Test the updated game
fixed_game = UltimateTicTacToeFixed()
print("Initial Board:")
fixed_game.print_board()

# Make a sequence of moves and print the board after each move
test_moves = [(1, 1, 0, 0), (0, 0, 1, 1), (0, 1, 0, 0), (1, 0, 2, 2), (2, 2, 1, 1), (1, 1, 0, 1)]
for move in test_moves:
    valid_move = fixed_game.move(*move)
    if valid_move:
        print(f"\nAfter move {move}:")
        fixed_game.print_board()
    else:
        print(f"Invalid move: {move}")

# Check for a win
winning_player = fixed_game.check_global_win()
winning_player
