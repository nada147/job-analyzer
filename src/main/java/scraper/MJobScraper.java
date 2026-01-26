package scraper;
import utils.DataNormalizer;
import model.JobAnnouncement;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Scraper pour le site m-job.ma
 * Scrape automatiquement toutes les pages disponibles
 */
public class MJobScraper implements SiteScraper {
    
    private static final String BASE_URL = "https://www.m-job.ma";
    private static final String SEARCH_URL = BASE_URL + "/recherche";
    private static final int TIMEOUT = 20000; // Augmenté à 20 secondes
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36";
    private static final int MAX_EMPTY_PAGES = 3;
    private static final int MAX_CONSECUTIVE_ERRORS = 5; // Arrêter après 5 erreurs consécutives
    
    @Override
    public List<JobAnnouncement> scrape() {
        List<JobAnnouncement> jobList = new ArrayList<>();
        int page = 1;
        int consecutiveEmptyPages = 0;
        int consecutiveErrors = 0;
        
        System.out.println("🕷️  Démarrage du scraping de m-job.ma...");
        System.out.println("📊 Mode : Scraping automatique de toutes les pages disponibles");
        System.out.println("⚠️  Note : M-Job peut être lent ou utiliser du JavaScript\n");
        
        while (true) {
            try {
                System.out.println("📄 Tentative scraping page " + page + "...");
                String pageUrl = SEARCH_URL + "?page=" + page;
                
                List<String> jobUrls = scrapeJobUrlsFromPage(pageUrl);
                
                if (jobUrls.isEmpty()) {
                    consecutiveEmptyPages++;
                    System.out.println("⚠️  Aucune offre trouvée sur la page " + page);
                    
                    if (consecutiveEmptyPages >= MAX_EMPTY_PAGES) {
                        System.out.println("🛑 Arrêt : " + MAX_EMPTY_PAGES + " pages vides consécutives");
                        break;
                    }
                    
                    page++;
                    Thread.sleep(3000); // Pause plus longue après une page vide
                    continue;
                }
                
                // Réinitialiser les compteurs
                consecutiveEmptyPages = 0;
                consecutiveErrors = 0;
                
                System.out.println("✓ " + jobUrls.size() + " URLs trouvées");
                
                int validJobs = 0;
                for (int i = 0; i < jobUrls.size(); i++) {
                    try {
                        System.out.print("   Traitement offre " + (i + 1) + "/" + jobUrls.size() + "... ");
                        
                        JobAnnouncement job = scrapeJobDetail(jobUrls.get(i));
                        
                        if (job != null && job.getTitle() != null && !job.getTitle().isEmpty()) {
                            jobList.add(job);
                            validJobs++;
                            System.out.println("✓");
                        } else {
                            System.out.println("✗ (données incomplètes)");
                        }
                        
                        // Pause entre chaque offre
                        Thread.sleep(2000);
                        
                    } catch (Exception e) {
                        System.out.println("✗ (" + e.getMessage() + ")");
                    }
                }
                
                System.out.println("   ➜ " + validJobs + " offres extraites avec succès\n");
                
                page++;
                Thread.sleep(2000); // Pause entre les pages
                
            } catch (IOException e) {
                consecutiveErrors++;
                System.err.println("❌ Erreur connexion page " + page + " : " + e.getMessage());
                
                if (consecutiveErrors >= MAX_CONSECUTIVE_ERRORS) {
                    System.out.println("🛑 Arrêt après " + MAX_CONSECUTIVE_ERRORS + " erreurs consécutives");
                    System.out.println("💡 Le site peut bloquer les requêtes automatiques ou utiliser du JavaScript");
                    break;
                }
                
                consecutiveEmptyPages++;
                if (consecutiveEmptyPages >= MAX_EMPTY_PAGES) {
                    break;
                }
                
                page++;
                
                try {
                    Thread.sleep(5000); // Pause plus longue après une erreur
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }
                
            } catch (InterruptedException e) {
                System.err.println("❌ Interruption : " + e.getMessage());
                Thread.currentThread().interrupt();
                break;
            }
        }
        
        System.out.println("\n✅ Scraping terminé :");
        System.out.println("   📄 Pages tentées : " + page);
        System.out.println("   📋 Offres récupérées : " + jobList.size());
        
        if (jobList.isEmpty()) {
            System.out.println("\n⚠️  ATTENTION : Aucune offre récupérée !");
            System.out.println("   Causes possibles :");
            System.out.println("   - Le site utilise du JavaScript pour charger le contenu");
            System.out.println("   - Le site bloque les scrapers");
            System.out.println("   - La structure HTML a changé");
            System.out.println("   - Le site est temporairement indisponible");
        }
        
        return jobList;
    }
    
    /**
     * Récupère les URLs des offres d'une page de résultats
     */
    private List<String> scrapeJobUrlsFromPage(String pageUrl) throws IOException {
        List<String> jobUrls = new ArrayList<>();
        
        try {
            Document doc = Jsoup.connect(pageUrl)
                    .userAgent(USER_AGENT)
                    .timeout(TIMEOUT)
                    .referrer("https://www.m-job.ma/")
                    .followRedirects(true)
                    .ignoreHttpErrors(false)
                    .get();
            
            // Essayer plusieurs sélecteurs possibles
            String[] selectors = {
                ".offer-box h3.offer-title a",
                ".offer-box a[href*='/offres-emploi/']",
                "h3.offer-title a",
                "a.offer-link"
            };
            
            for (String selector : selectors) {
                Elements links = doc.select(selector);
                
                if (!links.isEmpty()) {
                    System.out.println("   ℹ️  Utilisation du sélecteur : " + selector);
                    
                    for (Element link : links) {
                        String url = link.attr("abs:href"); // Utiliser abs:href pour URL absolue
                        if (url.isEmpty()) {
                            url = link.attr("href");
                            if (!url.startsWith("http")) {
                                url = BASE_URL + url;
                            }
                        }
                        
                        if (url != null && !url.isEmpty() && url.contains("offres-emploi")) {
                            jobUrls.add(url);
                        }
                    }
                    
                    if (!jobUrls.isEmpty()) {
                        break; // On a trouvé des URLs, on arrête
                    }
                }
            }
            
        } catch (IOException e) {
            System.err.println("   ⚠️  Erreur lecture page : " + e.getMessage());
            throw e;
        }
        
        return jobUrls;
    }
    
    /**
     * Scrape les détails d'une offre d'emploi
     */
    private JobAnnouncement scrapeJobDetail(String jobUrl) throws IOException {
        try {
            Document doc = Jsoup.connect(jobUrl)
                    .userAgent(USER_AGENT)
                    .timeout(TIMEOUT)
                    .referrer(SEARCH_URL)
                    .followRedirects(true)
                    .get();
            
            JobAnnouncement job = new JobAnnouncement();
            job.setSourceUrl(jobUrl);
            
            // Titre - essayer plusieurs sélecteurs
            Element titleElement = doc.selectFirst("h1.offer-title");
            if (titleElement == null) {
                titleElement = doc.selectFirst("h1");
            }
            if (titleElement != null) {
                job.setTitle(titleElement.text().trim());
            } else {
                return null; // Pas de titre = offre invalide
            }
            
            // Entreprise, contrat, salaire
            Elements listDetails = doc.select(".list-details li");
            for (Element li : listDetails) {
                Element span = li.selectFirst("span");
                Element h3 = li.selectFirst("h3");
                
                if (span != null && h3 != null) {
                    String label = span.text().trim().toLowerCase();
                    String value = h3.text().trim();
                    
                    if (label.contains("société") || label.contains("entreprise")) {
                        job.setCompany(value);
                    } else if (label.contains("contrat")) {
                        job.setContractType(value);
                    } else if (label.contains("salaire")) {
                        job.setSalary(value);
                    }
                }
            }
            
            // Localisation
            Element locationElement = doc.selectFirst(".header-info .location span");
            if (locationElement == null) {
                locationElement = doc.selectFirst(".location");
            }
            if (locationElement != null) {
                job.setLocation(locationElement.text().trim());
            }
            
            // Date de publication
            Element dateElement = doc.selectFirst(".bottom-content span");
            if (dateElement != null) {
                String dateText = dateElement.text().trim();
                job.setPublishDateString(dateText);
                job.setPublishDate(parseDate(dateText));
            }
            
            // Sections de contenu
            Elements contentSections = doc.select(".the-content h3.heading");
            
            for (Element heading : contentSections) {
                String headingText = heading.text().trim().toLowerCase();
                Element contentDiv = heading.nextElementSibling();
                
                if (contentDiv != null) {
                    String content = contentDiv.text().trim();
                    
                    if (headingText.contains("recruteur")) {
                        appendToDescription(job, "Le recruteur: " + content);
                    } else if (headingText.contains("poste")) {
                        appendToDescription(job, "Poste: " + content);
                    } else if (headingText.contains("profil")) {
                        appendToDescription(job, "Profil: " + content);
                    } else if (headingText.contains("secteur")) {
                        job.setSecteurActivite(content);
                        job.setDomain(content);
                    } else if (headingText.contains("métier")) {
                        job.setFonction(content);
                    } else if (headingText.contains("expérience")) {
                        job.setExperienceRequise(content);
                        job.setExperienceLevel(content);
                    } else if (headingText.contains("études") || headingText.contains("etudes")) {
                        job.setNiveauEtude(content);
                    } else if (headingText.contains("langue")) {
                        job.addSkill("Langue: " + content);
                    }
                }
            }
            
            // Nettoyer la description
            if (job.getDescription() != null) {
                job.setDescription(job.getDescription().trim());
            }

            return job;
            
        } catch (IOException e) {
            throw new IOException("Erreur scraping détails: " + e.getMessage());
        }
    }
    
    /**
     * Ajoute du contenu à la description
     */
    private void appendToDescription(JobAnnouncement job, String content) {
        String currentDesc = job.getDescription();
        if (currentDesc == null || currentDesc.isEmpty()) {
            job.setDescription(content);
        } else {
            job.setDescription(currentDesc + "\n\n" + content);
        }
    }
    
    /**
     * Parse la date depuis le texte
     */
    private Date parseDate(String dateText) {
        try {
            Date now = new Date();
            
            if (dateText.contains("aujourd'hui")) {
                return now;
            }
            
            Pattern pattern = Pattern.compile("(\\d+)\\s+(jour|jours|mois|semaine|semaines)");
            Matcher matcher = pattern.matcher(dateText.toLowerCase());
            
            if (matcher.find()) {
                int amount = Integer.parseInt(matcher.group(1));
                String unit = matcher.group(2);
                
                long millisToSubtract = 0;
                
                if (unit.contains("jour")) {
                    millisToSubtract = amount * 24L * 60 * 60 * 1000;
                } else if (unit.contains("semaine")) {
                    millisToSubtract = amount * 7L * 24 * 60 * 60 * 1000;
                } else if (unit.contains("mois")) {
                    millisToSubtract = amount * 30L * 24 * 60 * 60 * 1000;
                }
                
                return new Date(now.getTime() - millisToSubtract);
            }
            
        } catch (Exception e) {
            // Ignorer silencieusement
        }
        
        return new Date();
    }
    
    /**
     * Méthode pour scraper une URL spécifique (utile pour les tests)
     */
    public JobAnnouncement scrapeSingleJob(String jobUrl) {
        try {
            return scrapeJobDetail(jobUrl);
        } catch (IOException e) {
            System.err.println("Erreur lors du scraping de " + jobUrl + ": " + e.getMessage());
            return null;
        }
    }
    
    public String getSiteName() {
        return "M-Job";
    }
}