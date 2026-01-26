package interfaceui.matching;

import javax.swing.*;
import java.awt.*;

public class MatchingFrame extends JFrame {

    public MatchingFrame() {
        setTitle("CV ↔ Job Matching (Recommended Jobs)");
        setSize(1000, 650);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(new MatchingPanel(), BorderLayout.CENTER);
    }
}