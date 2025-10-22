//mkdir out
//javac -d out -sourcepath src src/com/quizvault/Main.java
//java -cp out com.quizvault.Main
package com.quizvault;

import com.quizvault.gui.MainMenuFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Main class to run the QuizVault application.
 */
public class Main {
    public static void main(String[] args) {
        // Run the GUI creation on the Event Dispatch Thread
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    // Set a clean system look and feel
                    // This will make it look like a modern Windows/Mac/Linux app
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                new MainMenuFrame(); // Create and show the main menu
            }
        });
    }
}