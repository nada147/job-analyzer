package utils.normalizer;

/**
 * Nettoyage du nom d'entreprise
 */
public class CompanyNormalizer {

    public static String normalize(String company) {
        if (company == null || company.isEmpty()) return company;

        String cleaned = company.trim().toLowerCase();
        if (cleaned.contains("confidentiel") || cleaned.contains("anonyme")) {
            return "Confidentiel";
        }
        return company.trim();
    }
}