package utils.normalizer;

import java.util.HashMap;
import java.util.Map;

/**
 * Normalise les noms de villes marocaines
 * Objectif : Uniformiser "Casa", "CASABLANCA", "Casablanca (Maroc)" → "Casablanca"
 */
public class CityNormalizer {
    
    private static final Map<String, String> CITY_MAPPING = new HashMap<>();
    
    static {
        // ===== CASABLANCA =====
        CITY_MAPPING.put("casablanca", "Casablanca");
        CITY_MAPPING.put("casa", "Casablanca");
        CITY_MAPPING.put("dar el beida", "Casablanca");
        CITY_MAPPING.put("dar el-beida", "Casablanca");
        CITY_MAPPING.put("grand casablanca", "Casablanca");
        CITY_MAPPING.put("région casablanca", "Casablanca");
        
        // ===== RABAT =====
        CITY_MAPPING.put("rabat", "Rabat");
        CITY_MAPPING.put("rabat-salé", "Rabat");
        CITY_MAPPING.put("rabat sale", "Rabat");
        CITY_MAPPING.put("rabat salé kénitra", "Rabat");
        
        // ===== SALÉ =====
        CITY_MAPPING.put("salé", "Salé");
        CITY_MAPPING.put("sale", "Salé");
        
        // ===== TANGER =====
        CITY_MAPPING.put("tanger", "Tanger");
        CITY_MAPPING.put("tangier", "Tanger");
        CITY_MAPPING.put("tanja", "Tanger");
        CITY_MAPPING.put("tanger-tétouan", "Tanger");
        
        // ===== MARRAKECH =====
        CITY_MAPPING.put("marrakech", "Marrakech");
        CITY_MAPPING.put("marrakesh", "Marrakech");
        
        // ===== FÈS =====
        CITY_MAPPING.put("fès", "Fès");
        CITY_MAPPING.put("fes", "Fès");
        CITY_MAPPING.put("fez", "Fès");
        
        // ===== MEKNÈS =====
        CITY_MAPPING.put("meknès", "Meknès");
        CITY_MAPPING.put("meknes", "Meknès");
        
        // ===== AGADIR =====
        CITY_MAPPING.put("agadir", "Agadir");
        
        // ===== OUJDA =====
        CITY_MAPPING.put("oujda", "Oujda");
        CITY_MAPPING.put("ouejda", "Oujda");
        
        // ===== KÉNITRA =====
        CITY_MAPPING.put("kenitra", "Kénitra");
        CITY_MAPPING.put("kénitra", "Kénitra");
        
        // ===== TÉTOUAN =====
        CITY_MAPPING.put("tétouan", "Tétouan");
        CITY_MAPPING.put("tetouan", "Tétouan");
        CITY_MAPPING.put("tetuan", "Tétouan");
        
        // ===== MOHAMMEDIA =====
        CITY_MAPPING.put("mohammedia", "Mohammedia");
        CITY_MAPPING.put("mohammédia", "Mohammedia");
        
        // ===== EL JADIDA =====
        CITY_MAPPING.put("el jadida", "El Jadida");
        CITY_MAPPING.put("el-jadida", "El Jadida");
        CITY_MAPPING.put("jadida", "El Jadida");
        
        // ===== AUTRES VILLES =====
        CITY_MAPPING.put("settat", "Settat");
        CITY_MAPPING.put("safi", "Safi");
        CITY_MAPPING.put("nador", "Nador");
        CITY_MAPPING.put("khouribga", "Khouribga");
        CITY_MAPPING.put("béni mellal", "Béni Mellal");
        CITY_MAPPING.put("beni mellal", "Béni Mellal");
        CITY_MAPPING.put("laâyoune", "Laâyoune");
        CITY_MAPPING.put("laayoune", "Laâyoune");
        CITY_MAPPING.put("essaouira", "Essaouira");
        CITY_MAPPING.put("errachidia", "Errachidia");
        
        // ===== CAS SPÉCIAUX =====
        CITY_MAPPING.put("maroc", "National (Maroc)");
        CITY_MAPPING.put("national", "National (Maroc)");
        CITY_MAPPING.put("tout le maroc", "National (Maroc)");
        CITY_MAPPING.put("plusieurs villes", "Plusieurs villes");
        CITY_MAPPING.put("télétravail", "Télétravail");
        CITY_MAPPING.put("teletravail", "Télétravail");
        CITY_MAPPING.put("remote", "Télétravail");
        CITY_MAPPING.put("à distance", "Télétravail");
    }
    
    /**
     * Normalise le nom d'une ville
     * @param city Nom de la ville brut
     * @return Nom standardisé
     */
    public static String normalize(String city) {
        if (city == null || city.trim().isEmpty()) {
            return "Non spécifié";
        }
        
        // Nettoyage initial
        String cleaned = city.trim()
            .replace("(Maroc)", "")
            .replace("| Maroc", "")
            .replace("- Maroc", "")
            .replace(", Maroc", "")
            .replaceAll("\\s+", " ")
            .trim();
        
        if (cleaned.isEmpty()) {
            return "Non spécifié";
        }
        
        String lowerCase = cleaned.toLowerCase();
        
        // Recherche exacte dans le mapping
        if (CITY_MAPPING.containsKey(lowerCase)) {
            return CITY_MAPPING.get(lowerCase);
        }
        
        // Recherche par contient (pour "Casablanca et environs")
        for (Map.Entry<String, String> entry : CITY_MAPPING.entrySet()) {
            if (lowerCase.contains(entry.getKey())) {
                return entry.getValue();
            }
        }
        
        // Cas multiples villes séparées par virgule ou slash
        if (lowerCase.contains(",") || lowerCase.contains("/")) {
            String firstCity = cleaned.split("[,/]")[0].trim();
            return normalize(firstCity); // Appel récursif
        }
        
        // Si aucune correspondance, capitaliser proprement
        return capitalize(cleaned);
    }
    
    /**
     * Détermine la région administrative à partir de la ville
     * @param city Nom de la ville standardisé
     * @return Région administrative
     */
    public static String getRegion(String city) {
        if (city == null || city.isEmpty()) {
            return "Autre";
        }
        
        Map<String, String> cityToRegion = new HashMap<>();
        
        // Casablanca-Settat
        cityToRegion.put("Casablanca", "Casablanca-Settat");
        cityToRegion.put("Mohammedia", "Casablanca-Settat");
        cityToRegion.put("Settat", "Casablanca-Settat");
        cityToRegion.put("El Jadida", "Casablanca-Settat");
        
        // Rabat-Salé-Kénitra
        cityToRegion.put("Rabat", "Rabat-Salé-Kénitra");
        cityToRegion.put("Salé", "Rabat-Salé-Kénitra");
        cityToRegion.put("Kénitra", "Rabat-Salé-Kénitra");
        
        // Tanger-Tétouan-Al Hoceïma
        cityToRegion.put("Tanger", "Tanger-Tétouan-Al Hoceïma");
        cityToRegion.put("Tétouan", "Tanger-Tétouan-Al Hoceïma");
        
        // Marrakech-Safi
        cityToRegion.put("Marrakech", "Marrakech-Safi");
        cityToRegion.put("Safi", "Marrakech-Safi");
        
        // Fès-Meknès
        cityToRegion.put("Fès", "Fès-Meknès");
        cityToRegion.put("Meknès", "Fès-Meknès");
        
        // Oriental
        cityToRegion.put("Oujda", "Oriental");
        cityToRegion.put("Nador", "Oriental");
        
        // Souss-Massa
        cityToRegion.put("Agadir", "Souss-Massa");
        
        // Béni Mellal-Khénifra
        cityToRegion.put("Béni Mellal", "Béni Mellal-Khénifra");
        cityToRegion.put("Khouribga", "Béni Mellal-Khénifra");
        
        return cityToRegion.getOrDefault(city, "Autre");
    }
    
    /**
     * Capitalise les mots d'une chaîne
     */
    private static String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        
        String[] words = str.split("\\s+");
        StringBuilder result = new StringBuilder();
        
        for (String word : words) {
            if (!word.isEmpty()) {
                result.append(Character.toUpperCase(word.charAt(0)))
                      .append(word.substring(1).toLowerCase())
                      .append(" ");
            }
        }
        
        return result.toString().trim();
    }
}