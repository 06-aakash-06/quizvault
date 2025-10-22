package com.quizvault.round;

/**
 * A concrete implementation of a Round. [cite: 344]
 */
public class NormalRound extends Round {
    public NormalRound(String roundName) {
        super(roundName);
    }

    @Override
    public void displayRound() { 
        // This method's logic is primarily for backend or console-based display.
        // In the GUI, the frame will handle displaying the round details.
        System.out.println("Displaying Normal Round: " + this.roundName);
    }
}