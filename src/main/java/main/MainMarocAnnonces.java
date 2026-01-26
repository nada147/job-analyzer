package main;

import model.JobAnnouncement;
import scraper.MarocAnnoncesScraper;
import java.util.List;

/**
 * Classe de test pour le scraper MarocAnnonces
 */
public class MainMarocAnnonces {
    
    public static void main(String[] args) {
        System.out.println("═══════════════════════════════════════════════════");
        System.out.println("     JobAnalyzer - MarocAnnonces.com");
        System.out.println("═══════════════════════════════════════════════════\n");
        
        try {
            MarocAnnoncesScraper scraper = new MarocAnnoncesScraper();
            
            // Scrape automatiquement toutes les pages disponibles
            List<JobAnnouncement> jobs = scraper.scrape();
            
            if (jobs.isEmpty()) {
                System.out.println("❌ Aucune offre trouvée");
                return;
            }
            
            System.out.println("\n═══════════════════════════════════════════════════");
            System.out.println("     RÉSULTATS");
            System.out.println("═══════════════════════════════════════════════════\n");
            
            System.out.println("✅ " + jobs.size() + " offres récupérées\n");
            
            // Afficher les 15 premières offres
            int displayCount = Math.min(jobs.size(), 15);
            for (int i = 0; i < displayCount; i++) {
                displayJobSummary(jobs.get(i), i + 1);
            }
            
            if (jobs.size() > 15) {
                System.out.println("... et " + (jobs.size() - 15) + " autres offres\n");
            }
            
            // Afficher les statistiques
            displayStatistics(jobs);
            
            // Export CSV (optionnel - décommenter si nécessaire)
            // boolean success = CSVExporter.exportToCSV(jobs);
            // if (success) {
            //     System.out.println("\n✅ Export CSV réussi !");
            // }
            
        } catch (Exception e) {
            System.err.println("\n❌ ERREUR : " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Affiche un résumé d'une offre
     */
    private static void displayJobSummary(JobAnnouncement job, int numero) {
        System.out.println("─────────────────────────────────────────────────────");
        System.out.println("Offre #" + numero);
        System.out.println("─────────────────────────────────────────────────────");
        System.out.println("📌 Titre : " + nvl(job.getTitle()));
        System.out.println("🏢 Entreprise : " + nvl(job.getCompany()));
        System.out.println("📍 Localisation : " + nvl(job.getLocation()));
        System.out.println("📝 Type contrat : " + nvl(job.getContractType()));
        
        if (job.getSalary() != null && !job.getSalary().isEmpty()) {
            System.out.println("💰 Salaire : " + job.getSalary());
        }
        
        if (job.getDomain() != null && !job.getDomain().isEmpty()) {
            System.out.println("🏭 Domaine : " + job.getDomain());
        }
        
        if (job.getFonction() != null && !job.getFonction().isEmpty()) {
            System.out.println("💼 Fonction : " + job.getFonction());
        }
        
        if (job.getNiveauEtude() != null && !job.getNiveauEtude().isEmpty()) {
            System.out.println("📚 Niveau : " + job.getNiveauEtude());
        }
        
        if (job.getExperienceRequise() != null && !job.getExperienceRequise().isEmpty()) {
            System.out.println("🎓 Expérience : " + job.getExperienceRequise());
        }
        
        if (job.getDescription() != null && !job.getDescription().isEmpty()) {
            String desc = job.getDescription();
            if (desc.length() > 200) {
                desc = desc.substring(0, 200) + "...";
            }
            System.out.println("📄 Description : " + desc);
        }
        
        System.out.println("🔗 URL : " + nvl(job.getSourceUrl()));
        System.out.println();
    }
    
    /**
     * Affiche des statistiques sur les annonces récupérées
     */
    private static void displayStatistics(List<JobAnnouncement> jobs) {
        System.out.println("═══════════════════════════════════════════════════");
        System.out.println("     STATISTIQUES");
        System.out.println("═══════════════════════════════════════════════════");
        
        // Compter les annonces avec des informations complètes
        long withCompany = jobs.stream()
                .filter(j -> j.getCompany() != null && !j.getCompany().isEmpty())
                .count();
        long withContract = jobs.stream()
                .filter(j -> j.getContractType() != null && !j.getContractType().isEmpty())
                .count();
        long withSalary = jobs.stream()
                .filter(j -> j.getSalary() != null && !j.getSalary().isEmpty())
                .count();
        long withDescription = jobs.stream()
                .filter(j -> j.getDescription() != null && !j.getDescription().isEmpty())
                .count();
        long withDomain = jobs.stream()
                .filter(j -> j.getDomain() != null && !j.getDomain().isEmpty())
                .count();
        
        System.out.println("📊 Total offres      : " + jobs.size());
        System.out.println("🏢 Avec entreprise   : " + withCompany + " (" + percentage(withCompany, jobs.size()) + "%)");
        System.out.println("📄 Avec contrat      : " + withContract + " (" + percentage(withContract, jobs.size()) + "%)");
        System.out.println("💰 Avec salaire      : " + withSalary + " (" + percentage(withSalary, jobs.size()) + "%)");
        System.out.println("📝 Avec description  : " + withDescription + " (" + percentage(withDescription, jobs.size()) + "%)");
        System.out.println("🏭 Avec domaine      : " + withDomain + " (" + percentage(withDomain, jobs.size()) + "%)");
        
        // Top 5 villes
        if (!jobs.isEmpty()) {
            System.out.println("\n🌍 Villes (Top 5):");
            jobs.stream()
                    .filter(j -> j.getLocation() != null && !j.getLocation().isEmpty())
                    .collect(java.util.stream.Collectors.groupingBy(
                        JobAnnouncement::getLocation,
                        java.util.stream.Collectors.counting()
                    ))
                    .entrySet().stream()
                    .sorted((e1, e2) -> Long.compare(e2.getValue(), e1.getValue()))
                    .limit(5)
                    .forEach(e -> System.out.println("   - " + e.getKey() + " : " + e.getValue() + " offre(s)"));
        }
        
        System.out.println("\n═══════════════════════════════════════════════════\n");
    }
    
    /**
     * Retourne une valeur par défaut si la chaîne est nulle
     */
    private static String nvl(String value) {
        return (value != null && !value.isEmpty()) ? value : "N/A";
    }
    
    /**
     * Calcule un pourcentage
     */
    private static int percentage(long part, int total) {
        if (total == 0) return 0;
        return (int) ((part * 100.0) / total);
    }
}