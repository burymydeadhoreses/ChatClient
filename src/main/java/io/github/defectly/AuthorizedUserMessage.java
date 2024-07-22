package io.github.defectly;

public class AuthorizedUserMessage {
    public String Username;
    public String Token;
    public String Message;

    public AuthorizedUserMessage(String username, String token, String message) {
        Username = username;
        Token = token;
        Message = message;
    }
}
