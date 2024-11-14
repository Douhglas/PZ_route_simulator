package cr.ac.una.mapp.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import cr.ac.una.mapp.model.Arista;
import cr.ac.una.mapp.model.Grafo;
import cr.ac.una.mapp.model.Vertice;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author stward segura
 */
public class AppManager {

    private static AppManager INSTANCE = null;
    private List<Arista> aristas = new ArrayList<>();
    Grafo grafo;

    private AppManager() {

    }

    private static void createInstance() {
        if (INSTANCE == null) {
            synchronized (AppManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new AppManager();
                }
            }
        }
    }

    public static AppManager getInstance() {
        if (INSTANCE == null) {
            createInstance();
        }
        return INSTANCE;
    }

//    public static void guardar(List<Arista> aristas) {
//        String projectDir = System.getProperty("user.dir");
//        String ruta = projectDir + File.separator + "aristas" + ".json";
//
//        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create(); // Crear Gson con formato de impresión amigable
//
//        try (FileWriter writer = new FileWriter(ruta)) {
//            gson.toJson(aristas, writer);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
    public static void guardar(Grafo grafo) {
        String projectDir = System.getProperty("user.dir");
        String ruta = projectDir + File.separator + "Grafo" + ".json";

        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create(); // Crear Gson con formato de impresión amigable

        try (FileWriter writer = new FileWriter(ruta)) {
            gson.toJson(grafo, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Grafo cargar() {
        String projectDir = System.getProperty("user.dir");
        String ruta = projectDir + File.separator + "Grafo" + ".json";

        File file = new File(ruta);
        if (!file.exists()) {
            System.out.println("El archivo no existe: " + ruta);
            return null;
        }
        Gson gson = new Gson();

        try (FileReader reader = new FileReader(ruta)) {

            Type aristaListType = new TypeToken<Grafo>() {
            }.getType();
            grafo = gson.fromJson(reader, aristaListType);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return grafo;
    }

//    public List<Arista> cargar() {
//        String projectDir = System.getProperty("user.dir");
//        String ruta = projectDir + File.separator + "aristas" + ".json";
//
//        File file = new File(ruta);
//        if (!file.exists()) {
//            System.out.println("El archivo no existe: " + ruta);
//            return null;  
//        }
//        Gson gson = new Gson();
//         
//        try (FileReader reader = new FileReader(ruta)) {
//
//            Type aristaListType = new TypeToken<List<Arista>>() {
//            }.getType();
//            aristas = gson.fromJson(reader, aristaListType);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return aristas;
//    }

    
}
