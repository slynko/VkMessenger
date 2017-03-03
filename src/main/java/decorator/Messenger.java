package decorator;

import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;

public interface Messenger {
    void printUnreadMessages() throws ClientException, ApiException;
    void sendMessage(String message) throws ClientException, ApiException;
    void markAsRead() throws ClientException, ApiException;
    void printDialogHistory() throws ClientException, ApiException;
    void trackNewMessages() throws InterruptedException, ClientException, ApiException;
}
