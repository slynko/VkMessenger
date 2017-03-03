package decorator;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.messages.Message;
import org.apache.commons.collections4.CollectionUtils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static constants.Constants.AuthenticationJson.ACCESS_TOKEN_FIELD;
import static constants.Constants.AuthenticationJson.USER_ID_FIELD;
import static constants.Constants.HER_ID;
import static constants.Constants.MY_ID;

public class VkMessenger implements Messenger {

    private VkApiClient vkApiClient;
    private UserActor actor;

    public VkMessenger(String userName, String password) {
        vkApiClient = new VkApiClient(HttpTransportClient.getInstance(), new Gson());
        JsonObject authenticationJson = getAccessToken(userName, password);
        String accessToken = authenticationJson.get(ACCESS_TOKEN_FIELD).getAsString();
        Integer userId = authenticationJson.get(USER_ID_FIELD).getAsInt();
        actor = new UserActor(userId, accessToken);
    }

    @Override
    public void printUnreadMessages() throws ClientException, ApiException {
        List<Message> unreadMessages = getUnreadMessagesFromHer();

        if (CollectionUtils.isNotEmpty(unreadMessages)) {
            unreadMessages
                    .forEach(message ->
                            System.out.println(message.getBody())
                    );
        } else {
            System.out.println("No new messages");
        }

    }

    @Override
    public void sendMessage(String message) throws ClientException, ApiException {
        vkApiClient.messages()
                .send(actor)
                .userId(HER_ID)
                .message(message)
                .execute();
    }

    @Override
    public void markAsRead() throws ClientException, ApiException {
        List<Integer> unreadMessagesIds = getUnreadMessagesFromHer().stream()
                .map(Message::getId)
                .collect(Collectors.toList());

        if (CollectionUtils.isNotEmpty(unreadMessagesIds)) {
            vkApiClient.messages()
                    .markAsRead(actor)
                    .messageIds(unreadMessagesIds)
                    .execute();
        }
    }

    @Override
    public void printDialogHistory() throws ClientException, ApiException {
        vkApiClient.messages()
                .getHistory(actor)
                .userId(HER_ID.toString())
                .execute()
                .getItems()
                .stream()
                .sorted(Comparator.comparing(Message::getDate))
                .forEach(message ->
                        System.out.println(getNameByUserId(message.getFromId()) + ": " + message.getBody())
                );
    }

    @Override
    public void trackNewMessages() throws ClientException, ApiException, InterruptedException {
        while (true) {
            if (haveNewMessagesFromHer()) {
                System.out.println("You have new messages!");
                break;
            }

            Thread.sleep(1000);
        }
    }

    private boolean haveNewMessagesFromHer() throws ClientException, ApiException {
        return vkApiClient.messages()
                .get(actor)
                .out(false)
                .execute()
                .getItems()
                .stream()
                .anyMatch(message -> message.getUserId().equals(HER_ID) && !message.isReadState());
    }

    private List<Message> getUnreadMessagesFromHer() throws ClientException, ApiException {
        return vkApiClient.messages()
                .getHistory(actor)
                .userId(HER_ID.toString())
                .execute()
                .getItems()
                .stream()
                .filter(message -> !message.isReadState())
                .sorted(Comparator.comparing(Message::getDate))
                .collect(Collectors.toList());
    }

    private String getNameByUserId(int userId) {
        if (userId == MY_ID) {
            return "    Me";
        } else if (userId == HER_ID) {
            return "She";
        }
        return "Unknown";
    }

    private JsonObject getAccessToken(String username, String password) {
        HttpURLConnection connection = null;

        try {
            String urlString = "https://oauth.vk.com/token?grant_type=password&client_id=2274003&client_secret=hHbZxrka2uZ6jB1inYsH&username={USERNAME}&password={PASSWORD}";
            urlString = urlString.replace("{USERNAME}", username);
            urlString = urlString.replace("{PASSWORD}", password);

            //Create connection
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();

            connection.setUseCaches(false);
            connection.setDoOutput(true);

            //Send request
            DataOutputStream wr = new DataOutputStream(
                    connection.getOutputStream());
            wr.close();

            //Get Response
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            StringBuilder response = new StringBuilder(); // or StringBuffer if Java version 5+
            String line;
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();
            return new JsonParser().parse(response.toString()).getAsJsonObject();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}
