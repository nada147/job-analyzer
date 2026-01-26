package interfaceui.matching;

import ml.matching.MatchResult;

import javax.swing.*;
import java.awt.*;

public class MatchResultRenderer extends JPanel implements ListCellRenderer<MatchResult> {

    private final JLabel titleLabel = new JLabel();
    private final JProgressBar bar = new JProgressBar(0, 100);

    public MatchResultRenderer() {
        setLayout(new BorderLayout(8, 6));
        setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));

        bar.setStringPainted(true);

        add(titleLabel, BorderLayout.CENTER);
        add(bar, BorderLayout.EAST);
    }

    @Override
    public Component getListCellRendererComponent(
            JList<? extends MatchResult> list,
            MatchResult value,
            int index,
            boolean isSelected,
            boolean cellHasFocus
    ) {
        String title = value.getJobTitle();
        int pct = (int) Math.round(value.getScore() * 100);

        titleLabel.setText(title);
        bar.setValue(pct);
        bar.setString(pct + "%");

        if (isSelected) {
            setBackground(list.getSelectionBackground());
            titleLabel.setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            titleLabel.setForeground(list.getForeground());
        }
        setOpaque(true);
        return this;
    }
}