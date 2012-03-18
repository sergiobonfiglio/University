package grafo.listaAdiacenza;

import java.awt.geom.Point2D;

public class NodoPuntoPeso implements NodoListaAdiacenza {

	private Point2D p;
	private Double weight;

	public NodoPuntoPeso(Point2D p, Double weight) {
		this.p = p;
		this.weight = weight;
	}

	public Point2D getPoint() {
		return p;
	}

	public Double getWeight() {
		return weight;
	}

	public boolean equals(Object o) {
		if (o == null || !(o instanceof NodoListaAdiacenza))
			return false;
		else
			return p.equals(((NodoListaAdiacenza) o).getPoint());
	}

}
