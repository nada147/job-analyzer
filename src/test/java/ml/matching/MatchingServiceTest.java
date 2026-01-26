package ml.matching;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class MatchingServiceTest {

    @Test
    void testMatchingCVJobs() {
        MatchingService service = new MatchingService();

        List<String> skills = List.of("java", "sql", "spring");
        List<MatchResult> results = service.matchCVWithJobs(skills, null);

        assertNotNull(results);
        assertFalse(results.isEmpty());

        for (MatchResult r : results) {
            assertTrue(r.getScore() >= 0 && r.getScore() <= 1);
        }
    }
}
