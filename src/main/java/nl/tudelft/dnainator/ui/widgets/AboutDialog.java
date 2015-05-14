package nl.tudelft.dnainator.ui.widgets;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Creates a {@link Alert} displaying information about the
 * application.
 */
public class AboutDialog {
	private Alert alert;
	private Node parent;
	private Properties prop;
	private static final String LOGO = "/ui/icons/dnainator128x128.png";
	private static final String STYLE = "/ui/style.css";
	private static final String PROPERTIES = "config.properties";
	
	/**
	 * Instantiates a new AboutDialog.
	 * It will set up all the necessities for displaying 
	 * information of the application.
	 * @param parent The parent {@link Node} of this dialog.
	 */
	public AboutDialog(Node parent) {
		this.parent = parent;
		setupDialog();
	}

	private void setupDialog() {
		alert = new Alert(Alert.AlertType.NONE);
		alert.setTitle("About DNAinator");
		alert.setResizable(true);
		alert.getDialogPane().getStylesheets().add(getClass().getResource(STYLE).toString());
		alert.initOwner(parent.getScene().getWindow());
		
		ImageView img = new ImageView();
		img.setImage(new Image(getClass().getResourceAsStream(LOGO)));
		alert.setGraphic(img);
		readProperties();
		
		alert.setHeaderText("DNAinator\nDNA network visualization tool");
		alert.setContentText(contentText());
		
		ButtonType close = new ButtonType("Close", ButtonData.CANCEL_CLOSE);
		alert.getDialogPane().getButtonTypes().add(close);
	}

	private String contentText() {
		StringBuilder contents = new StringBuilder();
		contents.append("Version: " + prop.getProperty("version") + "\n");
		contents.append("Jente Hidskes, Gerlof Fokkema, Owen Huang, "
				+ "Skip Lentz, Piet van Agtmaal");
		return contents.toString();
	}
	
	private void readProperties() {
		prop = new Properties();
		try (InputStream in = getClass().getClassLoader().getResourceAsStream(PROPERTIES)) {
			prop.load(in);
		} catch (IOException e) {
			new ExceptionDialog(parent, e, "Error reading properties.");
		}
	}

	/**
	 * Shows the {@link Alert} if it is not null.
	 * The {@link Alert} will block until user input is received. 
	 */
	public void show() {
		if (alert != null) {
			alert.showAndWait();
		}
	}
}
