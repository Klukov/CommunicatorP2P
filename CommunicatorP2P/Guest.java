package CommunicatorP2P;

import javafx.fxml.FXML;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.regex.Pattern;

/**
 * This class is a controller class for Host application
 * As GUI is used guest.fxml
 * <p>
 * @author Piotr Klukowski
 * @version 1.0
 * @see Main
 * @see Controller
 * @see Guest
 */

public class Guest {
	
	/**
     * When is clicked connection starts - connect method.
     * When it is visible, then disconnectButton is unvisible.
     */
    @FXML private javafx.scene.control.Button connectButton;

    /**
     * When is clicked disconnection process starts - disconnect method.
     * When it is visible, then connectButton is unvisible.
     */
    @FXML private javafx.scene.control.Button disconnectButton;

    /**When it is clicked, program sends message.*/
    @FXML private javafx.scene.control.Button send;

    /**Area where received and sent text is shown.*/
    @FXML private javafx.scene.control.TextArea readArea;

    /**Area where user writes text to send.*/
    @FXML private javafx.scene.control.TextArea writeArea;

    /**Editable area with where user have to write host IP address.*/
    @FXML private javafx.scene.control.TextField ip;

    /** Editable area before connection, it gets PortNumber from User. As Default is set to port: 5000.*/
    @FXML private javafx.scene.control.TextField port;

    /**
     * Editable area before connection, it gets nickName from User. As Default is set to nickname: Guest.
     * It may only contain max 10 chars, which can only be letters or digits.
     */
    @FXML private javafx.scene.control.TextField nick;

    /** Uneditable area, it inform user if something in input data is wrong. */
    @FXML private javafx.scene.control.Label infoLabel;

    /** User writes friend's ipAddress, however it is controlled by checkCorrectness method. */
    private String ipAddress;

    /** User writes friend's portNumber, however it is controlled by checkCorrectness method. */
    private int portAddress;

    /** User nickName, it can only contain letters and digits, max 10. */
    private String nickName;

    /** User friend's nickName, chars are controlled by friend's application. */
    private String friendName;

    /** Connection socket, null if disconnected. */
    private Socket guestSocket;

    /** Guest Socket input, null if socket is disconnected. */
    private BufferedReader input;

    /** Guest Socket output, null if socket is disconnected. */
    private PrintWriter output;



    /**
     * Initialization of Guest Communicator Window.
     * It establish event handler on enter button to send message.
     */
    public void initialize() {
        this.writeArea.setOnKeyPressed(event -> {
            if(event.getCode() == KeyCode.ENTER){
                sendMessage();
            }
        });

    }

    /**
     * This method is called when connectButton is pressed.
     * It creates new Thread, which runs Socket. This new Thread after communication is established,
     * is listening for new messages coming from Guest and showing these messages in the readArea.
     */
    @FXML public void connect() {
        if (!checkCorrectness()) { return; }
        this.connectButton.setDisable(true);
        infoLabel.setText("Write IP of your friend");
        infoLabel.setTextFill(Color.BLACK);
        Thread connector = new Thread(() -> {
            try (Socket socket = new Socket(ipAddress, portAddress)) {
                // setup streams
                this.guestSocket = socket;
                System.out.println("Connection successful");
                this.connectButton.setDisable(false);
                this.input = new BufferedReader(new InputStreamReader(guestSocket.getInputStream()));
                this.output = new PrintWriter(guestSocket.getOutputStream(), true);
                // send my nickName and obtain friend's one
                this.output.println(nickName);
                while (!this.guestSocket.isClosed()) {
                    this.friendName = this.input.readLine();
                    if (this.friendName == null) {throw new IOException("Can't obtain friend's Name"); }
                    if (!this.friendName.equals("")) { break; }
                }
                this.readArea.appendText("Connected to " + this.friendName + "\n");
                successConnectionScreen();
                // intercepting
                while (!this.guestSocket.isClosed()) {
                    String text = this.input.readLine();
                    if (text == null) {break;}
                    if (!text.equals("")) { this.readArea.appendText(this.friendName + ": " + text + "\n"); }
                }
            } catch (Exception e) {
                System.out.println("Connection error: " + e);
            }
            finally {
                try {
                    this.readArea.appendText("Disconnected\n\n");
                } catch (Exception e) {
                    System.out.println("Text addition error: " + e);
                }
                restartScreen();
                System.out.println("Session restarted");
            }
        });
        connector.start();
    }

    /** Method which checks correctness of ip, port and nickName entered by user. */
    private boolean checkCorrectness() {
        // check IP
        String ipString = ip.getCharacters().toString();
        java.util.regex.Pattern patternIP = Pattern.compile("^(([01]?\\d\\d?|2[0-4]\\d|25[0-5]).){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");
        java.util.regex.Matcher checkIP = patternIP.matcher(ipString);
        if ((!checkIP.matches()) && (!ipString.equals("localhost"))) {
            infoLabel.setText("Incorrect IP");
            infoLabel.setTextFill(Color.RED);
            return false;
        }
        this.ipAddress = ipString;

        // check port
        String portString = port.getCharacters().toString();
        java.util.regex.Pattern patternPort = Pattern.compile("^\\d{4}+$");
        java.util.regex.Matcher checkPort = patternPort.matcher(portString);
        if (!checkPort.matches()) {
            infoLabel.setText("Incorrect port");
            infoLabel.setTextFill(Color.RED);
            return false;
        }
        this.portAddress = Integer.parseInt(portString);

        // check nickName
        String nickString = nick.getCharacters().toString();
        java.util.regex.Pattern patternNick = Pattern.compile("^(\\d|\\w){1,10}$");
        java.util.regex.Matcher checkNick = patternNick.matcher(nickString);
        if (!checkNick.matches()) {
            infoLabel.setText("Incorrect nick");
            infoLabel.setTextFill(Color.RED);
            return false;
        }
        this.nickName = nickString;

        // all tests are passed
        return true;
    }

    /** Method which set up screen - enable/disable buttons and textAreas. */
    private void successConnectionScreen() {
        readArea.setDisable(false);
        writeArea.setDisable(false);
        ip.setDisable(true);
        port.setDisable(true);
        nick.setDisable(true);
        send.setDisable(false);
        connectButton.setVisible(false);
        disconnectButton.setVisible(true);
    }

    /** Method which restart screen to default - enable/disable buttons and textAreas. */
    private void restartScreen() {
        readArea.setDisable(true);
        writeArea.setDisable(true);
        ip.setDisable(false);
        port.setDisable(false);
        nick.setDisable(false);
        send.setDisable(true);
        connectButton.setVisible(true);
        disconnectButton.setVisible(false);
        connectButton.setDisable(false);
    }

    /** Method realized when send button is clicked or when enter key is pressed. */
    @FXML public void sendMessage() {
        String message = this.writeArea.getText();
        message = message.replaceAll("\n","");
        this.writeArea.clear();
        if (!message.equals("")) {
            try {
                this.output.println(message);
                this.readArea.appendText(this.nickName + ": " + message + "\n");
            } catch (Exception e) {
                System.out.println("Error with sending message: " + e);
            }
        }
    }

    /** Method realized when disconnect button is pressed. It executes closeSocket method.*/
    @FXML public void disconnect() {
        closeSocket();
    }

    /** Method closes the Socket*/
    private void closeSocket() {
        try {
            if (this.output != null) { this.output.close(); this.output = null;}
            if (this.input != null) { this.input.close(); this.input = null;}
            if (this.guestSocket != null) { this.guestSocket.close();; this.guestSocket = null;}
        } catch (IOException e){
            System.out.println("Socket closing error:  " + e);
        }
    }
}
