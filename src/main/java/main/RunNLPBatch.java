package main;

import dao.JobAnnouncementDAO;
import model.JobAnnouncement;
import nlp.NLPProcessingService;

import java.util.List;

public class RunNLPBatch {

    public static void main(String[] args) {

        JobAnnouncementDAO jobDao = new JobAnnouncementDAO();
        NLPProcessingService nlpService = new NLPProcessingService();

        List<JobAnnouncement> jobs = jobDao.findAll();

        System.out.println("🚀 Lancement du NLP batch...");
        nlpService.process(jobs);
        System.out.println("✅ NLP terminé avec succès !");
    }
}
