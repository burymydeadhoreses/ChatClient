package io.github.defectly;

import com.firework.gson.Gson;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class SettingsWindow extends JFrame {

    private final Gson json = new Gson();
    private final HttpClient client = HttpClient.newHttpClient();

    public SettingsWindow() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("Chat Client Settings");
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(screenSize.width / 3, screenSize.height / 3);
        setVisible(true);


        JPanel panel = new JPanel(new GridLayout(2, 2));
        JLabel userLabel = new JLabel("Username:");
        JTextField usernameField = new JTextField();
        JLabel passLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField();
        panel.add(userLabel);
        panel.add(usernameField);
        panel.add(passLabel);
        panel.add(passwordField);
        add(panel, BorderLayout.CENTER);

        var loginButton = new JButton("login or register");
        add(loginButton, BorderLayout.SOUTH);

        loginButton.addActionListener(e -> {
            try {
                registerThenLogin(usernameField.getText(), passwordField.getText());
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
            // Perform authentication logic here (compare with stored credentials, etc.)
            // If valid, proceed to the next screen; otherwise, show an error message.
        });
    }

    private void registerThenLogin(String username, String password) throws URISyntaxException, IOException, InterruptedException {
        login(username, password);
    }

    void login(String username, String password) throws URISyntaxException, IOException, InterruptedException {
        var user = new User(username, password);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("http://127.0.0.1:34/login"))
                .POST(HttpRequest.BodyPublishers.ofString(json.toJson(user)))
                .build();

        var response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if(response.statusCode() != 200)
        {
            register(username, password);
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }

        var token = response.body();

        var window = new ChatWindow(username, token);

        setVisible(false);
    }

    void register(String username, String password) throws URISyntaxException, IOException, InterruptedException {
        var user = new User(username, password);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("http://127.0.0.1:34/register"))
                .POST(HttpRequest.BodyPublishers.ofString(json.toJson(user)))
                .build();

        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
    }
}
