package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;

import javafx.scene.image.Image;



public class Main extends Application {
	
protected static Stage menuStage = new Stage();
protected static Stage mainStage = new Stage();

	
	@Override
	public void start(Stage primaryStage) {
		try {
			Parent root = FXMLLoader.load(getClass().getResource("main_FXML.fxml"));
			Scene scene = new Scene(root,690,645);
			
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			mainStage.setTitle("netbox bulk connect"); 
			mainStage.getIcons().add(new Image(("file:icon.png")));
			mainStage.setScene(scene);
			mainStage.setMaxHeight(670);
			mainStage.setMaxWidth(690);
			mainStage.setResizable(false);
			mainStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) { 
		launch(args);
		Config.load("config.json"); 
	}

}
