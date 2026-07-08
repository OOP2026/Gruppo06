package dao;

import java.util.ArrayList;

public interface PrestazioneDAO {
    boolean aggiungiPrestazione(int idPrestazione, String tipologiaPrestazione, String esitoPrestazione, String idTurno, String cfPaziente, String matricolaMedico, String idAgenda);
    ArrayList<ArrayList<String>> getAllPrestazioni();
}