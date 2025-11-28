import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class GenerarHashAdmin {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "Admin123!";
        String hash = encoder.encode(password);
        System.out.println("Password: " + password);
        System.out.println("Hash: " + hash);
        System.out.println("\nSQL UPDATE:");
        System.out.println("UPDATE usuarios SET password_hash = '" + hash + "', activo = true WHERE username = 'admin';");
    }
}
