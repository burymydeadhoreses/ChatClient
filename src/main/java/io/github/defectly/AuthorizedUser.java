package io.github.defectly;

public class AuthorizedUser {
    public String Username;
    public String Token;

    public AuthorizedUser(String username, String token) {
        Username = username;
        Token = token;
    }
}