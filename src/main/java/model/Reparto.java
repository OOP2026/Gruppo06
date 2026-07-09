package model;

/**
 * Rappresenta un reparto ospedaliero.
 * Contiene informazioni sul nome e il numero di medici afferenti.
 */
public class Reparto {
    private String nomeReparto;
    private int numeroAfferenti;

    /**
     * Costruisce un nuovo oggetto Reparto.
     * @param nomeReparto Il nome del reparto.
     * @param numeroAfferenti Il numero di medici afferenti.
     */
    public Reparto(String nomeReparto, int numeroAfferenti) {
        this.nomeReparto = nomeReparto;
        this.numeroAfferenti = numeroAfferenti;
    }

    /**
     * Restituisce il nome del reparto.
     * @return il nome del reparto.
     */
    public String getNomeReparto() {
        return nomeReparto;
    }

    /**
     * Imposta il nome del reparto.
     * @param nomeReparto il nuovo nome del reparto.
     */
    public void setNomeReparto(String nomeReparto) {
        this.nomeReparto = nomeReparto;
    }

    /**
     * Restituisce il numero di medici afferenti al reparto.
     * @return il numero di medici.
     */
    public int getNumeroAfferenti() {
        return numeroAfferenti;
    }

    /**
     * Imposta il numero di medici afferenti al reparto.
     * @param numeroAfferenti il nuovo numero di medici.
     */
    public void setNumeroAfferenti(int numeroAfferenti) {
        this.numeroAfferenti = numeroAfferenti;
    }

    @Override
    public String toString() {
        return "Reparto{" +
                "nomeReparto='" + nomeReparto + '\'' +
                ", numeroAfferenti=" + numeroAfferenti +
                '}';
    }

}
