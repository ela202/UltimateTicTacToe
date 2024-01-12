import pygame
import sys

pygame.init()

class StatusLabel:
    def __init__(self):
        self.text = ""

    def set_text(self, text):
        self.text = text

class UltimateTicTacToe:
    def __init__(self):
        pygame.init()
        self.screen = pygame.display.set_mode((600, 600))
        pygame.display.set_caption("Ultimate Tic Tac Toe")
        self.clock = pygame.time.Clock()
        self.game_controller = GameController(self.screen)
        self.running = True

    def run(self):
        while self.running:
            for event in pygame.event.get():
                if event.type == pygame.QUIT:
                    self.running = False
                elif event.type == pygame.MOUSEBUTTONDOWN:
                    self.game_controller.handle_click(event.pos)

            self.screen.fill((255, 255, 255))
            self.game_controller.board.update()
            pygame.display.flip()
            self.clock.tick(60)

        pygame.quit()

class GameController:
    def __init__(self, screen):
        self.board = Board(self, screen)
        self.current_player = 'X'
        self.status_label = StatusLabel()

    def handle_click(self, pos):
        self.board.handle_click(pos)

    def switch_player(self):
        self.current_player = 'O' if self.current_player == 'X' else 'X'
        self.status_label.set_text(f"Player {self.current_player}'s turn")

    def get_current_player(self):
        return self.current_player

    def check_win(self):
        return self.board.check_win()

    def check_draw(self):
        return self.board.is_full()

    def get_status_label(self):
        return self.status_label

    def get_board(self):
        return self.board

class Board:
    def __init__(self, game_controller, screen):
        self.game_controller = game_controller
        self.screen = screen
        self.small_boards = [[SmallBoard(game_controller) for _ in range(3)] for _ in range(3)]

    def handle_click(self, pos):
        for row in self.small_boards:
            for small_board in row:
                if small_board.rect.collidepoint(pos):
                    small_board.handle_click(pos)

    def update(self):
        for i, row in enumerate(self.small_boards):
            for j, small_board in enumerate(row):
                rect = small_board.rect.move(j * 200, i * 200)
                small_board.rect = rect  # Update the rect attribute
                small_board.update()

        # Draw the small boards on the screen
        for i, row in enumerate(self.small_boards):
            for j, small_board in enumerate(row):
                pygame.draw.rect(self.screen, (0, 0, 0), small_board.rect, 1)
    def __init__(self, game_controller, screen):
        self.game_controller = game_controller
        self.screen = screen
        self.small_boards = [[SmallBoard(game_controller) for _ in range(3)] for _ in range(3)]

    def handle_click(self, pos):
        for row in self.small_boards:
            for small_board in row:
                if small_board.rect.collidepoint(pos):
                    small_board.handle_click(pos)

    def update(self):
        for i, row in enumerate(self.small_boards):
            for j, small_board in enumerate(row):
                rect = small_board.rect.move(j * 200, i * 200)
                small_board.rect = rect  # Update the rect attribute
                small_board.update()

        # Draw the small boards on the screen
        for i, row in enumerate(self.small_boards):
            for j, small_board in enumerate(row):
                pygame.draw.rect(self.screen, (0, 0, 0), small_board.rect, 1)
    def __init__(self, game_controller, screen):
        self.game_controller = game_controller
        self.screen = screen
        self.small_boards = [[SmallBoard(game_controller) for _ in range(3)] for _ in range(3)]

    def handle_click(self, pos):
        for row in self.small_boards:
            for small_board in row:
                if small_board.rect.collidepoint(pos):
                    small_board.handle_click(pos)

    def update(self):
        for row in self.small_boards:
            for small_board in row:
                small_board.update()

        # Draw the small boards on the screen
        for i, row in enumerate(self.small_boards):
            for j, small_board in enumerate(row):
                rect = small_board.rect.move(j * 200, i * 200)
                pygame.draw.rect(self.screen, (0, 0, 0), rect, 1)

    def check_win(self):
        for i in range(3):
            if self.check_row_col(self.small_boards[i][0], self.small_boards[i][1], self.small_boards[i][2]) or \
               self.check_row_col(self.small_boards[0][i], self.small_boards[1][i], self.small_boards[2][i]):
                return True

        return self.check_row_col(self.small_boards[0][0], self.small_boards[1][1], self.small_boards[2][2]) or \
               self.check_row_col(self.small_boards[0][2], self.small_boards[1][1], self.small_boards[2][0])

    def check_row_col(self, a, b, c):
        winner = self.game_controller.get_current_player()
        return a.get_winner() == winner and b.get_winner() == winner and c.get_winner() == winner

    def is_full(self):
        for row in self.small_boards:
            for small_board in row:
                if not small_board.is_full():
                    return False
        return True

class SmallBoard:
    def __init__(self, game_controller):
        self.game_controller = game_controller
        self.cells = [[Cell(self, game_controller, i, j) for j in range(3)] for i in range(3)]
        self.winner = ' '
        self.rect = pygame.Rect(0, 0, 200, 200)

    def handle_click(self, pos):
        for row in self.cells:
            for cell in row:
                if cell.rect.collidepoint(pos):
                    cell.handle_click()

    def update(self):
        for row in self.cells:
            for cell in row:
                cell.update()

    def check_win(self):
        for i in range(3):
            if self.check_row_col(self.cells[i][0], self.cells[i][1], self.cells[i][2]) or \
               self.check_row_col(self.cells[0][i], self.cells[1][i], self.cells[2][i]):
                self.winner = self.game_controller.get_current_player()
                return True

        return self.check_row_col(self.cells[0][0], self.cells[1][1], self.cells[2][2]) or \
               self.check_row_col(self.cells[0][2], self.cells[1][1], self.cells[2][0])

    def check_row_col(self, a, b, c):
        symbol = self.game_controller.get_current_player()
        return a.get_symbol() == symbol and b.get_symbol() == symbol and c.get_symbol() == symbol

    def is_full(self):
        for row in self.cells:
            for cell in row:
                if cell.is_empty():
                    return False
        return True

    def get_winner(self):
        return self.winner

class Cell(pygame.sprite.Sprite):
    def __init__(self, small_board, game_controller, row, col):
        super().__init__()
        self.small_board = small_board
        self.game_controller = game_controller
        self.symbol = ' '
        self.rect = pygame.Rect(col * 66, row * 66, 50, 50)
        self.image = pygame.Surface((50, 50))
        self.image.fill((255, 255, 255))
        self.rect.x = col * 66
        self.rect.y = row * 66

    def handle_click(self):
        if self.is_empty():
            self.set_symbol(self.game_controller.get_current_player())
            self.game_controller.switch_player()
            self.small_board.check_win()
            self.small_board.update()

    def update(self):
        pygame.draw.rect(self.image, (0, 0, 0), (0, 0, 50, 50), 1)
        font = pygame.font.Font(None, 36)
        text = font.render(self.symbol, True, (0, 0, 0))
        self.image.blit(text, (20, 15))
        self.small_board.check_win()

    def is_empty(self):
        return self.symbol == ' '

    def set_symbol(self, symbol):
        self.symbol = symbol

    def get_symbol(self):
        return self.symbol

if __name__ == "__main__":
    game = UltimateTicTacToe()
    game.run()
