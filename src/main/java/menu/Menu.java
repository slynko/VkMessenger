package menu;

import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import decorator.Messenger;

import java.io.IOException;
import java.util.Scanner;

public class Menu {
    public void initialize() {
//        System.out.print("\033[H\033[2J"); // clear console
        System.out.println("================================");
        System.out.println("1 - Read new messages");
        System.out.println("2 - Write a message");
        System.out.println("3 - Track incoming messages");
        System.out.println("4 - Show dialog history");
        System.out.println("5 - Mark as read");
        System.out.println("================================");
        System.out.println("Please make your choice and press Enter");
    }

    public int readInput() {
        Scanner in = new Scanner(System.in);
        if (in.hasNextInt()) {
            int choice = in.nextInt();
            if (choice > 0 && choice <= 5) {
                return choice;
            } else {
                throw new IllegalArgumentException("The number is not in range");
            }
        } else {
            throw new IllegalArgumentException("Input should be a numeric value");
        }
    }

    public void processChoice(Messenger vkMessenger, int choice) throws ClientException, ApiException, InterruptedException, IOException {
        switch (choice) {
            case 1:
                vkMessenger.printUnreadMessages();
                break;
            case 2:
                System.out.println("Input a message:");
                String message = new Scanner(System.in).nextLine();
                if (!message.equals("q")) {
                    vkMessenger.sendMessage(message);
                }
                break;
            case 3:
                System.out.println("New messages are being tracked now");
                vkMessenger.trackNewMessages();
                break;
            case 4:
                vkMessenger.printDialogHistory();
                break;
            case 5:
                vkMessenger.markAsRead();
                break;
        }
    }
}
