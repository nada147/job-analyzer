package nlp;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TextPreprocessorTest {

    @Test
    void testPreprocess_removesPunctuationAndLowercases() {
        String input = "Java, SQL!! Spring-Boot ";
        String output = TextPreprocessor.preprocess(input);

        assertNotNull(output);
        assertTrue(output.contains("java"));
        assertTrue(output.contains("sql"));

        // caractères supprimés par le regex
        assertFalse(output.contains("!"));
        assertFalse(output.contains(","));

        // "-" est remplacé par espace, donc "spring boot" doit apparaître
        assertTrue(output.contains("spring boot"));
    }

    @Test
    void testPreprocess_removesAccents() {
        String input = "Développeur à Rabat";
        String output = TextPreprocessor.preprocess(input);

        assertTrue(output.contains("developpeur"));
        assertTrue(output.contains("a rabat"));
    }

    @Test
    void testBuildJobText_concatenatesAndPreprocesses() {
        String out = TextPreprocessor.buildJobText(
                "Java Developer",
                "SQL & Spring-Boot",
                "IT",
                "Backend"
        );

        assertNotNull(out);
        assertTrue(out.contains("java developer"));
        assertTrue(out.contains("backend"));
        assertTrue(out.contains("it"));
        assertTrue(out.contains("sql"));
        assertTrue(out.contains("spring boot"));
    }

    @Test
    void testPreprocess_nullReturnsEmpty() {
        assertEquals("", TextPreprocessor.preprocess(null));
    }
}
