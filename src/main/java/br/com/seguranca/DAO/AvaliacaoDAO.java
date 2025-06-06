package br.com.seguranca.DAO;

import br.com.seguranca.Model.Avaliacao;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoClients;
import org.bson.Document;
import org.bson.types.ObjectId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import static com.mongodb.client.model.Filters.eq;

public class AvaliacaoDAO {

    private final MongoCollection<Document> collection;
    private final MongoCollection<Document> filmesCollection;

    public AvaliacaoDAO() {
        MongoClient client = MongoClients.create("mongodb://localhost:27017");
        MongoDatabase database = client.getDatabase("aeroFilmes");
        collection = database.getCollection("avaliacoes");
        filmesCollection = database.getCollection("filmes");
    }

    public void adicionarAvaliacao(Avaliacao avaliacao) {
        Document doc = new Document("idFilme", avaliacao.getIdFilme())
                .append("idUsuario", avaliacao.getIdUsuario())
                .append("nota", avaliacao.getNota())
                .append("comentario", avaliacao.getComentario())
                .append("data", avaliacao.getData() != null ? avaliacao.getData() : new Date());
        collection.insertOne(doc);
        avaliacao.setId(doc.getObjectId("_id"));
        atualizarMediaAvaliacoes((ObjectId) avaliacao.getIdFilme());
    }

    public List<Avaliacao> listarAvaliacoesPorUsuario(ObjectId idUsuario) {
        List<Avaliacao> avaliacoes = new ArrayList<>();
        for (Document doc : collection.find(eq("idUsuario", idUsuario))) {
            avaliacoes.add(documentParaAvaliacao(doc));
        }
        return avaliacoes;
    }

    public List<Avaliacao> listarAvaliacoesPorFilme(ObjectId idFilme) {
        List<Avaliacao> avaliacoes = new ArrayList<>();
        for (Document doc : collection.find(eq("idFilme", idFilme))) {
            avaliacoes.add(documentParaAvaliacao(doc));
        }
        return avaliacoes;
    }

    private void atualizarMediaAvaliacoes(ObjectId idFilme) {
        List<Document> avaliacoes = collection.find(eq("idFilme", idFilme)).into(new ArrayList<>());
        double soma = 0.0;
        for (Document doc : avaliacoes) {
            soma += doc.getInteger("nota", 0);
        }
        double media = !avaliacoes.isEmpty() ? soma / avaliacoes.size() : 0.0;
        filmesCollection.updateOne(
                eq("_id", idFilme),
                new Document("$set", new Document("mediaAvaliacoes", media))
        );
    }

    private Avaliacao documentParaAvaliacao(Document doc) {
        Avaliacao a = new Avaliacao();
        a.setId(doc.getObjectId("_id"));
        a.setIdFilme(doc.get("idFilme"));
        a.setIdUsuario(doc.get("idUsuario"));
        a.setNota(doc.getInteger("nota"));
        a.setComentario(doc.getString("comentario"));
        a.setData(doc.getDate("data"));
        return a;
    }
}
