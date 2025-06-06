package br.com.seguranca.DAO;

import br.com.seguranca.Model.Usuario;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoClients;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.types.ObjectId;
import static com.mongodb.client.model.Filters.eq;

public class UsuarioDAO {

    private final MongoCollection<Document> collection;

    public UsuarioDAO() {
        MongoClient client = MongoClients.create("mongodb://localhost:27017");
        MongoDatabase database = client.getDatabase("aeroFilmes");
        collection = database.getCollection("usuarios");
    }

    public void cadastrar(Usuario usuario) {
        Document doc = new Document("nomeUsuario", usuario.getNomeUsuario())
                .append("email", usuario.getEmail())
                .append("senha", usuario.getSenha())
                .append("apelido", usuario.getApelido())
                .append("bio", usuario.getBio());
        collection.insertOne(doc);
        usuario.setId(doc.getObjectId("_id"));
    }

    public Usuario login(String email, String senha) {
        Document doc = collection.find(Filters.and(eq("email", email), eq("senha", senha))).first();
        if (doc != null) {
            return documentParaUsuario(doc);
        }
        return null;
    }

    public Usuario buscarPorId(ObjectId id) {
        Document doc = collection.find(eq("_id", id)).first();
        if (doc != null) {
            return documentParaUsuario(doc);
        }
        return null;
    }

    public void atualizar(ObjectId id, String novoNomeUsuario, String novoApelido, String novaBio) {
        Document updateDoc = new Document();
        if (novoNomeUsuario != null && !novoNomeUsuario.isEmpty()) {
            updateDoc.append("nomeUsuario", novoNomeUsuario);
        }
        if (novoApelido != null) {
            updateDoc.append("apelido", novoApelido);
        }
        if (novaBio != null) {
            updateDoc.append("bio", novaBio);
        }
        if (!updateDoc.isEmpty()) {
            collection.updateOne(eq("_id", id), new Document("$set", updateDoc));
        }
    }

    private Usuario documentParaUsuario(Document doc) {
        Usuario u = new Usuario();
        u.setId(doc.getObjectId("_id"));
        u.setNomeUsuario(doc.getString("nomeUsuario"));
        u.setEmail(doc.getString("email"));
        u.setSenha(doc.getString("senha"));
        u.setApelido(doc.getString("apelido"));
        u.setBio(doc.getString("bio"));
        return u;
    }
}
