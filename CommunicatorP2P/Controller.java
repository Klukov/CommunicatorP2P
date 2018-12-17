package CommunicatorP2P;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * This class is a controller class for start P2P application
 * As GUI is used sample.fxml
 * <p>
 * @author Piotr Klukowski
 * @version 1.0
 * @see Main
 * @see Host
 * @see Guest
 */

public class Controller {
	
    /**When is clicked Guest Window starts*/
    @FXML private javafx.scene.control.Button guestButton;
	
    /**When is clicked Host Controller starts*/
    @FXML private javafx.scene.control.Button hostButton;

	
    /**Initialization of main window. It only print msg to the console*/
    public void initialize() {
        System.out.println("Controller inicialized");
    }

    /**
     * Method which open new window with Guest Communicator and closing this one.
     * GUI for guest in guest.fxml
     */
    @FXML public void guestButtonPressed() {
        try {
            Stage oldStage = (Stage) guestButton.getScene().getWindow();
            oldStage.close();
            System.out.println("Controller should be closed");
        } catch (Exception e) {
            System.out.println("Exception: " + e);
        }
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("guest.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Guest Communicator");
            stage.setScene(new Scene(fxmlLoader.load()));
            stage.setResizable(false);
            stage.show();
            Guest guest = fxmlLoader.getController();
            stage.setOnCloseRequest(e -> guest.disconnect());
        } catch (Exception e) {
            System.out.println("Exception: " + e);
        }
    }

    /**
     * Method which open new window with Host Communicator and closing this one.
     * GUI for host in host.fxml
     */
    @FXML public void hostButtonPressed() {
        try {
            Stage oldStage = (Stage) hostButton.getScene().getWindow();
            oldStage.close();
            System.out.println("Controller should be closed");
        } catch (Exception e) {
            System.out.println("Exception: " + e);
        }
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("host.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Host Communicator");
            stage.setScene(new Scene(fxmlLoader.load()));
            stage.setResizable(false);
            stage.show();
            Host host =  fxmlLoader.getController();
            stage.setOnCloseRequest(e -> {
                host.disconnect();
            });
        } catch (Exception e) {
            System.out.println("Exception: " + e);
        }
    }
}
