package nlp;

import dao.*;
import model.JobAnnouncement;

import java.util.List;
import java.util.Set;

public class NLPProcessingService {

    private final SkillDAO skillDAO = new SkillDAO();
    private final JobSkillDAO jobSkillDAO = new JobSkillDAO();
    private final JobClassificationDAO classificationDAO = new JobClassificationDAO();

    public void process(List<JobAnnouncement> jobs) {

        for (JobAnnouncement job : jobs) {

            // 🔹 Construire le texte NLP
            String text = TextPreprocessor.buildJobText(
                    job.getTitle(),
                    job.getDescription(),
                    job.getSecteurActivite(),
                    job.getFonction()
            );

            // 🔹 1. Extraction des skills
            Set<String> skills = SkillExtractor.extract(text);
            for (String skill : skills) {
                int skillId = skillDAO.findOrCreate(skill);
                if (skillId != -1) {
                    jobSkillDAO.link(job.getId(), skillId);
                }
            }

            // 🔹 2. Classification domaine
            String domain = DomainClassifier.classifyTopDomains(text, 1).get(0);

            // 🔹 3. Confiance simple
            double confidence = skills.isEmpty() ? 0.4 : Math.min(1.0, 0.5 + skills.size() * 0.1);

            classificationDAO.insert(job.getId(), domain, confidence);
        }
    }
}
