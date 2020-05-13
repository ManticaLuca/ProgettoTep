package me.progettotep.spesaonline.mantica;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import me.progettotep.spesaonline.DbManager;

/**
 * REST Web Service
 *
 * @author Luca Mantica
 */
@Path("negozio")
public class NegozioResources {

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of NegozioResources
     */
    public NegozioResources() {
    }

    /**
     * Restituisce le informazioni di un negozio
     *
     * @param idNegozio id del negozio
     * @return xml con le informazioni richieste
     */
    @GET
    @Path("{idNegozio}")
    @Produces(MediaType.APPLICATION_XML)
    public String getXmlScheda(@PathParam(value = "idNegozio") int idNegozio) {
        final DbManager db = DbManager.getInstance();
        if (!db.isConnected()) {
            System.err.println("DB non connesso");
            throw new WebApplicationException("DB non connesso!", 500);
        }

        String sql = "SELECT count(*) FROM negozi " + "WHERE negozi.ID = ?";
        try (PreparedStatement statement = db.getConnection().prepareStatement(sql)) {
            statement.setInt(1, idNegozio);
            try (ResultSet result = statement.executeQuery()) {
                result.next();
                if (result.getInt(1) == 0) {
                    throw new WebApplicationException("Negozio non trovato", 404);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new WebApplicationException("DBMS server error!", 500);
        }

        sql = "SELECT * FROM negozi WHERE ID = ?";
        try (PreparedStatement statement = db.getConnection().prepareStatement(sql)) {
            statement.setInt(1, idNegozio);

            StringBuilder ris;
            try (ResultSet result = statement.executeQuery()) {
                ris = new StringBuilder();
                ris.append("<negozio>");
                while (result.next()) {
                    ris.append("<idNegozio>").append(result.getInt(1)).append("</idNegozio>");
                    ris.append("<nome>").append(result.getString(2)).append("</nome>");
                    ris.append("<luogo>").append(result.getString(3)).append("</luogo>");
                    ris.append("<via>").append(result.getString(4)).append("</via>");
                    ris.append("<numTelefono>").append(result.getString(5)).append("</numTelefono>");
                }
                ris.append("</negozio>");
            }
            return ris.toString();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new WebApplicationException("DBMS server error!", 500);
        }
    }

    @PUT
    @Consumes(MediaType.APPLICATION_XML)
    public void modificaScheda(String content) {
        //TODO
    }

    @POST
    @Consumes(MediaType.APPLICATION_XML)
    public void creaScheda(String content) {
        //TODO
    }
}
