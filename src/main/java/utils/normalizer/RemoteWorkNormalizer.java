package utils.normalizer;

/**
 * Normalisation du télétravail
 */
public class RemoteWorkNormalizer {

    public static String normalize(String remote) {
        if (remote == null || remote.isEmpty()) return remote;

        String r = remote.toLowerCase();
        if (r.contains("non")) return "Non";
        if (r.contains("hybride")) return "Hybride";
        if (r.contains("100") || r.contains("complet")) return "Full Remote";
        if (r.contains("oui") || r.contains("possible")) return "Oui";

        return remote;
    }
}