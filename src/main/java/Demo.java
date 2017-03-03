import authentication.Authenticator;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import messenger.Messenger;
import menu.Menu;

import java.io.IOException;

public class Demo {
    public static void main(String[] args) throws InterruptedException, ClientException, ApiException, IOException {
        Authenticator authenticator = new Authenticator();
        Messenger vkClient = authenticator.authenticateAndGetMessenger();
        Menu menu = new Menu();
        for (;;) {
            menu.initialize();
            int choice = menu.readInput();
            menu.processChoice(vkClient, choice);
        }
    }
}