package utils.normalizer;

import java.util.HashMap;
import java.util.Map;

/**
 * Normalisation des secteurs d'activité
 */
public class SectorNormalizer {

    private static final Map<String, String> MAP = new HashMap<>();

    static {
        MAP.put("informatique", "Informatique / IT");
        MAP.put("it", "Informatique / IT");
        MAP.put("finance", "Banque / Finance");
        MAP.put("banque", "Banque / Finance");
        MAP.put("industrie", "Industrie");
        MAP.put("btp", "BTP / Construction");
    }

    public static String normalize(String sector) {
        if (sector == null || sector.isEmpty()) return sector;

        String cleaned = sector.toLowerCase();
        for (Map.Entry<String, String> e : MAP.entrySet()) {
            if (cleaned.contains(e.getKey())) {
                return e.getValue();
            }
        }
        return sector;
    }
}