package hashForwardList;

import java.awt.Point;
import java.util.HashMap;
import java.util.LinkedList;


public class HashForwardList extends HashMap {

	private static final long serialVersionUID = 1L;

	public HashForwardList() {
		super();
	}

	public HashForwardList(int initialCapacity) {
		super(initialCapacity);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object put(Object key, Object value) {
		// se la lista non è ancora stata creata la creo
		if (super.get(key) == null) {
			// creo la lista e vi aggiungo l'oggetto
			LinkedList<Object> lista = new LinkedList<Object>();
			lista.add(value);

			Nodo nodo = new Nodo(lista, 256);
			// metto nella hashmap questa lista come valore associato alla
			// chiave
			super.put(key, nodo);

			return value;
		}
		// se invece la lista è già presente inserisco l'oggetto dentro la lista
		else if (this.get(key) != null) {
			Nodo nodo = (Nodo) super.get(key);
			nodo.lista.add(value);
			return value;
		}

		else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public void buildHashForwardList() {
			int ultimaLista = 256;
			for (int i = 255; i >= 0; i--) {
				Nodo nodo = (Nodo) (this.get(i));
				if (nodo == null) {
					super
							.put(i, new Nodo(null,
									ultimaLista));
				} else {
					nodo.nearestList = ultimaLista;
					ultimaLista = i;
				}

			}
	}


}
