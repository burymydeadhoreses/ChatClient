package io.github.defectly;

import com.firework.gson.Gson;
import com.firework.gson.reflect.TypeToken;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatWindow extends JFrame {

    private JTextArea chatArea = new JTextArea(20, 16);
    private JTextField typedText = new JTextField(16);
    private final HttpClient client = HttpClient.newHttpClient();
    private final Gson json = new Gson();

    private String username;
    private String token;

    public ChatWindow(String username, String token) throws URISyntaxException, IOException, InterruptedException {

        this.username = username;
        this.token = token;

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("Chat Client");
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(screenSize.width / 3, screenSize.height / 3);

        chatArea.setEditable(false);
        chatArea.setBackground(Color.LIGHT_GRAY);

        Container content = getContentPane();
        content.add(new JScrollPane(chatArea), BorderLayout.CENTER);
        var sendButton = new JButton("send");
        var updateButton = new JButton("update");
        sendButton.addActionListener(this::sendMessage);
        updateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                new UpdateChatWorker(username, token, chatArea).execute();
            }
        });
        var panel = new JPanel(new GridLayout(1, 3));
        panel.add(typedText);
        panel.add(sendButton);
        panel.add(updateButton);
        content.add(panel, BorderLayout.SOUTH);

        pack();
        typedText.requestFocusInWindow();

        setVisible(true);
    }

    private void updateChat(ActionEvent actionEvent) {
        updateChat();
    }

    private void sendMessage(ActionEvent actionEvent) {
        try {
            var user = new AuthorizedUserMessage(username, token, typedText.getText());

            var jsonUser = json.toJson(user);

            var request = HttpRequest.newBuilder()
                    .uri(new URI("http://127.0.0.1:34/message"))
                    .POST(HttpRequest.BodyPublishers.ofString(jsonUser))
                    .build();

            var response = client.send(request, HttpResponse.BodyHandlers.ofString());

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
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

            chatArea.removeAll();

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
