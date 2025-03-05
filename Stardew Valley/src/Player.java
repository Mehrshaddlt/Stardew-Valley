import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class Player {
    
    private double x;  // Position in pixel coordinates
    private double y;  // Position in pixel coordinates
    private String direction = "down";
    private int frameIndex = 0;
    private long lastFrameChange = 0;
    private static final long FRAME_DURATION = 150; // milliseconds
    private Image sprite;
    
    // Player size will be exactly one tile
    private int tileSize = 48;
    
    public Player(double x, double y) {
        this.x = x;
        this.y = y;
        
        // Load player sprite
        try {
            // Uncomment when you have a sprite image
            // sprite = new Image(getClass().getResourceAsStream("/assets/images/player.png"));
        } catch (Exception e) {
            System.err.println("Failed to load player sprite: " + e.getMessage());
        }
    }
    
    public void setTileSize(int size) {
        this.tileSize = size;
    }
    
    // Update player's position directly
    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }
    
    // Set player's direction without moving
    public void setDirection(String direction) {
        this.direction = direction;
        updateAnimation();
    }
    
    private void updateAnimation() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastFrameChange > FRAME_DURATION) {
            frameIndex = (frameIndex + 1) % 4; // Assuming 4 frames per direction
            lastFrameChange = currentTime;
        }
    }
    
    public void render(GraphicsContext gc, double screenX, double screenY) {
        if (sprite != null) {
            // Calculate source rectangle from sprite sheet based on direction and frame
            int directionIndex = getDirectionIndex();
            
            // Assuming sprite sheet layout: 4 rows (directions) x 4 columns (frames)
            double srcX = frameIndex * tileSize;
            double srcY = directionIndex * tileSize;
            
            gc.drawImage(
                sprite, 
                srcX, srcY, tileSize, tileSize, 
                screenX, screenY, tileSize, tileSize
            );
        } else {
            // Placeholder if sprite isn't loaded - exactly one tile size
            gc.setFill(Color.BLUE);
            gc.fillRect(screenX, screenY, tileSize, tileSize);
            
            // Draw a face to indicate direction
            gc.setFill(Color.WHITE);
            switch (direction) {
                case "down":
                    // Eyes on top half
                    gc.fillOval(screenX + tileSize * 0.2, screenY + tileSize * 0.2, tileSize * 0.2, tileSize * 0.2);
                    gc.fillOval(screenX + tileSize * 0.6, screenY + tileSize * 0.2, tileSize * 0.2, tileSize * 0.2);
                    // Mouth on bottom half
                    gc.fillRect(screenX + tileSize * 0.3, screenY + tileSize * 0.6, tileSize * 0.4, tileSize * 0.1);
                    break;
                case "up":
                    // Eyes on bottom half
                    gc.fillOval(screenX + tileSize * 0.2, screenY + tileSize * 0.6, tileSize * 0.2, tileSize * 0.2);
                    gc.fillOval(screenX + tileSize * 0.6, screenY + tileSize * 0.6, tileSize * 0.2, tileSize * 0.2);
                    // Mouth on top half
                    gc.fillRect(screenX + tileSize * 0.3, screenY + tileSize * 0.3, tileSize * 0.4, tileSize * 0.1);
                    break;
                case "left":
                    // Eyes on right half
                    gc.fillOval(screenX + tileSize * 0.6, screenY + tileSize * 0.2, tileSize * 0.2, tileSize * 0.2);
                    gc.fillOval(screenX + tileSize * 0.6, screenY + tileSize * 0.6, tileSize * 0.2, tileSize * 0.2);
                    // Mouth on left half
                    gc.fillRect(screenX + tileSize * 0.2, screenY + tileSize * 0.4, tileSize * 0.2, tileSize * 0.1);
                    break;
                case "right":
                    // Eyes on left half
                    gc.fillOval(screenX + tileSize * 0.2, screenY + tileSize * 0.2, tileSize * 0.2, tileSize * 0.2);
                    gc.fillOval(screenX + tileSize * 0.2, screenY + tileSize * 0.6, tileSize * 0.2, tileSize * 0.2);
                    // Mouth on right half
                    gc.fillRect(screenX + tileSize * 0.6, screenY + tileSize * 0.4, tileSize * 0.2, tileSize * 0.1);
                    break;
            }
        }
    }
    
    private int getDirectionIndex() {
        switch (direction) {
            case "down": return 0;
            case "left": return 1;
            case "right": return 2;
            case "up": return 3;
            default: return 0;
        }
    }
    
    public double getX() {
        return x;
    }
    
    public double getY() {
        return y;
    }
}