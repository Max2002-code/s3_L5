package org.example;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.time.LocalDate;

// Enum per rappresentare la periodicità delle riviste
enum Periodicity {
    SETTIMANALE,
    MENSILE,
    MONTHLY,
    SEMESTRALE
}

// Classe per rappresentare un elemento del catalogo bibliotecario
abstract class CatalogItem implements Serializable {
    private String isbn;
    private String title;
    int publicationYear;

    // Costruttore
    public CatalogItem(String isbn, String title, int publicationYear) {
        this.isbn = isbn;
        this.title = title;
        this.publicationYear = publicationYear;
    }

    // Metodi getter
    public String getIsbn() {
        return isbn;
    }

    public String getTitle() {
        return title;
    }

    public int getPublicationYear() {
        return publicationYear;
    }
}

// Classe per rappresentare una rivista nel catalogo
class Magazine extends CatalogItem {
    private int issueNumber;
    private Periodicity periodicity;

    // Costruttore
    public Magazine(String isbn, String title, int publicationYear, int issueNumber, Periodicity periodicity) {
        super(isbn, title, publicationYear);
        this.issueNumber = issueNumber;
        this.periodicity = periodicity;
    }

    // Metodo getter per il numero dell'edizione
    public int getIssueNumber() {
        return issueNumber;
    }

    // Metodo getter per la periodicità
    public Periodicity getPeriodicity() {
        return periodicity;
    }
}

// Classe per rappresentare un libro nel catalogo
class Book extends CatalogItem {
    private String author;
    private String genre;
    private int numPages;

    // Costruttore
    public Book(String isbn, String title, int publicationYear, String author, String genre, int numPages) {
        super(isbn, title, publicationYear);
        this.author = author;
        this.genre = genre;
        this.numPages = numPages;
    }

    // Metodi getter per autore, genere e numero di pagine
    public String getAuthor() {
        return author;
    }

    public String getGenre() {
        return genre;
    }

    public int getNumPages() {
        return numPages;
    }
}

// Classe per gestire l'archivio della biblioteca
class LibraryArchive {
    private List<CatalogItem> catalog;

    public LibraryArchive() {
        this.catalog = new ArrayList<>();
    }

    // Aggiunta di un elemento all'archivio
    public void addItem(CatalogItem item) {
        catalog.add(item);
    }

    // Rimozione di un elemento dato un codice ISBN
    public void removeItemByISBN(String isbn) {
        catalog.removeIf(item -> item.getIsbn().equals(isbn));
    }

    // Ricerca per ISBN
    public Optional<CatalogItem> findByISBN(String isbn) {
        return catalog.stream()
                .filter(item -> item.getIsbn().equals(isbn))
                .findFirst();
    }

    // Ricerca per anno di pubblicazione
    public List<CatalogItem> findByPublicationYear(int year) {
        return catalog.stream()
                .filter(item -> item.getPublicationYear() == year)
                .collect(Collectors.toList());
    }

    // Ricerca per autore
    public List<Book> findByAuthor(String author) {
        return catalog.stream()
                .filter(item -> item instanceof Book)
                .map(item -> (Book) item)
                .filter(book -> book.getAuthor().equals(author))
                .collect(Collectors.toList());
    }

    // Salvataggio su disco dell'archivio
    public void saveToDisk(String filename) {
        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(filename))) {
            outputStream.writeObject(catalog);
        } catch (IOException e) {
            System.err.println("Errore durante il salvataggio su disco: " + e.getMessage());
        }
    }

    // Caricamento dal disco dell'archivio in una nuova lista
    public void loadFromDisk(String filename) {
        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(filename))) {
            catalog = (List<CatalogItem>) inputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Errore durante il caricamento dal disco: " + e.getMessage());
        }
    }

    // Metodo per ottenere l'elenco completo degli elementi nell'archivio
    public List<CatalogItem> getItems() {
        return catalog;
    }
}

class Utente {
    private String nome;
    private String cognome;
    private LocalDate dataDiNascita;
    private String numeroTessera;

    public Utente(String nome, String cognome, LocalDate dataDiNascita, String numeroTessera) {
        this.nome = nome;
        this.cognome = cognome;
        this.dataDiNascita = dataDiNascita;
        this.numeroTessera = numeroTessera;
    }

    // Metodi getter
    public String getNome() {
        return nome;
    }

    public String getCognome() {
        return cognome;
    }

    public LocalDate getDataDiNascita() {
        return dataDiNascita;
    }

    public String getNumeroTessera() {
        return numeroTessera;
    }
}

abstract class ElementoPrestato {
    protected String titolo;

    public ElementoPrestato(String titolo) {
        this.titolo = titolo;
    }

    // Metodi getter
    public String getTitolo() {
        return titolo;
    }

    // Metodi setter
    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }

    @Override
    public String toString() {
        return titolo;
    }
}

class Prestito {
    private Utente utente;
    private Book elementoPrestato;
    private LocalDate dataInizioPrestito;
    private LocalDate dataRestituzionePrevista;
    private LocalDate dataRestituzioneEffettiva;

    public Prestito(Utente utente, Book elementoPrestato, LocalDate dataInizioPrestito) {
        this.utente = utente;
        this.elementoPrestato = elementoPrestato;
        this.dataInizioPrestito = dataInizioPrestito;
        this.dataRestituzionePrevista = dataInizioPrestito.plusDays(30);
    }

    // Metodi getter
    public Utente getUtente() {
        return utente;
    }

    public Book getElementoPrestato() {
        return elementoPrestato;
    }

    public LocalDate getDataInizioPrestito() {
        return dataInizioPrestito;
    }

    public LocalDate getDataRestituzionePrevista() {
        return dataRestituzionePrevista;
    }

    public LocalDate getDataRestituzioneEffettiva() {
        return dataRestituzioneEffettiva;
    }
    public boolean isScaduto() {
        return LocalDate.now().isAfter(dataRestituzionePrevista);
    }
}

class ArchivioPrestiti {
    private List<Prestito> prestiti;

    public ArchivioPrestiti() {
        this.prestiti = new ArrayList<>();
    }

    public void aggiungiPrestito(Prestito prestito) {
        prestiti.add(prestito);
    }

    public List<Prestito> ricercaPrestitiUtente(String numeroTessera) {
        List<Prestito> prestitiUtente = new ArrayList<>();
        for (Prestito prestito : prestiti) {
            if (prestito.getUtente().getNumeroTessera().equals(numeroTessera)) {
                prestitiUtente.add(prestito);
            }
        }
        return prestitiUtente;
    }

    public List<Prestito> ricercaPrestitiScaduti() {
        List<Prestito> prestitiScaduti = new ArrayList<>();
        for (Prestito prestito : prestiti) {
            if (prestito.isScaduto() && prestito.getDataRestituzioneEffettiva() == null) {
                prestitiScaduti.add(prestito);
            }
        }
        return prestitiScaduti;
    }
}

// Classe principale per testare le funzionalità dell'archivio
public class Main {
    public static void main(String[] args) {
        int publicationYear = 1954;

        // Creazione di un archivio
        LibraryArchive archive = new LibraryArchive();

        // Aggiunta di un libro e una rivista all'archivio
        Book book = new Book("978-3-16-148410-0", "Il Signore degli Anelli", publicationYear, "J.R.R. Tolkien", "Fantasy", 1178);
        Magazine magazine = new Magazine("123-456-789", "National Geographic", 2024, 4, Periodicity.MONTHLY);
        Book book2 = new Book("975-3-15-148410-0", "Il Signore degli Anelli 2", publicationYear, "J.R.R. Tolkien", "Fantasy", 800);
        archive.addItem(book);
        archive.addItem(magazine);
        archive.addItem(book2);

        // Esempio di utilizzo delle funzionalità dell'archivio
        Optional<CatalogItem> foundItem = archive.findByISBN("978-3-16-148410-0");
        if (foundItem.isPresent()) {
            System.out.println("Book: " + foundItem.get().getTitle());
        } else {
            System.out.println("Nessun elemento trovato con ISBN 978-3-16-148410-0");
        }

        foundItem = archive.findByISBN("975-3-15-148410-0");
        if (foundItem.isPresent()) {
            System.out.println("Book: " + foundItem.get().getTitle());
        } else {
            System.out.println("Nessun elemento trovato con ISBN 978-3-16-148410-0");
        }

        foundItem = archive.findByISBN("123-456-789");
        if (foundItem.isPresent()) {
            System.out.println("Magazine: " + foundItem.get().getTitle());
        } else {
            System.out.println("Nessun elemento trovato con ISBN 978-3-16-148410-0");
        }

        List<CatalogItem> itemsPublishedIn1954 = archive.findByPublicationYear(1954);
        System.out.println("Elementi pubblicati nel 1954: " + itemsPublishedIn1954.size());

        List<Book> tolkienBooks = archive.findByAuthor("J.R.R. Tolkien");
        System.out.println("Libri di J.R.R. Tolkien: " + tolkienBooks.size());

        // Salvataggio e caricamento dell'archivio su disco
        archive.saveToDisk("archive.dat");
        LibraryArchive loadedArchive = new LibraryArchive();
        loadedArchive.loadFromDisk("archive.dat");

        // Verifica se il caricamento ha funzionato
        List<CatalogItem> loadedItems = loadedArchive.getItems();
        System.out.println("Numero di elementi nell'archivio caricato: " + loadedItems.size());

        // Creazione di alcuni utenti, elementi prestati e prestiti
        Utente utente = new Utente("Mario", "Rossi", LocalDate.of(1990, 5, 15), "0001");
        Book book3 = new Book("Il signore degli anelli", "J.R.R. Tolkien", publicationYear, "J.R.R. Tolkien", "Fantasy", 1178);
        Prestito prestito = new Prestito(utente, book3, LocalDate.of(2024, 4, 1));

        // Aggiunta del prestito all'archivio
        ArchivioPrestiti archivioPrestiti = new ArchivioPrestiti();
        archivioPrestiti.aggiungiPrestito(prestito);

        // Stampa dei dettagli del prestito
        System.out.println("\nDettagli del prestito:");
        System.out.println("- Utente: " + prestito.getUtente().getNome() + " " + prestito.getUtente().getCognome());
        System.out.println("- Elemento prestato: " + prestito.getElementoPrestato().getTitle());
        System.out.println("- Data inizio prestito: " + prestito.getDataInizioPrestito());
        System.out.println("- Data restituzione prevista: " + prestito.getDataRestituzionePrevista());

        // Ricerca degli elementi in prestito per l'utente specifico
        String numeroTesseraUtente = "0001";
        System.out.println("\nPrestiti per l'utente con numero tessera " + numeroTesseraUtente + ":");
        for (Prestito p : archivioPrestiti.ricercaPrestitiUtente(numeroTesseraUtente)) {
            System.out.println("- " + p.getElementoPrestato().getTitle() + ", inizio prestito: " + p.getDataInizioPrestito());
        }

        // Ricerca dei prestiti scaduti e non ancora restituiti
        System.out.println("\nPrestiti scaduti e non ancora restituiti:");
        for (Prestito p : archivioPrestiti.ricercaPrestitiScaduti()) {
            System.out.println("- " + p.getElementoPrestato().getTitle() + ", data restituzione prevista: " + p.getDataRestituzionePrevista());
        }
    }
}