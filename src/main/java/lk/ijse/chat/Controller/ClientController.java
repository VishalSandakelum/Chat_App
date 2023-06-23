package lk.ijse.chat.Controller;

import com.jfoenix.controls.JFXTextArea;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.*;
import java.awt.event.KeyEvent;
import javafx.event.ActionEvent;
import javafx.util.Duration;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class ClientController implements Initializable {
    public TextField typetxt;
    public Button sendbtn;
    public JFXTextArea textarea;
    public VBox messagContainer;
    public ScrollPane scrollPane;
    public Button emojibtn;
    public Button Camerabtn;
    public ScrollPane pane;

    static boolean openWindow = false;

    private static final double PANE_HEIGHT = 500;

    Socket socket;

     DataInputStream dataInputStream;
     BufferedReader reader;
     DataOutputStream dataOutputStream;

    public ClientController() throws IOException {
    }

    public void sendbtnonAction(ActionEvent actionEvent) throws IOException {
        new Thread(() -> {
        String reply;
            try {
                    reply = typetxt.getText();
                    if(reply!=null) {
                        LocalTime currentTime = LocalTime.now();
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
                        String time = currentTime.format(formatter);
                        dataOutputStream.writeUTF(reply+"\n"+time);
                        dataOutputStream.flush();
                        appendMessageMe("Me :"+ reply, "-fx-border-color: #11D2E5; -fx-background-color: #34E9FA; -fx-background-radius: 20px 0px 20px 20px; -fx-border-radius: 20px 0px 20px 20px;");
                    }
                    typetxt.clear();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }).start();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        closePane();
        new Thread(() -> {
        try {
            if(socket==null) {
                socket = new Socket("localhost", 3001);
            }
            dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
            openWindow = true;
        } catch (IOException e) {

            e.printStackTrace();
        }

            String message;
            while (true) {
                try {
                    message = dataInputStream.readUTF();
                    if(message!= null) {
                        System.out.println("Server: " + message);
                        appendMessage("Server: " + message, "-fx-border-color: #CF76FF; -fx-background-color: #CF76FF; -fx-background-radius: 0px 20px 20px 20px; -fx-border-radius: 0px 20px 20px 20px;");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }).start();

        try {
            Thread.sleep(500);
            pane.setVisible(true);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private void appendMessage(String message, String style) {
        Platform.runLater(() -> {
            Label messageLabel = new Label(message);
            messageLabel.setWrapText(true);
            //messageLabel.setPrefWidth(200);
            messageLabel.setPadding(new Insets(10));
            messageLabel.setStyle(style);

            HBox messageContainer = new HBox(messageLabel);
            messageContainer.setAlignment(Pos.TOP_LEFT);
            messageContainer.setPadding(new Insets(10));
            messageContainer.setFillHeight(true);

            VBox chatBubble = new VBox(messageContainer);
            chatBubble.setAlignment(Pos.TOP_LEFT);
            chatBubble.setPadding(new Insets(5));

            messagContainer.getChildren().add(chatBubble);
        });
    }
    private void appendMessageMe(String message, String style) {
        Platform.runLater(() -> {
            Label messageLabel = new Label(message);
            messageLabel.setWrapText(true);
            //messageLabel.setPrefWidth(200);
            messageLabel.setPadding(new Insets(10));
            messageLabel.setStyle(style);

            messageLabel.setPrefHeight(Region.USE_COMPUTED_SIZE);

            HBox messageContainer = new HBox(messageLabel);
            messageContainer.setAlignment(Pos.TOP_RIGHT);
            messageContainer.setPadding(new Insets(10));
            messageContainer.setFillHeight(true);

            Label timeLabel = new Label(getCurrentTime());
            timeLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #888888;");
            timeLabel.setPadding(new Insets(0, 0, 5, 0));
            HBox.setHgrow(timeLabel, Priority.ALWAYS);

            VBox chatBubble = new VBox(timeLabel, messageContainer);
            chatBubble.setAlignment(Pos.TOP_RIGHT);
            chatBubble.setPadding(new Insets(5));

            messagContainer.getChildren().add(chatBubble);
        });
    }

    public void emojibtnonAction(ActionEvent actionEvent) throws AWTException {
        if(openWindow==true){
            openPane();
            typetxt.requestFocus();
            openWindow = false;
        }else {
            closePane();
            openWindow = true;
        }
    }

    private String getCurrentTime() {
        LocalTime currentTime = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        return currentTime.format(formatter);
    }

    public void CamerabtnonAction(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Photo");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        File selectedFile = fileChooser.showOpenDialog(null);
        typetxt.requestFocus();
        if (selectedFile != null) {
            sendFileToServer(selectedFile);
            displayFileInScrollPane(selectedFile);
        }
    }

    private void sendPhoto(File photoFile){
        try {
            FileInputStream fileInputStream = new FileInputStream(photoFile);
            byte[] photoData = new byte[(int) photoFile.length()];
            fileInputStream.read(photoData);

            dataOutputStream.write(photoData);
            dataOutputStream.flush();

            String message = "Sent a photo: " + photoFile.getName();
            appendMessageMe(message, "-fx-border-color: #11D2E5; -fx-background-color: #34E9FA; -fx-background-radius: 20px 0px 20px 20px; -fx-border-radius: 20px 0px 20px 20px;");

            fileInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void gringbigeyesOnAction(ActionEvent actionEvent) {
        typetxt.appendText(convertEmojiCode("U+1f603"));
    }

    public void gringfacesmileonAction(ActionEvent actionEvent) {
        typetxt.appendText(convertEmojiCode("U+1f604"));
    }

    public void smillyonAction(ActionEvent actionEvent) {
        typetxt.appendText(convertEmojiCode("U+1f642"));
    }

    public void upsidedownonaction(ActionEvent actionEvent) {
        typetxt.appendText(convertEmojiCode("U+1f643"));
    }

    public void facewithtearsjoyonAction(ActionEvent actionEvent) {
        typetxt.appendText(convertEmojiCode("U+1f602"));
    }

    public void rollingfacewithtearsjoyonAction(ActionEvent actionEvent) {
        typetxt.appendText(convertEmojiCode("U+1f923"));
    }

    public void vinkifaceonAction(ActionEvent actionEvent) {
        typetxt.appendText(convertEmojiCode("U+1f609"));
    }

    public void savoringfoodonAction(ActionEvent actionEvent) {
        typetxt.appendText(convertEmojiCode("U+1f60B"));
    }

    private void openPane() {
        TranslateTransition transition = new TranslateTransition(Duration.seconds(0.5), pane);
        transition.setToY(0);
        transition.play();
    }

    private void closePane() {
        TranslateTransition transition = new TranslateTransition(Duration.seconds(0.5), pane);
        transition.setToY(PANE_HEIGHT);
        transition.play();
    }

    private String convertEmojiCode(String emojiCode) {
        int codePoint = Integer.parseInt(emojiCode.substring(2), 16);
        return new String(Character.toChars(codePoint));
    }

    private void sendFileToServer(File file) {
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            byte[] fileData = new byte[(int) file.length()];
            fileInputStream.read(fileData);

            dataOutputStream.write(fileData);
            dataOutputStream.flush();

            fileInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void displayFileInScrollPane(File file) {
        Platform.runLater(() -> {
            try {
                ImageView imageView = new ImageView(new Image(file.toURI().toString()));
                imageView.setFitWidth(105);
                imageView.setPreserveRatio(true);

                VBox imageContainer = new VBox(imageView);
                imageContainer.setAlignment(Pos.CENTER_RIGHT);
                imageContainer.setPadding(new Insets(10));
                imageContainer.setSpacing(10);

                Label textLabel = new Label("Me : ");
                textLabel.setWrapText(true);
                textLabel.setAlignment(Pos.CENTER_RIGHT);
                VBox.setMargin(textLabel, new Insets(0, 10, 0, 0));

                HBox imageBox = new HBox(textLabel, imageContainer);
                imageBox.setAlignment(Pos.CENTER_RIGHT);
                imageBox.setSpacing(10);

                messagContainer.getChildren().add(imageBox);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }


}
