package dao;

import model.JobAnnouncement;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class JobAnnouncementDAOTest {

    @Test
    void testInsertAndFindAll() {
        JobAnnouncementDAO dao = new JobAnnouncementDAO();

        JobAnnouncement job = new JobAnnouncement();
        job.setTitle("JUnit Test Job");
        job.setCompany("Test Company");
        job.setSourceUrl("http://junit-test.com/" + System.currentTimeMillis());

        boolean inserted = dao.insert(job);
        List<JobAnnouncement> jobs = dao.findAll();

        assertTrue(inserted, "Job should be inserted");
        assertFalse(jobs.isEmpty(), "Jobs list should not be empty");
    }
}
