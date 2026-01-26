package main;

import scraper.AnapecOrgScraper;
import model.JobAnnouncement;

import java.util.List;

public class MainAnapecTest {

    public static void main(String[] args) {

        System.out.println("══════════════════════════════════════");
        System.out.println("      JobAnalyzer - ANAPEC.org        ");
        System.out.println("══════════════════════════════════════\n");

        AnapecOrgScraper scraper = new AnapecOrgScraper();
        List<JobAnnouncement> jobs = scraper.scrape();

        System.out.println("══════════════════════════════════════");
        System.out.println("✅ Total offres récupérées : " + jobs.size());
        System.out.println("══════════════════════════════════════");

        int limit = Math.min(jobs.size(), 10);
        for (int i = 0; i < limit; i++) {
            JobAnnouncement job = jobs.get(i);
            System.out.println("\n#" + (i + 1) + " " + job.getTitle());
            System.out.println("🏢 " + job.getCompany());
            System.out.println("📍 " + job.getLocation());
            System.out.println("📅 " + job.getPublishDateString());
        }
    }
}