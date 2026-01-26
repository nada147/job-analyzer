package utils.normalizer;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CityNormalizerTest {

    @Test
    void testNormalizeCity_basicMappings() {
        assertEquals("Casablanca", CityNormalizer.normalize("casa"));
        assertEquals("Casablanca", CityNormalizer.normalize("CASABLANCA"));
        assertEquals("Rabat", CityNormalizer.normalize(" rabat "));
        assertEquals("Fès", CityNormalizer.normalize("fes"));
        assertEquals("Tanger", CityNormalizer.normalize("Tangier"));
    }

    @Test
    void testNormalizeCity_nullAndEmpty() {
        assertEquals("Non spécifié", CityNormalizer.normalize(null));
        assertEquals("Non spécifié", CityNormalizer.normalize(""));
        assertEquals("Non spécifié", CityNormalizer.normalize("   "));
    }

    @Test
    void testNormalizeCity_removesMoroccoSuffixes() {
        assertEquals("Casablanca", CityNormalizer.normalize("Casablanca (Maroc)"));
        assertEquals("Casablanca", CityNormalizer.normalize("Casablanca - Maroc"));
        assertEquals("Casablanca", CityNormalizer.normalize("Casablanca, Maroc"));
        assertEquals("Casablanca", CityNormalizer.normalize("Casablanca | Maroc"));
    }

    @Test
    void testNormalizeCity_containsMatching() {
        // ton code fait un "contains" sur les clés du mapping
        assertEquals("Casablanca", CityNormalizer.normalize("Casablanca et environs"));
        assertEquals("Télétravail", CityNormalizer.normalize("Poste en remote possible"));
    }

    @Test
    void testNormalizeCity_multipleCitiesCommaOrSlash() {
        assertEquals("Casablanca", CityNormalizer.normalize("Casablanca, Rabat"));
        assertEquals("Rabat", CityNormalizer.normalize("Rabat/Salé"));
    }

    @Test
    void testGetRegion() {
        assertEquals("Casablanca-Settat", CityNormalizer.getRegion("Casablanca"));
        assertEquals("Rabat-Salé-Kénitra", CityNormalizer.getRegion("Rabat"));
        assertEquals("Autre", CityNormalizer.getRegion("VilleInconnue"));
        assertEquals("Autre", CityNormalizer.getRegion(null));
    }
}
