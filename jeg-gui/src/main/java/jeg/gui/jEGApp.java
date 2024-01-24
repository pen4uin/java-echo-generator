package jeg.gui;

import com.formdev.flatlaf.FlatLightLaf;
import jeg.gui.form.jEGForm;

import javax.swing.*;

public class jEGApp {
    public static void main(String[] args) {
        FlatLightLaf.setup();
        SwingUtilities.invokeLater(jEGApp::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        jEGForm.start();
    }
}
