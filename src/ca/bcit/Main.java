package ca.bcit;

import com.sun.javafx.application.LauncherImpl;

import javax.swing.*;

public class Main {
	public static void main(String[] args) {
		try {
			LauncherImpl.launchApplication(Application.class, SplashScreen.class, args);
		}
		catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Fatal error occurred: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}
}
