package nlp;

import java.util.*;
import java.util.regex.*;
import java.util.stream.Collectors;

/**
 * Extracteur de compétences enrichi MULTI-SECTEURS
 * Couvre IT, Commerce, Finance, Santé, Tourisme, Construction, etc.
 */
public class SkillExtractor {
    
    // ===== IT & TECH =====
    
    private static final List<String> PROGRAMMING_LANGUAGES = List.of(
        "java", "python", "javascript", "typescript", "c\\+\\+", "c#", "php",
        "ruby", "go", "golang", "rust", "kotlin", "swift", "scala", "r",
        "perl", "dart", "objective-c", "vb\\.net", "matlab", "groovy"
    );
    
    private static final List<String> BACKEND_FRAMEWORKS = List.of(
        "spring", "spring boot", "hibernate", "jpa", "django", "flask", 
        "node\\.js", "express\\.js", "laravel", "symfony", "asp\\.net", 
        ".net", "rails"
    );
    
    private static final List<String> FRONTEND_FRAMEWORKS = List.of(
        "react", "angular", "vue", "vue\\.js", "next\\.js", "jquery", "redux",
        "sass", "scss", "tailwind", "bootstrap", "material-ui"
    );
    
    private static final List<String> DATABASES = List.of(
        "mysql", "postgresql", "oracle", "mongodb", "redis", "elasticsearch",
        "sql server", "mariadb", "sqlite", "sql"
    );
    
    private static final List<String> CLOUD_DEVOPS = List.of(
        "aws", "azure", "gcp", "docker", "kubernetes", "jenkins", "gitlab ci",
        "terraform", "ansible", "nginx", "apache"
    );
    
    private static final List<String> DEV_TOOLS = List.of(
        "git", "github", "jira", "maven", "gradle", "npm", "vscode", 
        "intellij", "postman", "swagger"
    );
    
    // ===== COMMERCE & VENTE =====
    
    private static final List<String> COMMERCE_VENTE = List.of(
        "vente", "commerce", "négociation", "prospection", "fidélisation",
        "techniques de vente", "relation client", "crm", "salesforce",
        "closing", "merchandising", "gestion des stocks", "inventaire",
        "caisse", "encaissement", "service client", "argumentation commerciale",
        "b2b", "b2c", "vente terrain", "téléprospection", "cold calling",
        "gestion de la relation client", "satisfaction client", "retail",
        "grande distribution", "commerce de détail", "commerce international",
        "import-export", "douane", "incoterms", "logistique commerciale"
    );
    
    // ===== MARKETING & COMMUNICATION =====
    
    private static final List<String> MARKETING_COM = List.of(
        "marketing digital", "seo", "sem", "google ads", "facebook ads",
        "content marketing", "copywriting", "social media", "community management",
        "instagram", "linkedin", "tiktok", "email marketing", "mailchimp",
        "analytics", "google analytics", "brand management", "branding",
        "communication corporate", "relations publiques", "événementiel",
        "inbound marketing", "outbound marketing", "growth hacking",
        "marketing automation", "hubspot", "photoshop", "illustrator",
        "canva", "adobe creative suite", "montage vidéo", "premiere pro"
    );
    
    // ===== FINANCE & COMPTABILITÉ =====
    
    private static final List<String> FINANCE_COMPTA = List.of(
        "comptabilité", "comptabilité générale", "comptabilité analytique",
        "fiscalité", "audit", "contrôle de gestion", "bilan", "compte de résultat",
        "trésorerie", "cash flow", "budget", "sage", "ciel", "sap",
        "oracle financials", "excel", "tableaux de bord", "reporting financier",
        "normes ifrs", "normes ias", "consolidation", "liasse fiscale",
        "déclarations fiscales", "tva", "is", "ir", "cnss", "paie",
        "gestion de paie", "bulletin de salaire", "charges sociales",
        "finance d'entreprise", "analyse financière", "investment banking",
        "risk management", "gestion des risques", "compliance"
    );
    
    // ===== RESSOURCES HUMAINES =====
    
    private static final List<String> RESSOURCES_HUMAINES = List.of(
        "recrutement", "sourcing", "entretien d'embauche", "talent acquisition",
        "linkedin recruiter", "gestion des talents", "formation",
        "développement des compétences", "onboarding", "gestion administrative",
        "gestion du personnel", "droit du travail", "code du travail",
        "relations sociales", "négociation syndicale", "sirh",
        "système d'information rh", "gestion des carrières", "évaluation",
        "assessment", "performance management", "rémunération",
        "politique salariale", "avantages sociaux"
    );
    
    // ===== SANTÉ & MÉDICAL =====
    
    private static final List<String> SANTE_MEDICAL = List.of(
        "soins infirmiers", "pharmacie", "médecine", "radiologie",
        "kinésithérapie", "physiothérapie", "orthophonie", "diététique",
        "nutrition", "sage-femme", "aide-soignant", "ambulancier",
        "secourisme", "premiers secours", "hygiène hospitalière",
        "stérilisation", "bloc opératoire", "urgences", "réanimation",
        "gériatrie", "pédiatrie", "cardiologie", "dermatologie",
        "ophtalmologie", "dentisterie", "orthodontie", "prothèse dentaire",
        "laboratoire", "analyses médicales", "imagerie médicale",
        "échographie", "scanner", "irm", "manipulation de matériel médical"
    );
    
    // ===== ÉDUCATION & FORMATION =====
    
    private static final List<String> EDUCATION_FORMATION = List.of(
        "enseignement", "pédagogie", "didactique", "gestion de classe",
        "évaluation des apprentissages", "différenciation pédagogique",
        "tice", "outils numériques", "moodle", "e-learning", "lms",
        "formation pour adultes", "animation de formation", "coaching",
        "tutorat", "accompagnement scolaire", "soutien scolaire",
        "alphabétisation", "fle", "français langue étrangère",
        "enseignement primaire", "enseignement secondaire", "université",
        "recherche académique", "rédaction scientifique"
    );
    
    // ===== TOURISME & HÔTELLERIE =====
    
    private static final List<String> TOURISME_HOTELLERIE = List.of(
        "accueil", "réception", "front office", "réservation", "booking",
        "amadeus", "sabre", "galileo", "yield management", "revenue management",
        "housekeeping", "étages", "conciergerie", "room service",
        "restauration", "service en salle", "bar", "sommellerie",
        "œnologie", "chef de cuisine", "pâtisserie", "cuisine", "haccp",
        "hygiène alimentaire", "banqueting", "événementiel hôtelier",
        "spa", "wellness", "animation touristique", "guide touristique",
        "accompagnement", "billetterie", "forfaits touristiques"
    );
    
    // ===== CONSTRUCTION & BTP =====
    
    private static final List<String> CONSTRUCTION_BTP = List.of(
        "génie civil", "bâtiment", "travaux publics", "maçonnerie",
        "ferraillage", "coffrage", "béton armé", "charpente", "menuiserie",
        "électricité", "plomberie", "sanitaire", "chauffage", "climatisation",
        "cvc", "peinture", "plâtrerie", "carrelage", "revêtement",
        "étanchéité", "isolation", "lecture de plans", "autocad", "revit",
        "métré", "devis", "conducteur de travaux", "chef de chantier",
        "coordination", "sécurité chantier", "vrd", "voirie", "réseaux divers",
        "terrassement", "gros œuvre", "second œuvre", "finitions"
    );
    
    // ===== TRANSPORT & LOGISTIQUE =====
    
    private static final List<String> TRANSPORT_LOGISTIQUE = List.of(
        "supply chain", "chaîne d'approvisionnement", "approvisionnement",
        "gestion des stocks", "entreposage", "magasinage", "wms",
        "préparation de commandes", "picking", "packing", "expédition",
        "réception marchandises", "inventaire", "manutention", "cariste",
        "chariot élévateur", "caces", "permis poids lourd", "permis c",
        "permis ec", "transport routier", "livraison", "messagerie",
        "fret", "transit", "dédouanement", "import-export logistique",
        "planification", "ordonnancement", "erp logistique", "sap mm"
    );
    
    // ===== AGRICULTURE & AGROALIMENTAIRE =====
    
    private static final List<String> AGRICULTURE_AGRO = List.of(
        "agronomie", "agriculture", "cultures", "irrigation", "fertilisation",
        "phytosanitaire", "traitement des cultures", "élevage", "zootechnie",
        "production animale", "vétérinaire", "insémination", "alimentation animale",
        "machinisme agricole", "tracteur", "moissonneuse", "agroalimentaire",
        "transformation alimentaire", "contrôle qualité alimentaire",
        "normes iso 22000", "traçabilité", "conservation", "conditionnement"
    );
    
    // ===== INDUSTRIE & PRODUCTION =====
    
    private static final List<String> INDUSTRIE_PRODUCTION = List.of(
        "production", "fabrication", "usinage", "tournage", "fraisage",
        "soudure", "assemblage", "maintenance industrielle", "mécanique",
        "électromécanique", "automatisme", "robotique", "pneumatique",
        "hydraulique", "contrôle qualité", "métrologie", "iso 9001",
        "lean manufacturing", "5s", "kaizen", "six sigma", "tpm",
        "gestion de production", "gpao", "planification production",
        "ordonnancement", "approvisionnement industriel"
    );
    
    // ===== JURIDIQUE & DROIT =====
    
    private static final List<String> JURIDIQUE_DROIT = List.of(
        "droit des affaires", "droit commercial", "droit du travail",
        "droit pénal", "droit civil", "droit administratif", "contrats",
        "rédaction juridique", "contentieux", "arbitrage", "médiation",
        "compliance", "conformité", "due diligence", "corporate law",
        "propriété intellectuelle", "brevets", "marques", "droit social"
    );
    
    // ===== SOFT SKILLS UNIVERSELS =====
    
    private static final List<String> SOFT_SKILLS = List.of(
        "leadership", "management", "gestion d'équipe", "communication",
        "négociation", "résolution de problèmes", "esprit d'équipe",
        "autonomie", "rigueur", "organisation", "gestion du temps",
        "adaptabilité", "flexibilité", "créativité", "esprit d'initiative",
        "sens du service", "écoute active", "gestion du stress",
        "prise de décision", "analytical thinking", "pensée critique",
        "polyvalence", "réactivité", "proactivité", "diplomatie"
    );
    
    // ===== LANGUES =====
    
    private static final List<String> LANGUES = List.of(
        "français", "arabe", "anglais", "espagnol", "allemand", "italien",
        "chinois", "mandarin", "portugais", "russe", "japonais", "turc",
        "bilingue", "trilingue", "multilingue", "darija", "amazigh",
        "berbère", "tamazight"
    );
    
    // ===== BUREAUTIQUE & OUTILS TRANSVERSES =====
    
    private static final List<String> BUREAUTIQUE = List.of(
        "microsoft office", "word", "excel", "powerpoint", "outlook",
        "access", "google workspace", "google sheets", "google docs",
        "suite office", "traitement de texte", "tableur", "présentation",
        "messagerie", "internet", "navigation web", "recherche d'information"
    );
    
    // Map complète
    private static final Map<String, List<String>> SKILLS_BY_CATEGORY = Map.ofEntries(
        Map.entry("Langages programmation", PROGRAMMING_LANGUAGES),
        Map.entry("Backend", BACKEND_FRAMEWORKS),
        Map.entry("Frontend", FRONTEND_FRAMEWORKS),
        Map.entry("Bases de données", DATABASES),
        Map.entry("Cloud & DevOps", CLOUD_DEVOPS),
        Map.entry("Outils Dev", DEV_TOOLS),
        Map.entry("Commerce & Vente", COMMERCE_VENTE),
        Map.entry("Marketing & Communication", MARKETING_COM),
        Map.entry("Finance & Comptabilité", FINANCE_COMPTA),
        Map.entry("Ressources Humaines", RESSOURCES_HUMAINES),
        Map.entry("Santé & Médical", SANTE_MEDICAL),
        Map.entry("Éducation & Formation", EDUCATION_FORMATION),
        Map.entry("Tourisme & Hôtellerie", TOURISME_HOTELLERIE),
        Map.entry("Construction & BTP", CONSTRUCTION_BTP),
        Map.entry("Transport & Logistique", TRANSPORT_LOGISTIQUE),
        Map.entry("Agriculture & Agroalimentaire", AGRICULTURE_AGRO),
        Map.entry("Industrie & Production", INDUSTRIE_PRODUCTION),
        Map.entry("Juridique & Droit", JURIDIQUE_DROIT),
        Map.entry("Langues", LANGUES),
        Map.entry("Bureautique", BUREAUTIQUE),
        Map.entry("Soft Skills", SOFT_SKILLS)
    );
    
    private static final List<String> ALL_SKILLS = SKILLS_BY_CATEGORY.values()
        .stream()
        .flatMap(List::stream)
        .collect(Collectors.toList());
    
    // ===== MÉTHODES D'EXTRACTION =====
    
    public static Set<String> extract(String text) {
        Set<String> foundSkills = new HashSet<>();
        if (text == null || text.trim().isEmpty()) {
            return foundSkills;
        }
        
        String lower = text.toLowerCase();
        
        for (String skill : ALL_SKILLS) {
            Pattern p = Pattern.compile("\\b" + skill + "\\b", Pattern.CASE_INSENSITIVE);
            Matcher m = p.matcher(lower);
            
            if (m.find()) {
                foundSkills.add(normalizeSkillName(skill));
            }
        }
        
        return foundSkills;
    }
    
    public static Map<String, Set<String>> extractByCategory(String text) {
        Map<String, Set<String>> result = new LinkedHashMap<>();
        
        if (text == null || text.trim().isEmpty()) {
            return result;
        }
        
        String lower = text.toLowerCase();
        
        for (Map.Entry<String, List<String>> entry : SKILLS_BY_CATEGORY.entrySet()) {
            String category = entry.getKey();
            List<String> skills = entry.getValue();
            Set<String> foundInCategory = new HashSet<>();
            
            for (String skill : skills) {
                Pattern p = Pattern.compile("\\b" + skill + "\\b", Pattern.CASE_INSENSITIVE);
                Matcher m = p.matcher(lower);
                
                if (m.find()) {
                    foundInCategory.add(normalizeSkillName(skill));
                }
            }
            
            if (!foundInCategory.isEmpty()) {
                result.put(category, foundInCategory);
            }
        }
        
        return result;
    }
    
    

    private static String normalizeSkillName(String skill) {
        Map<String, String> specialCases = Map.ofEntries(
            Map.entry("javascript", "JavaScript"),
            Map.entry("typescript", "TypeScript"),
            Map.entry("mysql", "MySQL"),
            Map.entry("postgresql", "PostgreSQL"),
            Map.entry("aws", "AWS"),
            Map.entry("seo", "SEO"),
            Map.entry("crm", "CRM"),
            Map.entry("erp", "ERP"),
            Map.entry("sql", "SQL"),
            Map.entry("vrd", "VRD"),
            Map.entry("cvc", "CVC"),
            Map.entry("haccp", "HACCP"),
            Map.entry("fle", "FLE")
        );
        
        String lower = skill.toLowerCase();
        if (specialCases.containsKey(lower)) {
            return specialCases.get(lower);
        }
        
        return Arrays.stream(lower.split("\\s+"))
            .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1))
            .collect(Collectors.joining(" "));
    }
    
    public static List<String> getAllSkills() {
        return ALL_SKILLS.stream()
            .map(SkillExtractor::normalizeSkillName)
            .sorted()
            .collect(Collectors.toList());
    }
    
 
}