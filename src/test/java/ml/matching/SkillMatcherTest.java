package ml.matching;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class SkillMatcherTest {

    @Test
    void testJaccardScoreRange() {

        double score = SkillMatcher.jaccard(
                List.of("java", "spring", "sql"),
                List.of("java", "sql")
        );

        assertTrue(score >= 0.0 && score <= 1.0);
    }

    @Test
    void testJaccardExactMatch() {

        double score = SkillMatcher.jaccard(
                List.of("java", "sql"),
                List.of("java", "sql")
        );

        assertEquals(1.0, score);
    }

    @Test
    void testJaccardNoMatch() {

        double score = SkillMatcher.jaccard(
                List.of("python", "django"),
                List.of("java", "sql")
        );

        assertEquals(0.0, score);
    }

    @Test
    void testJaccardNullInputs() {

        assertEquals(0.0, SkillMatcher.jaccard(null, List.of("java")));
        assertEquals(0.0, SkillMatcher.jaccard(List.of("java"), null));
    }
}
