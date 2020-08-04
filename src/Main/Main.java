package Main;

import Game.Game;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;


public class Main extends Application {
    @FXML
    TextField maxTime;
    @FXML
    TextField minTime;
    @FXML
    Label gameStart;

    public void initNewGame(ActionEvent event) {
        try {
            int maxT = Integer.parseInt(maxTime.getText());
            int minT = Integer.parseInt(minTime.getText());
            if (maxT < minT)
                gameStart.setText("Try again");
            else {
                Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
                new Game(maxT, minT).start(window);
            }
        } catch (Exception e) {
            gameStart.setText("Enter integer value in seconds");
        }
    }


    public void playNewGame(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("GameSettings.fxml"));
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(new Scene(root));
        window.show();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = null;
        if (Database.getInstance().signedInPlayer == null) {
            root = FXMLLoader.load(getClass().getResource("SignIn.fxml"));
            MediaPlayer BGPlayer = new MediaPlayer(new Media(new File("src/resources/sounds/bg.mpeg").toURI().toString()));
            BGPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            BGPlayer.play();
        }
        else
            root = FXMLLoader.load(getClass().getResource("Main.fxml"));
        primaryStage.setScene(new Scene(root));
        primaryStage.setResizable(false);
        primaryStage.sizeToScene();
        primaryStage.setTitle("Space Invaders");
        primaryStage.getIcons().add(new Image("resources/images/0.png"));
        primaryStage.show();
    }


    public static void main(String[] args) {
        Database.getInstance().loadPlayers();
        launch(args);
    }

    public void exit(ActionEvent event) {
        System.exit(1);
    }

    @FXML
    TextField usernameField;
    @FXML
    TextField passwordField;
    @FXML
    Label accLabel;

    public void signInAccount(ActionEvent event) throws IOException {
        String username = usernameField.getText();
        String password = passwordField.getText();
        Player player = Database.getInstance().getPlayerByUsername(username);
        if (player == null) {
            accLabel.setText("account not found");
        } else if (!player.getPassword().equals(password)) {
            accLabel.setText("wrong password");
        } else {
            Database.getInstance().signedInPlayer = player;
            Parent root = FXMLLoader.load(getClass().getResource("Main.fxml"));
            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
            window.setScene(new Scene(root));
            window.show();
        }
    }

    public void signUpAccount(ActionEvent event) throws IOException {
        String username = usernameField.getText();
        String password = passwordField.getText();
        Player player = Database.getInstance().getPlayerByUsername(username);
        if (player != null) {
            accLabel.setText("username exists");
        } else {
            if (!username.matches("\\S+"))
                accLabel.setText("wrong username format");
            else if (!password.matches("\\S+"))
                accLabel.setText("wrong password format");
            else {
                player = new Player(username, password, 0);
                Database.getInstance().signedInPlayer = player;
                Database.getInstance().savePlayer(player);
                Parent root = FXMLLoader.load(getClass().getResource("Main.fxml"));
                Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
                window.setScene(new Scene(root));
                window.show();
            }
        }
    }

    public void editAccount(ActionEvent event) throws IOException {
        String username = usernameField.getText();
        String password = passwordField.getText();
        Player player = Database.getInstance().getPlayerByUsername(username);
        if (player != null && player != Database.getInstance().signedInPlayer) {
            accLabel.setText("username exists");
        } else {
            if (!username.matches("\\S+"))
                accLabel.setText("wrong username format");
            else if (!password.matches("\\S+"))
                accLabel.setText("wrong password format");
            else {
                Database.getInstance().signedInPlayer.setUsername(username);
                Database.getInstance().signedInPlayer.setPassword(password);
                Database.getInstance().savePlayer(Database.getInstance().signedInPlayer);
                Parent root = FXMLLoader.load(getClass().getResource("Main.fxml"));
                Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
                window.setScene(new Scene(root));
                window.show();
            }
        }
    }

    public void goToMainMenu(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("Main.fxml"));
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(new Scene(root));
        window.show();
    }

    public void showAccountMenu(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("Account.fxml"));
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(new Scene(root));
        window.show();
    }

    public void showScoreboard(ActionEvent event) throws IOException {
        TableView tableView = new TableView();
        tableView.setLayoutX(305);
        tableView.setLayoutY(400);
        tableView.setMaxSize(190, 150);
        TableColumn<String, Player> column1 = new TableColumn<>("Rank");
        column1.setCellValueFactory(new PropertyValueFactory<>("password"));
        column1.setPrefWidth(40);
        column1.setResizable(false);

        TableColumn<String, Player> column2 = new TableColumn<>("Player");
        column2.setCellValueFactory(new PropertyValueFactory<>("username"));
        column2.setPrefWidth(80);
        column2.setResizable(false);

        TableColumn<String, Player> column3 = new TableColumn<>("Score");
        column3.setCellValueFactory(new PropertyValueFactory<>("score"));
        column3.setPrefWidth(50);
        column3.setResizable(false);

        tableView.getColumns().add(column1);
        tableView.getColumns().add(column2);
        tableView.getColumns().add(column3);

        for (Player player : Database.getInstance().getSortedPlayers()) {
            tableView.getItems().add(player);
        }

        Parent root = FXMLLoader.load(getClass().getResource("Scoreboard.fxml"));
        AnchorPane pane = new AnchorPane();
        pane.getChildren().addAll(root, tableView);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(new Scene(pane));
        window.show();
    }
}