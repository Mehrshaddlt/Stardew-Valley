import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class GameView extends Pane {
    
    private Canvas canvas;
    private GraphicsContext gc;
    private AnimationTimer gameLoop;
    
    private Player player;
    private World world;
    
    // Tile size
    private static final int TILE_SIZE = 48;
    
    // Movement flags and state
    private boolean isMoving = false;
    private String pendingDirection = null;
    
    public GameView() {
        // Initialize canvas to fill entire window
        canvas = new Canvas();
        gc = canvas.getGraphicsContext2D();
        
        // Bind canvas size to parent pane
        canvas.widthProperty().bind(this.widthProperty());
        canvas.heightProperty().bind(this.heightProperty());
        
        // Add the canvas to this pane
        getChildren().add(canvas);
        
        // Initialize world first
        world = new World(TILE_SIZE);
        
        // Initialize player - position exactly at tile center
        player = new Player(5 * TILE_SIZE, 5 * TILE_SIZE);
        player.setTileSize(TILE_SIZE);
        
        // Set up key listeners
        setUpInputHandlers();
        
        // Start the game loop
        startGameLoop();
    }
    
    private void setUpInputHandlers() {
        // Set focus traversable to receive key events
        this.setFocusTraversable(true);
        
        // Key press handler - only register a pending direction if player is not currently moving
        this.setOnKeyPressed(event -> {
            if (!isMoving) {
                KeyCode code = event.getCode();
                switch (code) {
                    case W: case UP:    attemptMove("up"); break;
                    case S: case DOWN:  attemptMove("down"); break;
                    case A: case LEFT:  attemptMove("left"); break;
                    case D: case RIGHT: attemptMove("right"); break;
                    default: break;
                }
            } else if (pendingDirection == null) {
                // Register a pending move if player is already moving
                KeyCode code = event.getCode();
                switch (code) {
                    case W: case UP:    pendingDirection = "up"; break;
                    case S: case DOWN:  pendingDirection = "down"; break;
                    case A: case LEFT:  pendingDirection = "left"; break;
                    case D: case RIGHT: pendingDirection = "right"; break;
                    default: break;
                }
            }
        });
    }
    
    // Attempt to move in a direction
    private void attemptMove(String direction) {
        if (isMoving) return;
        
        // Get player's current tile position
        int currentTileX = (int) (player.getX() / TILE_SIZE);
        int currentTileY = (int) (player.getY() / TILE_SIZE);
        
        // Calculate destination tile
        int destTileX = currentTileX;
        int destTileY = currentTileY;
        
        switch (direction) {
            case "up":    destTileY--; break;
            case "down":  destTileY++; break;
            case "left":  destTileX--; break;
            case "right": destTileX++; break;
        }
        
        // Check if destination is walkable
        if (world.isWalkable(destTileX, destTileY)) {
            // Set player direction
            player.setDirection(direction);
            
            // Start smooth movement animation
            animateMovement(currentTileX, currentTileY, destTileX, destTileY);
        }
    }
    
    // Animate player moving from one tile to another
    private void animateMovement(int startX, int startY, int endX, int endY) {
        isMoving = true;
        
        // Calculate target position - exact tile position
        double targetX = endX * TILE_SIZE;
        double targetY = endY * TILE_SIZE;
        
        // Create animation timeline
        final int steps = 8; // Number of steps in animation
        final double stepX = (targetX - player.getX()) / steps;
        final double stepY = (targetY - player.getY()) / steps;
        
        Timeline timeline = new Timeline();
        
        for (int i = 1; i <= steps; i++) {
            final int step = i;
            KeyFrame keyFrame = new KeyFrame(
                Duration.millis(25 * i), // 25ms per step, 200ms total (8*25)
                event -> {
                    player.setPosition(player.getX() + stepX, player.getY() + stepY);
                    if (step == steps) {
                        // Completed movement - snap to exact tile position
                        isMoving = false;
                        player.setPosition(targetX, targetY);
                        
                        // Handle pending direction if there is one
                        if (pendingDirection != null) {
                            String next = pendingDirection;
                            pendingDirection = null;
                            attemptMove(next);
                        }
                    }
                }
            );
            timeline.getKeyFrames().add(keyFrame);
        }
        
        timeline.play();
    }
    
    private void startGameLoop() {
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                // Render the game
                render();
            }
        };
        
        gameLoop.start();
    }
    
    private void render() {
        // Clear the canvas
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        
        // Draw the grid lines FIRST (so they're behind everything)
        drawGridLines(gc);
        
        // Render the world (map) - this will skip drawing the tile where the player is
        world.render(gc, player.getX(), player.getY(), canvas.getWidth(), canvas.getHeight());
        
        // Render the player in the center of the screen
        player.render(gc, canvas.getWidth() / 2 - TILE_SIZE / 2, canvas.getHeight() / 2 - TILE_SIZE / 2);
    }
    
    // Helper method to visualize the tile grid - now draws lines at tile BOUNDARIES
    private void drawGridLines(GraphicsContext gc) {
        gc.setStroke(Color.DARKGRAY);
        gc.setLineWidth(1);
        
        // Calculate the player's position in the world
        double playerX = player.getX();
        double playerY = player.getY();
        
        // Calculate the position of the center tile on the screen
        double centerX = canvas.getWidth() / 2;
        double centerY = canvas.getHeight() / 2;
        
        // Calculate the offset to the nearest grid line
        // This shift ensures grid lines are at tile borders, not centers
        double offsetX = centerX - (playerX % TILE_SIZE);
        double offsetY = centerY - (playerY % TILE_SIZE);
        
        // Draw vertical grid lines (at tile borders)
        for (double x = offsetX; x < canvas.getWidth(); x += TILE_SIZE) {
            gc.strokeLine(x, 0, x, canvas.getHeight());
        }
        // Also draw leftward from the offset
        for (double x = offsetX - TILE_SIZE; x >= 0; x -= TILE_SIZE) {
            gc.strokeLine(x, 0, x, canvas.getHeight());
        }
        
        // Draw horizontal grid lines (at tile borders)
        for (double y = offsetY; y < canvas.getHeight(); y += TILE_SIZE) {
            gc.strokeLine(0, y, canvas.getWidth(), y);
        }
        // Also draw upward from the offset
        for (double y = offsetY - TILE_SIZE; y >= 0; y -= TILE_SIZE) {
            gc.strokeLine(0, y, canvas.getWidth(), y);
        }
    }
}