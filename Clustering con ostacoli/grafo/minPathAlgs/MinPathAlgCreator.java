package grafo.minPathAlgs;

import grafo.GrafoOstacoli;
import grafo.minPathAlgs.bellmanFord.BellmanFordAlg;
import grafo.minPathAlgs.dijkstra.DijkstraAlg;

public class MinPathAlgCreator {

    public static MinPathAlg getAlgorithm(GrafoOstacoli g) {
	return new DijkstraAlg(g);
    }

    public static MinPathAlg getDijkstraAlg(GrafoOstacoli g) {
	return new DijkstraAlg(g);
    }

    public static MinPathAlg getBellmanFordAlg(GrafoOstacoli g) {
	return new BellmanFordAlg(g);
    }

}
