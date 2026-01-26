package interfaceui.analytics;

import analytics.TrendAnalysisService;
import analytics.TrendAnalysisService.TrendResult;
import org.jfree.chart.*;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class CityChartPanel extends JPanel {

    private final TrendAnalysisService service = new TrendAnalysisService();

    // 🔹 Classe interne style
    private static class ColorPalette {
        static final Color PRIMARY = new Color(52, 152, 219);
    }

    public CityChartPanel() {
        setLayout(new BorderLayout());
        add(buildChart(), BorderLayout.CENTER);
    }

    private ChartPanel buildChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        List<TrendResult> data = service.jobsByCity(10);
        for (TrendResult r : data) {
            dataset.addValue(r.getValue(), "Jobs", r.getLabel());
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Cities With Most Jobs",
                "City",
                "Number of Jobs",
                dataset
        );

        chart.setBackgroundPaint(ColorPalette.PRIMARY);
        return new ChartPanel(chart);
    }
}