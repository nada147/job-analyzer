package main;

import scraper.MarocEmploiScraper;
import model.JobAnnouncement;
import java.util.List;

public class MainMarocEmploi {
    
    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════╗");
        System.out.println("║    JobAnalyzer - MarocEmploi.net      ║");
        System.out.println("╚═══════════════════════════════════════╝\n");
        
        try {
            MarocEmploiScraper scraper = new MarocEmploiScraper();
            
            // Scrape automatiquement toutes les pages disponibles
            List<JobAnnouncement> jobs = scraper.scrape();
            
            if (jobs.isEmpty()) {
                System.out.println("❌ Aucune offre trouvée");
                return;
            }
            
            System.out.println("\n╔═══════════════════════════════════════╗");
            System.out.println("║            RÉSULTATS                  ║");
            System.out.println("╚═══════════════════════════════════════╝\n");
            
            System.out.println("✅ " + jobs.size() + " offres récupérées\n");
            
            // Afficher les 15 premières offres
            int displayCount = Math.min(jobs.size(), 15);
            for (int i = 0; i < displayCount; i++) {
                JobAnnouncement job = jobs.get(i);
                afficherOffre(job, i + 1);
            }
            
            if (jobs.size() > 15) {
                System.out.println("... et " + (jobs.size() - 15) + " autres offres\n");
            }
            
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
    
    private static void afficherOffre(JobAnnouncement job, int numero) {
        System.out.println("─────────────────────────────────────────────────────");
        System.out.println("Offre #" + numero);
        System.out.println("─────────────────────────────────────────────────────");
        System.out.println("📌 Titre : " + job.getTitle());
        System.out.println("🏢 Entreprise : " + (job.getCompany() != null ? job.getCompany() : "N/A"));
        System.out.println("📍 Localisation : " + (job.getLocation() != null ? job.getLocation() : "N/A"));
        System.out.println("📝 Type contrat : " + (job.getContractType() != null ? job.getContractType() : "N/A"));
        
        if (job.getSecteurActivite() != null && !job.getSecteurActivite().isEmpty()) {
            System.out.println("🏭 Secteur : " + job.getSecteurActivite());
        }
        
        if (job.getPublishDateString() != null && !job.getPublishDateString().isEmpty()) {
            System.out.println("📅 Date publication : " + job.getPublishDateString());
        }
        
        if (job.getExperienceRequise() != null && !job.getExperienceRequise().isEmpty()) {
            System.out.println("🎓 Expérience : " + job.getExperienceRequise());
        }
        
        if (job.getNiveauEtude() != null && !job.getNiveauEtude().isEmpty()) {
            System.out.println("📚 Niveau : " + job.getNiveauEtude());
        }
        
        System.out.println("🔗 URL : " + (job.getSourceUrl() != null ? job.getSourceUrl() : "N/A"));
        System.out.println();
    }
}