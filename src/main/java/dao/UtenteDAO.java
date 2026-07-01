package dao;

import model.Utente;

public interface UtenteDAO {
    boolean checkLoginEsistente(String login);
    boolean aggiungiUtente(Utente utente, String pin);
    Utente getUtenteByLoginAndPassword(String login, String password);
}