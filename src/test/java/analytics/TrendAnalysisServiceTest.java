package analytics;

import analytics.TrendAnalysisService.TrendResult;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TrendAnalysisServiceTest {

    @Test
    void testTopSkills() {
        TrendAnalysisService service = new TrendAnalysisService();

        List<TrendResult> skills = service.topSkills(5);

        assertNotNull(skills);
        assertFalse(skills.isEmpty());
        assertTrue(skills.size() <= 5);

        // Vérifier la structure du résultat
        TrendResult first = skills.get(0);
        assertNotNull(first.getLabel());
        assertFalse(first.getLabel().isBlank());
        assertTrue(first.getValue() > 0);
    }

    @Test
    void testJobsByCity() {
        TrendAnalysisService service = new TrendAnalysisService();

        List<TrendResult> cities = service.jobsByCity(5);

        assertNotNull(cities);
        assertFalse(cities.isEmpty());
        assertTrue(cities.size() <= 5);

        TrendResult first = cities.get(0);
        assertNotNull(first.getLabel());
        assertFalse(first.getLabel().isBlank());
        assertTrue(first.getValue() > 0);
    }
}
