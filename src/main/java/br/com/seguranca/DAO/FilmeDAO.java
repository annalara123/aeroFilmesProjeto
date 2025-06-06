package br.com.seguranca.DAO;

import br.com.seguranca.Model.Filme;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoClients;
import org.bson.Document;
import org.bson.types.ObjectId;
import java.util.ArrayList;
import java.util.List;
import static com.mongodb.client.model.Filters.eq;

public class FilmeDAO {

    private final MongoCollection<Document> collection;

    public FilmeDAO() {
        MongoClient client = MongoClients.create("mongodb://localhost:27017");
        MongoDatabase database = client.getDatabase("aeroFilmes");
        collection = database.getCollection("filmes");
    }

    public void adicionarFilme(Filme filme) {
        Document doc = new Document("nomeFilme", filme.getNomeFilme())
                .append("genero", filme.getGenero())
                .append("sinopse", filme.getSinopse())
                .append("diretor", filme.getDiretor())
                .append("dataExtreia", filme.getDataExtreia())
                .append("mediaAvaliacoes", filme.getMediaAvaliacoes() != null ? filme.getMediaAvaliacoes() : 0.0);
        collection.insertOne(doc);
        filme.setId(doc.getObjectId("_id"));
    }

    public Filme buscarPorNome(String nomeFilme) {
        Document doc = collection.find(eq("nomeFilme", nomeFilme)).first();
        if (doc != null) {
            return documentParaFilme(doc);
        }
        return null;
    }

    public List<Filme> filtrarPorGenero(String genero) {
        List<Filme> filmes = new ArrayList<>();
        for (Document doc : collection.find(eq("genero", genero))) {
            filmes.add(documentParaFilme(doc));
        }
        return filmes;
    }

    public List<Filme> listarFilmes() {
        List<Filme> filmes = new ArrayList<>();
        for (Document doc : collection.find()) {
            filmes.add(documentParaFilme(doc));
        }
        return filmes;
    }

    public Filme detalhesFilme(ObjectId id) {
        Document doc = collection.find(eq("_id", id)).first();
        if (doc != null) {
            return documentParaFilme(doc);
        }
        return null;
    }

    private Filme documentParaFilme(Document doc) {
        Filme f = new Filme();
        f.setId(doc.getObjectId("_id"));
        f.setNomeFilme(doc.getString("nomeFilme"));
        f.setGenero(doc.getString("genero"));
        f.setSinopse(doc.getString("sinopse"));
        f.setDiretor(doc.getString("diretor"));
        f.setDataExtreia(doc.getDate("dataExtreia"));
        Double media = doc.getDouble("mediaAvaliacoes");
        f.setMediaAvaliacoes(media != null ? media : 0.0);
        return f;
    }
}
