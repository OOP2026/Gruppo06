package dao;

import java.util.ArrayList;
import java.util.List;

public interface RicoveroDAO {
    boolean aggiungiRicovero(String cfPaziente, String idLetto, String dataInizio, String motivo);
    boolean aggiornaRicoveroDimissione(String idRicovero, String dataFine, String prognosi, String esito);
    ArrayList<String> getRicoveroAttivo(String cfPaziente);
    ArrayList<ArrayList<String>> getStoricoRicoveri(String cfPaziente);
    ArrayList<ArrayList<String>> getAllDimissioni();
    ArrayList<String> getUltimoRicoveroChiuso(String cfPaziente);
}