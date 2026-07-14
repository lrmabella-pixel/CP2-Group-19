package motorph;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


 // Static functions only. Loads user accounts from users.csv and
 // validates login credentials. No instance state, no constructor.
 
public final class UserFileIO {

    private UserFileIO() {
    }

    /** Loads all user accounts from users.csv into a List. */
    public static List<UserData> loadUsers(String filepath) {
        List<UserData> users = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filepath))) {
            String line;
            boolean firstLine = true;

            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }
                if (line.trim().isEmpty()) continue;

                String[] parts = line.split(",");
                if (parts.length < 3) continue;

                UserData user = new UserData();
                user.username = parts[0].trim();
                user.password = parts[1].trim();
                user.role = parts[2].trim().toUpperCase();
                users.add(user);
            }
        } catch (IOException ex) {
            Logger.getLogger(UserFileIO.class.getName())
                    .log(Level.SEVERE, "Error loading users from " + filepath, ex);
        }

        return users;
    }

    
     // Checks username and password against the loaded user list.
     // Returns the matching UserData if found, null if login fails.
  
    public static UserData authenticate(List<UserData> users,
            String username, String password) {
        for (UserData user : users) {
            if (user.username.equals(username) &&
                    user.password.equals(password)) {
                return user;
            }
        }
        return null;
    }
}