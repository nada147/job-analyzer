package main;

import java.util.List;
import model.JobAnnouncement;
import scraper.RekruteScraper;


public class MainRekrute {
    
    public static void main(String[] args) {
        System.out.println("=================================");
        System.out.println(" JobAnalyzer - Scraping Rekrute");
        System.out.println("=================================\n");
        
        try {
            RekruteScraper scraper = new RekruteScraper();
            
            // Scrape automatiquement toutes les pages disponibles
            List<JobAnnouncement> jobs = scraper.scrape();
            
            if (!jobs.isEmpty()) {
                System.out.println("\n" + "=".repeat(50));
                System.out.println("RÉSUMÉ DES OFFRES RÉCUPÉRÉES");
                System.out.println("=".repeat(50) + "\n");
                
                for (int i = 0; i < Math.min(jobs.size(), 10); i++) {
                    afficherOffreResume(jobs.get(i), i + 1);
                }
                
                if (jobs.size() > 10) {
                    System.out.println("... et " + (jobs.size() - 10) + " autres offres\n");
                }
                
                
                
            } else {
                System.out.println("❌ Aucune offre trouvée");
            }
            
        } catch (Exception e) {
            System.err.println("\n❌ ERREUR : " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void afficherOffreResume(JobAnnouncement job, int numero) {
        System.out.println("─────────────────────────────────────────────────────");
        System.out.println("Offre #" + numero);
        System.out.println("─────────────────────────────────────────────────────");
        System.out.println("📌 Titre : " + (job.getTitle() != null ? job.getTitle() : "N/A"));
        System.out.println("🏢 Entreprise : " + (job.getCompany() != null ? job.getCompany() : "N/A"));
        System.out.println("📍 Ville : " + job.getLocation());
        
        if (job.getNombrePostes() > 1) {
            System.out.println("👥 Postes : " + job.getNombrePostes());
        }
        
        System.out.println("📝 Contrat : " + (job.getContractType() != null ? job.getContractType() : "N/A"));
        
        if (job.getPublishDateString() != null) {
            System.out.println("📅 " + job.getPublishDateString());
        }
        
        if (job.getSecteurActivite() != null && !job.getSecteurActivite().isEmpty()) {
            System.out.println("🏭 Secteur : " + job.getSecteurActivite());
        }
        
        if (job.getFonction() != null && !job.getFonction().isEmpty()) {
            System.out.println("💼 Fonction : " + job.getFonction());
        }
        
        if (job.getExperienceRequise() != null && !job.getExperienceRequise().isEmpty()) {
            System.out.println("🎓 Expérience : " + job.getExperienceRequise());
        }
        
        if (job.getNiveauEtude() != null && !job.getNiveauEtude().isEmpty()) {
            System.out.println("📚 Niveau : " + job.getNiveauEtude());
        }
        
        if (job.getTypeTeletravail() != null && !job.getTypeTeletravail().isEmpty()) {
            System.out.println("🏠 Télétravail : " + job.getTypeTeletravail());
        }
        
        if (job.getDescription() != null && !job.getDescription().isEmpty()) {
            String desc = job.getDescription();
            if (desc.length() > 250) {
                desc = desc.substring(0, 250) + "...";
            }
            System.out.println("📄 Description : " + desc);
        }
        
        System.out.println("🔗 URL : " + (job.getSourceUrl() != null ? job.getSourceUrl() : "N/A"));
        System.out.println();
    }
}