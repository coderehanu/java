import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ChainReaction extends JFrame {

    static final int ROWS = 6;
    static final int COLS = 9;

    Cell[][] grid = new Cell[ROWS][COLS];
    int currentPlayer = 1;
    boolean gameOver = false;

    boolean player1HasPlayed = false;
    boolean player2HasPlayed = false;

    public ChainReaction() {
        setTitle("Chain Reaction Game");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridLayout(ROWS, COLS));

        initGrid();
        updateGridBorders(); // set initial border color
        setVisible(true);
    }

    void initGrid() {
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                Cell cell = new Cell(r, c);
                grid[r][c] = cell;
                add(cell);
            }
        }
    }

    class Cell extends JPanel {
        int row, col;
        int count = 0;
        int owner = 0;

        Cell(int r, int c) {
            row = r;
            col = c;
            setBackground(Color.WHITE);
            setBorder(new LineBorder(Color.BLACK));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (!gameOver) {
                        handleMove(Cell.this);
                    }
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            if (count > 0) {
                g.setColor(owner == 1 ? Color.RED : Color.BLUE);
                int size = Math.min(getWidth(), getHeight()) / 3;

                if (count == 1) {
                    g.fillOval((getWidth() - size) / 2, (getHeight() - size) / 2, size, size);
                } else if (count == 2) {
                    g.fillOval(getWidth() / 4 - size / 2, (getHeight() - size) / 2, size, size);
                    g.fillOval(3 * getWidth() / 4 - size / 2, (getHeight() - size) / 2, size, size);
                } else if (count == 3) {
                    g.fillOval(getWidth() / 2 - size / 2, getHeight() / 4 - size / 2, size, size);
                    g.fillOval(getWidth() / 4 - size / 2, 3 * getHeight() / 4 - size / 2, size, size);
                    g.fillOval(3 * getWidth() / 4 - size / 2, 3 * getHeight() / 4 - size / 2, size, size);
                } else {
                    g.fillOval(getWidth() / 4 - size / 2, getHeight() / 4 - size / 2, size, size);
                    g.fillOval(3 * getWidth() / 4 - size / 2, getHeight() / 4 - size / 2, size, size);
                    g.fillOval(getWidth() / 4 - size / 2, 3 * getHeight() / 4 - size / 2, size, size);
                    g.fillOval(3 * getWidth() / 4 - size / 2, 3 * getHeight() / 4 - size / 2, size, size);
                }
            }
        }
    }

    void handleMove(Cell cell) {
        if (cell.owner != 0 && cell.owner != currentPlayer) return;

        addOrb(cell, currentPlayer);

        // Mark that the player has played at least once
        if (currentPlayer == 1) player1HasPlayed = true;
        else player2HasPlayed = true;

        // Only check winner if both players have played at least once
        if (player1HasPlayed && player2HasPlayed) {
            checkWinner();
        }

        if (!gameOver) {
            switchPlayer();
        }
    }

    void addOrb(Cell cell, int player) {
        cell.count++;
        cell.owner = player;
        cell.repaint();

        if (cell.count >= getCriticalMass(cell)) {
            explode(cell);
        }
    }

    int getCriticalMass(Cell cell) {
        int edges = 0;
        if (cell.row == 0 || cell.row == ROWS - 1) edges++;
        if (cell.col == 0 || cell.col == COLS - 1) edges++;
        return 4 - edges;
    }

    void explode(Cell cell) {
        int player = cell.owner;
        cell.count = 0;
        cell.owner = 0;
        cell.repaint();

        int[][] dirs = {{1,0},{-1,0},{0,1},{0,-1}};
        for (int[] d : dirs) {
            int nr = cell.row + d[0];
            int nc = cell.col + d[1];

            if (nr >= 0 && nr < ROWS && nc >= 0 && nc < COLS) {
                addOrb(grid[nr][nc], player);
            }
        }
    }

    void switchPlayer() {
        currentPlayer = (currentPlayer == 1) ? 2 : 1;
        updateGridBorders();
    }

    void updateGridBorders() {
        Color borderColor = (currentPlayer == 1) ? Color.RED : Color.BLUE;
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                grid[r][c].setBorder(new LineBorder(borderColor));
            }
        }
    }

    void checkWinner() {
        int winner = 0;
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                if (grid[r][c].owner != 0) {
                    if (winner == 0) {
                        winner = grid[r][c].owner;
                    } else if (winner != grid[r][c].owner) {
                        return; // more than one color present
                    }
                }
            }
        }

        if (winner != 0) {
            gameOver = true;
            String winnerName = (winner == 1) ? "Red" : "Blue";
            JOptionPane.showMessageDialog(this, winnerName + " wins!");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ChainReaction::new);
    }
}
