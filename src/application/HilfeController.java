package application;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class HilfeController implements Initializable {

@FXML private ScrollPane scroll;

@FXML private Text hilfetext;

	
@FXML
private Button exitbutton;



@FXML 
protected void ExitOnAction(ActionEvent event) {
    Stage stage = (Stage) exitbutton.getScene().getWindow();
    stage.close();
}
	
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub

	}

}
