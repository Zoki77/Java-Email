/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.ztintor.web.zrna;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
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
 *Bean za log in.
 * @author zoran
 */
@ManagedBean
@SessionScoped
public class EmailPovezivanje{

    private Konfiguracija konfig;
    private String emailPosluzitelj;
    private String emailKorisnik;
    private String emailLozinka;
    private Boolean uspjesnostAutentifikacije;

    /**
     * Creates a new instance of EmailPovezivanje
     */
    public EmailPovezivanje() {
    }
/**
 * Funkcija koja se poziva pritiskom na određeni gumb 
 * i preusmjerava na temelju povratne vrijednosti na željenu stranicu.
 * @return 
 */
    public String saljiPoruku() {
        return "OK";

    }

/**
 * Funkcija koja se poziva pritiskom na određeni gumb 
 * i preusmjerava na temelju povratne vrijednosti na željenu stranicu.
 * Provjerava da li su uneseni podaci u log in-u ispravni.
 * 
 * @return 
 */
    public Boolean citajPoruke() {
        Session session = null;
        Store store = null;
        Folder folder = null;


        
            
            session = Session.getDefaultInstance(System.getProperties(), null);
        try {
            
            store = session.getStore("imap");
            store.connect(this.getEmailPosluzitelj(), this.getEmailKorisnik(), this.getEmailLozinka());
            uspjesnostAutentifikacije=true;
            return uspjesnostAutentifikacije;
            
        } catch (AuthenticationFailedException e) {
            uspjesnostAutentifikacije = false;            
            e.printStackTrace();
            return uspjesnostAutentifikacije;
        } catch (FolderClosedException e) {
            uspjesnostAutentifikacije = false; 
            e.printStackTrace();
            return uspjesnostAutentifikacije;
        } catch (FolderNotFoundException e) {
            uspjesnostAutentifikacije = false; 
            e.printStackTrace();
            return uspjesnostAutentifikacije;
        } catch (NoSuchProviderException e) {
            uspjesnostAutentifikacije = false; 
            e.printStackTrace();
            return uspjesnostAutentifikacije;
        } catch (ReadOnlyFolderException e) {
            uspjesnostAutentifikacije = false; 
            e.printStackTrace();
            return uspjesnostAutentifikacije;
        } catch (StoreClosedException e) {
            uspjesnostAutentifikacije = false; 
            e.printStackTrace();
            return uspjesnostAutentifikacije;
        } catch (Exception e) {
            uspjesnostAutentifikacije = false; 
            e.printStackTrace();
            return uspjesnostAutentifikacije;
        }

                    
    }

    public String getEmailPosluzitelj() {
        return emailPosluzitelj;
    }

    public void setEmailPosluzitelj(String emailPosluzitelj) {
        this.emailPosluzitelj = emailPosluzitelj;
    }

    public String getEmailKorisnik() {
        return emailKorisnik;
    }

    public void setEmailKorisnik(String emailKorisnik) {
        this.emailKorisnik = emailKorisnik;
    }

    public String getEmailLozinka() {
        return emailLozinka;
    }

    public void setEmailLozinka(String emailLozinka) {
        this.emailLozinka = emailLozinka;
    }

    public Boolean getUspjesnostAutentifikacije() {
        return uspjesnostAutentifikacije;
    }

    public void setUspjesnostAutentifikacije(Boolean uspjesnostAutentifikacije) {
        this.uspjesnostAutentifikacije = uspjesnostAutentifikacije;
    }

    
    /**
     * dohvaćanje podataka iz konfiguracije za prvi prikaz
     */

    public void uzmiIzKonfiguracije() {
       konfig = SlusacAplikacije.getConf();
       emailPosluzitelj = konfig.dajPostavku("emailPosluziteljLogin");
       emailKorisnik =  konfig.dajPostavku("emailKorisnikLogin");

    }

}
