/**
 * Questa classe avvia l'algoritmo di inpainting in un thread separato. Ha
 * quindi bisogno di tutti i parametri da passare all'algoritmo.
 * 
 * @author Sergio Bonfiglio
 * 
 */
public class ThreadInpainting implements Runnable {

	private OpzioniInpainting opzioniInpainting;

	private boolean ripetiMaschera;

	private Object lock;

	public ThreadInpainting(OpzioniInpainting opzioniInpainting,
			boolean ripetiMaschera, Object lock) {
		this.opzioniInpainting = opzioniInpainting;

		this.ripetiMaschera = ripetiMaschera;
		this.lock = lock;
	}

	public void run() {
		if (lock != null) {

			synchronized (lock) {
				PaintFrame.getIstanza().inpainting(this.opzioniInpainting,
						ripetiMaschera);
			}

		} else {
			PaintFrame.getIstanza().inpainting(this.opzioniInpainting,
					ripetiMaschera);
		}
	}
}
