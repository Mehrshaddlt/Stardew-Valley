import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class World {
    
    private int[][] map;
    private int tileSize;
    private static final int MAP_WIDTH = 50;  // in tiles
    private static final int MAP_HEIGHT = 50; // in tiles
    
    // Tile types
    private static final int GRASS = 0;
    private static final int DIRT = 1;
    private static final int WATER = 2;
    
    public World(int tileSize) {
        this.tileSize = tileSize;
        initializeMap();
    }
    
    private void initializeMap() {
        // Create a simple map layout
        map = new int[MAP_HEIGHT][MAP_WIDTH];
        
        // Fill the map with grass by default
        for (int y = 0; y < MAP_HEIGHT; y++) {
            for (int x = 0; x < MAP_WIDTH; x++) {
                map[y][x] = GRASS;
                
                // Create some water at the edges
                if (x == 0 || y == 0 || x == MAP_WIDTH - 1 || y == MAP_HEIGHT - 1) {
                    map[y][x] = WATER;
                }
                
                // Add some dirt patches randomly
                if (Math.random() < 0.1 && x > 5 && y > 5 && x < MAP_WIDTH - 5 && y < MAP_HEIGHT - 5) {
                    map[y][x] = DIRT;
                }
            }
        }
    }
    
    public void update() {
        // Handle world updates like crop growth, NPC movements, etc.
    }
    
    public void render(GraphicsContext gc, double playerX, double playerY, double screenWidth, double screenHeight) {
        // Calculate the player's tile position
        int playerTileX = (int)(playerX / tileSize);
        int playerTileY = (int)(playerY / tileSize);
        
        // Center of the screen in tile coordinates
        double centerX = playerX / tileSize;
        double centerY = playerY / tileSize;
        
        // Calculate how many tiles we can see
        int tilesVisibleX = (int) Math.ceil(screenWidth / tileSize) + 1;
        int tilesVisibleY = (int) Math.ceil(screenHeight / tileSize) + 1;
        
        // Calculate the range of tiles to render
        int halfVisibleTilesX = tilesVisibleX / 2;
        int halfVisibleTilesY = tilesVisibleY / 2;
        
        int startTileX = (int) Math.max(0, centerX - halfVisibleTilesX);
        int endTileX = (int) Math.min(MAP_WIDTH - 1, centerX + halfVisibleTilesX);
        int startTileY = (int) Math.max(0, centerY - halfVisibleTilesY);
        int endTileY = (int) Math.min(MAP_HEIGHT - 1, centerY + halfVisibleTilesY);
        
        // Calculate screen position offset
        double offsetX = playerX % tileSize;
        double offsetY = playerY % tileSize;
        
        // Render visible tiles
        for (int y = startTileY; y <= endTileY; y++) {
            for (int x = startTileX; x <= endTileX; x++) {
                // Skip rendering the tile where the player is standing
                if (x == playerTileX && y == playerTileY) {
                    continue;
                }
                
                // Calculate screen position of tile - align with grid
                double screenX = (x * tileSize) - playerX + screenWidth / 2;
                double screenY = (y * tileSize) - playerY + screenHeight / 2;
                
                // Render tile based on type
                renderTile(gc, map[y][x], screenX, screenY);
            }
        }
    }
    
    private void renderTile(GraphicsContext gc, int tileType, double x, double y) {
        // Set color based on tile type
        switch (tileType) {
            case GRASS:
                gc.setFill(Color.GREEN);
                break;
            case DIRT:
                gc.setFill(Color.BROWN);
                break;
            case WATER:
                gc.setFill(Color.BLUE);
                break;
            default:
                gc.setFill(Color.BLACK);
                break;
        }
        
        // Draw the tile - fills exactly within grid lines
        gc.fillRect(x, y, tileSize, tileSize);
        
        // NO NEED to draw tile border since we have grid lines
        // This ensures tiles touch each other without gaps
    }
    
    public boolean isWalkable(int tileX, int tileY) {
        // Check if the tile is within bounds
        if (tileX < 0 || tileX >= MAP_WIDTH || tileY < 0 || tileY >= MAP_HEIGHT) {
            return false;
        }
        
        // Check if the tile type is walkable
        int tileType = map[tileY][tileX];
        return tileType != WATER; // Water tiles are not walkable
    }
}