package database;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.sql.Connection;

public class DatabaseConnectionTest {

    @Test
    void testConnection() throws Exception {
        try (Connection c = DatabaseConnection.getConnection()) {
            assertNotNull(c);
            assertFalse(c.isClosed());
        }
    }
}
