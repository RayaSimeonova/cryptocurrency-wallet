package bg.sofia.uni.fmi.mjt.cryptocurrency.server.dto;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

public class User {
    private static final String HASH_ALGORITHM = "SHA-256";
    private final String username;
    private final String password;

    public User(String username, String password) throws NoSuchAlgorithmException {
        validateStringArgument(username);
        validateStringArgument(password);

        this.username = username;

        MessageDigest messageDigest = MessageDigest.getInstance(HASH_ALGORITHM);
        messageDigest.update(password.getBytes());
        this.password = new String(messageDigest.digest());
    }

    public boolean hasSameUsername(User user) {
        return this.username.equals(user.username);
    }

    private void validateStringArgument(String arg) {
        if (arg == null || arg.isBlank()) {
            throw new IllegalArgumentException("Argument must be a non-null, non-empty string.");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(username, user.username) && Objects.equals(password, user.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, password);
    }

}
