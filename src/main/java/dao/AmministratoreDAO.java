package dao;

import java.util.ArrayList;
 
public interface AmministratoreDAO {
    boolean checkLoginEsistente(String login);
    boolean aggiungiAmministratore(String matricola, String login, String password, String nome, String cognome, String pin);
    ArrayList<String> getAmministratoreByLoginAndPassword(String login, String password);
}