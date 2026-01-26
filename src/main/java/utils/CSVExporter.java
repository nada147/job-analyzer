package utils;

import model.JobAnnouncement;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Classe responsable de l'export des offres d'emploi vers un fichier CSV
 */
public class CSVExporter {
    
    /**
     * Exporte une liste d'offres vers un fichier CSV
     */
    public static void export(List<JobAnnouncement> jobs, String filePath) {
        if (jobs == null || jobs.isEmpty()) {
            System.out.println("⚠️ Aucun job à exporter");
            return;
        }
        
        try (FileWriter writer = new FileWriter(filePath, java.nio.charset.StandardCharsets.UTF_8)) {
            
            // 🔹 En-têtes CSV
            writer.append("id,title,company,description,location,contractType,secteurActivite,fonction,");
            writer.append("experienceRequise,experienceLevel,niveauEtude,typeTeletravail,");
            writer.append("nombrePostes,salary,skills,domain,publishDate,sourceSite,sourceUrl\n");
            
            // 🔹 Données
            for (JobAnnouncement job : jobs) {
                StringBuilder line = new StringBuilder();
                
                line.append(csvValue(job.getId())).append(",");
                line.append(csvValue(job.getTitle())).append(",");
                line.append(csvValue(job.getCompany())).append(",");
                line.append(csvValue(job.getDescription())).append(",");
                line.append(csvValue(job.getLocation())).append(",");
                line.append(csvValue(job.getContractType())).append(",");
                line.append(csvValue(job.getSecteurActivite())).append(",");
                line.append(csvValue(job.getFonction())).append(",");
                line.append(csvValue(job.getExperienceRequise())).append(",");
                line.append(csvValue(job.getExperienceLevel())).append(",");
                line.append(csvValue(job.getNiveauEtude())).append(",");
                line.append(csvValue(job.getTypeTeletravail())).append(",");
                line.append(csvValue(job.getNombrePostes())).append(",");
                line.append(csvValue(job.getSalary())).append(",");
                line.append(csvValue(formatSkills(job))).append(",");
                line.append(csvValue(job.getDomain())).append(",");
                line.append(csvValue(job.getPublishDateString())).append(",");
                line.append(csvValue(job.getSourceSite())).append(",");
                line.append(csvValue(job.getSourceUrl())); // Pas de virgule à la fin
                line.append("\n");
                
                writer.append(line);
            }
            
            System.out.println("📁 Export CSV réussi : " + filePath);
            System.out.println("   📊 " + jobs.size() + " offres exportées");
            
        } catch (IOException e) {
            System.err.println("❌ Erreur export CSV : " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Sécurise une valeur CSV (null, virgules, guillemets, sauts de ligne)
     */
    private static String csvValue(Object value) {
        if (value == null) {
            return "\"\"";
        }
        
        String str = value.toString().trim();
        
        // Remplacer les guillemets doubles par deux guillemets
        str = str.replace("\"", "\"\"");
        
        // Remplacer les sauts de ligne par des espaces
        str = str.replace("\n", " ").replace("\r", " ");
        
        // Entourer de guillemets
        return "\"" + str + "\"";
    }
    
    /**
     * Convertit la liste des skills en une seule chaîne
     */
    private static String formatSkills(JobAnnouncement job) {
        if (job.getSkills() == null || job.getSkills().isEmpty()) {
            return "";
        }
        return job.getSkills().stream()
                .filter(s -> s != null && !s.trim().isEmpty())
                .collect(Collectors.joining("|"));
    }
}