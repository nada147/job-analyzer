package nlp;

import org.junit.jupiter.api.Test;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

public class SkillExtractorTest {

    @Test
    void testSkillExtraction() {

        String text = "Java Spring SQL Docker";

        Set<String> skills = SkillExtractor.extract(text);

        // Le résultat doit exister
        assertNotNull(skills);

        // Si des compétences sont détectées, elles doivent être valides
        if (!skills.isEmpty()) {
            skills.forEach(skill -> {
                assertNotNull(skill);
                assertFalse(skill.isBlank());
            });
        }
    }
}
