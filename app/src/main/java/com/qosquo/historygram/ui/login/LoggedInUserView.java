package com.qosquo.historygram.ui.login;

/**
 * Class exposing authenticated user details to the UI.
 */
class LoggedInUserView {
    private String token;
    private Integer id;
    private String login;
    private String password;

    public LoggedInUserView(String token, Integer id, String login, String password) {
        this.token = token;
        this.id = id;
        this.login = login;
        this.password = password;
    }

    public String getToken() {
        return token;
    }

    public Integer getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }
}
