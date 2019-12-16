package application;

import java.net.URL;
import java.text.Collator;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;


public class MainController implements Initializable {
	HashMap<String, ArrayList<Port> > allDevices = new HashMap<String, ArrayList<Port> >();
	HashMap<String, ArrayList<Port> >  devicesA = new HashMap<String, ArrayList<Port> >();
	HashMap<String, ArrayList<Port> > devicesB = new HashMap<String, ArrayList<Port> >();
	HashMap<String, Integer> cable_types = new HashMap<String, Integer>();
	protected static HashMap<String, Integer> length_unit = new HashMap<String, Integer>();
	ObservableList<String> cablelist = FXCollections.observableArrayList();
	ObservableList<String> devicelistA = FXCollections.observableArrayList();
	ObservableList<String> devicelistB = FXCollections.observableArrayList();
	
	@FXML
	private ChoiceBox<String> Kabeltyp;
	
	@FXML
	private MenuItem menuSettings, menuClose, menuHelp;
	
	@FXML
	private VBox vbox;

	@FXML
	private ComboBox<String> DeviceB, DeviceA, StartIndexA, StartIndexB, DeviceTypeA, DeviceTypeB;

	@FXML
	protected Button ausgabebutton, kopierbutton, importbutton, aktualisierbtn;

	@FXML
	private TextField Ports, Kabellänge, IndexScaleA, IndexScaleB;
	
	@FXML 
	private TextArea Ausgabe;
	
	@FXML
	private Text leinheit, choose;
	
	int PortanzahlA = 0;
	int PortanzahlB = 0;
	int PortanzahlANeu = 0;
	int PortanzahlBNeu = 0;
	
	public void done () throws IOException {
//		DeviceA.getItems().removeAll(DeviceA.getItems());
//		DeviceB.getItems().removeAll(DeviceB.getItems());
		DeviceA.setItems(new SortedList<String>(devicelistA, Collator.getInstance()));
		DeviceB.setItems(new SortedList<String>(devicelistB, Collator.getInstance()));
		DeviceA.setValue("Select Device");
		IndexScaleA.setText("1");
		IndexScaleB.setText("1");
		DeviceB.setValue("-");
		Ports.setText("");
		Kabellänge.setText("");
		StartIndexA.setValue(null);
		StartIndexB.setValue(null);
		StartIndexA.getItems().removeAll(StartIndexA.getItems());
		StartIndexB.getItems().removeAll(StartIndexB.getItems());
	}
	
	// Optionen im Menüfeld angeklickt. Ein neues Fenster mit den Einstellungen wird geladen
	@FXML 
	protected void OptionenOnAction(ActionEvent event) throws IOException {
		Parent menuLayout = FXMLLoader.load(getClass().getResource("menu_FXML.fxml"));
		Scene menuScene = new Scene(menuLayout, 500, 300);
		menuScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		Main.menuStage.setTitle("Settings"); 
		Main.menuStage.getIcons().add(new Image(("file:icon1.png")));
		Main.menuStage.setScene(menuScene);
		Main.menuStage.setResizable(false);
		Main.menuStage.show();
	}
	
	@FXML 
	protected void AktOnAction(ActionEvent event) throws IOException {
		Parent mainlayout = FXMLLoader.load(getClass().getResource("main_FXML.fxml"));
		Scene scene = new Scene(mainlayout,684,662);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		Main.mainStage.setScene(scene);
	}

	@FXML 
	protected void HelpOnAction(ActionEvent event) {
		try {
			Parent helpLayout = FXMLLoader.load(getClass().getResource("hilfe_FXML.fxml"));
			Scene helpScene = new Scene(helpLayout, 600, 540);
			Stage helpWindow = new Stage();
			helpWindow.setTitle("about - netbox bulk connect");
			helpWindow.setScene(helpScene);
			helpScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			helpWindow.getIcons().add(new Image(("file:icon1.png")));
			helpWindow.setResizable(false);
			helpWindow.initModality(Modality.APPLICATION_MODAL);
			helpWindow.show();
		} catch(Exception e) {
				e.printStackTrace();
			}
	}
	
	//Schließen Button schließt die komplette Anwendung
	@FXML 
	protected void ExitOnAction(ActionEvent event) {
		Platform.exit();
	}
	
	//Kopierbutton. Alles was in der Ausgabe Textarea steht, wird in die Zwischenablage kopiert
	@FXML 
	protected void KopierenOnAction(ActionEvent event) {
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Clipboard clipboard = toolkit.getSystemClipboard();
		StringSelection strSel = new StringSelection(Ausgabe.getText());
		clipboard.setContents(strSel, null);
	}

	
	@FXML
	protected void handleAction(ActionEvent event) {
		String Standardzeile = "side_a_device,side_a_type,side_a_name,side_b_device,side_b_type,side_b_name,type,length,length_unit";
		String Ausgabezeile = "";
		String Kabel = Kabeltyp.getValue();
		String devicea = DeviceA.getValue();
		String deviceb = DeviceB.getValue();
		
		if (DeviceA.getValue().toString().equals(DeviceB.getValue().toString())) {
			Ausgabe.setText("It is not possible to connect a Device with itself.");
			return;
		}
		
		if(devicea.equals("Select Device") || devicea.equals("") || deviceb.equals("") || deviceb.equals("Select Device")) {
			Ausgabe.setText("Please correct your entries. All Fields are mandatory fields and must be filled.");
			return;
		}
			
		if (DeviceA.getValue().toString().equals("Connection failed")){
			Ausgabe.setText("The connection to the server failed. Please check the settings.");
			return;
		}
		
		else {
			ArrayList<String> Portsa = new ArrayList<String>();
			ArrayList<String> Portsb = new ArrayList<String>();

			for (int i = 0; i < (devicesA.get(devicea).size()); i++) {
				Portsa.add(devicesA.get(devicea).get(i).getName());
			}
			for (int i = 0; i < (devicesB.get(deviceb).size()); i++) {
				Portsb.add(devicesB.get(deviceb).get(i).getName());
			}
			String PortStarta = StartIndexA.getValue();

			String indexScaleAString = IndexScaleA.getText();
			if(!isInteger(indexScaleAString, 10)) {
				Ausgabe.setText("Input for index scale accepts only integer numbers.");
				return;
			}
			int indexScaleA = Integer.parseInt(indexScaleAString);

			int starta = Portsa.indexOf(PortStarta);
			for (int i = 0; i < starta; i++) {
				Portsa.remove(0);
			}
			String PortStartb = StartIndexB.getValue();

			String indexScaleBString = IndexScaleB.getText();
			if(!isInteger(indexScaleBString, 10)) {
				Ausgabe.setText("Input for index scale accepts only integer numbers.");
				return;
			}
			int indexScaleB = Integer.parseInt(indexScaleBString);

			int startb = Portsb.indexOf(PortStartb);
			for (int i = 0; i < startb; i++) {
				Portsb.remove(0);
			}

		// Test wie viele Ports pro Device vorhanden sind, die kleinere Anzahl Ports wird ausgewählt

			int portanzahla = Portsa.size();
			int portanzahlb = Portsb.size();
			int portanzahl = Math.min(portanzahla / indexScaleA, portanzahlb / indexScaleB);
			
			int ports_manuell = Integer.parseInt(Ports.getText());

			if (ports_manuell < portanzahl && ports_manuell != portanzahl) {
				portanzahl = ports_manuell;
			}
			
			else if (ports_manuell > portanzahl) {

				Ausgabe.setText("Please correct the number of ports. Maximum available port number at current index scale is : " + portanzahl);
				return;
			}

			String cc = Kabellänge.getText();
			boolean pruefzahl = true;
				
			for(int i = 0, n = cc.length(); i<n; i++ )
				if( ! Character.isDigit(cc.charAt(i))){
					pruefzahl = false;
				}
				
				if (pruefzahl==false) {
					Ausgabe.setText("Please check the cable length. Only whole numbers without decimal places can be entered");
					return;
				}

				if (portanzahl <= 0 || Kabellänge.getText().equals("")) {
					Ausgabe.setText("Please correct your entries. All Fields are mandatory fields and must be filled.");
				}
				else {
					int deviceAIndex = 0;
					int deviceBIndex = 0;
					String typeStringA = DeviceTypeA.getValue();
					Port.Type typeA = Port.Type.valueOf(typeStringA);
					String typeStringB = DeviceTypeB.getValue();
					Port.Type typeB = Port.Type.valueOf(typeStringB);
					for (int i = 0; i < portanzahl; i++) {
						String porta = Portsa.get(deviceAIndex);
						String portb = Portsb.get(deviceBIndex);
						deviceAIndex += indexScaleA;
						deviceBIndex += indexScaleB;
						Ausgabezeile = Ausgabezeile + "\n" + devicea + "," + typeA + "," + porta + "," + deviceb + "," + typeB + "," + portb + "," + Kabel + "," + Kabellänge.getText() + "," + Config.getInstance().LengthUnit;
					}
					Ausgabe.setText(Standardzeile + Ausgabezeile);
				}
		}
	}
	
	// Import
	
	@FXML protected void ImportierenOnAction(ActionEvent event) {
		try {
		Config.load("config.json");
		HTTPQuery netbox = new HTTPQuery(Config.getInstance().URL, Config.getInstance().Token);
	
		if (Config.getInstance().Token.isEmpty()) {
			Ausgabe.setText("Please check the settings. Remember that your Token must have read and write privileges.");
			return;
		}
		
		if (DeviceA.getValue().toString().equals("Connection failed"))
		{
			Ausgabe.setText("The connection to the server failed. Please check the settings.");
			return;
		}
		
		if (DeviceA.getValue().toString().equals(DeviceB.getValue().toString())) {
			Ausgabe.setText("It is not possible to connect a Device with itself.");
			return;
		}
		
		else {
			String devicea = DeviceA.getValue();
			String deviceb = DeviceB.getValue();
			
			if (devicea.equals("Select Device") || deviceb.equals("-")|| deviceb.equals("Select Device") ) {
				Ausgabe.setText("Please correct your entries. All Fields are mandatory fields and must be filled.");
			}
			
			else {
				ArrayList<Integer> PortIDa = new ArrayList<Integer>();
				ArrayList<Integer> PortIDb = new ArrayList<Integer>();
				ArrayList<String> Portsa = new ArrayList<String>();
				ArrayList<String> Portsb = new ArrayList<String>();
		
				for (int i = 0; i < (devicesA.get(devicea).size()); i++) {
					PortIDa.add(devicesA.get(devicea).get(i).getID());
					Portsa.add(devicesA.get(devicea).get(i).getName());
				}
				for (int i = 0; i < (devicesB.get(deviceb).size()); i++) {
					PortIDb.add(devicesB.get(deviceb).get(i).getID());
					Portsb.add(devicesB.get(deviceb).get(i).getName());
				}
				
				String PortStarta = StartIndexA.getValue();

				String indexScaleAString = IndexScaleA.getText();
				if(!isInteger(indexScaleAString, 10)) {
					Ausgabe.setText("Input for index scale accepts only integer numbers.");
					return;
				}
				int indexScaleA = Integer.parseInt(indexScaleAString);

				int starta = Portsa.indexOf(PortStarta);
				for (int i = 0; i < starta; i++) {
					PortIDa.remove(0);
				}
				String PortStartb = StartIndexB.getValue();

				String indexScaleBString = IndexScaleB.getText();
				if(!isInteger(indexScaleBString, 10)) {
					Ausgabe.setText("Input for index scale accepts only integer numbers.");
					return;
				}
				int indexScaleB = Integer.parseInt(indexScaleBString);

				int startb = Portsb.indexOf(PortStartb);
				for (int i = 0; i < startb; i++) {
					PortIDb.remove(0);
				}

				int portanzahla = PortIDa.size();
				int portanzahlb = PortIDb.size();

				int indexedPortCountA, indexedPortCountB;

				if (portanzahla % indexScaleA != 0) {
				    indexedPortCountA = (portanzahla / indexScaleA) + 1;
                } else {
                    indexedPortCountA = portanzahla / indexScaleA;
                }

                if (portanzahlb % indexScaleB != 0) {
                    indexedPortCountB = (portanzahlb / indexScaleB) + 1;
                } else {
                    indexedPortCountB = portanzahlb / indexScaleB;
                }

				int portanzahl = Math.min(indexedPortCountA, indexedPortCountB);
				
				int ports_manuell = Integer.parseInt(Ports.getText());

				if (ports_manuell < portanzahl && ports_manuell != portanzahl) {
					portanzahl = ports_manuell;
				}
				
				else if (ports_manuell > portanzahl) {
                    Ausgabe.setText("Please correct the number of ports. Maximum available port number at current index scale is : " + portanzahl);
					return;
				}
				
				int einheit = length_unit.get(Config.getInstance().LengthUnit);
				String streinheit = Config.getInstance().LengthUnit;
				
				if (portanzahl <= 0 || Kabellänge.getText().equals("") || Kabellänge.getText().equals("0") ) {
					Ausgabe.setText("Please correct your entries. All Fields are mandatory fields and must be filled.");
				}
				else {
					int cabletype = cable_types.get(Kabeltyp.getValue());
					String cc = Kabellänge.getText();
					boolean pruefzahl = true;
						
					for(int i = 0, n = cc.length(); i<n; i++ )
						if( ! Character.isDigit(cc.charAt(i))){
							pruefzahl = false;
						}

						if(pruefzahl) {
							int cablelength = Integer.parseInt(Kabellänge.getText());
							StringBuffer postrearports = new StringBuffer(4096);
							postrearports.append('[');
							int deviceAIndex = 0;
							int deviceBIndex = 0;
							String typeStringA = DeviceTypeA.getValue();
							Port.Type typeA = Port.Type.valueOf(typeStringA);
							String typeStringB = DeviceTypeB.getValue();
							Port.Type typeB = Port.Type.valueOf(typeStringB);
							for (int i = 0; i < portanzahl; i++) {
								int porta = PortIDa.get(deviceAIndex);
								int portb = PortIDb.get(deviceBIndex);
								deviceAIndex += indexScaleA;
								deviceBIndex += indexScaleB;
									if(i>0)
										postrearports.append(',');
										postrearports.append("{\"termination_a_type\": \"" + typeA.getApiName() + "\", \"termination_a_id\": "+ porta + ", \"termination_b_type\": \"" + typeB.getApiName() + "\", \"termination_b_id\": " + portb + ", \"type\": "+ cabletype + ", \"length_unit\": " + einheit + ", \"length\": " + cablelength + "}");
							}
							postrearports.append(']');
							netbox.post("api/dcim/cables/", postrearports.toString());
							Ausgabe.setText(devicea + " and " + deviceb + " successfully connected! \n" + portanzahl + " Ports, " + cablelength + " "+ streinheit + ", cable type " + Kabeltyp.getValue());
							devicelistA.remove(devicea);
							devicelistB.remove(deviceb);

							done();
						}
						  else {
							  Ausgabe.setText("Please check the cable length. Only whole numbers without decimal places can be entered");
						  }
				}
			}
		}
		} catch (MalformedURLException e) {
			String fehler = e.toString();
			Ausgabe.setText(fehler);
		
		} catch (IOException e) {
			String fehler = e.toString();
			Ausgabe.setText(fehler);
		}
	}
	

	// Kabelliste abfragen

	private void queryNetbox() throws Exception {
		Config.load("config.json");
		HTTPQuery netbox = new HTTPQuery(Config.getInstance().URL, Config.getInstance().Token);
		JSONObject obj = new JSONObject(netbox.query("api/dcim/_choices/?limit=0&?brief=1"));
		JSONArray arr = obj.getJSONArray("cable:type");
		cablelist.removeAll(cablelist);
		for (int i = 0; i < arr.length(); i++){
			cablelist.add(arr.getJSONObject(i).getString("label"));
			cable_types.put(arr.getJSONObject(i).getString("label"), arr.getJSONObject(i).getInt("value"));
		}
		Kabeltyp.getItems().addAll(cablelist);
		Kabeltyp.setValue("Multimode Fiber");
		leinheit.setText(Config.getInstance().LengthUnit);
	}

	private void refreshNetboxDevices() throws Exception{
		devicelistA.clear();
		devicelistB.clear();
		devicesA.clear();
		devicesB.clear();
		allDevices.clear();
		queryNetboxDevices(Port.Type.FRONTPORT);
		queryNetboxDevices(Port.Type.REARPORT);
		queryNetboxDevices(Port.Type.INTERFACE);
	}


	// Liste der Device Namen abfragen
	private void queryNetboxDevices(final Port.Type type) throws Exception  {
		Config.load("config.json");
		HTTPQuery netbox = new HTTPQuery(Config.getInstance().URL, Config.getInstance().Token);

		int offsetzähler = 0;
		boolean next;

		String apiPath = getURLPathByDeviceType(type);
		if(apiPath == null) {
			Ausgabe.setText("Can't find the API path for device type: " + type);
			return;
		}

		do {
			JSONObject obj = new JSONObject(netbox.query("api/dcim/" + apiPath + "/?brief=1&limit=100&offset=" + offsetzähler));
			JSONArray ports = obj.getJSONArray("results");

			for (int i = 0; i < ports.length(); i++) {
				String deviceName = ports.getJSONObject(i).getJSONObject("device").optString("name");
				boolean isConnected = !ports.getJSONObject(i).isNull("cable");

				if(!isConnected) {
					if(allDevices.containsKey(deviceName)) {
						ArrayList<Port> list = allDevices.get(deviceName);
						Port r = new Port(ports.getJSONObject(i).getInt("id"), ports.getJSONObject(i).getString("name"), type);
						list.add(r);
						allDevices.put(deviceName, list);
					}
					else {
						ArrayList<Port> list = new ArrayList<Port>();
						Port r = new Port(ports.getJSONObject(i).getInt("id"), ports.getJSONObject(i).getString("name"), type);
						list.add(r);
						allDevices.put(deviceName, list);
					}
				}
			}
			next = !obj.isNull("next");
			offsetzähler = offsetzähler +100;
		}while(next);


		done();
	}

	private String getURLPathByDeviceType(Port.Type type) {
		switch (type) {
			case REARPORT: return "rear-ports";
			case FRONTPORT: return "front-ports";
			case INTERFACE: return "interfaces";
			default: return null;
		}
	}


	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		Ausgabe.setEditable(false);
		length_unit.put("Meters", 1200);
		length_unit.put("Centimeters", 1100);
		length_unit.put("Inches", 1200);
		length_unit.put("Feet", 2100);
		initializeDeviceTypeDropdowns();
		try {
			refreshNetboxDevices();
			queryNetbox();
		} catch (Exception e1) {
			System.out.println("Fehler: "+e1);
			DeviceA.setValue("Connection failed");
			DeviceB.setValue("-");
			Ausgabe.setText("The connection to the server failed. Please check the settings.\n" + e1);
		}
		
		

		//Listener für das Eingabefeld Device A
		DeviceA.getSelectionModel().selectedIndexProperty()
		.addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
			
		try {	
			String deviceName = DeviceA.getValue().toString();
			ArrayList<String> Portsneu = new ArrayList<String>();
			Portsneu.removeAll(Portsneu);
			
			for (int i = 0; i < (devicesA.get(deviceName).size()); i++) {
				Portsneu.add(devicesA.get(deviceName).get(i).getName());
			}
			
			PortanzahlA = Portsneu.size();
			int PortAnzahlAutomatisch;
			
			if (DeviceA.getValue().equals("Select Device")) {
			PortAnzahlAutomatisch = Math.min(PortanzahlA, PortanzahlB);
			}
			
			else {
				PortAnzahlAutomatisch = PortanzahlA;
			}
			
			Ports.setText(Integer.toString(PortAnzahlAutomatisch));
			StartIndexA.getItems().clear();
			StartIndexA.getItems().addAll(Portsneu);
			StartIndexA.setValue(Portsneu.get(0));

			//Test ob Bindestrich vorhanden (sonst kein gültiger Name, Suche nach Device B nicht möglich)
			String searchString = "-";
			if (deviceName.contains(searchString)) {
				String[] splitstrings = deviceName.split( Pattern.quote( "-" ) );
				String combovorauswahl = splitstrings[1] + "-" + splitstrings[0]; 

				//Device wurde gesplittet. Test ob römische 2 vorhanden, sowie Test ob Device überhaupt existiert

				if (splitstrings.length <= 2) {
					if (devicelistB.contains(combovorauswahl)) {
						DeviceB.setValue(combovorauswahl);
					} 
					else {
						DeviceB.setValue("Select Device");
						StartIndexB.getItems().clear();
						StartIndexB.setValue(null);
					}
				}

				else {
					combovorauswahl = combovorauswahl + "-" + splitstrings[2];
					if (devicelistB.contains(combovorauswahl)) {
						DeviceB.setValue(combovorauswahl);
					} 
					else {
						DeviceB.setValue("Select Device");
						StartIndexB.getItems().clear();
						StartIndexB.setValue(null);
					}
				}
			}
			else {
				DeviceB.setValue("Select Device");
				StartIndexB.getItems().clear();
				StartIndexB.setValue(null);
			}
			PortanzahlANeu = 0;
		} catch (NullPointerException e) {			
		}
	});
		
		
		//Listener für das Eingabefeld Device B
		DeviceB.getSelectionModel().selectedIndexProperty()
		.addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
		try {	
			String deviceName = DeviceB.getValue().toString();
			ArrayList<String> Portsneu = new ArrayList<String>();
			Portsneu.removeAll(Portsneu);
			
			for (int i = 0; i < (devicesB.get(deviceName).size()); i++) {
				Portsneu.add(devicesB.get(deviceName).get(i).getName());
			}
			
			PortanzahlB = Portsneu.size();
			int PortAnzahlAutomatisch;
			
			if (DeviceB.getValue().equals("Select Device")) {
				PortAnzahlAutomatisch = PortanzahlB;
			}
			
			else {
				PortAnzahlAutomatisch = Math.min(PortanzahlA, PortanzahlB);
			}
			
			Ports.setText(Integer.toString(PortAnzahlAutomatisch));
			StartIndexB.getItems().clear();
			StartIndexB.getItems().addAll(Portsneu);
			StartIndexB.setValue(Portsneu.get(0));
			PortanzahlBNeu = 0;
		} catch (NullPointerException e) {			
		}
	});	
		
	//Listener für Index Start von Device A
	StartIndexA.getSelectionModel().selectedIndexProperty()
	.addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
		int alt = 0;
		int neu = 0;
		alt = (int) oldValue;
		neu = (int) newValue;
		if (alt == -1 || neu == -1) {
			return;
		}
		else {
			int Test = 0;
			int nr = (int) newValue;
			Test = PortanzahlA-nr;
			PortanzahlANeu = Test;
			int PortAnzahlAutomatisch = 0;
			
			if (PortanzahlBNeu == 0) {
				PortAnzahlAutomatisch = Math.min(PortanzahlANeu, PortanzahlB);
			}
			else {
				PortAnzahlAutomatisch = Math.min(PortanzahlANeu, PortanzahlBNeu);
			}
			Ports.setText(Integer.toString(PortAnzahlAutomatisch));
		}
	});
		
	//Listener für Index Start von Device B
	StartIndexB.getSelectionModel().selectedIndexProperty()
	.addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
		int alt = 0;
		int neu = 0;
		alt = (int) oldValue;
		neu = (int) newValue;
		if (alt == -1 || neu == -1) {
			return;
		}
		else {
			int Test = 0;
			int nr = (int) newValue;
			Test = PortanzahlB-nr;
			PortanzahlBNeu = Test;
			int PortAnzahlAutomatisch = 0;
			if (PortanzahlANeu == 0) {
				PortAnzahlAutomatisch = Math.min(PortanzahlBNeu, PortanzahlA);
			}
			else {
				PortAnzahlAutomatisch = Math.min(PortanzahlBNeu, PortanzahlANeu);
			}
			Ports.setText(Integer.toString(PortAnzahlAutomatisch));
		}
	});

	DeviceTypeA.getSelectionModel().selectedIndexProperty()
			.addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {

				devicesA.clear();
				devicelistA.clear();
				String typeString = DeviceTypeA.getValue();
				Port.Type type = Port.Type.valueOf(typeString);
				Set<String> p = allDevices.keySet();
				Iterator<String> it = p.iterator();
				while(it.hasNext()) {
					String panel = it.next();
					List<Port> list = allDevices.get(panel)
							.stream()
							.filter(port -> port.getType().equals(type))
							.collect(Collectors.toList());

					if(list.size()>1) {
						devicesA.put(panel, (ArrayList)list);
						devicelistA.add(panel);
					}
				}

			});

		DeviceTypeB.getSelectionModel().selectedIndexProperty()
				.addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {

					devicesB.clear();
					devicelistB.clear();
					String typeString = DeviceTypeB.getValue();
					Port.Type type = Port.Type.valueOf(typeString);
					Set<String> p = allDevices.keySet();
					Iterator<String> it = p.iterator();
					while(it.hasNext()) {
						String panel = it.next();
						List<Port> list = allDevices.get(panel)
								.stream()
								.filter(port -> port.getType().equals(type))
								.collect(Collectors.toList());

						if(list.size()>1) {
							devicesB.put(panel, (ArrayList)list);
							devicelistB.add(panel);
						}
					}

				});

	}

	private void initializeDeviceTypeDropdowns() {
		ObservableList<String> deviceTypes = FXCollections.observableArrayList(Arrays.stream(Port.Type.values())
				.map(Enum::name)
				.collect(Collectors.toList()));

		DeviceTypeA.getItems().clear();
		DeviceTypeA.setItems(deviceTypes);
		DeviceTypeA.setValue("Choose type");

		DeviceTypeB.getItems().clear();
		DeviceTypeB.setItems(deviceTypes);
		DeviceTypeB.setValue("Choose type");
	}

	private boolean isInteger(String s, int radix) {
		if(s.isEmpty()) return false;
		for(int i = 0; i < s.length(); i++) {
			if(i == 0 && s.charAt(i) == '-') {
				if(s.length() == 1) return false;
				else continue;
			}
			if(Character.digit(s.charAt(i),radix) < 0) return false;
		}
		return true;
	}

}
