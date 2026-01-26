package utils;

import model.JobAnnouncement;
import utils.normalizer.*;

import java.util.List;

/**
 * Orchestrateur global de normalisation
 */
public class DataNormalizer {

    public static void normalize(JobAnnouncement job) {
        if (job == null) return;

        job.setLocation(CityNormalizer.normalize(job.getLocation()));
        job.setContractType(ContractNormalizer.normalize(job.getContractType()));
        job.setExperienceRequise(
                ExperienceNormalizer.normalize(job.getExperienceRequise())
        );
        job.setNiveauEtude(
                EducationNormalizer.normalize(job.getNiveauEtude())
        );
        job.setSecteurActivite(
                SectorNormalizer.normalize(job.getSecteurActivite())
        );
        job.setCompany(
                CompanyNormalizer.normalize(job.getCompany())
        );
        job.setTypeTeletravail(
                RemoteWorkNormalizer.normalize(job.getTypeTeletravail())
        );
    }

    public static void normalizeAll(List<JobAnnouncement> jobs) {
        if (jobs == null) return;
        jobs.forEach(DataNormalizer::normalize);
    }
}