
public class OpzioniInpainting {
	public boolean algoritmoNaive;
	public byte strategia;
	public boolean minimizzaAncheDelta;
	public boolean allineaBuchi;
	public boolean usaSubPattern;

	public OpzioniInpainting(boolean algoritmoNaive, byte strategia,
			boolean minimizzaAncheDelta, boolean allineaBuchi,
			boolean usaSubPattern) {
		this.algoritmoNaive = algoritmoNaive;
		this.strategia = strategia;
		this.minimizzaAncheDelta = minimizzaAncheDelta;
		this.allineaBuchi = allineaBuchi;
		this.usaSubPattern = usaSubPattern;
	}
}