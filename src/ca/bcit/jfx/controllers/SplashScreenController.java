package ca.bcit.jfx.controllers;

import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane; 

public class SplashScreenController {

	@FXML 
	private ImageView splashScreenEONLogo;
	@FXML
	private ImageView splashScreenBCITLogo;
	@FXML
	private AnchorPane splashScreenPane;

	@FXML
	public void initialize() {
		String path = "file:" + System.getProperty("user.dir") + "/src/ca/bcit/jfx/res/images/bg.png";
		splashScreenPane.setStyle("-fx-background-image: url(\"" + path + "\"); -fx-background-size: cover;");
		displayBCITLogo();
		displayEONLogo();
	}

	private void displayBCITLogo() {
		Image bcitLogo = new Image(getClass().getResourceAsStream("/ca/bcit/jfx/res/images/LogoBCIT.png"));
		splashScreenBCITLogo.setFitHeight(100); 
		splashScreenBCITLogo.setFitWidth(100); 
		splashScreenBCITLogo.setLayoutX(30); 
		splashScreenBCITLogo.setLayoutY(30); 
		splashScreenBCITLogo.setPreserveRatio(true);
		splashScreenBCITLogo.setPickOnBounds(true);
		splashScreenBCITLogo.setImage(bcitLogo);
	}

	private void displayEONLogo() {
		Image eonLogo = new Image(getClass().getResourceAsStream("/ca/bcit/jfx/res/images/LogoEON.png"));
		splashScreenEONLogo.setFitHeight(125); 
		splashScreenEONLogo.setFitWidth(125); 
		splashScreenEONLogo.setLayoutX(450); 
		splashScreenEONLogo.setLayoutY(250); 
		splashScreenEONLogo.setPreserveRatio(true);
		splashScreenEONLogo.setPickOnBounds(true);
		splashScreenEONLogo.setImage(eonLogo);
	}
}