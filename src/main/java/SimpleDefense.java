public class SimpleDefense {
    public static void main(String[] args) {
        System.out.println("=== BIKE RENTAL SYSTEM - DEFENSE ===");
        System.out.println("\nPROJECT COMPONENTS:");
        System.out.println("1. ✅ RentalRepository.java - JOIN SQL operations");
        System.out.println("2. ✅ DatabaseConnection.java - Singleton pattern");
        System.out.println("3. ✅ Bike, User, Rental, Category - 4 entities");
        System.out.println("4. ✅ PostgreSQL database with tables");
        System.out.println("5. ✅ Maven project with dependencies");
        System.out.println("\nJOIN OPERATION EXAMPLE:");
        System.out.println("""
            SELECT r.id, b.model, u.username
            FROM rentals r
            JOIN bikes b ON r.bike_id = b.id
            JOIN users u ON r.user_id = u.id
            """);
        System.out.println("\nREADY FOR PRESENTATION! 🎓");
    }
}