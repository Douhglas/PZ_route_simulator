package cr.ac.una.mapp.model;

/**
 *
 * @author stward segura
 */
import com.google.gson.annotations.Expose;
import java.util.*;

public class Grafo {

    @Expose
    private List<Vertice> vertices;
    @Expose
    private List<List<Integer>> matrix;  //no se usa
    @Expose
    public List<List<Arista>> matrizAdyacencia;
    @Expose
    private List<Arista> aristas;
    @Expose
    private int[][] predecesor;
    
    public boolean isUsingDijkstra = true;
    

    public Grafo() {
        vertices = new ArrayList<>();
        aristas = new ArrayList<Arista>();
        matrizAdyacencia = new ArrayList<>();
        vertices.clear();
        matrizAdyacencia.clear();
        aristas.clear();
    }

    public void agregarVertice(Vertice nuevoVertice) {
        // Actualizar todas las filas existentes para que tengan una columna adicional
        for (List<Arista> fila : matrizAdyacencia) {
            fila.add(null);
        }
        nuevoVertice.setId(vertices.size());
        vertices.add(nuevoVertice);
        int nuevoTamano = vertices.size();
        Arista arista = new Arista();
        arista.setPeso(Integer.MAX_VALUE);
        matrizAdyacencia.add(new ArrayList<>(Collections.nCopies(nuevoTamano, null)));

        System.out.println("Nuevo vértice agregado: " + nuevoVertice.getId());
    }

    public void agregarArista(Arista nuevoArista) {
        matrizAdyacencia.get(nuevoArista.getOrigen().getId())
                .set(nuevoArista.getDestino().getId(), nuevoArista);
        aristas.add(nuevoArista);
        System.out.println("ARISTA " + nuevoArista.getOrigen().getId() + " - > " + nuevoArista.getDestino().getId());
    }

    private List<Vertice> obtenerVerticesUnicos(List<Arista> aristas) {
        Set<Vertice> verticesSet = new HashSet<>();
        for (Arista arista : aristas) {
            verticesSet.add(arista.getOrigen());
            verticesSet.add(arista.getDestino());
        }
        return new ArrayList<>(verticesSet);
    }

    public void mostrarMatrizAdyacenciaActual() {
        System.out.println("Matriz de Adyacencia:");
        for (List<Arista> fila : matrizAdyacencia) {
            for (Arista arista : fila) {
                if (arista == null) {
                    System.out.print("∞ ");
                } else {
                    System.out.print(arista.getPeso() + " ");
                }
            }
            System.out.println();
        }
    }

    public List<Arista> dijkstra(int origenId, int destinoId) {
        int numVertices = vertices.size();

        int[] distancias = new int[numVertices];
        Arrays.fill(distancias, Integer.MAX_VALUE);
        distancias[origenId] = 0;

        int[] predecesores = new int[numVertices];
        Arrays.fill(predecesores, -1);

        PriorityQueue<Vertice> pq = new PriorityQueue<>(Comparator.comparingInt(v -> distancias[v.getId()]));
        pq.add(vertices.get(origenId));


        while (!pq.isEmpty()) {
            Vertice actual = pq.poll();
            int actualId = actual.getId();
            if (actualId == destinoId) {

                List<Integer> caminoInteger = reconstruirCaminoDjikstra(predecesores, origenId, destinoId);
                return crearCamino(caminoInteger);
            }

            if (matrizAdyacencia.get(actualId) == null) {
                continue;
            }

            for (Arista arista : matrizAdyacencia.get(actualId)) {
                if (arista != null && !arista.getIsClosed()) {
                    int vecinoId = arista.getDestino().getId();
                    int nuevaDistancia = distancias[actualId] + arista.getPeso();


                    if (nuevaDistancia < distancias[vecinoId]) {
                        distancias[vecinoId] = nuevaDistancia;
                        predecesores[vecinoId] = actualId;
                        pq.add(vertices.get(vecinoId));
                    }
                } else if (arista != null && arista.getIsClosed()) {
                    System.out.println("Saltando arista cerrada: origen=" + arista.getOrigen().getId() + ", destino=" + arista.getDestino().getId());
                }
            }
        }

        return null;
    }




    public List<Arista> floydWarshall(int origen, int destino) {
        int numVertices = matrizAdyacencia.size();
        int[][] dist = new int[numVertices][numVertices];
        predecesor = new int[numVertices][numVertices];

        for (int i = 0; i < numVertices; i++) {
            for (int j = 0; j < numVertices; j++) {
                if (i == j) {
                    dist[i][j] = 0;
                    predecesor[i][j] = -1;
                } else {
                    dist[i][j] = Integer.MAX_VALUE;
                    predecesor[i][j] = -1;
                }
            }
        }

        for (int i = 0; i < numVertices; i++) {
            for (int j = 0; j < numVertices; j++) {
                Arista arista = matrizAdyacencia.get(i).get(j);
                if (arista != null && !arista.getIsClosed()) {
                    dist[i][j] = arista.getPeso();
                    predecesor[i][j] = i;
                }
            }
        }

        for (int k = 0; k < numVertices; k++) {
            for (int i = 0; i < numVertices; i++) {
                for (int j = 0; j < numVertices; j++) {
                    if (dist[i][k] != Integer.MAX_VALUE && dist[k][j] != Integer.MAX_VALUE
                            && dist[i][j] > dist[i][k] + dist[k][j]) {
                        dist[i][j] = dist[i][k] + dist[k][j];
                        predecesor[i][j] = predecesor[k][j];  
                    }
                }
            }
        }

        List<Integer> camino = obtenerCamino(origen, destino);
        if (camino == null) {
            return null;
        }
        return crearCamino(camino);
    }


    public List<Integer> obtenerCamino(int origenId, int destinoId) {
        List<Integer> camino = new ArrayList<>();
        if (predecesor[origenId][destinoId] == -1) {
            return null; // No hay camino
        }

        camino.add(destinoId);

        while (predecesor[origenId][destinoId] != -1 && destinoId != origenId) {
            destinoId = predecesor[origenId][destinoId];
            camino.add(destinoId);
        }

        Collections.reverse(camino);
        return camino;
    }

    private List<Vertice> reconstruirCamino(int[] predecesores, int origen, int destino) {
        List<Vertice> camino = new ArrayList<>();
        for (int at = destino; at != -1; at = predecesores[at]) {
            camino.add(vertices.get(at));
        }
        Collections.reverse(camino);
        return camino;
    }

    public List<Arista> crearCamino(List<Integer> camino) {
        if (camino == null || camino.isEmpty()) {
            return new ArrayList<>();
        }

        List<Arista> aristas = new ArrayList<>();

        for (int i = 0; i < camino.size(); i++) {
            if (i < camino.size() - 1) {
                int origenId = camino.get(i);
                int destinoId = camino.get(i + 1);

                boolean aristaEncontrada = false;
                for (Arista arista : this.aristas) {
                    if (arista == null) {
                        continue;
                    }

                    if (origenId == arista.getOrigen().getId() && destinoId == arista.getDestino().getId()) {
                        aristas.add(arista);
                        aristaEncontrada = true;
                        break;
                    }
                }

                if (!aristaEncontrada) {
                }
            }
        }

        if (aristas.isEmpty()) {
            //System.out.println("crearCamino: No se encontraron aristas para el camino proporcionado.");
        } else {
            //System.out.println("crearCamino: Aristas resultantes del camino: " + aristas);
        }

        return aristas;
    }

    public List<Vertice> getVertices() {
        return vertices;
    }

    public List<Arista> getAristas() {
        return aristas;
    }

    public long getPeso(int fila, int columna) {
        return matrizAdyacencia.get(fila)
                .get(columna).getPeso();
    }

    public Integer getPeso(Vertice origen, Vertice destino) {
        return matrizAdyacencia.get(origen.getId())
                .get(destino.getId()).getPeso();
    }

    private List<Integer> reconstruirCaminoDjikstra(int[] predecesores, int origen, int destino) {
        List<Integer> camino = new ArrayList<>();
        for (int at = destino; at != -1; at = predecesores[at]) {
            camino.add(at);
        }
        Collections.reverse(camino);
        return camino;
    }

    public List<Arista> crearCaminoDjikstra(List<Integer> camino) {
        List<Arista> aristas = new ArrayList<>();
        for (int i = 0; i < camino.size() - 1; i++) {
            int origenId = camino.get(i);
            int destinoId = camino.get(i + 1);

            Arista arista = matrizAdyacencia.get(origenId).get(destinoId);
            if (arista != null) {
                aristas.add(arista);
            }
        }
        return aristas;
    }

    public void actualizarPesoArista(int origenId, int destinoId, int nivelTrafico) {
        Arista arista = matrizAdyacencia.get(origenId).get(destinoId);
        if (arista != null) {
            arista.setPeso(arista.getLongitud() * nivelTrafico);
        }
    }




}
