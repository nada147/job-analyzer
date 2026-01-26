package main;

import model.JobAnnouncement;
import scraper.AnapecOrgScraper;
import scraper.EmploiMaScraper;
import scraper.MarocEmploiScraper;
import scraper.MarocAnnoncesScraper;
import scraper.MJobScraper;
import scraper.RekruteScraper;
import utils.CSVExporter;
import utils.DataNormalizer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import dao.JobAnnouncementDAO;
import database.DatabaseConnection;
/**
 * Classe principale pour scraper plusieurs sites et exporter en CSV
 */
public class JobScraperMain {
    
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════╗");
        System.out.println("║     🚀 JOB SCRAPER MAROC - VERSION MULTI-SITES   ║");
        System.out.println("╚════════════════════════════════════════════════════╝\n");
        
        // Liste pour stocker toutes les offres
        List<JobAnnouncement> allJobs = new ArrayList<>();
        
        // ===== 1. SCRAPING ANAPEC.ORG =====
//        System.out.println("\n┌─────────────────────────────────────┐");
//        System.out.println("│  📍 SITE 1: ANAPEC.ORG             │");
//        System.out.println("└─────────────────────────────────────┘");
//        
//        try {
//            AnapecOrgScraper anapecScraper = new AnapecOrgScraper();
//            List<JobAnnouncement> anapecJobs = anapecScraper.scrape();
//            anapecJobs.forEach(job -> job.setSourceSite("Anapec"));
//            allJobs.addAll(anapecJobs);
//            System.out.println("✅ Anapec : " + anapecJobs.size() + " offres récupérées\n");
//        } catch (Exception e) {
//            System.err.println("❌ Erreur Anapec : " + e.getMessage() + "\n");
//        }
        
        // ===== 2. SCRAPING EMPLOI.MA =====
        System.out.println("\n┌─────────────────────────────────────┐");
        System.out.println("│  📍 SITE 2: EMPLOI.MA              │");
        System.out.println("└─────────────────────────────────────┘");
        
        try {
            EmploiMaScraper emploiMaScraper = new EmploiMaScraper();
            List<JobAnnouncement> emploiMaJobs = emploiMaScraper.scrape();
            emploiMaJobs.forEach(job -> job.setSourceSite("EmploiMa"));
            allJobs.addAll(emploiMaJobs);
            System.out.println("✅ EmploiMa : " + emploiMaJobs.size() + " offres récupérées\n");
        } catch (Exception e) {
            System.err.println("❌ Erreur EmploiMa : " + e.getMessage() + "\n");
        }
        
        // ===== 3. SCRAPING MAROCEMPLOI =====
        System.out.println("\n┌─────────────────────────────────────┐");
        System.out.println("│  📍 SITE 3: MAROCEMPLOI.NET        │");
        System.out.println("└─────────────────────────────────────┘");
        
        try {
            MarocEmploiScraper marocEmploiScraper = new MarocEmploiScraper();
            List<JobAnnouncement> marocEmploiJobs = marocEmploiScraper.scrape();
            marocEmploiJobs.forEach(job -> job.setSourceSite("MarocEmploi"));
            allJobs.addAll(marocEmploiJobs);
            System.out.println("✅ MarocEmploi : " + marocEmploiJobs.size() + " offres récupérées\n");
        } catch (Exception e) {
            System.err.println("❌ Erreur MarocEmploi : " + e.getMessage() + "\n");
        }
        
        // ===== 4. SCRAPING MAROCANNONCES =====
        System.out.println("\n┌─────────────────────────────────────┐");
        System.out.println("│  📍 SITE 4: MAROCANNONCES.COM      │");
        System.out.println("└─────────────────────────────────────┘");
        
        try {
            MarocAnnoncesScraper marocAnnoncesScraper = new MarocAnnoncesScraper();
            List<JobAnnouncement> marocAnnoncesJobs = marocAnnoncesScraper.scrape();
            marocAnnoncesJobs.forEach(job -> job.setSourceSite("MarocAnnonces"));
            allJobs.addAll(marocAnnoncesJobs);
            System.out.println("✅ MarocAnnonces : " + marocAnnoncesJobs.size() + " offres récupérées\n");
        } catch (Exception e) {
            System.err.println("❌ Erreur MarocAnnonces : " + e.getMessage() + "\n");
        }
        
//        // ===== 5. SCRAPING MJOB =====
        System.out.println("\n┌─────────────────────────────────────┐");
        System.out.println("│  📍 SITE 5: MJOB.MA                │");
        System.out.println("└─────────────────────────────────────┘");
        
        try {
            MJobScraper mJobScraper = new MJobScraper();
            List<JobAnnouncement> mJobJobs = mJobScraper.scrape();
            mJobJobs.forEach(job -> job.setSourceSite("MJob"));
            allJobs.addAll(mJobJobs);
            System.out.println("✅ MJob : " + mJobJobs.size() + " offres récupérées\n");
        } catch (Exception e) {
            System.err.println("❌ Erreur MJob : " + e.getMessage() + "\n");
        }
        
//        // ===== 6. SCRAPING REKRUTE =====
        System.out.println("\n┌─────────────────────────────────────┐");
        System.out.println("│  📍 SITE 6: REKRUTE.COM            │");
        System.out.println("└─────────────────────────────────────┘");
        
        try {
            RekruteScraper rekruteScraper = new RekruteScraper();
            List<JobAnnouncement> rekruteJobs = rekruteScraper.scrape();
            rekruteJobs.forEach(job -> job.setSourceSite("Rekrute"));
            allJobs.addAll(rekruteJobs);
            System.out.println("✅ Rekrute : " + rekruteJobs.size() + " offres récupérées\n");
        } catch (Exception e) {
            System.err.println("❌ Erreur Rekrute : " + e.getMessage() + "\n");
        }
        
        // ===== 7. NORMALISATION =====
        System.out.println("\n┌─────────────────────────────────────┐");
        System.out.println("│  🔧 NORMALISATION DES DONNÉES       │");
        System.out.println("└─────────────────────────────────────┘");
        
        System.out.println("🔄 Normalisation en cours...");
        DataNormalizer.normalizeAll(allJobs);
        System.out.println("✅ Normalisation terminée : " + allJobs.size() + " offres normalisées\n");
        
        
        
     // ===== 8. INSERTION EN BASE DE DONNÉES =====
     // ===== STOCKAGE EN BASE DE DONNÉES ⭐ =====
        System.out.println("\n┌─────────────────────────────────────┐");
        System.out.println("│  💾 STOCKAGE EN BASE DE DONNÉES     │");
        System.out.println("└─────────────────────────────────────┘");
        
        JobAnnouncementDAO dao = new JobAnnouncementDAO();
        int inserted = dao.insertBatch(allJobs);
        
        System.out.println("✅ " + inserted + " offres insérées/mises à jour en BD");

        
        System.out.println("✅ " + inserted + " offres insérées/mises à jour en BD");
        
        // ===== 8. EXPORT CSV =====
        System.out.println("\n┌─────────────────────────────────────┐");
        System.out.println("│  💾 EXPORT EN CSV                   │");
        System.out.println("└─────────────────────────────────────┘");
        
        // Nom du fichier avec timestamp
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String fileName = "jobs_maroc_" + timestamp + ".csv";
        
        CSVExporter.export(allJobs, fileName);
        
        // ===== 9. RÉSUMÉ FINAL =====
        System.out.println("\n╔════════════════════════════════════════════════════╗");
        System.out.println("║              📊 RÉSUMÉ FINAL                      ║");
        System.out.println("╚════════════════════════════════════════════════════╝");
        
        long anapecCount = allJobs.stream()
            .filter(j -> "Anapec".equals(j.getSourceSite()))
            .count();
            
        long emploiMaCount = allJobs.stream()
            .filter(j -> "EmploiMa".equals(j.getSourceSite()))
            .count();
            
        long marocEmploiCount = allJobs.stream()
            .filter(j -> "MarocEmploi".equals(j.getSourceSite()))
            .count();
            
        long marocAnnoncesCount = allJobs.stream()
            .filter(j -> "MarocAnnonces".equals(j.getSourceSite()))
            .count();
            
        long mJobCount = allJobs.stream()
            .filter(j -> "MJob".equals(j.getSourceSite()))
            .count();
            
        long rekruteCount = allJobs.stream()
            .filter(j -> "Rekrute".equals(j.getSourceSite()))
            .count();
        
        System.out.println("📋 Total offres scrapées : " + allJobs.size());
        System.out.println("   ├─ Anapec          : " + anapecCount);
        System.out.println("   ├─ EmploiMa        : " + emploiMaCount);
        System.out.println("   ├─ MarocEmploi     : " + marocEmploiCount);
        System.out.println("   ├─ MarocAnnonces   : " + marocAnnoncesCount);
        System.out.println("   ├─ MJob            : " + mJobCount);
        System.out.println("   └─ Rekrute         : " + rekruteCount);
        System.out.println("\n💾 Fichier exporté : " + fileName);
        System.out.println("\n✅ Traitement terminé avec succès !");
    }
}