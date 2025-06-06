package br.com.seguranca;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.util.StringConverter;
import br.com.seguranca.DAO.FilmeDAO;
import br.com.seguranca.DAO.UsuarioDAO;
import br.com.seguranca.DAO.AvaliacaoDAO;
import br.com.seguranca.Model.Filme;
import br.com.seguranca.Model.Usuario;
import br.com.seguranca.Model.Avaliacao;
import org.bson.types.ObjectId;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

public class Main extends Application {

    private UsuarioDAO usuarioDAO = new UsuarioDAO();
    private FilmeDAO filmeDAO = new FilmeDAO();
    private AvaliacaoDAO avaliacaoDAO = new AvaliacaoDAO();

    private Stage primaryStage;
    private Usuario usuarioLogado;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        primaryStage.setTitle("Aero Filmes - Login");
        mostrarTelaLogin();
        primaryStage.show();
    }

    private void mostrarTelaLogin() {
        Label labelEmail = new Label("Email:");
        TextField tfEmail = new TextField();
        Label labelSenha = new Label("Senha:");
        PasswordField pfSenha = new PasswordField();
        Button btnLogin = new Button("Entrar");
        Button btnCadastrar = new Button("Cadastrar");
        Label lblMensagem = new Label();

        btnLogin.setOnAction(e -> {
            String email = tfEmail.getText();
            String senha = pfSenha.getText();
            Usuario usuario = usuarioDAO.login(email, senha);
            if (usuario != null) {
                usuarioLogado = usuario;
                mostrarTelaFilmes();
            } else {
                lblMensagem.setText("Email ou senha inválidos!");
            }
        });

        btnCadastrar.setOnAction(e -> mostrarTelaCadastro());

        HBox botoes = new HBox(10, btnLogin, btnCadastrar);
        VBox vbox = new VBox(10, labelEmail, tfEmail, labelSenha, pfSenha, botoes, lblMensagem);
        vbox.setPadding(new Insets(20));
        primaryStage.setScene(new Scene(vbox, 350, 300));
    }

    private void mostrarTelaCadastro() {
        Label lblNomeUsuario = new Label("Nome de Usuário:");
        TextField tfNomeUsuario = new TextField();
        Label lblEmail = new Label("E-mail:");
        TextField tfEmail = new TextField();
        Label lblSenha = new Label("Senha:");
        PasswordField pfSenha = new PasswordField();
        Label lblApelido = new Label("Apelido:");
        TextField tfApelido = new TextField();
        Label lblBio = new Label("Bio:");
        TextArea taBio = new TextArea();
        taBio.setPrefRowCount(3);

        Button btnCadastrar = new Button("Cadastrar");
        Button btnVoltar = new Button("Voltar");
        Label lblMensagem = new Label();

        btnCadastrar.setOnAction(e -> {
            String nomeUsuario = tfNomeUsuario.getText().trim();
            String email = tfEmail.getText().trim();
            String senha = pfSenha.getText();
            String apelido = tfApelido.getText().trim();
            String bio = taBio.getText().trim();
            if (nomeUsuario.isEmpty() || email.isEmpty() || senha.isEmpty()) {
                lblMensagem.setText("Nome de usuário, e-mail e senha são obrigatórios.");
                return;
            }
            Usuario novo = new Usuario(nomeUsuario, email, senha, apelido, bio);
            usuarioDAO.cadastrar(novo);
            mostrarTelaLogin();
        });

        btnVoltar.setOnAction(e -> mostrarTelaLogin());

        HBox botoes = new HBox(10, btnCadastrar, btnVoltar);
        VBox vbox = new VBox(10,
                lblNomeUsuario, tfNomeUsuario,
                lblEmail, tfEmail,
                lblSenha, pfSenha,
                lblApelido, tfApelido,
                lblBio, taBio,
                botoes,
                lblMensagem
        );
        vbox.setPadding(new Insets(20));
        primaryStage.setTitle("Aero Filmes - Cadastro");
        primaryStage.setScene(new Scene(vbox, 350, 450));
    }

    private void mostrarTelaFilmes() {
        primaryStage.setTitle("Aero Filmes - Catálogo");

        List<Filme> allFilmes = filmeDAO.listarFilmes();

        Label lblBuscar = new Label("Buscar por nome:");
        TextField tfBuscar = new TextField();
        Label lblGenero = new Label("Filtrar por gênero:");
        ComboBox<String> cbGenero = new ComboBox<>();
        Button btnFiltrar = new Button("Filtrar");
        Button btnLimparFiltro = new Button("Limpar");

        Set<String> generosSet = allFilmes.stream()
                .map(Filme::getGenero)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        List<String> generosList = new ArrayList<>(generosSet);
        Collections.sort(generosList);
        generosList.add(0, "Todos");
        cbGenero.setItems(FXCollections.observableArrayList(generosList));
        cbGenero.getSelectionModel().selectFirst();

        ListView<Filme> listaFilmes = new ListView<>();
        listaFilmes.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Filme item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getNomeFilme()
                            + " (" + item.getGenero() + ") - Média: "
                            + (item.getMediaAvaliacoes() != null ? item.getMediaAvaliacoes() : "0.0"));
                }
            }
        });
        listaFilmes.getItems().addAll(allFilmes);

        btnFiltrar.setOnAction(e -> {
            String textoBusca = tfBuscar.getText().trim().toLowerCase();
            String generoSelecionado = cbGenero.getValue();
            List<Filme> filtrados = allFilmes.stream()
                    .filter(f -> f.getNomeFilme().toLowerCase().contains(textoBusca))
                    .filter(f -> generoSelecionado.equals("Todos")
                            || f.getGenero().equalsIgnoreCase(generoSelecionado))
                    .collect(Collectors.toList());
            listaFilmes.getItems().setAll(filtrados);
        });

        btnLimparFiltro.setOnAction(e -> {
            tfBuscar.clear();
            cbGenero.getSelectionModel().selectFirst();
            listaFilmes.getItems().setAll(allFilmes);
        });

        Button btnAddFilme = new Button("Adicionar Filme");
        Button btnPerfil = new Button("Meu Perfil");
        Button btnLogout = new Button("Logout");

        btnAddFilme.setOnAction(e -> mostrarTelaAddFilme());
        btnPerfil.setOnAction(e -> mostrarTelaPerfil());
        btnLogout.setOnAction(e -> {
            usuarioLogado = null;
            mostrarTelaLogin();
        });

        listaFilmes.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                Filme selecionado = listaFilmes.getSelectionModel().getSelectedItem();
                if (selecionado != null) {
                    mostrarTelaDetalhesFilme(selecionado);
                }
            }
        });

        HBox filtros = new HBox(10, lblBuscar, tfBuscar, lblGenero, cbGenero, btnFiltrar, btnLimparFiltro);
        HBox botoesAcoes = new HBox(10, btnAddFilme, btnPerfil, btnLogout);
        VBox vbox = new VBox(10, filtros, listaFilmes, botoesAcoes);
        vbox.setPadding(new Insets(20));

        primaryStage.setScene(new Scene(vbox, 700, 500));
    }

    private void mostrarTelaAddFilme() {
        primaryStage.setTitle("Aero Filmes - Adicionar Filme");

        Label lblNome = new Label("Nome do Filme:");
        TextField tfNomeFilme = new TextField();
        Label lblGenero = new Label("Gênero:");
        TextField tfGenero = new TextField();
        Label lblSinopse = new Label("Sinopse:");
        TextArea taSinopse = new TextArea();
        taSinopse.setPrefRowCount(3);
        Label lblDiretor = new Label("Diretor:");
        TextField tfDiretor = new TextField();
        Label lblData = new Label("Data de Estreia:");
        DatePicker dpData = new DatePicker();

        Button btnSalvar = new Button("Salvar");
        Button btnVoltar = new Button("Voltar");
        Label lblMensagem = new Label();

        btnSalvar.setOnAction(e -> {
            String nome = tfNomeFilme.getText().trim();
            String genero = tfGenero.getText().trim();
            String sinopse = taSinopse.getText().trim();
            String diretor = tfDiretor.getText().trim();
            LocalDate dataLocal = dpData.getValue();

            if (nome.isEmpty() || genero.isEmpty() || dataLocal == null) {
                lblMensagem.setText("Nome, Gênero e Data de Estreia são obrigatórios.");
                return;
            }
            Date dataEstreia = Date.from(dataLocal.atStartOfDay(ZoneId.systemDefault()).toInstant());
            Filme novo = new Filme(nome, genero, sinopse, diretor, dataEstreia, 0.0);
            filmeDAO.adicionarFilme(novo);
            mostrarTelaFilmes();
        });

        btnVoltar.setOnAction(e -> mostrarTelaFilmes());

        HBox botoes = new HBox(10, btnSalvar, btnVoltar);
        VBox vbox = new VBox(10,
                lblNome, tfNomeFilme,
                lblGenero, tfGenero,
                lblSinopse, taSinopse,
                lblDiretor, tfDiretor,
                lblData, dpData,
                botoes,
                lblMensagem
        );
        vbox.setPadding(new Insets(20));
        primaryStage.setScene(new Scene(vbox, 400, 500));
    }

    private void mostrarTelaDetalhesFilme(Filme filme) {
        primaryStage.setTitle("Aero Filmes - Detalhes");

        Label lblNome = new Label("Nome: " + filme.getNomeFilme());
        Label lblGenero = new Label("Gênero: " + filme.getGenero());
        Label lblSinopse = new Label("Sinopse: " + (filme.getSinopse() != null ? filme.getSinopse() : ""));
        Label lblDiretor = new Label("Diretor: " + (filme.getDiretor() != null ? filme.getDiretor() : ""));
        Label lblData = new Label("Data de Estreia: " +
                (filme.getDataExtreia() != null ? filme.getDataExtreia().toString() : ""));
        Label lblMedia = new Label("Média de Avaliações: " +
                (filme.getMediaAvaliacoes() != null ? filme.getMediaAvaliacoes() : "0.0"));

        Label lblAvaliacoes = new Label("Avaliações:");
        ListView<String> lvAvaliacoes = new ListView<>();
        List<Avaliacao> avals = avaliacaoDAO.listarAvaliacoesPorFilme((ObjectId) filme.getId());
        for (Avaliacao a : avals) {
            Filme f = filmeDAO.detalhesFilme((ObjectId) a.getIdFilme());
            String nomeFilme = (f != null ? f.getNomeFilme() : "Desconhecido");
            String item = "Filme: " + nomeFilme + " / Nota: " + a.getNota() + " / Comentário: " + a.getComentario();
            lvAvaliacoes.getItems().add(item);
        }

        Label lblNovaNota = new Label("Sua nota (1-5):");
        ComboBox<Integer> cbNota = new ComboBox<>(FXCollections.observableArrayList(1, 2, 3, 4, 5));
        Label lblComentario = new Label("Comentário:");
        TextArea taComentario = new TextArea();
        taComentario.setPrefRowCount(2);
        Button btnEnviarAvaliacao = new Button("Enviar Avaliação");
        Button btnVoltar = new Button("Voltar");
        Label lblMensagem = new Label();

        btnEnviarAvaliacao.setOnAction(e -> {
            Integer nota = cbNota.getValue();
            String comentario = taComentario.getText().trim();
            if (nota == null) {
                lblMensagem.setText("Escolha uma nota de 1 a 5.");
                return;
            }
            Avaliacao nova = new Avaliacao(nota, comentario, new Date());
            nova.setIdFilme((ObjectId) filme.getId());
            nova.setIdUsuario((ObjectId) usuarioLogado.getId());
            avaliacaoDAO.adicionarAvaliacao(nova);
            mostrarTelaFilmes();
        });

        btnVoltar.setOnAction(e -> mostrarTelaFilmes());

        HBox notaBox = new HBox(10, lblNovaNota, cbNota);
        VBox vbox = new VBox(10,
                lblNome, lblGenero, lblSinopse, lblDiretor, lblData, lblMedia,
                lblAvaliacoes, lvAvaliacoes,
                notaBox, lblComentario, taComentario,
                new HBox(10, btnEnviarAvaliacao, btnVoltar),
                lblMensagem
        );
        vbox.setPadding(new Insets(20));
        primaryStage.setScene(new Scene(vbox, 600, 650));
    }

    private void mostrarTelaPerfil() {
        usuarioLogado = usuarioDAO.buscarPorId((ObjectId) usuarioLogado.getId());

        primaryStage.setTitle("Aero Filmes - Meu Perfil");

        Label lblNomeUsuario = new Label("Nome de Usuário: " + usuarioLogado.getNomeUsuario());
        Label lblEmail = new Label("E-mail: " + usuarioLogado.getEmail());
        Label lblApelido = new Label("Apelido: " + usuarioLogado.getApelido());
        Label lblBio = new Label("Bio: " + (usuarioLogado.getBio() != null ? usuarioLogado.getBio() : ""));

        Button btnAtualizar = new Button("Atualizar");
        Button btnVoltar = new Button("Voltar");
        btnAtualizar.setOnAction(e -> mostrarTelaAtualizarPerfil());
        btnVoltar.setOnAction(e -> mostrarTelaFilmes());

        Label lblMinhasAvaliacoes = new Label("Minhas Avaliações:");
        ListView<String> lvMinhasAvaliacoes = new ListView<>();
        List<Avaliacao> minhas = avaliacaoDAO.listarAvaliacoesPorUsuario((ObjectId) usuarioLogado.getId());
        for (Avaliacao a : minhas) {
            Filme f = filmeDAO.detalhesFilme((ObjectId) a.getIdFilme());
            String nomeFilme = (f != null ? f.getNomeFilme() : "Desconhecido");
            String item = "Filme: " + nomeFilme + " / Nota: " + a.getNota() + " / Comentário: " + a.getComentario();
            lvMinhasAvaliacoes.getItems().add(item);
        }
        lvMinhasAvaliacoes.setCellFactory(TextFieldListCell.forListView(new StringConverter<>() {
            @Override
            public String toString(String object) {
                return object;
            }
            @Override
            public String fromString(String string) {
                return string;
            }
        }));

        HBox botoes = new HBox(10, btnAtualizar, btnVoltar);
        VBox vbox = new VBox(10,
                lblNomeUsuario,
                lblEmail,
                lblApelido,
                lblBio,
                botoes,
                lblMinhasAvaliacoes, lvMinhasAvaliacoes
        );
        vbox.setPadding(new Insets(20));
        primaryStage.setScene(new Scene(vbox, 500, 600));
    }

    private void mostrarTelaAtualizarPerfil() {
        usuarioLogado = usuarioDAO.buscarPorId((ObjectId) usuarioLogado.getId());

        primaryStage.setTitle("Aero Filmes - Atualizar Perfil");

        Label lblNomeUsuario = new Label("Nome de Usuário:");
        TextField tfNomeUsuario = new TextField(usuarioLogado.getNomeUsuario());
        Label lblApelido = new Label("Apelido:");
        TextField tfApelido = new TextField(usuarioLogado.getApelido());
        Label lblBio = new Label("Bio:");
        TextArea taBio = new TextArea(usuarioLogado.getBio());
        taBio.setPrefRowCount(3);

        Button btnSalvar = new Button("Salvar Alterações");
        Button btnCancelar = new Button("Cancelar");
        Label lblMensagem = new Label();

        btnSalvar.setOnAction(e -> {
            String novoNome = tfNomeUsuario.getText().trim();
            String novoApelido = tfApelido.getText().trim();
            String novaBio = taBio.getText().trim();
            if (novoNome.isEmpty()) {
                lblMensagem.setText("Nome de usuário não pode ficar vazio.");
                return;
            }
            usuarioDAO.atualizar((ObjectId) usuarioLogado.getId(), novoNome, novoApelido, novaBio);
            usuarioLogado = usuarioDAO.buscarPorId((ObjectId) usuarioLogado.getId());
            mostrarTelaPerfil();
        });

        btnCancelar.setOnAction(e -> mostrarTelaPerfil());

        HBox botoes = new HBox(10, btnSalvar, btnCancelar);
        VBox vbox = new VBox(10,
                lblNomeUsuario, tfNomeUsuario,
                lblApelido, tfApelido,
                lblBio, taBio,
                botoes,
                lblMensagem
        );
        vbox.setPadding(new Insets(20));
        primaryStage.setScene(new Scene(vbox, 500, 400));
    }
}