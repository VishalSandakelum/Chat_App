package lk.ijse.chat.Controller;

import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

import java.awt.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class ServerController implements Initializable {
    public JFXTextField nametxt;

    Socket socket;
    static String name;

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

            String message="";
            while (!message.equals("finish")) {
                try {
                    message = dataInputStream.readUTF();
                    if(message!=null) {
                        System.out.println("Client: " + message);
                        sendmessage(message);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }).start();

    }


    public void Addbtnonaction(ActionEvent actionEvent) {
        if(nametxt.getText()!=null&&nametxt.getText()!="") {
            name = nametxt.getText();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/Client.fxml"));
            Parent root1 = null;
            try {
                root1 = (Parent) fxmlLoader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Stage stage = new Stage();
            stage.setScene(new Scene(root1));
            stage.show();
            stage.setTitle(name);
            nametxt.setText("");
        }else{
            nametxt.requestFocus();
            nametxt.setFocusColor(Paint.valueOf("Red"));
        }
    }

    private void sendmessage(String message){
        new Thread(() -> {
            String reply;
            reply = message;
            if(reply!=null) {
                try {
                    dataOutputStream.writeUTF(reply);
                    dataOutputStream.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}
