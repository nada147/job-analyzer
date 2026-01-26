package scraper;

import model.JobAnnouncement;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import utils.DataNormalizer;
/**
 * Scraper pour le site MarocAnnonces.com
 * Version corrigée avec limite de 100 pages
 */
public class MarocAnnoncesScraper implements SiteScraper {
    
    private static final String BASE_URL = "https://www.marocannonces.com";
    private static final String JOBS_URL = BASE_URL + "/categorie/309/Emploi/Offres-emploi.html";
    private static final int TIMEOUT = 15000;
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36";
    private static final int MAX_EMPTY_PAGES = 3;
    private static final int MAX_PAGES = 100; // Limite de sécurité pour éviter la boucle infinie
    
    @Override
    public List<JobAnnouncement> scrape() {
        List<JobAnnouncement> jobAnnouncements = new ArrayList<>();
        int page = 1;
        int consecutiveEmptyPages = 0;
        
        System.out.println("🕷️  Démarrage du scraping de MarocAnnonces.com...");
        System.out.println("📊 Mode : Scraping automatique (limite 100 pages)");
        System.out.println("🛡️  Protection contre la boucle infinie après page 100\n");
        
        while (true) {
            try {
                // Vérifier la limite de sécurité
                if (page > MAX_PAGES) {
                    System.out.println("\n🛑 Arrêt : Limite de sécurité atteinte (100 pages)");
                    System.out.println("💡 Le site boucle probablement après la page 100");
                    break;
                }
                
                System.out.println("📄 Scraping page " + page + "...");
                
                String pageUrl = (page == 1) ? JOBS_URL : 
                    BASE_URL + "/categorie/309/Emploi/Offres-emploi/" + page + ".html";
                
                List<JobAnnouncement> pageJobs = scrapePage(pageUrl);
                
                if (pageJobs.isEmpty()) {
                    consecutiveEmptyPages++;
                    System.out.println("⚠️  Aucune offre trouvée sur la page " + page);
                    
                    if (consecutiveEmptyPages >= MAX_EMPTY_PAGES) {
                        System.out.println("🛑 Arrêt : " + MAX_EMPTY_PAGES + " pages vides consécutives détectées");
                        break;
                    }
                    
                    page++;
                    continue;
                }
                
                consecutiveEmptyPages = 0;
                jobAnnouncements.addAll(pageJobs);
                System.out.println("   ➜ " + pageJobs.size() + " offres extraites avec succès");
                
                // Pause entre les pages
                Thread.sleep(2000);
                page++;
                
            } catch (InterruptedException e) {
                System.err.println("❌ Interruption : " + e.getMessage());
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                System.err.println("❌ Erreur page " + page + " : " + e.getMessage());
                consecutiveEmptyPages++;
                
                if (consecutiveEmptyPages >= MAX_EMPTY_PAGES) {
                    System.out.println("🛑 Arrêt après " + MAX_EMPTY_PAGES + " erreurs consécutives");
                    break;
                }
                
                page++;
            }
        }
        
        System.out.println("\n✅ Scraping terminé :");
        System.out.println("   📄 Pages scrapées : " + (page - 1));
        System.out.println("   📋 Offres récupérées : " + jobAnnouncements.size());
        
        return jobAnnouncements;
    }
    
    /**
     * Scrape une page de résultats
     */
    private List<JobAnnouncement> scrapePage(String pageUrl) throws IOException, InterruptedException {
        List<JobAnnouncement> jobs = new ArrayList<>();
        
        Document doc = Jsoup.connect(pageUrl)
                .userAgent(USER_AGENT)
                .timeout(TIMEOUT)
                .referrer(BASE_URL)
                .get();
        
        // Utiliser le BON sélecteur CSS de MarocAnnonces
        Elements jobLinks = doc.select("#main #twocolumns #content .used-cars .content_box a");
        
        System.out.println("✓ " + jobLinks.size() + " offres trouvées sur la page");
        
        for (int i = 0; i < jobLinks.size(); i++) {
            try {
                Element link = jobLinks.get(i);
                String relativeUrl = link.attr("href");
                
                if (relativeUrl == null || relativeUrl.isEmpty()) {
                    continue;
                }
                
                // Construire l'URL complète
                String fullUrl = BASE_URL + "/" + relativeUrl;
                
                // Scraper les détails de l'offre
                JobAnnouncement job = scrapeJobDetails(fullUrl);
                
                if (job != null && job.getTitle() != null && !job.getTitle().isEmpty()) {
                    jobs.add(job);
                    System.out.println("   ✓ Offre " + (i + 1) + "/" + jobLinks.size() + " : " + job.getTitle());
                }
                
                // Pause entre chaque offre
                if (i < jobLinks.size() - 1) {
                    Thread.sleep(1000);
                }
                
            } catch (Exception e) {
                System.err.println("   ⚠️ Erreur offre " + (i + 1) + " : " + e.getMessage());
            }
        }
        
        return jobs;
    }
    
    /**
     * Scrape les détails complets d'une annonce
     */
    private JobAnnouncement scrapeJobDetails(String url) throws IOException {
        JobAnnouncement job = new JobAnnouncement();
        job.setSourceUrl(url);
        
        try {
            Document doc = Jsoup.connect(url)
                    .userAgent(USER_AGENT)
                    .timeout(TIMEOUT)
                    .referrer(JOBS_URL)
                    .get();
            
            // Sélectionner la section description
            Element descriptionSection = doc.selectFirst("#main #twocolumns #content .description");
            
            if (descriptionSection == null) {
                System.err.println("   ⚠️ Section description non trouvée pour : " + url);
                return null;
            }
            
            // 1. Titre de l'offre
            Element titleElement = descriptionSection.selectFirst("h1");
            if (titleElement != null) {
                job.setTitle(titleElement.text().trim());
            }
            
            // 2. Localisation et date de publication
            Elements infoItems = descriptionSection.select(".info-holder li");
            if (infoItems.size() > 0) {
                job.setLocation(infoItems.get(0).text().trim());
            }
            
            // 3. Description de l'offre
            Element infoBlock = descriptionSection.selectFirst(".block");
            if (infoBlock != null) {
                job.setDescription(infoBlock.text().trim());
            }
            
            // 4. Détails structurés
            Elements extraQuestions = descriptionSection.select("#extraQuestionName li");
            
            for (Element question : extraQuestions) {
                String text = question.text().trim();
                Element linkElement = question.selectFirst("a");
                String value = (linkElement != null) ? linkElement.text().trim() : "N/A";
                
                String textLower = text.toLowerCase();
                
                if (textLower.contains("domaine")) {
                    job.setDomain(value);
                    job.setSecteurActivite(value);
                } else if (textLower.contains("fonction")) {
                    job.setFonction(value);
                } else if (textLower.contains("contrat")) {
                    job.setContractType(value);
                } else if (textLower.contains("entreprise") || textLower.contains("société")) {
                    job.setCompany(value);
                } else if (textLower.contains("salaire") || textLower.startsWith("sa")) {
                    if (!value.toLowerCase().contains("discuter")) {
                        job.setSalary(value);
                    } else {
                        job.setSalary("À discuter");
                    }
                } else if (textLower.contains("niveau d'études") || textLower.contains("niveau d'etudes")) {
                    job.setNiveauEtude(value);
                } else if (textLower.contains("expérience") || textLower.contains("experience")) {
                    job.setExperienceRequise(value);
                    job.setExperienceLevel(value);
                }
            }

            return job;
            
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            System.err.println("   ⚠️ Erreur parsing détails : " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Méthode pour scraper une URL spécifique (utile pour tests)
     */
    public List<JobAnnouncement> scrapeUrl(String url) {
        List<JobAnnouncement> jobs = new ArrayList<>();
        try {
            jobs.addAll(scrapePage(url));
        } catch (Exception e) {
            System.err.println("Erreur lors du scraping de l'URL : " + e.getMessage());
        }
        return jobs;
    }
    
    public String getSiteName() {
        return "MarocAnnonces";
    }
}