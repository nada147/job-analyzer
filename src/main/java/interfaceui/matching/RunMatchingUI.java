package interfaceui.matching;

import javax.swing.SwingUtilities;

public class RunMatchingUI {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MatchingFrame().setVisible(true));
    }
}