package scraper;
import utils.DataNormalizer;
import model.JobAnnouncement;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class EmploiMaScraper implements SiteScraper {

    private static final String BASE_URL = "https://www.emploi.ma/recherche-jobs-maroc";
    private static final int TIMEOUT = 15000;
    private static final String USER_AGENT =
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36";

    @Override
    public List<JobAnnouncement> scrape() {

        List<JobAnnouncement> jobs = new ArrayList<>();
        int page = 1;
        int emptyPages = 0;

        System.out.println("🕷️ Scraping Emploi.ma — TOUTES les pages");

        while (true) {
            try {
                String url = (page == 1)
                        ? BASE_URL
                        : BASE_URL + "?page=" + page;

                System.out.println("📄 Page " + page + " → " + url);

                Document doc = Jsoup.connect(url)
                        .userAgent(USER_AGENT)
                        .timeout(TIMEOUT)
                        .referrer("https://www.emploi.ma/")
                        .followRedirects(true)
                        .get();

                Elements jobElements = doc.select("div.card.card-job");
                System.out.println("➡ Offres trouvées : " + jobElements.size());

                if (jobElements.isEmpty()) {
                    emptyPages++;
                    if (emptyPages >= 3) {
                        System.out.println("🛑 Fin du scraping (3 pages vides)");
                        break;
                    }
                    page++;
                    continue;
                }

                emptyPages = 0;

                for (Element element : jobElements) {
                    JobAnnouncement job = parseJobElement(element);
                    if (job != null && job.getTitle() != null) {
                        jobs.add(job);
                    }
                }

                page++;
                Thread.sleep(2000);

            } catch (Exception e) {
                System.err.println("❌ Erreur page " + page + " : " + e.getMessage());
                break;
            }
        }

        System.out.println("✅ Emploi.ma terminé : " + jobs.size() + " offres récupérées\n");
        return jobs;
    }

    private JobAnnouncement parseJobElement(Element element) {

        try {
            JobAnnouncement job = new JobAnnouncement();

            // ===== TITRE & URL =====
            Element titleEl = element.selectFirst("h3 a");
            if (titleEl == null) return null;

            job.setTitle(titleEl.text().trim());

            String href = titleEl.attr("href");
            if (!href.startsWith("http")) {
                href = "https://www.emploi.ma" + href;
            }
            job.setSourceUrl(href);

            // ===== ENTREPRISE (CORRIGÉ) =====
            // Chercher d'abord dans le lien avec la classe "company-name"
            Element companyLink = element.selectFirst("a.company-name");
            if (companyLink != null && !companyLink.text().trim().isEmpty()) {
                job.setCompany(companyLink.text().trim());
            } else {
                // Sinon essayer l'image comme fallback
                Element img = element.selectFirst("picture img");
                if (img != null) {
                    String company = img.attr("title");
                    if (company == null || company.isEmpty()) {
                        company = img.attr("alt");
                    }
                    job.setCompany(
                            (company == null || company.isEmpty())
                                    ? "Confidentiel"
                                    : company.trim()
                    );
                } else {
                    job.setCompany("Confidentiel");
                }
            }

            // ===== DESCRIPTION COURTE =====
            Element desc = element.selectFirst("div.card-job-description p");
            if (desc != null) {
                job.setDescription(desc.text().trim());
            }

            // ===== INFOS =====
            for (Element li : element.select("ul li")) {
                String text = li.text().toLowerCase();
                Element strong = li.selectFirst("strong");
                if (strong == null) continue;

                if (text.contains("niveau d'études") || text.contains("niveau d´études")) {
                    job.setNiveauEtude(strong.text().trim());
                } else if (text.contains("expérience") || text.contains("experience")) {
                    job.setExperienceRequise(strong.text().trim());
                } else if (text.contains("contrat")) {
                    job.setContractType(strong.text().trim());
                } else if (text.contains("ville") || text.contains("région") || text.contains("region de")) {
                    job.setLocation(strong.text().trim());
                } else if (text.contains("secteur")) {
                    job.setSecteurActivite(strong.text().trim());
                }
            }

            // ===== DATE =====
            Element time = element.selectFirst("time");
            if (time != null) {
                job.setPublishDateString(time.text().trim());
                try {
                    job.setPublishDate(
                            new SimpleDateFormat("dd.MM.yyyy", Locale.FRENCH)
                                    .parse(time.text().trim())
                    );
                } catch (ParseException ignored) {}
            }

            return job;

        } catch (Exception e) {
            System.err.println("⚠️ Erreur parsing: " + e.getMessage());
            return null;
        }
    }

    public String getSiteName() {
        return "Emploi.ma";
    }
}