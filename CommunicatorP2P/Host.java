package CommunicatorP2P;

import javafx.fxml.FXML;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.regex.Pattern;

/**
 * This class is a controller class for Guest application
 * As GUI is used host.fxml
 * <p>
 * @author Piotr Klukowski
 * @version 1.0
 * @see Main
 * @see Controller
 * @see Host
 */
public class Host {
	
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
	
	/**When it is clicked, program sends message*/
    @FXML private javafx.scene.control.Button send;
	
	/**Area where received and sent text is shown*/
    @FXML private javafx.scene.control.TextArea readArea;
	
	/**Area where user writes text to send*/
    @FXML private javafx.scene.control.TextArea writeArea;
	
	/**Uneditable area with user's IP information*/
    @FXML private javafx.scene.control.TextField ip;
	
	/**Uneditable area with user's PC information*/
    @FXML private javafx.scene.control.TextField pcName;
	
	/**
     * Editable area before connection, it gets nickName from User. As Default is set to nickname: Host.
     * It may only contain max 10 chars, which can only be letters or digits.
     */
    @FXML private javafx.scene.control.TextField nick;
	
	/** Editable area before connection, it gets PortNumber from User. As Default is set to port: 5000.*/
    @FXML private javafx.scene.control.TextField port;
	
	/** Uneditable area, it inform user if something in input data is wrong */
    @FXML private javafx.scene.control.Label infoLabel;

	/** Connection socket, null if disconnected*/
    private Socket hostSocket;
	
	/** Host Socket input, null if socket is disconnected*/
    private BufferedReader input;
	
	/** Host Socket output, null if socket is disconnected*/
    private PrintWriter output;
	
	/** User nickName, it can only contain letters and digits, max 10*/
    private String nickName;
	
	/** User friend's nickName, chars are controlled by friend's application*/
    private String friendName;
	
	/** User defines portNumber, however it is controlled by checkCorrectness method*/
    private int portNumber;
	
	/** Server socket, null if disconnected*/
    private ServerSocket serverSocket;



	/**
     * Initialization of Host Communicator Window. It getting local host name and host address.
     * Additionally it prints them into a screen.
     */
    public void initialize() {
        System.out.println("Host GUI starts");
        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            this.ip.setText(inetAddress.getHostAddress());
            this.pcName.setText(inetAddress.getHostName());
        }
        catch (Exception e) {
            System.out.println("Error with address");
            this.ip.setText("Unknown");
            this.pcName.setText("Unknown");
        }
        this.writeArea.setOnKeyPressed(event -> {
            if(event.getCode() == KeyCode.ENTER){
                sendMessage();
            }
        });
    }

	/**
     * This method is called when connectButton is pressed.
     * It creates new Thread, which runs Server Socket and Socket.
     * This new Thread after communication is established, is listening
     * for new messages coming from Guest and showing these messages in the readArea.
     */
    @FXML public void connect() {
        // check port and nickName
        if (!checkCorrectness()) { return; }
        this.connectButton.setDisable(true);
        infoLabel.setText("Your address");
        infoLabel.setTextFill(Color.BLACK);
        // try to connect
        System.out.println("I'm waiting for connection: ");
        Thread connector = new Thread(() -> {
            successConnectionScreen();
            this.writeArea.setDisable(true);
            this.send.setDisable(true);
            try (ServerSocket serverSocket = new ServerSocket(this.portNumber)) {
                this.readArea.appendText("I'm waiting for yours friend connection\n");
                this.serverSocket = serverSocket;
                try (Socket check = serverSocket.accept()) {
                    // setup streams
                    this.hostSocket = check;
                    this.input = new BufferedReader(new InputStreamReader(this.hostSocket.getInputStream()));
                    this.output = new PrintWriter(this.hostSocket.getOutputStream(), true);
                    System.out.println("Connection successful");
                    // send and get nickName
                    this.output.println(this.nickName);
                    while (!this.hostSocket.isClosed()) {
                        this.friendName = this.input.readLine();
                        if (this.friendName == null) { throw new IOException("Can't obtain friend's Name"); }
                        if (!this.friendName.equals("")) { break; }
                    }
                    this.readArea.appendText("Connected to " + this.friendName + "\n");
                    this.writeArea.setDisable(false);
                    this.send.setDisable(false);
                    // listening
                    while (!this.hostSocket.isClosed()) {
                        String text = this.input.readLine();
                        if (text == null) {break;}
                        if (!text.equals(""))  {this.readArea.appendText(this.friendName + ": " + text + "\n"); }
                    }
                } catch (Exception e) {
                    System.out.println("Socket closed");
                }
            } catch (Exception e) {
                System.out.println("Server Socket problem: " + e);
            }
            finally {
                try {
                    this.readArea.appendText("Disconnected\n\n");
                } catch (Exception e) {
                    System.out.println("Text addition error: " + e);
                }
                restartScreen();
                this.hostSocket = null;
                this.serverSocket = null;
                System.out.println("Session restarted");
            }
        });
        connector.start();
    }

	/** Method which set up screen - enable/disable buttons and textAreas */
    private void successConnectionScreen() {
        readArea.setDisable(false);
        writeArea.setDisable(false);
        port.setDisable(true);
        nick.setDisable(true);
        send.setDisable(false);
        connectButton.setVisible(false);
        disconnectButton.setVisible(true);
    }

	/** Method which restart screen to default - enable/disable buttons and textAreas */
    private void restartScreen() {
        readArea.setDisable(true);
        writeArea.setDisable(true);
        port.setDisable(false);
        nick.setDisable(false);
        send.setDisable(true);
        connectButton.setVisible(true);
        disconnectButton.setVisible(false);
        connectButton.setDisable(false);
    }

	/** Method which checks correctness of port and nickName entered by user. */
    private boolean checkCorrectness () {
        // check port
        String portString = port.getCharacters().toString();
        java.util.regex.Pattern patternPort = Pattern.compile("^\\d{4}+$");
        java.util.regex.Matcher checkPort = patternPort.matcher(portString);
        portNumber = Integer.parseInt(portString);
        if (!checkPort.matches() || portNumber > 65535 || portNumber < 1024) {
            infoLabel.setText("Incorrect port");
            infoLabel.setTextFill(Color.RED);
            return false;
        }
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
        return true;
    }

	/** Method realized when send button is clicked or when enter key is pressed*/
    @FXML public void sendMessage() {
        String message = this.writeArea.getText();
        this.writeArea.clear();
        message = message.replaceAll("\n","");
        if (!message.equals("")) {
            try {
                this.output.println(message);
                this.readArea.appendText(this.nickName + ": " + message + "\n");
            } catch (Exception e) {
                System.out.println("Error with sending message: " + e);
            }
        }
    }
	
	/** Method realized when disconnect button is pressed. It executes closeSocket and closeServer methods*/
    @FXML public void disconnect() {
        closeSocket();
        closeServer();
    }

	/** Method closes the Socket*/
    private void closeSocket() {
        try {
            if (this.output != null) { this.output.close(); this.output = null;}
            if (this.input != null) { this.input.close(); this.input = null;}
            if (this.hostSocket != null) { this.hostSocket.close(); this.hostSocket = null;}
        } catch (Exception e) {
            System.out.println("Socket closing error:  " + e);
        }
    }

	/** Method closes the ServerSocket*/
    private void closeServer() {
        try {
            if (this.serverSocket != null) { this.serverSocket.close(); this.hostSocket = null;}
        } catch (Exception e) {
            System.out.println("Server socket closing error:  " + e);
        }
    }

}
