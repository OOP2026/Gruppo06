package dao;

import java.util.ArrayList;

public interface RicoveroDAO {
    boolean aggiungiRicovero(String cfPaziente, String idLetto, String dataInizio, String motivo);
    boolean aggiornaRicoveroDimissione(String idRicovero, String dataFine, String prognosi, String esito);
    ArrayList<String> getRicoveroAttivo(String cfPaziente);
    ArrayList<ArrayList<String>> getStoricoRicoveri(String cfPaziente);
    ArrayList<ArrayList<String>> getAllDimissioni();
}