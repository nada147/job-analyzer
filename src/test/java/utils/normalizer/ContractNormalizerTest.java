package utils.normalizer;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ContractNormalizerTest {

    @Test
    void testNormalizeCDI() {
        assertEquals("CDI", ContractNormalizer.normalize("CDI"));
        assertEquals("CDI", ContractNormalizer.normalize("Contrat à durée indéterminée"));
        assertEquals("CDI", ContractNormalizer.normalize("contrat a duree indeterminee"));
    }

    @Test
    void testNormalizeCDD() {
        assertEquals("CDD", ContractNormalizer.normalize("CDD"));
        assertEquals("CDD", ContractNormalizer.normalize("Contrat à durée déterminée"));
    }

    @Test
    void testNormalizeStage() {
        assertEquals("Stage", ContractNormalizer.normalize("stage"));
        assertEquals("Stage", ContractNormalizer.normalize("stagiaire"));
        assertEquals("Stage PFE", ContractNormalizer.normalize("PFE"));
    }

    @Test
    void testNormalizeFreelance() {
        assertEquals("Freelance", ContractNormalizer.normalize("freelance"));
        assertEquals("Freelance", ContractNormalizer.normalize("indépendant"));
    }

    @Test
    void testUnknownContractCapitalized() {
        assertEquals("Alternance", ContractNormalizer.normalize("alternance"));
    }

    @Test
    void testNullOrEmpty() {
        assertNull(ContractNormalizer.normalize(null));
        assertEquals("", ContractNormalizer.normalize(""));
    }
}
