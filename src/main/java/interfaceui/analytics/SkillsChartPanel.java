package interfaceui.analytics;

import analytics.TrendAnalysisService;
import analytics.TrendAnalysisService.TrendResult;
import org.jfree.chart.*;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class SkillsChartPanel extends JPanel {

    private final TrendAnalysisService service = new TrendAnalysisService();

    // 🔹 Classe interne utilitaire
    private static class DatasetBuilder {
        static DefaultCategoryDataset build(List<TrendResult> data) {
            DefaultCategoryDataset ds = new DefaultCategoryDataset();
            for (TrendResult r : data) {
                ds.addValue(r.getValue(), "Demand", r.getLabel());
            }
            return ds;
        }
    }

    public SkillsChartPanel() {
        setLayout(new BorderLayout());
        add(buildChart(), BorderLayout.CENTER);
    }

    private ChartPanel buildChart() {
    	List<TrendResult> data = service.topSkills(10);

        JFreeChart chart = ChartFactory.createBarChart(
                "Top Required Skills",
                "Skill",
                "Number of Jobs",
                DatasetBuilder.build(data)
        );

        return new ChartPanel(chart);
    }
}