package cr.ac.una.mapp.model;

import com.google.gson.annotations.Expose;

/**
 *
 * @author stward segura
 */
public class Arista { 
    @Expose
    private Vertice origen; // Origen de la arista
    @Expose
    private Vertice destino; // Destino de la arista
    @Expose
    private Integer peso;
    @Expose
    private Boolean isClosed;
    @Expose
    private Integer time;
    @Expose
    private Integer longitud;
    @Expose
    private Integer nivelTrafico;
    
    public Arista() {
        this.origen = null;
        this.destino = null;
        this.peso = 0;
        this.time = 0;
        this.isClosed = false;
        this.longitud = 0;
        this.nivelTrafico = 1;
        
    } 
    public Vertice getOrigen() {
        return origen;
    }

    public Boolean getIsClosed() {
        return isClosed;
    }

    public Integer getNivelTrafico() {
        return nivelTrafico;
    }

    public void setNivelTrafico(Integer nivelTrafico) {
        this.nivelTrafico = nivelTrafico;
    }

    public void setIsClosed(Boolean isClosed) {
        this.isClosed = isClosed;
    }

    public Integer getLongitud() {
        return longitud;
    }

    public void setLongitud(Integer longitud) {
        this.longitud = longitud;
    }

    public Integer getTime() {
        return time;
    }

    public void setTime(Integer time) {
        this.time = time;
    }

    public void setOrigen(Vertice origen) {
        this.origen = origen;
    }

    public Vertice getDestino() {
        return destino;
    }

    public void setDestino(Vertice destino) {
        this.destino = destino;
    }

    public Integer getPeso() {
        return peso;
    }

    public void setPeso(Integer peso) {
        this.peso = peso;
    }

    public Boolean getDisponibilidad() {
        return isClosed;
    }

    public void setDisponibilidad(Boolean disponibilidad) {
        this.isClosed = disponibilidad;
    }
    
}
