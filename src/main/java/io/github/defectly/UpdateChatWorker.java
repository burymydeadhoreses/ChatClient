package io.github.defectly;

import com.firework.gson.Gson;
import com.firework.gson.reflect.TypeToken;

import javax.swing.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class UpdateChatWorker extends SwingWorker<Integer, Integer>
{
    private final Gson json = new Gson();
    private final HttpClient client = HttpClient.newHttpClient();
    private final String username;
    private final String token;
    private final JTextArea chatArea;

    public UpdateChatWorker(String username, String token, JTextArea chatArea) {
        this.username = username;
        this.token = token;
        this.chatArea = chatArea;
    }

    protected Integer doInBackground() throws Exception
    {
        updateChat();

        return 42;
    }

    private void updateChat() {
        try {
            var user = new AuthorizedUser(username, token);

            var request = HttpRequest.newBuilder()
                    .uri(new URI("http://127.0.0.1:34/chat"))
                    .POST(HttpRequest.BodyPublishers.ofString(json.toJson(user)))
                    .build();

            var response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if(response.body().isEmpty())
                return;

            chatArea.setText("");

            java.lang.reflect.Type listType = new TypeToken<List<Message>>(){}.getType();
            List<Message> messages =  json.fromJson(response.body(), listType);

            for (var message : messages) {
                chatArea.append(message.toString() + "\n");
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}