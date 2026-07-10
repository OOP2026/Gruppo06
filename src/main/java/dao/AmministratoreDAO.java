package dao;

import java.util.ArrayList;
 
public interface AmministratoreDAO {
    boolean checkLoginEsistente(String login);
    boolean aggiungiAmministratore(String matricola, String login, String password, String nome, String cognome, String pin);
    boolean aggiornaAmministratore(String matricola, String nome, String cognome);
    ArrayList<String> getAmministratoreByLoginAndPassword(String login, String password);
}