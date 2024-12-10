package pe.edu.utp.pf_api.model;

public class Usuario {

    private String login;
    private String fullname;
    private String email;
    private String pwd;

    // Getters and Setters
    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }

    public String getFullname() { return fullname; }
    public void setFullname(String fullname) { this.fullname = fullname; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPwd() { return pwd; }
    public void setPwd(String pwd) { this.pwd = pwd; }
}
