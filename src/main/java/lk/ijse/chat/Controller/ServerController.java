package lk.ijse.chat.Controller;

import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.awt.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class ServerController implements Initializable {
    public JFXTextField nametxt;

    Socket socket;

     DataInputStream dataInputStream;
     DataOutputStream dataOutputStream;
     ServerSocket serverSocket;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        new Thread(() -> {
            try {
                if(serverSocket==null) {
                    serverSocket = new ServerSocket(3001);
                }
                socket = serverSocket.accept();
                dataInputStream = new DataInputStream(socket.getInputStream());
                dataOutputStream = new DataOutputStream(socket.getOutputStream());

            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }


    public void Addbtnonaction(ActionEvent actionEvent) {


    }
}
