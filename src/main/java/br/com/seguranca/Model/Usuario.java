package br.com.seguranca.Model;

public class Usuario {
    private Object id;
    private String nomeUsuario;
    private String email;
    private String senha;
    private String apelido;
    private String bio;

    public Usuario() {
    }

    public Usuario(String nomeUsuario, String email, String senha, String apelido, String bio) {
        this.nomeUsuario = nomeUsuario;
        this.email = email;
        this.senha = senha;
        this.apelido = apelido;
        this.bio = bio;
    }

    public Object getId() {
        return id;
    }

    public void setId(Object id) {
        this.id = id;
    }

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getApelido() {
        return apelido;
    }

    public void setApelido(String apelido) {
        this.apelido = apelido;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
}
