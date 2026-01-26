package utils.normalizer;

import java.util.HashMap;
import java.util.Map;

/**
 * Normalisation des types de contrat
 */
public class ContractNormalizer {

    private static final Map<String, String> CONTRACT_MAPPING = new HashMap<>();

    static {
        CONTRACT_MAPPING.put("cdi", "CDI");
        CONTRACT_MAPPING.put("contrat à durée indéterminée", "CDI");
        CONTRACT_MAPPING.put("contrat a duree indeterminee", "CDI");

        CONTRACT_MAPPING.put("cdd", "CDD");
        CONTRACT_MAPPING.put("contrat à durée déterminée", "CDD");

        CONTRACT_MAPPING.put("stage", "Stage");
        CONTRACT_MAPPING.put("stagiaire", "Stage");
        CONTRACT_MAPPING.put("pfe", "Stage PFE");

        CONTRACT_MAPPING.put("freelance", "Freelance");
        CONTRACT_MAPPING.put("indépendant", "Freelance");
    }

    public static String normalize(String contract) {
        if (contract == null || contract.isEmpty()) return contract;

        String cleaned = contract.toLowerCase().trim();
        for (Map.Entry<String, String> e : CONTRACT_MAPPING.entrySet()) {
            if (cleaned.contains(e.getKey())) {
                return e.getValue();
            }
        }
        return capitalize(cleaned);
    }

    private static String capitalize(String s) {
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }
}