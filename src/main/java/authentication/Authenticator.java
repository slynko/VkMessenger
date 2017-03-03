package authentication;

import decorator.Messenger;
import decorator.VkMessenger;

import java.io.Console;

public class Authenticator {
    public Messenger authenticateAndGetMessenger() {
        Console console = System.console();
        System.out.println("Input your username:");
        String username = console.readLine();
        System.out.println("Input your password:");
        String password = new String(console.readPassword());
        return new VkMessenger(username, password);
    }
}
