package me.progettotep.spesaonline.mantica;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import javax.xml.parsers.ParserConfigurationException;
import me.progettotep.spesaonline.DbManager;
import me.progettotep.spesaonline.utils.XMLUtils;
import me.progettotep.spesaonline.utils.XMLValidator;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 *
 * @author Luca Mantica
 */
@Path("negozi")
public class NegoziResource {

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of NegoziResource
     */
    public NegoziResource() {
    }

    /**
     * Restituisce tutto l'elenco dei negozi registarti
     *
     * @return xml con le informazioni richieste
     */
    @GET
    @Produces(MediaType.APPLICATION_XML)
    public String getXml() {
        final DbManager db = DbManager.getInstance();
        if (!db.isConnected()) {
            System.err.println("DB non connesso");
            throw new WebApplicationException("DB non connesso", 500);
        }

        final String sql = "SELECT * FROM negozi";
        try (PreparedStatement statement = db.getConnection().prepareStatement(sql)) {
            StringBuilder ris;
            try (ResultSet result = statement.executeQuery()) {
                ris = new StringBuilder();
                ris.append("<elenco_negozi>");
                while (result.next()) {
                    ris.append("<negozio>");
                    ris.append("<id>").append(result.getInt(1)).append("</id>");
                    ris.append("<nome>").append(result.getString(2)).append("</nome>");
                    ris.append("<luogo>").append(result.getString(3)).append("</luogo>");
                    ris.append("<via>").append(result.getString(4)).append("</via>");
                    ris.append("<num_telefono>").append(result.getInt(5)).append("</num_telefono>");
                    ris.append("</negozio>");
                }
                ris.append("</elenco_negozi>");
            }
            return ris.toString();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new WebApplicationException("DBMS server error!", 500);
        }
    }

    /**
     * Registrazione di un negozio
     *
     * @param content xml
     */
    @POST
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    public String inserisciNegozio(String content) throws IOException, URISyntaxException, ParserConfigurationException, SAXException {
        final URL res = getClass().getClassLoader().getResource("xsd/negozio.xsd");
        final File xsd = Paths.get(res.toURI()).toFile();
        if (!XMLValidator.validate(xsd, content))
            throw new WebApplicationException("Parametri non validi!", 406);

        final DbManager db = DbManager.getInstance();
        if (!db.isConnected()) {
            throw new WebApplicationException("DBMS server error!", 500);
        }

        final Element root = XMLUtils.loadXmlFromString(content);
        long id = -1;
        final String sql = "INSERT INTO negozi(ID, Nome, Luogo, Via, Num_Telefono) VALUES (?,?,?,?,?)";
        try (PreparedStatement statement = db.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, XMLUtils.getTextValue(root, "idNegozio"));
            statement.setString(2, XMLUtils.getTextValue(root, "nome"));
            statement.setString(3, XMLUtils.getTextValue(root, "luogo"));
            statement.setString(4, XMLUtils.getTextValue(root, "via"));
            statement.setString(5, XMLUtils.getTextValue(root, "numTelefono"));
            int affectedRows = statement.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = statement.getGeneratedKeys()) {
                    if (rs.next()) {
                        id = rs.getLong(1);
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    throw new WebApplicationException("DBMS server error!", 500);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new WebApplicationException("DBMS server error!", 500);
        }
        return "<idNegozio>" + id + "</idNegozio>";
    }
}