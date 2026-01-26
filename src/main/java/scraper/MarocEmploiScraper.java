package scraper;
import utils.DataNormalizer;
import model.JobAnnouncement;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Scraper pour MarocEmploi.net
 * Utilise l'API AJAX pour contourner le JavaScript
 * Scrape automatiquement toutes les pages disponibles
 */
public class MarocEmploiScraper implements SiteScraper {
    
    private static final String AJAX_URL = "https://marocemploi.net/offre/?ajax_filter=true&job_page=";
    private static final int TIMEOUT = 15000;
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36";
    private static final int MAX_EMPTY_PAGES = 3; // Arrêter après 3 pages vides consécutives

    @Override
    public List<JobAnnouncement> scrape() {
        List<JobAnnouncement> jobs = new ArrayList<>();
        int page = 1;
        int consecutiveEmptyPages = 0;
        
        System.out.println("🕷️  Démarrage du scraping de MarocEmploi.net...");
        System.out.println("💡 Utilisation de l'API AJAX");
        System.out.println("📊 Mode : Scraping automatique de toutes les pages disponibles\n");
        
        while (true) {
            try {
                System.out.println("📄 Scraping page " + page + "...");
                
                String url = AJAX_URL + page;
                
                Document doc = Jsoup.connect(url)
                        .userAgent(USER_AGENT)
                        .timeout(TIMEOUT)
                        .referrer("https://marocemploi.net/")
                        .ignoreHttpErrors(true)
                        .get();
                
                // Sélectionner les offres avec le bon sélecteur
                Elements jobElements = doc.select(".jobsearch-joblisting-classic-wrap");
                
                if (jobElements.isEmpty()) {
                    consecutiveEmptyPages++;
                    System.out.println("⚠️  Aucune offre trouvée sur la page " + page);
                    
                    if (consecutiveEmptyPages >= MAX_EMPTY_PAGES) {
                        System.out.println("🛑 Arrêt : " + MAX_EMPTY_PAGES + " pages vides consécutives détectées");
                        break;
                    }
                    
                    page++;
                    continue;
                }
                
                // Réinitialiser le compteur de pages vides
                consecutiveEmptyPages = 0;
                
                System.out.println("✓ " + jobElements.size() + " offres trouvées");
                
                int validJobs = 0;
                for (int i = 0; i < jobElements.size(); i++) {
                    try {
                        Element jobElement = jobElements.get(i);
                        JobAnnouncement job = parseJobElement(jobElement);
                        
                        if (job != null && job.getTitle() != null && !job.getTitle().isEmpty()) {
                            jobs.add(job);
                            validJobs++;
                        }
                    } catch (Exception e) {
                        System.err.println("❌ Erreur parsing offre " + (i + 1) + " : " + e.getMessage());
                    }
                }
                
                System.out.println("   ➜ " + validJobs + " offres extraites avec succès");
                
                // Pause entre les pages
                Thread.sleep(2000);
                page++;
                
            } catch (IOException e) {
                System.err.println("❌ Erreur connexion page " + page + " : " + e.getMessage());
                consecutiveEmptyPages++;
                
                if (consecutiveEmptyPages >= MAX_EMPTY_PAGES) {
                    System.out.println("🛑 Arrêt après " + MAX_EMPTY_PAGES + " erreurs consécutives");
                    break;
                }
                
                page++;
                
            } catch (InterruptedException e) {
                System.err.println("❌ Interruption : " + e.getMessage());
                Thread.currentThread().interrupt();
                break;
            }
        }
        
        System.out.println("\n✅ Scraping terminé :");
        System.out.println("   📄 Pages scrapées : " + (page - 1));
        System.out.println("   📋 Offres récupérées : " + jobs.size());
        
        return jobs;
    }
    
    private JobAnnouncement parseJobElement(Element element) {
        try {
            JobAnnouncement job = new JobAnnouncement();
            
            // ===== TITRE ET URL =====
            Element titleElement = element.selectFirst("h2.jobsearch-pst-title a");
            if (titleElement != null) {
                job.setTitle(titleElement.text().trim());
                job.setSourceUrl(titleElement.attr("href"));
            } else {
                return null;
            }
            
            // ===== ENTREPRISE =====
            Element companyElement = element.selectFirst("li.job-company-name a");
            if (companyElement != null) {
                String company = companyElement.text().trim();
                // Enlever le @ si présent
                if (company.startsWith("@")) {
                    company = company.substring(1).trim();
                }
                job.setCompany(company);
            }
            
            // ===== LOCALISATION =====
            Element locationIcon = element.selectFirst("i.jobsearch-maps-and-flags");
            if (locationIcon != null && locationIcon.parent() != null) {
                String locationText = locationIcon.parent().text().trim();
                
                // Garder toute la ligne de localisation
                if (!locationText.isEmpty()) {
                    job.setLocation(locationText);
                }
            }
            
            // ===== DATE DE PUBLICATION =====
            Element dateIcon = element.selectFirst("i.jobsearch-calendar");
            if (dateIcon != null && dateIcon.parent() != null) {
                String dateText = dateIcon.parent().text().trim();
                job.setPublishDateString(dateText);
            }
            
            // ===== SECTEUR =====
            Element secteurIcon = element.selectFirst("i.jobsearch-filter-tool-black-shape");
            if (secteurIcon != null && secteurIcon.parent() != null) {
                Element secteurLink = secteurIcon.parent().selectFirst("a");
                if (secteurLink != null) {
                    job.setSecteurActivite(secteurLink.text().trim());
                }
            }
            
            // ===== TYPE DE CONTRAT =====
            Element contractElement = element.selectFirst(".jobsearch-job-userlist a.jobsearch-option-btn");
            if (contractElement != null) {
                job.setContractType(contractElement.text().trim());
            }

            return job;
            
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Récupère les détails complets d'une offre
     */
    public void scrapeJobDetails(JobAnnouncement job) {
        if (job.getSourceUrl() == null || job.getSourceUrl().isEmpty()) {
            return;
        }
        
        try {
            System.out.println("🔍 Récupération détails : " + job.getTitle());
            
            Document doc = Jsoup.connect(job.getSourceUrl())
                    .userAgent(USER_AGENT)
                    .timeout(TIMEOUT)
                    .get();
            
            // ===== DESCRIPTION COMPLÈTE =====
            Element descElement = doc.selectFirst(".jobsearch-description");
            if (descElement != null) {
                // Extraire tout le texte
                StringBuilder description = new StringBuilder();
                
                // Prendre les paragraphes et listes
                Elements paragraphs = descElement.select("p");
                Elements lists = descElement.select("ul li");
                
                for (Element p : paragraphs) {
                    description.append(p.text()).append("\n");
                }
                
                if (!lists.isEmpty()) {
                    description.append("\n");
                    for (Element li : lists) {
                        description.append("• ").append(li.text()).append("\n");
                    }
                }
                
                job.setDescription(description.toString().trim());
            }
            
            // ===== EXTRACTION DEPUIS LA DESCRIPTION =====
            if (job.getDescription() != null) {
                String description = job.getDescription().toLowerCase();
                
                // Expérience
                Pattern expPattern = Pattern.compile("(\\d+)\\s*(?:an|année)s?\\s*(?:d')?(?:expérience|experience)");
                Matcher expMatcher = expPattern.matcher(description);
                if (expMatcher.find()) {
                    int years = Integer.parseInt(expMatcher.group(1));
                    
                    if (years == 0 || description.contains("débutant")) {
                        job.setExperienceLevel("Junior / Débutant");
                        job.setExperienceRequise("Débutant accepté");
                    } else if (years <= 3) {
                        job.setExperienceLevel("Junior (1-3 ans)");
                        job.setExperienceRequise(years + " ans");
                    } else if (years <= 10) {
                        job.setExperienceLevel("Confirmé (3-10 ans)");
                        job.setExperienceRequise(years + " ans");
                    } else {
                        job.setExperienceLevel("Senior (10+ ans)");
                        job.setExperienceRequise(years + "+ ans");
                    }
                }
                
                // Niveau d'études
                if (description.contains("bac+5") || description.contains("master") || description.contains("ingénieur")) {
                    job.setNiveauEtude("Bac +5 et plus");
                } else if (description.contains("bac+4")) {
                    job.setNiveauEtude("Bac +4");
                } else if (description.contains("bac+3") || description.contains("licence")) {
                    job.setNiveauEtude("Bac +3");
                } else if (description.contains("bac+2")) {
                    job.setNiveauEtude("Bac +2");
                } else if (description.contains("bac")) {
                    job.setNiveauEtude("Bac");
                }
                
                // Télétravail
                if (description.contains("télétravail") || description.contains("teletravail") || 
                    description.contains("remote") || description.contains("à distance")) {
                    
                    if (description.contains("hybride")) {
                        job.setTypeTeletravail("Hybride");
                    } else if (description.contains("100%") || description.contains("complet")) {
                        job.setTypeTeletravail("Complet");
                    } else {
                        job.setTypeTeletravail("Possible");
                    }
                }
                
                // Compétences
                extractSkillsFromDescription(job);
            }
            
            Thread.sleep(1000);
            
        } catch (IOException | InterruptedException e) {
            System.err.println("❌ Erreur détails : " + e.getMessage());
        }
    }
    
    /**
     * Extrait les compétences depuis la description
     */
    private void extractSkillsFromDescription(JobAnnouncement job) {
        if (job.getDescription() == null) return;
        
        String description = job.getDescription().toLowerCase();
        String[] skillsKeywords = {
            "java", "python", "javascript", "php", "c++", "c#", "react", "angular", "vue",
            "sql", "mysql", "postgresql", "mongodb", "docker", "kubernetes", "aws",
            "html", "css", "typescript", "agile", "scrum", "devops",
            "excel", "word", "powerpoint", "sap", "erp", "crm",
            "autocad", "photoshop", "illustrator", "indesign"
        };
        
        for (String skill : skillsKeywords) {
            if (description.contains(skill)) {
                job.addSkill(skill.substring(0, 1).toUpperCase() + skill.substring(1));
            }
        }
    }
    
    public String getSiteName() {
        return "MarocEmploi";
    }
}