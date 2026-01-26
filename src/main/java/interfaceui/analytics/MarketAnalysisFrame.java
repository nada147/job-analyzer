package interfaceui.analytics;

import javax.swing.*;

public class MarketAnalysisFrame extends JFrame {

    // 🔹 Classe interne configuration onglets
    private static class TabConfig {
        static final String SKILLS = "Top Skills";
        static final String DOMAINS = "Domains";
        static final String CITIES = "Cities";
    }

    public MarketAnalysisFrame() {
        setTitle("📊 Job Market Analysis");
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JTabbedPane tabs = new JTabbedPane();
        tabs.add(TabConfig.SKILLS, new SkillsChartPanel());
        tabs.add(TabConfig.DOMAINS, new DomainChartPanel());
        tabs.add(TabConfig.CITIES, new CityChartPanel());

        add(tabs);
    }
}