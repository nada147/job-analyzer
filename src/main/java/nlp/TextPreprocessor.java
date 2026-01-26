package nlp;

import java.text.Normalizer;

public class TextPreprocessor {

    /**
     * Nettoie un texte brut pour le NLP
     */
    public static String preprocess(String text) {
        if (text == null) return "";

        // 1. Minuscules
        String cleaned = text.toLowerCase();

        // 2. Supprimer HTML
        cleaned = cleaned.replaceAll("<[^>]+>", " ");

        // 3. Normaliser les accents (é → e, à → a)
        cleaned = Normalizer.normalize(cleaned, Normalizer.Form.NFD);
        cleaned = cleaned.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");

        // 4. Supprimer caractères spéciaux inutiles
        cleaned = cleaned.replaceAll("[^a-z0-9+.#/ ]", " ");

        // 5. Espaces multiples → un seul
        cleaned = cleaned.replaceAll("\\s+", " ").trim();

        return cleaned;
    }

    /**
     * Concatène plusieurs champs d’un job en un seul texte NLP
     */
    public static String buildJobText(
            String title,
            String description,
            String secteur,
            String fonction
    ) {
        StringBuilder sb = new StringBuilder();

        if (title != null) sb.append(title).append(" ");
        if (fonction != null) sb.append(fonction).append(" ");
        if (secteur != null) sb.append(secteur).append(" ");
        if (description != null) sb.append(description);

        return preprocess(sb.toString());
    }
}
