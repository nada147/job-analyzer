package utils.normalizer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Normalisation du niveau d'expérience
 */
public class ExperienceNormalizer {

    private static final Pattern YEARS_PATTERN =
            Pattern.compile("(\\d+)\\s*(an|année)s?");

    public static String normalize(String experience) {
        if (experience == null || experience.isEmpty()) return experience;

        String cleaned = experience.toLowerCase().trim();
        Matcher m = YEARS_PATTERN.matcher(cleaned);

        if (m.find()) {
            int years = Integer.parseInt(m.group(1));
            if (years <= 1) return "Débutant (0-1 an)";
            if (years <= 3) return "Junior (1-3 ans)";
            if (years <= 7) return "Confirmé (3-7 ans)";
            return "Senior (7+ ans)";
        }

        if (cleaned.contains("débutant")) return "Débutant (0-1 an)";
        if (cleaned.contains("junior")) return "Junior (1-3 ans)";
        if (cleaned.contains("senior") || cleaned.contains("expert"))
            return "Senior (7+ ans)";

        return experience;
    }
}