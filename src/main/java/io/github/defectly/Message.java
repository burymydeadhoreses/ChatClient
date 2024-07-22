package io.github.defectly;

public class Message {
    public String Username;
    public String Content;

    public Message(String username, String content) {
        Username = username;
        Content = content;
    }

    @Override
    public String toString() {
        return Username + ": " + Content;
    }
}
