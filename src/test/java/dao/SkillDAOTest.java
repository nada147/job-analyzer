package dao;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SkillDAOTest {

    @Test
    void testFindOrCreateSkill() {
        SkillDAO dao = new SkillDAO();

        int id1 = dao.findOrCreate("java");
        int id2 = dao.findOrCreate("java");

        assertTrue(id1 > 0, "ID must be positive");
        assertEquals(id1, id2, "Same skill must return same ID");
    }
}
