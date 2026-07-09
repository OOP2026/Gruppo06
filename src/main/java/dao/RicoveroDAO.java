package dao;

import java.util.ArrayList;
import java.util.List;

public interface RicoveroDAO {
    boolean aggiungiRicovero(String cfPaziente, String idLetto, String reparto, String dataInizio, String motivo);
    boolean aggiornaRicoveroDimissione(String idRicovero, String dataFine, String prognosi, String esito);
    ArrayList<String> getRicoveroAttivo(String cfPaziente);
    ArrayList<ArrayList<String>> getStoricoRicoveri(String cfPaziente);
    ArrayList<ArrayList<String>> getAllDimissioni();
    ArrayList<String> getUltimoRicoveroChiuso(String cfPaziente);
    List<ArrayList<String>> getAllRicoveriAttivi();
    boolean isLettoAttualmenteOccupato(String idLetto, String reparto);

    // Recupera lo storico dei ricoveri per un letto e reparto specifici
    List<ArrayList<String>> getStoricoRicoveriByLetto(String idLetto, String reparto);

    boolean aggiornaLettoRicovero(String idRicovero, String nuovoLetto, String nuovoReparto);
}