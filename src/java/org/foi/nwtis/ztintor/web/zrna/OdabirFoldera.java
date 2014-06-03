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
import javax.mail.NoSuchProviderException;
import javax.mail.ReadOnlyFolderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.StoreClosedException;
import org.foi.nwtis.ztintor.konfiguracije.Konfiguracija;
import org.foi.nwtis.ztintor.web.slusaci.SlusacAplikacije;

/**
 *Bean za odabir željenog foldera.
 * @author zoran
 */
@ManagedBean
@SessionScoped
public class OdabirFoldera {

    private String emailPosluzitelj;
    private String emailKorisnik;
    private String emailLozinka;
    private List<String> fld = new ArrayList<String>();
    private String odabraniFolder = "inbox";
    private int pocetak;
    private int kraj;
    private int broj;
    private Konfiguracija konfig;

    /**
     * Creates a new instance of OdabirFoldera
     */
    public OdabirFoldera() {
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

    public List<String> getFld() {
        return fld;
    }

    public void setFld(List<String> fld) {
        this.fld = fld;
    }

    public String getOdabraniFolder() {
        return odabraniFolder;
    }

    public void setOdabraniFolder(String odabraniFolder) {
        this.odabraniFolder = odabraniFolder;
    }

    public int getPocetak() {
        return pocetak;
    }

    public void setPocetak(int pocetak) {
        this.pocetak = pocetak;
    }

    public int getKraj() {
        return kraj;
    }

    public void setKraj(int kraj) {
        this.kraj = kraj;
    }

    
    /**
 * Funkcija koja se poziva pritiskom na određeni gumb 
 * i preusmjerava na temelju povratne vrijednosti na željenu stranicu.
 * @return 
 */
    public String saljiOdabir() {
        PregledSvihPoruka.promjeniPocetnoStanje();
        return "OK";

    }

    public void dohvatiZaIspis() {
        dohvatiPoruke();
    }

    //Responsible for printing Data to Console
    private void printData(String data) {
        System.out.println(data);
    }

    private void dohvatiPoruke() {
        Session session = null;
        Store store = null;
        Folder folder = null;


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

            printData("************Lista foldera*****************:");
            for (Folder f : folder.list()) {
                fld.add(f.getName());
                printData(f.getName());
            }

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
