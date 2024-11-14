package cr.ac.una.mapp.model;

import com.google.gson.annotations.Expose;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author stward segura
 */
public class Vertice {

    private List<Arista> aristas;//aristas que van desde este vertice a otro vertice 
    private List<Arista> recibidas;//aristas que vienen de otro vertice a este vertice

    @Expose
    private Integer id;
    @Expose
    private double x, y;//coordenadas

    public Vertice() {
        this.x = 0;
        this.y = 0;
        this.id = 0;
        this.aristas = new ArrayList<>();
        this.recibidas = new ArrayList<>();
    }

    public List<Arista> getRecibidas() {
        return recibidas;
    }

    public void setRecibidas(List<Arista> recibidas) {
        this.recibidas = recibidas;
    }

    public List<Arista> getAristas() {
        return aristas;
    }

    public void setAristas(List<Arista> aristas) {
        this.aristas = aristas;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Vertice vertice = (Vertice) obj;
        return id == vertice.id; // O compara el atributo único que identifica a cada vértice
    }

    @Override
    public int hashCode() {
        return Objects.hash(id); // O el atributo único correspondiente
    }

}
