package main;

import model.JobAnnouncement;
import scraper.MJobScraper;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Classe principale pour tester le scraper m-job.ma
 */
public class MainMJob {
    
    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("   JobAnalyzer - M-Job.ma");
        System.out.println("=================================================\n");
        
        try {
            MJobScraper scraper = new MJobScraper();
            
            // Scrape automatiquement toutes les pages disponibles
            List<JobAnnouncement> jobs = scraper.scrape();
            
            if (jobs.isEmpty()) {
                System.out.println("❌ Aucune offre trouvée");
                return;
            }
            
            System.out.println("\n=================================================");
            System.out.println("   RÉSULTATS");
            System.out.println("=================================================\n");
            
            System.out.println("✅ " + jobs.size() + " offres récupérées\n");
            
            // Afficher les 15 premières offres
            int displayCount = Math.min(jobs.size(), 15);
            for (int i = 0; i < displayCount; i++) {
                displayJobSummary(jobs.get(i), i + 1);
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
    
    /**
     * Affiche un résumé d'une offre
     */
    private static void displayJobSummary(JobAnnouncement job, int numero) {
        System.out.println("─────────────────────────────────────────────────────");
        System.out.println("Offre #" + numero);
        System.out.println("─────────────────────────────────────────────────────");
        System.out.println("📌 Titre : " + job.getTitle());
        System.out.println("🏢 Entreprise : " + (job.getCompany() != null ? job.getCompany() : "N/A"));
        System.out.println("📍 Localisation : " + (job.getLocation() != null ? job.getLocation() : "N/A"));
        System.out.println("📝 Type contrat : " + (job.getContractType() != null ? job.getContractType() : "N/A"));
        
        if (job.getSalary() != null && !job.getSalary().isEmpty()) {
            System.out.println("💰 Salaire : " + job.getSalary());
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
        
        if (job.getPublishDateString() != null && !job.getPublishDateString().isEmpty()) {
            System.out.println("📅 Date publication : " + job.getPublishDateString());
        }
        
        if (job.getSkills() != null && !job.getSkills().isEmpty()) {
            System.out.println("🔧 Compétences : " + String.join(", ", job.getSkills()));
        }
        
        if (job.getDescription() != null && !job.getDescription().isEmpty()) {
            String desc = job.getDescription();
            if (desc.length() > 200) {
                desc = desc.substring(0, 200) + "...";
            }
            System.out.println("📄 Description : " + desc);
        }
        
        System.out.println("🔗 URL : " + (job.getSourceUrl() != null ? job.getSourceUrl() : "N/A"));
        System.out.println();
    }
    
    /**
     * Affiche les détails complets d'une offre (pour usage futur)
     */
    private static void displayJobDetails(JobAnnouncement job) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        
        System.out.println("=================================================");
        System.out.println("   DÉTAILS DE L'OFFRE");
        System.out.println("=================================================\n");
        
        System.out.println("📋 TITRE: " + job.getTitle());
        System.out.println("🏢 ENTREPRISE: " + job.getCompany());
        System.out.println("📍 LOCALISATION: " + job.getLocation());
        System.out.println("📝 TYPE DE CONTRAT: " + job.getContractType());
        System.out.println("💰 SALAIRE: " + job.getSalary());
        System.out.println("🏭 SECTEUR: " + job.getSecteurActivite());
        System.out.println("💼 FONCTION: " + job.getFonction());
        System.out.println("🎓 NIVEAU D'ÉTUDE: " + job.getNiveauEtude());
        System.out.println("⭐ EXPÉRIENCE: " + job.getExperienceRequise());
        
        if (job.getPublishDate() != null) {
            System.out.println("📅 DATE DE PUBLICATION: " + sdf.format(job.getPublishDate()));
        }
        if (job.getPublishDateString() != null) {
            System.out.println("📅 DATE (texte): " + job.getPublishDateString());
        }
        
        System.out.println("🔗 URL: " + job.getSourceUrl());
        
        if (job.getSkills() != null && !job.getSkills().isEmpty()) {
            System.out.println("\n🔧 COMPÉTENCES:");
            for (String skill : job.getSkills()) {
                System.out.println("   • " + skill);
            }
        }
        
        if (job.getDescription() != null && !job.getDescription().isEmpty()) {
            System.out.println("\n📄 DESCRIPTION:");
            System.out.println(job.getDescription());
        }
        
        System.out.println("\n=================================================\n");
    }
}