package utils.normalizer;

import java.util.HashMap;
import java.util.Map;

/**
 * Normalisation du niveau d'études
 */
public class EducationNormalizer {

    private static final Map<String, String> MAP = new HashMap<>();

    static {
        MAP.put("bac", "Bac");
        MAP.put("bac+2", "Bac+2");
        MAP.put("bac+3", "Bac+3");
        MAP.put("licence", "Bac+3");
        MAP.put("bac+5", "Bac+5");
        MAP.put("master", "Bac+5");
        MAP.put("ingénieur", "Bac+5");
        MAP.put("doctorat", "Doctorat");
        MAP.put("phd", "Doctorat");
    }

    public static String normalize(String edu) {
        if (edu == null || edu.isEmpty()) return edu;

        String cleaned = edu.toLowerCase();
        for (Map.Entry<String, String> e : MAP.entrySet()) {
            if (cleaned.contains(e.getKey())) {
                return e.getValue();
            }
        }
        return edu;
    }
}