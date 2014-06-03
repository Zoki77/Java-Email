/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.ztintor.web;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Address;
import org.foi.nwtis.ztintor.konfiguracije.bp.BP_Konfiguracija;
import javax.mail.AuthenticationFailedException;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.FolderClosedException;
import javax.mail.FolderNotFoundException;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Part;
import javax.mail.ReadOnlyFolderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.StoreClosedException;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.foi.nwtis.ztintor.konfiguracije.Konfiguracija;

/**
 * Pozadinska dretva koja provjerava na poslužitelj u pravilnom intervalu ima li
 * poruka u poštanskom sandučiću korisnika.
 *
 * @author zoran
 */
public class ObradaPoruka extends Thread {

    private BP_Konfiguracija bpKonfig;
    private Konfiguracija konfig;
    private String emailPosluzitelj;
    private String emailKorisnik;
    private String emailLozinka;
    private String emailPort;
    private String emailPredmet;
    private int interval;
    public int sveukupnoPoruka;
    public int ukupnoPoruka;
    public int ispravnoPoruka;
    public int neispravnoPoruka;
    public int ostaloPoruka;
    private String nazivIspravnogDirektorija;
    private String nazivNeispravnogDirektorija;
    private String nazivOstalogDirektorija;
    private Date date1;
    private Date date2;
    private SimpleDateFormat sdf;
    private long pocetak;
    private long razlika;
    private Session sesija;

    public ObradaPoruka() {
    }

    //Responsible for printing Data to Console
    private void printData(String data) {
        System.out.println(data);
    }

    public Session processMail() {
        Session session = null;
        Store store = null;
        Folder folder = null;
        Message message = null;
        Message[] messages = null;
        Object messagecontentObject = null;
        String sender = null;
        String subject = null;
        Multipart multipart = null;
        Part part = null;
        String contentType = null;

        try {
            printData("--------------processing mails started-----------------");
            session = Session.getDefaultInstance(System.getProperties(), null);

            printData("getting the session for accessing email.");
            store = session.getStore("imap");

            store.connect(emailPosluzitelj, emailKorisnik, emailLozinka);
            printData("Connection established with IMAP server.");

            // Get a handle on the default folder
            folder = store.getDefaultFolder();

            printData("Getting the Inbox folder.");

            printData("----Lista foldera:");
            for (Folder f : folder.list()) {
                printData(f.getName());
            }

            // Retrieve the "Inbox"
            folder = folder.getFolder("inbox");

            //Reading the Email Index in Read / Write Mode
            folder.open(Folder.READ_WRITE);

            // Retrieve the messages
            messages = folder.getMessages();

            // Loop over all of the messages
            for (int messageNumber = 0; messageNumber < messages.length; messageNumber++) {

                sveukupnoPoruka++;
                ukupnoPoruka++;

                // Retrieve the next message to be read
                message = messages[messageNumber];

                String vrstaPoruke = message.getContentType();

                if (vrstaPoruke.equals("TEXT/PLAIN; charset=us-ascii")) {

                    if (ispravnostPoruke(message)) {
                        ispravnoPoruka++;
                        premjestanjePoruke(nazivIspravnogDirektorija, store, message, folder);
                    } else {
                        neispravnoPoruka++;
                        premjestanjePoruke(nazivNeispravnogDirektorija, store, message, folder);
                    }
                } else {
                    ostaloPoruka++;
                    premjestanjePoruke(nazivOstalogDirektorija, store, message, folder);
                }
            }

            // Close the folder
            folder.close(true);

            // Close the message store
            store.close();

        } catch (AuthenticationFailedException e) {
            printData("Not able to process the mail reading.");
            e.printStackTrace();
        } catch (FolderClosedException e) {
            printData("Not able to process the mail reading.");
            e.printStackTrace();
        } catch (FolderNotFoundException e) {
            printData("Not able to process the mail reading.");
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            printData("Not able to process the mail reading.");
            e.printStackTrace();
        } catch (ReadOnlyFolderException e) {
            printData("Not able to process the mail reading.");
            e.printStackTrace();
        } catch (StoreClosedException e) {
            printData("Not able to process the mail reading.");
            e.printStackTrace();
        } catch (Exception e) {
            printData("Not able to process the mail reading.");
            e.printStackTrace();
        }
        return session;
    }

    @Override
    public synchronized void start() {
        emailPosluzitelj = konfig.dajPostavku("emailPosluziteljDretva");
        emailKorisnik = konfig.dajPostavku("emailKorisnikDretva");
        emailLozinka = konfig.dajPostavku("emailLozinkaDretva");
        emailPort = konfig.dajPostavku("emailPort");
        emailPredmet = konfig.dajPostavku("emailPredmet");
        interval = Integer.parseInt(konfig.dajPostavku("interval"));
        nazivIspravnogDirektorija = konfig.dajPostavku("ispravnePoruke");
        nazivNeispravnogDirektorija = konfig.dajPostavku("neispravnePoruke");
        nazivOstalogDirektorija = konfig.dajPostavku("ostalePoruke");
        super.start();
    }

    @Override
    public void run() {
        sveukupnoPoruka = 0;
        ukupnoPoruka = 0;
        ispravnoPoruka = 0;
        neispravnoPoruka = 0;
        ostaloPoruka = 0;
        while (true) {
            pocetak = new Date().getTime();
            ukupnoPoruka = 0;
            ispravnoPoruka = 0;
            neispravnoPoruka = 0;
            ostaloPoruka = 0;
            date1 = new Date();
            sesija = processMail();
            date2 = new Date();
            try {
                statistika(sesija, date1, date2);
            } catch (MessagingException ex) {
                System.out.println("Problem kod slanja statistike");
            }

            try {
                razlika = new Date().getTime() - pocetak;
                sleep((interval * 1000) - razlika);
            } catch (InterruptedException ex) {
                //swallow
            }
        }
    }

    @Override
    public void interrupt() {
        super.interrupt();
    }

    public BP_Konfiguracija getBpKonfig() {
        return bpKonfig;
    }

    public void setBpKonfig(BP_Konfiguracija bpKonfig) {
        this.bpKonfig = bpKonfig;
    }

    public Konfiguracija getKonfig() {
        return konfig;
    }

    public void setKonfig(Konfiguracija konfig) {
        this.konfig = konfig;
    }

    /**
     * Funkcija koja provjerava ispravnost poruke prema određenim zahtjevima
     * ispravnosti
     *
     * @param message
     * @return
     */
    private boolean ispravnostPoruke(Message message) {
        int count1 = 0;
        int count2 = 0;
        int count3 = 0;
        int br1 = 0;
        int br2 = 0;
        String naslov = konfig.dajPostavku("predmet");
        String user = "";
        String pass = "";
        try {
            if (message.getSubject().startsWith(naslov)) {

                String msg = (String) message.getContent();
                String kljucneRijeci[] = msg.split("\n| ");
                for (int i = 0; i < kljucneRijeci.length; i++) {

                    String izraz = kljucneRijeci[i].trim();
                    if (br1 == 1) {
                        user = izraz;
                        br1 = 0;
                    } else if (br2 == 1) {
                        pass = izraz;
                        br2 = 0;
                    }
                    if (izraz.compareTo("USER") != 0) {
                        count1++;
                        br1++;
                    }
                    if (izraz.compareTo("PASSWORD") != 0) {
                        count2++;
                        br2++;
                    }
                    if (izraz.compareTo("GALLERY") != 0) {
                        count3++;
                    }

                }
                if (count1 > 0 && count2 > 0 && count3 > 0) {
                    String baza = bpKonfig.getServer_database() + bpKonfig.getUser_database();
                    String bazaKorisnik = bpKonfig.getUser_username();
                    String bazaLozinka = bpKonfig.getUser_password();
                    Connection connection = null;
                    PreparedStatement ps = null;
                    ResultSet rs = null;

                    try {
                        connection = DriverManager.getConnection(baza, bazaKorisnik, bazaLozinka);
                        String upit = "SELECT kor_ime FROM polaznici WHERE kor_ime=" + user + " AND lozinka=" + pass;
                        ps = connection.prepareStatement(upit);
                        rs = ps.executeQuery();

                        if (rs.next()) {
                            return true;
                        } else {
                            return false;
                        }

                    } catch (SQLException e) {
                        e.printStackTrace();
                        return false;

                    }

                } else {
                    return false;
                }
            } else {
                return false;
            }
        } catch (MessagingException ex) {
            Logger.getLogger(ObradaPoruka.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } catch (IOException ex) {
            Logger.getLogger(ObradaPoruka.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        //TODO provjeriti ostale elemente (predmet, komande)


    }

    /**
     * Funkcija za premještanje poruke u željeni direktorij
     *
     * @param nazivNovogDirektorija
     * @param store
     * @param message
     * @param folder
     * @throws MessagingException
     */
    private void premjestanjePoruke(String nazivNovogDirektorija, Store store, Message message, Folder folder) throws MessagingException {

        Folder noviFolder = store.getFolder(nazivNovogDirektorija);
        if (!noviFolder.exists()) {
            noviFolder.create(Folder.HOLDS_MESSAGES);
        }
        noviFolder.open(folder.READ_WRITE);
        Message[] zaKopiranje = new Message[1];
        zaKopiranje[0] = message;
        folder.copyMessages(zaKopiranje, noviFolder);
        noviFolder.close(false);
        message.setFlag(Flags.Flag.DELETED, true);
        System.out.println("Poruka je premjštena u mapu: " + noviFolder);

    }

    /**
     * Funkcija za obradu i slanje poruke statistike
     *
     * @param mail_session
     * @param date1
     * @param date2
     * @throws AddressException
     * @throws MessagingException
     */
    private void statistika(Session mail_session, Date date1, Date date2) throws AddressException, MessagingException {
        // Construct the message
        MimeMessage message = new MimeMessage(mail_session);
        // Set the from address
        Address fromAddress;

        fromAddress = new InternetAddress(this.emailKorisnik);
        message.setFrom(fromAddress);
        // Parse and set the recipient addresses
        Address[] toAddresses = InternetAddress.parse(konfig.dajPostavku("primateljStatistike"));
        message.setRecipients(Message.RecipientType.TO, toAddresses);
        // Set the subject and text
        message.setSubject(konfig.dajPostavku("predmetStatistike"));
        message.setSentDate(new Date());

        sdf = new SimpleDateFormat("dd.MMM.yyyy hh:mm:ss:zzz");

        String sadrzajPoruke = "<h1>Statistika</h1><br>"
                + "Obrada započela u: " + sdf.format(date1) + "<br>"
                + "Obrada završila u: " + sdf.format(date2) + "<br>"
                + "Trajanje obrade u ms: " + (int) (date2.getTime() - date1.getTime()) + "<br>"
                + "Sveukupan broj poruka: " + sveukupnoPoruka + "<br>"
                + "Ukupan broj poruka: " + ukupnoPoruka + "<br>"
                + "Broj ispravnih poruka: " + ispravnoPoruka + "<br>"
                + "Broj neispravnih poruka: " + neispravnoPoruka + "<br>"
                + "Broj ostalih poruka: " + ostaloPoruka + "<br>"
                + "Broj preuzetih datoteka: <br>";
        message.setContent(sadrzajPoruke, "text/html;charset=\"UTF-8\"");
        Transport.send(message);

        // get Session object's store
        Store storeStatistika = mail_session.getStore("imap");
        // connect to store
        storeStatistika.connect(emailPosluzitelj, emailKorisnik, emailLozinka);
        // obtain reference to "Sent" folder
        Folder f = storeStatistika.getFolder(konfig.dajPostavku("folderStatistike"));
        // create "Sent" folder if it does not exist
        if (!f.exists()) {
            f.create(Folder.HOLDS_MESSAGES);
        }
        // add message to "Sent" folder
        f.appendMessages(new Message[]{message});

    }
}