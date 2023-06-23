package lk.ijse.chat.Controller;

import com.jfoenix.controls.JFXTextArea;
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
    public TextField typetxt;
    public Button sendbtn;
    public JFXTextArea textarea;

     Socket socket;

     DataInputStream dataInputStream;
     BufferedReader reader;
     DataOutputStream dataOutputStream;
     ServerSocket serverSocket;
     Label label = new Label("Blue Label");
     Pane pane;

    public void sendbtnonAction(ActionEvent actionEvent) throws IOException {
        new Thread(() -> {
        String reply;
                reply = typetxt.getText();
                if(reply!=null) {
                    try {
                        dataOutputStream.writeUTF(reply);
                        dataOutputStream.flush();
                        textarea.appendText("\n"+"Me :"+reply);
                        label.setText("ok");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                typetxt.clear();
        }).start();
    }

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
                        if (message.startsWith("image:")) {
                            String imagePath = message.substring(6);
                            //displayImageInScrollPane(new File(imagePath));
                        }else {
                            System.out.println("Client: " + message);
                            textarea.appendText("\n" + "Client: " + message);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }).start();
    }


}
