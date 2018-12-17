package CommunicatorP2P;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * This class is a main class in P2P application.
 * It starts the application.
 * <p>
 * @author Piotr Klukowski
 * @version 1.0
 * @see Controller
 * @see Host
 * @see Guest
 */
public class Main extends Application {

    /**Method which starts GUI. It opens sample.fxml*/
    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Network Communicator");
        primaryStage.setResizable(false);
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    /** Main method of the application */
    public static void main(String[] args) {
        System.out.println("Start application");
        launch();
        System.out.println("Stop application");
    }
}
