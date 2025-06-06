package br.com.seguranca.Model;

import java.util.Date;

public class Avaliacao {
    private Object id;
    private Object idFilme;
    private Object idUsuario;
    private Integer nota;
    private String comentario;
    private Date data;

    public Avaliacao() {
    }
    public Avaliacao(Integer nota, String comentario, Date data) {
        this.nota = nota;
        this.comentario = comentario;
        this.data = data;
    }
    public Object getId() {
        return id;
    }

    public void setId(Object id) {
        this.id = id;
    }

    public Object getIdFilme() {
        return idFilme;
    }

    public void setIdFilme(Object idFilme) {
        this.idFilme = idFilme;
    }

    public Object getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Object idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Integer getNota() {
        return nota;
    }

    public void setNota(Integer nota) {
        this.nota = nota;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

}
