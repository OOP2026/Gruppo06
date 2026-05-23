package model;

import java.time.LocalDate;
import java.time.Period;
import java.util.Date;
import java.util.Scanner;

public class Amministratore extends Utente {
    //Richiamo classi
    protected String nome;
    protected String cognome;
    protected String pin;

    public Amministratore(String login, String password, String matricola, String nome, String cognome) {
        super(login, password, matricola);
        this.nome = nome;
        this.cognome = cognome;
        this.pin = pin;
    }


    public String getMatricola() {
        return this.matricola;
    }

    public void anagraficaPaziente(){

        Scanner scanner = new Scanner(System.in);
        System.out.println("--Anagrafica Paziente--");

        System.out.println("Sesso");
        char sesso = scanner.nextLine().charAt(0);
        System.out.println("Nome: ");
        String nome = scanner.nextLine();
        System.out.println("Cognome: ");
        String cognome = scanner.nextLine();
        System.out.println("Data di nascita (dd/MM/yyyy): ");
        String dataNascita = scanner.nextLine();

        //Calcolo età nascita
        LocalDate dataDiNascita = LocalDate.parse(dataNascita, java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        LocalDate oggi = LocalDate.now();
        int eta = Period.between(dataDiNascita, oggi).getYears();
        System.out.println("Età: " + eta);

        System.out.println("Codice Fiscale: ");
        String codiceFiscale = scanner.nextLine();
        System.out.println("Indirizzo: ");
        String indirizzo = scanner.nextLine();

    }

    public void assegnaLetto(){
        //
    }

    public Date setDataOraInizio(){
        return new Date();
    }

    public void registraRicovero(){
        System.out.println();
    }

    public String calcolaPrognosi(){
        return "Il paziente ha una prognosi di 10 giorni";
    }

    public String dimissioni(){
        return "Il paziente è stato dimesso";
    }


    public void checkDisponibilitaLetto(){ //Metodo non piu in uso
    }

    public void ricercaDimissioni(){
        //
    }

    public void setAssenza(){
        //
    }

    public void checkPeriodoAssenza(){
        //
    }

    public void assegnaTurno(){
        //
    }
}
