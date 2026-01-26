package ml;

import database.DatabaseConnection;
import java.sql.*;
import java.util.*;

// ========== 1. RECORD (inchangé) ==========
public class MLRecord {
    private String text;
    private String label;

    public MLRecord(String text, String label) {
        this.text = text;
        this.label = label;
    }

    public String getText() { return text; }
    public String getLabel() { return label; }
}