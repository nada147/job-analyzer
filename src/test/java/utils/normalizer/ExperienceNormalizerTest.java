package utils.normalizer;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ExperienceNormalizerTest {

    @Test
    void testExperienceByYears() {
        assertEquals("Débutant (0-1 an)", ExperienceNormalizer.normalize("1 an"));
        assertEquals("Junior (1-3 ans)", ExperienceNormalizer.normalize("2 ans"));
        assertEquals("Confirmé (3-7 ans)", ExperienceNormalizer.normalize("5 ans"));
        assertEquals("Senior (7+ ans)", ExperienceNormalizer.normalize("10 ans"));
    }

    @Test
    void testExperienceByKeywords() {
        assertEquals("Débutant (0-1 an)", ExperienceNormalizer.normalize("débutant"));
        assertEquals("Junior (1-3 ans)", ExperienceNormalizer.normalize("junior"));
        assertEquals("Senior (7+ ans)", ExperienceNormalizer.normalize("senior"));
        assertEquals("Senior (7+ ans)", ExperienceNormalizer.normalize("expert"));
    }

    @Test
    void testUnknownExperienceReturnedAsIs() {
        String input = "expérience significative";
        assertEquals(input, ExperienceNormalizer.normalize(input));
    }

    @Test
    void testNullOrEmpty() {
        assertNull(ExperienceNormalizer.normalize(null));
        assertEquals("", ExperienceNormalizer.normalize(""));
    }
}
