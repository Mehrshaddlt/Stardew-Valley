import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.Screen;
import javafx.geometry.Rectangle2D;

public class Main extends Application {
    
    private static final String GAME_TITLE = "Stardew Valley Clone";

    @Override
    public void start(Stage primaryStage) {
        try {
            // Root layout
            BorderPane root = new BorderPane();
            
            // Create main game view
            GameView gameView = new GameView();
            root.setCenter(gameView);
            
            // Get screen dimensions for maximized window
            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            
            // Set up the scene - using screen size
            Scene scene = new Scene(root, screenBounds.getWidth(), screenBounds.getHeight());
            
            // Configure the stage
            primaryStage.setTitle(GAME_TITLE);
            primaryStage.setScene(scene);
            primaryStage.setMaximized(true); // Use maximized instead of fullscreen
            primaryStage.setResizable(true);
            primaryStage.show();
            
            // Give focus to the game view for keyboard input
            gameView.requestFocus();
            
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}