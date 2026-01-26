package database;

import java.sql.Connection;

public class TestDatabaseConnection {
    
    public static void main(String[] args) {
        System.out.println("🧪 TEST DE CONNEXION À LA BASE DE DONNÉES");
        System.out.println("==========================================\n");
        
        // Test de connexion
        Connection conn = DatabaseConnection.getConnection();
        
        if (conn != null) {
            System.out.println("🎉 SUCCÈS : Connexion établie !");
            System.out.println("📊 Base de données : jobs_maroc_db");
            System.out.println("🌐 Port MySQL : 3307");
            System.out.println("👤 Utilisateur : root");
        } else {
            System.out.println("💥 ÉCHEC : Impossible de se connecter");
            System.out.println("\n🔧 Vérifications à faire :");
            System.out.println("   1. XAMPP est démarré ?");
            System.out.println("   2. MySQL tourne sur le port 3307 ?");
            System.out.println("   3. La base 'jobs_maroc_db' existe ?");
            System.out.println("   4. Le driver MySQL est dans le projet ?");
        }
        
        // Fermeture
        DatabaseConnection.closeConnection();
        
        System.out.println("\n✅ Test terminé");
    }
}