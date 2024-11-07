import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.PriorityQueue;
import java.util.Comparator;

public class PathfindingGame extends JPanel implements KeyListener {
    private static final int TILE_SIZE = 32;
    private static final int GRID_WIDTH = 20;
    private static final int GRID_HEIGHT = 15;
    private static final int ANIMATION_SPEED = 100; // in milliseconds

    private int playerX = 1, playerY = 1;
    private int goalX = 18, goalY = 13;
    private boolean hasWon = false;

    private int[][] grid = new int[GRID_WIDTH][GRID_HEIGHT]; // 0 = free, -1 = obstacle
    private Node[][] previous; // For path visualization

    private Image backgroundImage;

    // Player animation state
    private int currentFrame = 0; // For animation
    private boolean isRunning = false;

    public PathfindingGame() {
        // Load the background image
        // Adjust the path as necessary. If using relative paths, ensure the image is in the correct directory.
        String imagePath = "NEW.webp"; // Example relative path
        ImageIcon icon = new ImageIcon(imagePath);
        if (icon.getImageLoadStatus() == MediaTracker.COMPLETE) {
            backgroundImage = icon.getImage();
            System.out.println("Background image loaded successfully.");
        } else {
            System.err.println("Error: Background image failed to load from " + imagePath);
            backgroundImage = null; // Optionally, set a default background or handle accordingly
        }

        setPreferredSize(new Dimension(GRID_WIDTH * TILE_SIZE, GRID_HEIGHT * TILE_SIZE));
        setFocusable(true);
        addKeyListener(this);

        // Place some obstacles in the grid (example positions)
        grid[3][3] = -1;
        grid[4][3] = -1;
        grid[5][3] = -1;
        grid[6][3] = -1;

        // Precompute shortest path distances from each cell to the goal
        computeShortestPaths();
    }

    // Dijkstra’s algorithm to compute shortest path distances to the goal
    private void computeShortestPaths() {
        previous = new Node[GRID_WIDTH][GRID_HEIGHT];
        int[][] dist = new int[GRID_WIDTH][GRID_HEIGHT];
        for (int x = 0; x < GRID_WIDTH; x++) {
            for (int y = 0; y < GRID_HEIGHT; y++) {
                dist[x][y] = Integer.MAX_VALUE;
            }
        }
        dist[goalX][goalY] = 0;

        PriorityQueue<Node> pq = new PriorityQueue<>(Comparator.comparingInt(node -> node.distance));
        pq.add(new Node(goalX, goalY, 0));

        int[][] directions = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};

        while (!pq.isEmpty()) {
            Node current = pq.poll();
            for (int[] dir : directions) {
                int nx = current.x + dir[0];
                int ny = current.y + dir[1];
                if (nx >= 0 && nx < GRID_WIDTH && ny >= 0 && ny < GRID_HEIGHT && grid[nx][ny] != -1) {
                    int newDist = current.distance + 1;
                    if (newDist < dist[nx][ny]) {
                        dist[nx][ny] = newDist;
                        previous[nx][ny] = current; // Store the path
                        pq.add(new Node(nx, ny, newDist));
                    }
                }
            }
        }
    }

    // Smooth movement animation
    private void animatePlayerMovement(int targetX, int targetY) {
        int startX = playerX * TILE_SIZE;
        int startY = playerY * TILE_SIZE;
        int endX = targetX * TILE_SIZE;
        int endY = targetY * TILE_SIZE;
        int steps = 10;

        for (int i = 1; i <= steps; i++) {
            int dx = startX + (endX - startX) * i / steps;
            int dy = startY + (endY - startY) * i / steps;
            playerX = dx / TILE_SIZE;
            playerY = dy / TILE_SIZE;
            repaint();

            try {
                Thread.sleep(ANIMATION_SPEED / steps);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
        playerX = targetX;
        playerY = targetY;
        repaint();
    }

    // Method to highlight the shortest path
    private void drawShortestPath(Graphics g) {
        Node current = previous[playerX][playerY];
        g.setColor(new Color(255, 223, 186, 150)); // Light orange with transparency
        while (current != null) {
            g.fillRect(current.x * TILE_SIZE, current.y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            current = previous[current.x][current.y];
        }
    }

    // Draw the running player (similar to SimpleRunningPlayer)
    private void drawPlayer(Graphics g) {
        int playerWidth = 24;
        int playerHeight = 24;
        int x = playerX * TILE_SIZE + (TILE_SIZE - playerWidth) / 2;
        int y = playerY * TILE_SIZE + (TILE_SIZE - playerHeight) / 2;

        // Player body (rectangle)
        g.setColor(Color.BLUE);
        g.fillRect(x, y, playerWidth, playerHeight);

        // Player legs animation (running)
        g.setColor(Color.BLACK);
        int legX = x + 4;
        int legY = y + playerHeight;

        if (currentFrame == 0) {
            g.drawLine(legX, legY, legX - 6, legY + 12);  // left leg
            g.drawLine(legX + playerWidth - 8, legY, legX + playerWidth - 2, legY + 12);  // right leg
        } else {
            g.drawLine(legX, legY, legX + 6, legY + 12);  // left leg
            g.drawLine(legX + playerWidth - 8, legY, legX + playerWidth - 14, legY + 12);  // right leg
        }

        // Player arms (simple lines)
        g.setColor(Color.RED);
        int armX = x + 2;
        int armY = y + 6;
        g.drawLine(armX, armY, armX - 10, armY - 12); // left arm
        g.drawLine(armX + playerWidth - 4, armY, armX + playerWidth + 4, armY - 12); // right arm

        // Update frame for running animation
        currentFrame = (currentFrame + 1) % 2;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw the background image if it exists
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, this.getWidth(), this.getHeight(), this);
        } else {
            g.setColor(Color.LIGHT_GRAY);
            g.fillRect(0, 0, this.getWidth(), this.getHeight());
        }

        // Draw path
        drawShortestPath(g);

        // Draw grid, obstacles, player, and goal
        for (int x = 0; x < GRID_WIDTH; x++) {
            for (int y = 0; y < GRID_HEIGHT; y++) {
                if (grid[x][y] == -1) {
                    g.setColor(Color.RED); // Obstacles
                    g.fillRect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                }
            }
        }

        // Draw player
        drawPlayer(g);

        // Draw goal
        g.setColor(Color.GREEN);
        g.fillRect(goalX * TILE_SIZE, goalY * TILE_SIZE, TILE_SIZE, TILE_SIZE);

        // Draw win message if player has reached the goal
        if (hasWon) {
            g.setColor(new Color(0, 0, 0, 150)); // Semi-transparent overlay
            g.fillRect(0, 0, getWidth(), getHeight());

            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 36));

            // Center the message
            String message = "Congratulations! You've reached home!";
            FontMetrics fm = g.getFontMetrics();
            int msgWidth = fm.stringWidth(message);
            int msgHeight = fm.getHeight();
            int x = (getWidth() - msgWidth) / 2;
            int y = (getHeight() - msgHeight) / 2 + fm.getAscent();

            g.drawString(message, x, y);
        }
    }

    // Handle player movement with arrow keys
    @Override
    public void keyPressed(KeyEvent e) {
        if (hasWon) return;

        int newX = playerX, newY = playerY;
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                newY = playerY - 1;
                break;
            case KeyEvent.VK_DOWN:
                newY = playerY + 1;
                break;
            case KeyEvent.VK_LEFT:
                newX = playerX - 1;
                break;
            case KeyEvent.VK_RIGHT:
                newX = playerX + 1;
                break;
        }

        // Check if new position is within bounds and not an obstacle
        if (newX >= 0 && newX < GRID_WIDTH && newY >= 0 && newY < GRID_HEIGHT && grid[newX][newY] != -1) {
            animatePlayerMovement(newX, newY);

            if (newX == goalX && newY == goalY) {
                hasWon = true;
                repaint();
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // Not used
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Not used
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Pathfinding Game");
        PathfindingGame gamePanel = new PathfindingGame();
        frame.add(gamePanel);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    // Node class for pathfinding
    private static class Node {
        int x, y, distance;

        Node(int x, int y, int distance) {
            this.x = x;
            this.y = y;
            this.distance = distance;
        }
    }
}
