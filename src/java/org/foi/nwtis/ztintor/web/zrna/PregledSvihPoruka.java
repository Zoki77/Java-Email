/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.ztintor.web.zrna;

import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.mail.AuthenticationFailedException;
import javax.mail.Folder;
import javax.mail.FolderClosedException;
import javax.mail.FolderNotFoundException;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Part;
import javax.mail.ReadOnlyFolderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.StoreClosedException;
import javax.mail.internet.InternetAddress;
import org.foi.nwtis.ztintor.konfiguracije.Konfiguracija;
import org.foi.nwtis.ztintor.web.kontrole.Poruka;
import org.foi.nwtis.ztintor.web.kontrole.PrivitakPoruke;
import org.foi.nwtis.ztintor.web.slusaci.SlusacAplikacije;

/**
 *Bean za pregled svih poruka unutar odabranog foldera.
 * @author zoran
 */
@ManagedBean
@SessionScoped
public class PregledSvihPoruka {

    private String emailPosluzitelj;
    private String emailKorisnik;
    private String emailLozinka;
    private List<Poruka> poruke = new ArrayList<Poruka>();
    private Poruka odabranaPoruka;
    private String porukaID;
    private List<String> fld = new ArrayList<String>();
    private String odabraniFolder;
    private static int pocetak;
    private static int kraj;
    private Konfiguracija konfig;
    private static int broj;

    /**
     * Creates a new instance of PregledSvihPoruka
     */
    public PregledSvihPoruka() {
        konfig = SlusacAplikacije.getConf();
        broj = Integer.parseInt(konfig.dajPostavku("stranicenje"));

    }

    public String getEmailPosluzitelj() {
        EmailPovezivanje ep = (EmailPovezivanje) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("emailPovezivanje");
        this.emailPosluzitelj = ep.getEmailPosluzitelj();
        return emailPosluzitelj;
    }

    public void setEmailPosluzitelj(String emailPosluzitelj) {
        this.emailPosluzitelj = emailPosluzitelj;
    }

    public String getEmailKorisnik() {
        EmailPovezivanje ep = (EmailPovezivanje) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("emailPovezivanje");
        this.emailKorisnik = ep.getEmailKorisnik();
        return emailKorisnik;
    }

    public void setEmailKorisnik(String emailKorisnik) {
        this.emailKorisnik = emailKorisnik;
    }

    public String getEmailLozinka() {
        EmailPovezivanje ep = (EmailPovezivanje) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("emailPovezivanje");
        this.emailLozinka = ep.getEmailLozinka();
        return emailLozinka;
    }

    public void setEmailLozinka(String emailLozinka) {
        this.emailLozinka = emailLozinka;
    }

    public String getPorukaID() {
        return porukaID;
    }

    public void setPorukaID(String porukaID) {
        for (Poruka p : poruke) {
            if (p.getId().equals(porukaID)) {
                this.odabranaPoruka = p;
                break;
            }
        }
        this.porukaID = porukaID;
    }

    public String pregledPoruke() {
        return "OK";
    }

    public List<Poruka> getPoruke() {
        dohvatiPoruke();
        return poruke;
    }

    public void setPoruke(List<Poruka> poruke) {
        this.poruke = poruke;
    }

    public Poruka getOdabranaPoruka() {
        return odabranaPoruka;
    }

    public void setOdabranaPoruka(Poruka odabranaPoruka) {
        this.odabranaPoruka = odabranaPoruka;
    }

    public List<String> getFld() {
        return fld;
    }

    public void setFld(List<String> fld) {
        this.fld = fld;
    }

    public String getOdabraniFolder() {
        OdabirFoldera op = (OdabirFoldera) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("odabirFoldera");
        this.odabraniFolder = op.getOdabraniFolder();
        return odabraniFolder;
    }

    public void setOdabraniFolder(String odabraniFolder) {
        this.odabraniFolder = odabraniFolder;
    }

    
    
 /**
 * Funkcija koja se poziva pritiskom na određeni gumb 
 * i preusmjerava na temelju povratne vrijednosti na željenu stranicu.
 * Potrebna za straničenje i prikaz prethodne stranice.
 * @return 
 */

    public String prethodna() {
        if (pocetak > broj) {
            pocetak = pocetak - broj;
            kraj = kraj - broj;
            if ((kraj - pocetak) != (broj - 1)) {
                kraj = pocetak + (broj - 1);
            }
        }
        return "OK";
    }

 /**
 * Funkcija koja se poziva pritiskom na određeni gumb 
 * i preusmjerava na temelju povratne vrijednosti na željenu stranicu.
 * Potrebna za straničenje i prikaz sljedeće stranice.
 * @return 
 */    
    
    public String sljedeca() {
        pocetak = pocetak + broj;
        kraj = kraj + broj;
        return "OK";
    }

/**
* Funkcija za resetiranje početnog stanja argumenata pocetka i kraja pri povratku na odabir foldera. 
*/
    public static void promjeniPocetnoStanje() {
        pocetak = 1;
        kraj = broj;
    }
    //Responsible for printing Data to Console

    private void printData(String data) {
        System.out.println(data);
    }

    private void dohvatiPoruke() {
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

            store.connect(this.getEmailPosluzitelj(), this.getEmailKorisnik(), this.getEmailLozinka());

            printData("Connection established with IMAP server.");

            // Get a handle on the default folder
            folder = store.getDefaultFolder();

            printData("Getting the Inbox folder.");

            fld = new ArrayList<String>();

            printData("----Lista foldera:");
            for (Folder f : folder.list()) {
                fld.add(f.getName());
                printData(f.getName());
            }

            // Retrieve the "Inbox"
            folder = folder.getFolder(this.getOdabraniFolder());
            
            //Reading the Email Index in Read / Write Mode
            folder.open(Folder.READ_ONLY);

            // Retrieve the messages
            messages = folder.getMessages();
            if (messages.length < kraj) {
                kraj = messages.length;
            }
            messages = folder.getMessages(pocetak, kraj);

            poruke = new ArrayList<Poruka>();

            // Loop over all of the messages
            for (int messageNumber = 0; messageNumber < messages.length; messageNumber++) {
                // Retrieve the next message to be read
                message = messages[messageNumber];
                // Retrieve the message content
                messagecontentObject = message.getContent();

                List<PrivitakPoruke> pp = new ArrayList<PrivitakPoruke>();
                // Determine email type
                if (messagecontentObject instanceof Multipart) {
                    printData("Found Email with Attachment");
                    sender = ((InternetAddress) message.getFrom()[0]).getPersonal();

                    // If the "personal" information has no entry, check the address for the sender information
                    printData("If the personal information has no entry, check the address for the sender information.");

                    if (sender == null) {
                        sender = ((InternetAddress) message.getFrom()[0]).getAddress();
                        printData("sender in NULL. Printing Address:" + sender);
                    }
                    printData("Sender -." + sender);

                    // Get the subject information
                    subject = message.getSubject();

                    printData("subject=" + subject);

                    // Retrieve the Multipart object from the message
                    multipart = (Multipart) message.getContent();

                    printData("Retrieve the Multipart object from the message");

                    // Loop over the parts of the email
                    for (int i = 0; i < multipart.getCount(); i++) {
                        // Retrieve the next part
                        part = multipart.getBodyPart(i);

                        // Get the content type
                        contentType = part.getContentType();

                        // Display the content type
                        printData("Content: " + contentType);

                        String fileName = "";
                        if (contentType.startsWith("text/plain")) {
                            printData("---------reading content type text/plain  mail -------------");
                        } else {
                            // Retrieve the file name
                            fileName = part.getFileName();
                            printData("retrive the fileName=" + fileName);
                        }

                        PrivitakPoruke privitak = new PrivitakPoruke(i, contentType,
                                part.getSize(), fileName);
                        pp.add(privitak);

                    }
                } else {
                    printData("Found Mail Without Attachment");
                    sender = ((InternetAddress) message.getFrom()[0]).getPersonal();

                    // If the "personal" information has no entry, check the address for the sender information
                    printData("If the personal information has no entry, check the address for the sender information.");

                    if (sender == null) {
                        sender = ((InternetAddress) message.getFrom()[0]).getAddress();
                        printData("sender in NULL. Printing Address:" + sender);
                    }

                    // Get the subject information
                    subject = message.getSubject();
                    printData("subject=" + subject);
                }
                String messID = "";
                String[] zaglavlje = message.getHeader("Message-ID");
                if (zaglavlje != null && zaglavlje.length > 0) {
                    messID = zaglavlje[0];
                }

                Poruka poruka = new Poruka(messID, message.getSentDate(), sender, subject,
                        contentType, message.getSize(), pp.size(), message.getFlags(),
                        pp, true, true, message.getContent().toString(),message.getContentType());
                poruke.add(poruka);

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
    }
}
