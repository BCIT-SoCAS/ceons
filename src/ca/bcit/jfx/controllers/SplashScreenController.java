package ca.bcit.jfx.controllers;

import javafx.fxml.FXML;
import javafx.scene.layout.*;
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
		BackgroundSize bgSize = new BackgroundSize(100, 100, true, true, false, true);
		BackgroundImage bg = new BackgroundImage(new Image(getClass().getResourceAsStream("/ca/bcit/jfx/res/images/bg.png")),
			BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, bgSize);
		splashScreenPane.setBackground(new Background(bg));

		displayBCITLogo();
		displayEONLogo();
	}

	private void displayBCITLogo() {
		Image bcitLogo = new Image(getClass().getResourceAsStream("/ca/bcit/jfx/res/images/LogoBCIT.png"));
		splashScreenBCITLogo.setImage(bcitLogo);
	}

	private void displayEONLogo() {
		Image eonLogo = new Image(getClass().getResourceAsStream("/ca/bcit/jfx/res/images/LogoEON.png"));
		splashScreenEONLogo.setImage(eonLogo);
	}
}