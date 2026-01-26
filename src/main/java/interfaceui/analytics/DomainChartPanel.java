package interfaceui.analytics;

import analytics.TrendAnalysisService;
import analytics.TrendAnalysisService.TrendResult;
import org.jfree.chart.*;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class DomainChartPanel extends JPanel {

    private final TrendAnalysisService service = new TrendAnalysisService();

    // 🔹 Classe interne config
    private static class ChartConfig {
        static int MAX = 10;
    }

    public DomainChartPanel() {
        setLayout(new BorderLayout());
        add(buildChart(), BorderLayout.CENTER);
    }

    private ChartPanel buildChart() {
        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();

        List<TrendResult> data = service.jobsByDomain();
        int i = 0;
        for (TrendResult r : data) {
            dataset.setValue(r.getLabel(), r.getValue());
            if (++i >= ChartConfig.MAX) break;
        }

        JFreeChart chart = ChartFactory.createPieChart(
                "Most Requested ML Domains",
                dataset,
                true,
                true,
                false
        );

        return new ChartPanel(chart);
    }
}