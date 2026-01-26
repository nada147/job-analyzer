package scraper;

import java.util.List;
import model.JobAnnouncement;

public interface SiteScraper {
	String getSiteName();
    /**
     * Lance le scraping du site
     * @return liste des annonces d'emploi récupérées
     */
    List<JobAnnouncement> scrape();
}
