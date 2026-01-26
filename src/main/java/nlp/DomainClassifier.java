package nlp;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Classificateur de domaines MULTI-SECTEURS simplifié
 */
public class DomainClassifier {
    
    private static final Map<String, List<String>> DOMAINS = new LinkedHashMap<>();
    
    static {
        // IT & Tech
        DOMAINS.put("Data Science & IA", Arrays.asList(
            "data science", "data scientist", "machine learning", "deep learning",
            "intelligence artificielle", "big data", "python", "tensorflow", "analytics"
        ));
        
        DOMAINS.put("Développement Backend", Arrays.asList(
            "backend", "développeur backend", "api", "microservices", "spring boot",
            "django", "laravel", "node.js", "java", "python", "php"
        ));
        
        DOMAINS.put("Développement Frontend", Arrays.asList(
            "frontend", "développeur frontend", "react", "angular", "vue",
            "javascript", "html", "css", "ui", "interface"
        ));
        
        DOMAINS.put("DevOps & Cloud", Arrays.asList(
            "devops", "cloud", "docker", "kubernetes", "aws", "azure",
            "ci/cd", "jenkins", "terraform", "infrastructure"
        ));
        
        DOMAINS.put("Mobile", Arrays.asList(
            "mobile", "android", "ios", "react native", "flutter",
            "kotlin", "swift", "application mobile"
        ));
        
        DOMAINS.put("Cybersécurité", Arrays.asList(
            "sécurité", "cybersécurité", "pentest", "ethical hacking",
            "firewall", "soc", "owasp"
        ));
        
        // Commerce & Vente
        DOMAINS.put("Commerce & Vente", Arrays.asList(
            "commercial", "vendeur", "vente", "négociation", "prospection",
            "télévendeur", "attaché commercial", "business developer", "crm"
        ));
        
        DOMAINS.put("Retail & Grande Distribution", Arrays.asList(
            "caissier", "vendeur rayon", "merchandising", "chef de rayon",
            "grande distribution", "retail", "inventaire", "caisse"
        ));
        
        // Marketing & Communication
        DOMAINS.put("Marketing Digital", Arrays.asList(
            "marketing digital", "seo", "sem", "social media", "community manager",
            "google ads", "facebook ads", "content marketing"
        ));
        
        DOMAINS.put("Communication", Arrays.asList(
            "communication", "chargé de communication", "relations publiques",
            "événementiel", "attaché de presse"
        ));
        
        DOMAINS.put("Design Graphique", Arrays.asList(
            "designer", "graphiste", "infographiste", "ui designer", "ux designer",
            "photoshop", "illustrator", "figma", "webdesigner"
        ));
        
        // Finance & Comptabilité
        DOMAINS.put("Comptabilité", Arrays.asList(
            "comptable", "comptabilité", "expert comptable", "aide comptable",
            "sage", "bilan", "fiscalité", "tva"
        ));
        
        DOMAINS.put("Finance", Arrays.asList(
            "finance", "analyste financier", "contrôleur de gestion",
            "directeur financier", "trésorier", "audit", "excel"
        ));
        
        DOMAINS.put("Banque & Assurance", Arrays.asList(
            "banque", "assurance", "conseiller bancaire", "chargé de clientèle",
            "gestionnaire de patrimoine", "crédit"
        ));
        
        // Ressources Humaines
        DOMAINS.put("Ressources Humaines", Arrays.asList(
            "ressources humaines", "rh", "recruteur", "recrutement",
            "talent acquisition", "paie", "formation", "droit du travail"
        ));
        
        // Santé & Médical
        DOMAINS.put("Médical & Soins", Arrays.asList(
            "médecin", "infirmier", "infirmière", "aide-soignant",
            "pharmacien", "sage-femme", "urgences", "soins"
        ));
        
        DOMAINS.put("Paramédical", Arrays.asList(
            "kinésithérapeute", "physiothérapeute", "orthophoniste",
            "diététicien", "opticien", "rééducation"
        ));
        
        // Éducation & Formation
        DOMAINS.put("Enseignement", Arrays.asList(
            "enseignant", "professeur", "instituteur", "formateur",
            "pédagogie", "éducation", "enseignement"
        ));
        
        // Tourisme & Hôtellerie
        DOMAINS.put("Hôtellerie", Arrays.asList(
            "réceptionniste", "réception", "hôtel", "front office",
            "gouvernante", "femme de chambre", "accueil", "réservation"
        ));
        
        DOMAINS.put("Restauration", Arrays.asList(
            "cuisinier", "chef de cuisine", "pâtissier", "serveur",
            "barman", "cuisine", "restaurant", "haccp"
        ));
        
        DOMAINS.put("Tourisme & Voyage", Arrays.asList(
            "agent de voyage", "guide touristique", "accompagnateur",
            "tourisme", "billetterie", "amadeus"
        ));
        
        // Construction & BTP
        DOMAINS.put("Construction & BTP", Arrays.asList(
            "maçon", "chef de chantier", "conducteur de travaux", "btp",
            "électricien", "plombier", "menuisier", "charpentier", "génie civil"
        ));
        
        // Transport & Logistique
        DOMAINS.put("Logistique & Supply Chain", Arrays.asList(
            "logistique", "logisticien", "supply chain", "gestionnaire stock",
            "approvisionneur", "entrepôt", "wms", "gestion stocks"
        ));
        
        DOMAINS.put("Transport", Arrays.asList(
            "chauffeur", "conducteur", "livreur", "poids lourd",
            "transport", "livraison", "permis c", "caces"
        ));
        
        // Industrie & Production
        DOMAINS.put("Production Industrielle", Arrays.asList(
            "production", "agent de production", "opérateur machine",
            "usinage", "contrôle qualité", "assemblage", "iso 9001"
        ));
        
        DOMAINS.put("Maintenance Industrielle", Arrays.asList(
            "maintenance", "technicien maintenance", "électromécanicien",
            "mécanicien", "automatisme", "dépannage"
        ));
        
        // Agriculture
        DOMAINS.put("Agriculture & Agroalimentaire", Arrays.asList(
            "agriculteur", "agriculture", "agronome", "élevage",
            "irrigation", "agroalimentaire", "cultures"
        ));
        
        // Juridique
        DOMAINS.put("Juridique & Droit", Arrays.asList(
            "juriste", "avocat", "notaire", "droit", "contrats",
            "contentieux", "juridique", "legal"
        ));
        
        // Administration
        DOMAINS.put("Administration & Secrétariat", Arrays.asList(
            "secrétaire", "assistant administratif", "assistante de direction",
            "office manager", "administration", "bureautique"
        ));
        
        // Autres
        DOMAINS.put("Architecture & Urbanisme", Arrays.asList(
            "architecte", "urbaniste", "dessinateur", "autocad", "revit"
        ));
        
        DOMAINS.put("Immobilier", Arrays.asList(
            "agent immobilier", "immobilier", "négociateur immobilier",
            "transaction", "location", "syndic"
        ));
        
        DOMAINS.put("Call Center & Service Client", Arrays.asList(
            "téléconseiller", "call center", "service client",
            "conseiller client", "support client", "téléphonie"
        ));
    }
    
    /**
     * Classifie un texte dans le domaine le plus pertinent
     */
  
    /**
     * Classifie et retourne les N meilleurs domaines
     */
  
    
    /**
     * Vérifie si un mot-clé est présent dans le texte
     */
    private static boolean containsWord(String text, String keyword) {
        String escapedKeyword = Pattern.quote(keyword);
        Pattern pattern = Pattern.compile("\\b" + escapedKeyword + "\\b", Pattern.CASE_INSENSITIVE);
        return pattern.matcher(text).find();
    }
    
    /**
     * Retourne tous les domaines disponibles
     */
    public static List<String> getAllDomains() {
        return new ArrayList<>(DOMAINS.keySet());
    }
    public static List<String> classifyTopDomains(String text, int topN) {
        if (text == null || text.isEmpty()) {
            return List.of("Autre");
        }

        Map<String, Integer> scores = new LinkedHashMap<>();

        for (Map.Entry<String, List<String>> entry : DOMAINS.entrySet()) {
            int score = 0;
            for (String keyword : entry.getValue()) {
                if (containsWord(text, keyword)) {
                    score++;
                }
            }
            if (score > 0) {
                scores.put(entry.getKey(), score);
            }
        }

        if (scores.isEmpty()) {
            return List.of("Autre");
        }

        return scores.entrySet()
                .stream()
                .sorted((a, b) -> b.getValue() - a.getValue())
                .limit(topN)
                .map(Map.Entry::getKey)
                .toList();
    }
    
    public static String classify(String text) {

        if (text == null || text.isEmpty()) {
            return "Autre";
        }

        Map<String, Integer> scores = new HashMap<>();

        for (Map.Entry<String, List<String>> entry : DOMAINS.entrySet()) {
            int score = 0;
            for (String keyword : entry.getValue()) {
                if (text.contains(keyword)) {
                    score++;
                }
            }
            scores.put(entry.getKey(), score);
        }

        return scores.entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .filter(e -> e.getValue() > 0)
                .map(Map.Entry::getKey)
                .orElse("Autre");
    }

}