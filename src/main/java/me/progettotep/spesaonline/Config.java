package me.progettotep.spesaonline;

import java.util.Set;
import javax.ws.rs.core.Application;

/**
 *
 * @author Luca Mantica
 */
@javax.ws.rs.ApplicationPath("api")
public class Config extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new java.util.HashSet<Class<?>>();
        addRestResourceClasses(resources);
        return resources;
    }

    /**
     * Do not modify addRestResourceClasses() method.
     * It is automatically populated with
     * all resources defined in the project.
     * If required, comment out calling this method in getClasses().
     */
    private void addRestResourceClasses(Set<Class<?>> resources) {
        resources.add(me.progettotep.spesaonline.DbManager.class);
    }
    //mettere le classi
}