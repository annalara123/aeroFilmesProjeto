package br.com.seguranca.Model;

import java.util.Date;

public class Filme {
    private Object id;
    private String nomeFilme;
    private String genero;
    private String sinopse;
    private String Diretor;
    private Date dataExtreia;
    private Double mediaAvaliacoes;

    public Filme() {
    }
    public Filme(String nomeFilme, String genero, String sinopse,
                 String diretor, Date dataExtreia, Double mediaAvaliacoes) {
        this.nomeFilme = nomeFilme;
        this.genero = genero;
        this.sinopse = sinopse;
        Diretor = diretor;
        this.dataExtreia = dataExtreia;
        this.mediaAvaliacoes = mediaAvaliacoes;
    }
    public Object getId() {
        return id;
    }

    public void setId(Object id) {
        this.id = id;
    }

    public String getNomeFilme() {
        return nomeFilme;
    }

    public void setNomeFilme(String nomeFilme) {
        this.nomeFilme = nomeFilme;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getSinopse() {
        return sinopse;
    }

    public void setSinopse(String sinopse) {
        this.sinopse = sinopse;
    }

    public String getDiretor() {
        return Diretor;
    }

    public void setDiretor(String diretor) {
        Diretor = diretor;
    }

    public Date getDataExtreia() {
        return dataExtreia;
    }

    public void setDataExtreia(Date dataExtreia) {
        this.dataExtreia = dataExtreia;
    }

    public Double getMediaAvaliacoes() {
        return mediaAvaliacoes;
    }

    public void setMediaAvaliacoes(Double mediaAvaliacoes) {
        this.mediaAvaliacoes = mediaAvaliacoes;
    }

}
