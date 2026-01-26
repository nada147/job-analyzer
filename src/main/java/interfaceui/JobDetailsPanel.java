package interfaceui;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class JobDetailsPanel extends JPanel {
    private JTextArea textArea;

    public JobDetailsPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Détails de l'offre"));

        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setMargin(new Insets(10, 10, 10, 10));
        textArea.setFont(new Font("Arial", Font.PLAIN, 13));

        add(new JScrollPane(textArea), BorderLayout.CENTER);
    }

    public void loadJob(Integer jobId) {
        if (jobId == null) {
            textArea.setText("Sélectionnez une offre pour voir les détails.");
            return;
        }

        String sql = "SELECT j.title, j.company, j.description, j.location, " +
                     "j.contract_type, j.experience_level, j.experience_requise, " +
                     "j.niveau_etude, j.secteur_activite, j.fonction, " +
                     "j.type_teletravail, j.nombre_postes, j.salary, " +
                     "j.source_site, j.source_url, j.publish_date, j.publish_date_string, " +
                     "GROUP_CONCAT(DISTINCT s.name SEPARATOR ', ') AS skills " +
                     "FROM job_announcements j " +
                     "LEFT JOIN job_skills js ON js.job_id = j.id " +
                     "LEFT JOIN skills s ON s.id = js.skill_id " +
                     "WHERE j.id = ? " +
                     "GROUP BY j.id";

        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3307/jobs_maroc_db?useSSL=false&serverTimezone=UTC",
                "root", "");
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, jobId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                StringBuilder details = new StringBuilder();
                
                // Titre et entreprise
                details.append("═══════════════════════════════════════\n");
                details.append("📋 TITRE : ").append(rs.getString("title")).append("\n");
                details.append("═══════════════════════════════════════\n\n");
                
                details.append("🏢 Entreprise : ").append(
                    rs.getString("company") != null ? rs.getString("company") : "Non spécifiée"
                ).append("\n\n");

                // Secteur d'activité (remplace la catégorie ML)
                String secteur = rs.getString("secteur_activite");
                if (secteur != null && !secteur.isEmpty()) {
                    details.append("🏭 Secteur d'activité : ").append(secteur).append("\n\n");
                }

                // Fonction
                String fonction = rs.getString("fonction");
                if (fonction != null && !fonction.isEmpty()) {
                    details.append("🎯 Fonction : ").append(fonction).append("\n\n");
                }

                // Informations générales
                details.append("📍 Localisation : ").append(
                    rs.getString("location") != null ? rs.getString("location") : "Non spécifiée"
                ).append("\n");
                
                details.append("📝 Type de contrat : ").append(
                    rs.getString("contract_type") != null ? rs.getString("contract_type") : "Non spécifié"
                ).append("\n");

                // Télétravail
                String teletravail = rs.getString("type_teletravail");
                if (teletravail != null && !teletravail.isEmpty()) {
                    details.append("🏠 Télétravail : ").append(teletravail).append("\n");
                }

                // Nombre de postes
                int nbPostes = rs.getInt("nombre_postes");
                if (nbPostes > 1) {
                    details.append("👥 Nombre de postes : ").append(nbPostes).append("\n");
                }
                
                // Niveau d'études
                String niveauEtude = rs.getString("niveau_etude");
                if (niveauEtude != null && !niveauEtude.isEmpty()) {
                    details.append("🎓 Niveau d'études : ").append(niveauEtude).append("\n");
                }
                
                // Expérience (utilise experience_requise si disponible, sinon experience_level)
                String expRequise = rs.getString("experience_requise");
                String expLevel = rs.getString("experience_level");
                String experience = expRequise != null && !expRequise.isEmpty() ? expRequise : expLevel;
                
                if (experience != null && !experience.isEmpty()) {
                    details.append("💼 Expérience requise : ").append(experience).append("\n");
                }
                
                details.append("💰 Salaire : ").append(
                    rs.getString("salary") != null ? rs.getString("salary") : "Non spécifié"
                ).append("\n");
                
                // Date de publication (utilise publish_date_string si disponible, sinon publish_date)
                String dateString = rs.getString("publish_date_string");
                if (dateString != null && !dateString.isEmpty()) {
                    details.append("📅 Date de publication : ").append(dateString).append("\n\n");
                } else {
                    Date publishDate = rs.getDate("publish_date");
                    details.append("📅 Date de publication : ").append(
                        publishDate != null ? publishDate : "N/A"
                    ).append("\n\n");
                }

                // Compétences
                String skills = rs.getString("skills");
                if (skills != null && !skills.isEmpty()) {
                    details.append("🔧 Compétences requises :\n");
                    // Afficher les compétences sur plusieurs lignes si trop longues
                    String[] skillArray = skills.split(", ");
                    StringBuilder skillsFormatted = new StringBuilder();
                    int lineLength = 0;
                    for (String skill : skillArray) {
                        if (lineLength + skill.length() > 60) {
                            skillsFormatted.append("\n   ");
                            lineLength = 0;
                        }
                        skillsFormatted.append(skill).append(", ");
                        lineLength += skill.length() + 2;
                    }
                    // Retirer la dernière virgule
                    String skillsFinal = skillsFormatted.toString();
                    if (skillsFinal.endsWith(", ")) {
                        skillsFinal = skillsFinal.substring(0, skillsFinal.length() - 2);
                    }
                    details.append("   ").append(skillsFinal).append("\n\n");
                }

                // Description
                details.append("───────────────────────────────────────\n");
                details.append("📄 DESCRIPTION :\n");
                details.append("───────────────────────────────────────\n");
                String description = rs.getString("description");
                if (description != null && !description.isEmpty()) {
                    // Nettoyer les balises HTML et formater
                    description = description
                        .replaceAll("<br[^>]*>", "\n")
                        .replaceAll("<p[^>]*>", "\n")
                        .replaceAll("</p>", "\n")
                        .replaceAll("<li[^>]*>", "\n  • ")
                        .replaceAll("</li>", "")
                        .replaceAll("<ul[^>]*>", "\n")
                        .replaceAll("</ul>", "\n")
                        .replaceAll("<[^>]+>", "")
                        .replaceAll("&nbsp;", " ")
                        .replaceAll("&amp;", "&")
                        .replaceAll("&lt;", "<")
                        .replaceAll("&gt;", ">")
                        .replaceAll("\\s+", " ")
                        .replaceAll("\\n\\s+\\n", "\n\n")
                        .trim();
                    details.append(description).append("\n\n");
                } else {
                    details.append("Aucune description disponible.\n\n");
                }

                // Source
                details.append("───────────────────────────────────────\n");
                details.append("🔗 Source : ").append(
                    rs.getString("source_site") != null ? rs.getString("source_site") : "N/A"
                ).append("\n");
                
                String url = rs.getString("source_url");
                if (url != null && !url.isEmpty()) {
                    details.append("🌐 URL : ").append(url).append("\n");
                }

                textArea.setText(details.toString());
                textArea.setCaretPosition(0); // Retour en haut
            } else {
                textArea.setText("Aucune information disponible pour cette offre.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            textArea.setText("Erreur lors du chargement des détails :\n" + e.getMessage());
        }
    }
}