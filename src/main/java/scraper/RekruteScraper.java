package scraper;
import utils.DataNormalizer;
import model.JobAnnouncement;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Scraper pour le site Rekrute.com
 * Scrape automatiquement toutes les pages disponibles
 */
public class RekruteScraper implements SiteScraper {
    
    private static final String BASE_URL = "https://www.rekrute.com/offres-emploi-maroc.html";
    private static final int TIMEOUT = 10000;
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36";
    private static final int MAX_EMPTY_PAGES = 3; // Arrêter après 3 pages vides consécutives
    
    @Override
    public List<JobAnnouncement> scrape() {
        List<JobAnnouncement> jobs = new ArrayList<>();
        int page = 1;
        int emptyPagesCount = 0;
        
        System.out.println("🕷  Démarrage du scraping de Rekrute.com...");
        System.out.println("📊 Mode : Scraping automatique de toutes les pages disponibles\n");
        
        while (true) {
            try {
                System.out.println("📄 Scraping page " + page + "...");
                
                String url = (page == 1) ? BASE_URL : 
                    "https://www.rekrute.com/offres.html?s=1&p=" + page + "&o=1&keyword=&st=d&jobLocation=RK";
                
                Document doc = Jsoup.connect(url)
                        .userAgent(USER_AGENT)
                        .timeout(TIMEOUT)
                        .get();
                
                Elements jobElements = doc.select("li.post-id");
                
                if (jobElements.isEmpty()) {
                    emptyPagesCount++;
                    System.out.println("⚠  Aucune offre trouvée sur la page " + page);
                    
                    if (emptyPagesCount >= MAX_EMPTY_PAGES) {
                        System.out.println("🛑 Arrêt : " + MAX_EMPTY_PAGES + " pages vides consécutives détectées");
                        break;
                    }
                    
                    page++;
                    continue;
                }
                
                // Réinitialiser le compteur de pages vides
                emptyPagesCount = 0;
                
                System.out.println("✓ " + jobElements.size() + " offres trouvées");
                
                int jobsParsed = 0;
                for (Element jobElement : jobElements) {
                    try {
                        JobAnnouncement job = parseJobElement(jobElement);
                        if (job != null) {
                            jobs.add(job);
                            jobsParsed++;
                        }
                    } catch (Exception e) {
                        System.err.println("❌ Erreur parsing : " + e.getMessage());
                    }
                }
                
                System.out.println("   ➜ " + jobsParsed + " offres extraites avec succès");
                
                // Pause entre les pages pour éviter de surcharger le serveur
                Thread.sleep(2000);
                page++;
                
            } catch (IOException e) {
                System.err.println("❌ Erreur connexion page " + page + " : " + e.getMessage());
                emptyPagesCount++;
                
                if (emptyPagesCount >= MAX_EMPTY_PAGES) {
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
            String fullText = element.text();
            
            // ===== TITRE ET LOCALISATION =====
            Element titleElement = element.selectFirst("h2 a, h3 a");
            if (titleElement != null) {
                String fullTitle = titleElement.text().trim();
                
                if (fullTitle.contains("|")) {
                    String[] parts = fullTitle.split("\\|", 2);
                    job.setTitle(parts[0].trim());
                    
                    if (parts.length > 1) {
                        String location = parts[1].trim()
                            .replace("(Maroc)", "")
                            .trim();
                        job.setLocation(location);
                    }
                } else {
                    job.setTitle(fullTitle);
                }
                
                String href = titleElement.attr("href");
                if (!href.isEmpty()) {
                    job.setSourceUrl("https://www.rekrute.com" + href);
                }
            }
            
            // ===== ENTREPRISE =====
            Element imgElement = element.selectFirst("img.photo");
            if (imgElement != null) {
                String company = imgElement.attr("alt");
                if (company == null || company.isEmpty()) {
                    company = imgElement.attr("title");
                }
                
                if (company != null && !company.isEmpty() && 
                    !company.contains("logo-confidentiel")) {
                    job.setCompany(company.trim());
                } else {
                    job.setCompany("Confidentiel");
                }
            }
            
            // ===== DESCRIPTION - EXTRACTION OPTIMISÉE =====
            String description = extractDescription(element, job.getTitle(), job.getLocation(), fullText);
            job.setDescription(description != null ? description : "Description non disponible");
            
            // ===== DATE DE PUBLICATION =====
            Element dateElement = element.selectFirst("em.date");
            if (dateElement != null) {
                Elements dateSpans = dateElement.select("span");
                
                if (!dateSpans.isEmpty()) {
                    String dateText = dateElement.text().trim();
                    int pipeIndex = dateText.indexOf('|');
                    String publishInfo = (pipeIndex != -1) 
                        ? dateText.substring(0, pipeIndex).trim() 
                        : dateText;
                    
                    job.setPublishDateString(publishInfo);
                    
                    String startDateStr = dateSpans.get(0).text().trim();
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.FRENCH);
                        Date publishDate = sdf.parse(startDateStr);
                        job.setPublishDate(publishDate);
                    } catch (ParseException e) {
                        // Ignorer silencieusement
                    }
                }
            }
            
            // ===== EXTRACTION DES INFORMATIONS STRUCTURÉES =====
            extractStructuredInfo(job, fullText);
            
            if (job.getTitle() != null && !job.getTitle().isEmpty()) {

                return job;
            }
            
        } catch (Exception e) {
            System.err.println("Erreur parsing : " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Extrait la description de l'offre en nettoyant les informations de localisation
     */
    private String extractDescription(Element element, String title, String location, String fullText) {
        // Méthode 1 : Chercher dans les paragraphes <p>
        Elements paragraphs = element.select("p");
        for (Element p : paragraphs) {
            String text = p.text().trim();
            
            if (text.length() > 50 && 
                !text.startsWith("Publication") &&
                !text.startsWith("Postes proposés") &&
                !text.contains("Secteur d'activité") &&
                !text.contains("* Secteur")) {
                return text;
            }
        }
        
        // Méthode 2 : Extraction du texte entre titre et "Publication"
        if (title != null) {
            int titlePos = fullText.indexOf(title);
            int pubPos = fullText.indexOf("Publication :");
            
            if (titlePos != -1 && pubPos != -1 && pubPos > titlePos) {
                String extracted = fullText.substring(titlePos + title.length(), pubPos).trim();
                
                // Nettoyage agressif
                extracted = extracted.replaceFirst("^\\d+\\s*", ""); // Supprimer chiffres
                extracted = extracted.replaceFirst("^\\|\\s*", ""); // Supprimer pipe
                
                // Supprimer toutes les villes marocaines courantes
                String[] cities = {"Casablanca", "Rabat", "Marrakech", "Fès", "Fez", "Tanger", "Agadir", 
                                   "Meknès", "Meknes", "Oujda", "Kenitra", "Kénitra", "Tétouan", 
                                   "Tetouan", "Salé", "Sale", "Mohammedia", "El Jadida", "Settat",
                                   "Béni Mellal", "Nador", "Khouribga", "Safi", "Laâyoune"};
                for (String city : cities) {
                    extracted = extracted.replaceFirst("^" + city + "\\s*", "");
                    extracted = extracted.replaceFirst("^" + city.toLowerCase() + "\\s*", "");
                }
                
                // Supprimer "(Maroc)" et "Maroc"
                extracted = extracted.replaceFirst("^\\(Maroc\\)\\s*", "");
                extracted = extracted.replaceFirst("^Maroc\\s*", "");
                
                // Si location existe, la supprimer aussi
                if (location != null && !location.isEmpty()) {
                    extracted = extracted.replaceFirst("^" + Pattern.quote(location) + "\\s*", "");
                }
                
                extracted = extracted.trim();
                
                if (extracted.length() > 50 && !extracted.startsWith("*")) {
                    return extracted;
                }
            }
        }
        
        return null;
    }
    
    /**
     * Extrait les informations structurées de l'offre
     */
    private void extractStructuredInfo(JobAnnouncement job, String fullText) {
        // Nombre de postes
        Pattern postesPattern = Pattern.compile("Postes proposés\\s*:\\s*(\\d+)");
        Matcher postesMatcher = postesPattern.matcher(fullText);
        if (postesMatcher.find()) {
            try {
                job.setNombrePostes(Integer.parseInt(postesMatcher.group(1).trim()));
            } catch (NumberFormatException e) {
                job.setNombrePostes(1);
            }
        }
        
        // Secteur d'activité
        Pattern secteurPattern = Pattern.compile("Secteur d'activité\\s*:\\s*([^*\\n]+?)(?=\\*|Fonction|\\n|$)");
        Matcher secteurMatcher = secteurPattern.matcher(fullText);
        if (secteurMatcher.find()) {
            job.setSecteurActivite(secteurMatcher.group(1).trim());
        }
        
        // Fonction
        Pattern fonctionPattern = Pattern.compile("Fonction\\s*:\\s*([^*\\n]+?)(?=\\*|Expérience|\\n|$)");
        Matcher fonctionMatcher = fonctionPattern.matcher(fullText);
        if (fonctionMatcher.find()) {
            job.setFonction(fonctionMatcher.group(1).trim());
        }
        
        // Expérience requise
        Pattern experiencePattern = Pattern.compile("Expérience requise\\s*:\\s*([^*\\n]+?)(?=\\*|Niveau|\\n|$)");
        Matcher experienceMatcher = experiencePattern.matcher(fullText);
        if (experienceMatcher.find()) {
            String experience = experienceMatcher.group(1).trim();
            job.setExperienceRequise(experience);
            
            if (experience.toLowerCase().contains("débutant") || experience.contains("0")) {
                job.setExperienceLevel("Junior / Débutant");
            } else if (experience.matches(".*[1-3]\\s*an.*")) {
                job.setExperienceLevel("Junior (1-3 ans)");
            } else if (experience.matches(".*[4-9]\\s*an.*") || experience.toLowerCase().contains("confirmé")) {
                job.setExperienceLevel("Confirmé (3-10 ans)");
            } else if (experience.matches(".*1[0-9]\\s*an.*") || experience.matches(".*2[0-9]\\s*an.*")) {
                job.setExperienceLevel("Senior (10+ ans)");
            } else {
                job.setExperienceLevel(experience);
            }
        }
        
        // Niveau d'étude
        Pattern niveauPattern = Pattern.compile("Niveau d'étude demandé\\s*:\\s*([^*\\n]+?)(?=\\*|Type de contrat|\\n|$)");
        Matcher niveauMatcher = niveauPattern.matcher(fullText);
        if (niveauMatcher.find()) {
            job.setNiveauEtude(niveauMatcher.group(1).trim());
        }
        
        // Type de contrat
        Pattern contractPattern = Pattern.compile("Type de contrat proposé\\s*:\\s*([^-*\\n]+?)(?=-|\\*|Télétravail|\\n|$)");
        Matcher contractMatcher = contractPattern.matcher(fullText);
        if (contractMatcher.find()) {
            job.setContractType(contractMatcher.group(1).trim());
        } else {
            if (fullText.contains("CDI")) job.setContractType("CDI");
            else if (fullText.contains("CDD")) job.setContractType("CDD");
            else if (fullText.contains("Stage")) job.setContractType("Stage");
        }
        
        // Télétravail
        Pattern teletravailPattern = Pattern.compile("Télétravail\\s*:\\s*([^*\\n]+)");
        Matcher teletravailMatcher = teletravailPattern.matcher(fullText);
        if (teletravailMatcher.find()) {
            job.setTypeTeletravail(teletravailMatcher.group(1).trim());
        }
    }
    
    /**
     * Scrape les détails complets d'une offre en visitant sa page
     */
    public void scrapeJobDetails(JobAnnouncement job) {
        if (job.getSourceUrl() == null) return;
        
        try {
            System.out.println("🔍 Récupération détails : " + job.getTitle());
            
            Document doc = Jsoup.connect(job.getSourceUrl())
                    .userAgent(USER_AGENT)
                    .timeout(TIMEOUT)
                    .get();
            
            Element descElement = doc.selectFirst("div.detail-offre, div[itemprop='description']");
            if (descElement != null) {
                job.setDescription(descElement.text().trim());
            }
            
            extractSkillsFromDescription(job);
            
            Thread.sleep(1000);
            
        } catch (IOException | InterruptedException e) {
            System.err.println("❌ Erreur détails : " + e.getMessage());
        }
    }
    
    /**
     * Extrait les compétences techniques depuis la description
     */
    private void extractSkillsFromDescription(JobAnnouncement job) {
        if (job.getDescription() == null) return;
        
        String description = job.getDescription().toLowerCase();
        String[] skillsKeywords = {
            "java", "python", "javascript", "php", "c++", "c#", "ruby", "go", "kotlin",
            "react", "angular", "vue", "node.js", "spring", "django", "laravel",
            "sql", "mysql", "postgresql", "mongodb", "oracle", "redis",
            "docker", "kubernetes", "aws", "azure", "gcp", "jenkins", "git",
            "html", "css", "typescript", "agile", "scrum", "devops",
            "excel", "sap", "erp", "crm", "autocad", "photoshop", "illustrator",
            "figma", "camunda", "bpmn", "json"
        };
        
        for (String skill : skillsKeywords) {
            if (description.contains(skill)) {
                job.addSkill(capitalizeFirst(skill));
            }
        }
    }
    
    /**
     * Met la première lettre en majuscule
     */
    private String capitalizeFirst(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
    
    public String getSiteName() {
        return "Rekrute";
    }
}