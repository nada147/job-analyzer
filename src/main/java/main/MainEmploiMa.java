package main;
import scraper.EmploiMaScraper;
import model.JobAnnouncement;
import java.util.List;

public class MainEmploiMa {
    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════╗");
        System.out.println("║      JobAnalyzer - Emploi.ma          ║");
        System.out.println("╚═══════════════════════════════════════╝\n");
        
        EmploiMaScraper scraper = new EmploiMaScraper();
        List<JobAnnouncement> jobs = scraper.scrape();
        
        System.out.println("\n═══════════════════════════════════════");
        System.out.println("✅ Total offres récupérées : " + jobs.size());
        System.out.println("═══════════════════════════════════════\n");
        
        // Afficher les 10 premières offres avec tous les détails
        int limit = Math.min(jobs.size(), 10);
        
        for (int i = 0; i < limit; i++) {
            JobAnnouncement job = jobs.get(i);
            
            System.out.println("╔══════════════════════════════════════════════════════════════════════╗");
            System.out.println("║  OFFRE #" + (i + 1));
            System.out.println("╚══════════════════════════════════════════════════════════════════════╝");
            
            // Titre
            System.out.println("📌 TITRE : " + (job.getTitle() != null ? job.getTitle() : "N/A"));
            
            // Entreprise
            System.out.println("🏢 ENTREPRISE : " + (job.getCompany() != null ? job.getCompany() : "N/A"));
            
            // Localisation
            System.out.println("📍 LOCALISATION : " + (job.getLocation() != null ? job.getLocation() : "N/A"));
            
            // Type de contrat
            System.out.println("📝 TYPE CONTRAT : " + (job.getContractType() != null ? job.getContractType() : "N/A"));
            
            // Date de publication
            System.out.println("📅 DATE PUBLICATION : " + (job.getPublishDateString() != null ? job.getPublishDateString() : "N/A"));
            
            // Niveau d'études
            System.out.println("🎓 NIVEAU ÉTUDES : " + (job.getNiveauEtude() != null ? job.getNiveauEtude() : "N/A"));
            
            // Expérience requise
            System.out.println("💼 EXPÉRIENCE : " + (job.getExperienceRequise() != null ? job.getExperienceRequise() : "N/A"));
            
            // Secteur d'activité
            System.out.println("🏭 SECTEUR : " + (job.getSecteurActivite() != null ? job.getSecteurActivite() : "N/A"));
            
            // URL de l'offre
            System.out.println("🔗 URL : " + (job.getSourceUrl() != null ? job.getSourceUrl() : "N/A"));
            
            // Description (limitée à 150 caractères)
            if (job.getDescription() != null && !job.getDescription().isEmpty()) {
                String desc = job.getDescription();
                if (desc.length() > 150) {
                    desc = desc.substring(0, 150) + "...";
                }
                System.out.println("📄 DESCRIPTION : " + desc);
            } else {
                System.out.println("📄 DESCRIPTION : N/A");
            }
            
            System.out.println("─────────────────────────────────────────────────────────────────────\n");
        }
        
        // Statistiques finales
        System.out.println("\n╔══════════════════════════════════════════════════════════════════════╗");
        System.out.println("║                          STATISTIQUES                                ║");
        System.out.println("╚══════════════════════════════════════════════════════════════════════╝");
        
        // Compter les offres par type de contrat
        long cdiCount = jobs.stream()
            .filter(j -> j.getContractType() != null && j.getContractType().contains("CDI"))
            .count();
        long cddCount = jobs.stream()
            .filter(j -> j.getContractType() != null && j.getContractType().contains("CDD"))
            .count();
        long stageCount = jobs.stream()
            .filter(j -> j.getContractType() != null && 
                   (j.getContractType().toLowerCase().contains("stage") || 
                    j.getContractType().toLowerCase().contains("stagiaire")))
            .count();
        
        System.out.println("📊 CDI : " + cdiCount);
        System.out.println("📊 CDD : " + cddCount);
        System.out.println("📊 Stage : " + stageCount);
        
        // Compter les offres avec entreprise confidentielle
        long confidentielCount = jobs.stream()
            .filter(j -> j.getCompany() != null && j.getCompany().equalsIgnoreCase("Confidentiel"))
            .count();
        
        System.out.println("🔒 Offres confidentielles : " + confidentielCount);
        System.out.println("🏢 Offres avec entreprise : " + (jobs.size() - confidentielCount));
        
        // Top 5 villes
        System.out.println("\n📍 TOP 5 VILLES :");
        jobs.stream()
            .filter(j -> j.getLocation() != null && !j.getLocation().isEmpty())
            .collect(java.util.stream.Collectors.groupingBy(
                JobAnnouncement::getLocation,
                java.util.stream.Collectors.counting()
            ))
            .entrySet()
            .stream()
            .sorted((e1, e2) -> Long.compare(e2.getValue(), e1.getValue()))
            .limit(5)
            .forEach(entry -> System.out.println("   • " + entry.getKey() + " : " + entry.getValue() + " offres"));
        
        System.out.println("\n═══════════════════════════════════════════════════════════════════════");
        System.out.println("                    ✅ Scraping terminé avec succès !                  ");
        System.out.println("═══════════════════════════════════════════════════════════════════════\n");
    }
}