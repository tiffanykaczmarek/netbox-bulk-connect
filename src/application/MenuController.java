package application;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.text.Text;


public class MenuController implements Initializable {
	ObservableList<String> units = FXCollections.observableArrayList();

	@FXML
	private Button exitbutton, speicherbutton;
	
	@FXML
	private TextField Server, Token;
	
	@FXML
	private ComboBox<String> LengthUnit;
	
	@FXML
	private RadioButton Auswahlhttp, Auswahlhttps;
	
	@FXML
	private ToggleGroup toggleGroup;
	
	@FXML
	private Text gespeichert;
	

	//Button "Speichern" angeklickt 
	
	public void mainaufrufen () throws IOException {
		Parent mainlayout = FXMLLoader.load(getClass().getResource("main_FXML.fxml"));
		Scene scene = new Scene(mainlayout,684,662);
		Main.mainStage.setResizable(false);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		Main.mainStage.setScene(scene);
	}
	
	@FXML 
	protected void SpeichernOnAction(ActionEvent event) throws IOException {
		String server = Server.getText();
		String url = ((RadioButton) toggleGroup.getSelectedToggle()).getText();
		String token = Token.getText();
		String lunit = LengthUnit.getValue();
		
		//Test auf Slash am Ende
		String slashtest = server.substring(server.length()-1);
		if (slashtest.equals("/")) {
			server = server.substring(0, server.length()-1);
		}
		
        // Konfigurationsdatei laden, falls vorhanden. 
        // Ansonsten werden die Werte im Konstruktor verwendet 
        Config.load("config.json"); 

        // Zugriff auf Attribut, Attribute neu setzen
        Config.getInstance().URL = url+server; 
        Config.getInstance().Token = token;   
        Config.getInstance().LengthUnit = lunit;  

        // Speichern der Konfigurationsdatei 
        Config.getInstance().toFile("config.json"); 
        
        Main.menuStage.close();
        mainaufrufen();
	}
	
	@FXML 
	protected void ExitOnAction(ActionEvent event) throws IOException {
		Main.menuStage.close();
	}
	
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
	
		String url = Config.getInstance().URL;
		Token.setText(Config.getInstance().Token);
		
		String[] splitstring = url.split( Pattern.quote( "://" ) );
		String servername = splitstring[1]; 

			if (Config.getInstance().URL.contains( "http:" )) {
				Auswahlhttp.setSelected(true);
			}
			else if (Config.getInstance().URL.contains("https:")) {
				Auswahlhttps.setSelected(true);
			}
			
		Server.setText(servername);
	    units.add("Meters");
	    units.add("Centimeters");
	    units.add("Inches");
	    units.add("Feet");
	    LengthUnit.setItems(units);
	    LengthUnit.setValue(Config.getInstance().LengthUnit);
	}

}
