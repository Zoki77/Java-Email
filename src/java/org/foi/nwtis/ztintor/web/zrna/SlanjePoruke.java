/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.ztintor.web.zrna;

import java.util.Date;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 *Bean za slanje poruke.
 * @author zoran
 */
@ManagedBean
//@RequestScoped
@SessionScoped
public class SlanjePoruke {

    private String emailPosluzitelj;
    private String emailKorisnik;
    private String primaPoruku;
    private String saljePoruku;
    private String predmetPoruke;
    private String sadrzajPoruke;
    private Boolean uspjesnoPoslano;

    public SlanjePoruke() {
    }

    public String getPrimaPoruku() {
        return primaPoruku;
    }

    public void setPrimaPoruku(String primaPoruku) {
        this.primaPoruku = primaPoruku;
    }

    public String getSaljePoruku() {
        EmailPovezivanje ep = (EmailPovezivanje) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("emailPovezivanje");
        this.saljePoruku = ep.getEmailKorisnik();
        return saljePoruku;
    }

    public void setSaljePoruku(String saljePoruku) {
        this.saljePoruku = saljePoruku;
    }

    public String getPredmetPoruke() {
        return predmetPoruke;
    }

    public void setPredmetPoruke(String predmetPoruke) {
        this.predmetPoruke = predmetPoruke;
    }

    public String getSadrzajPoruke() {
        return sadrzajPoruke;
    }

    public void setSadrzajPoruke(String sadrzajPoruke) {
        this.sadrzajPoruke = sadrzajPoruke;
    }

    public Boolean getUspjesnoPoslano() {
        return uspjesnoPoslano;
    }

    public void setUspjesnoPoslano(Boolean uspjesnoPoslano) {
        this.uspjesnoPoslano = uspjesnoPoslano;
    }
    
    public void promjeniPocetniPrikazSlanja(){
        this.uspjesnoPoslano = false;
    }
    
    /**
     * Funkcija za slanje poruke koja se poziva pritiskom na gumb unutar prikaza slanja poruka.
     * @return 
     */

    public String saljiPoruku() {
        EmailPovezivanje ep = (EmailPovezivanje) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("emailPovezivanje");
        this.emailPosluzitelj = ep.getEmailPosluzitelj();

        try {
            // Create the JavaMail session
            java.util.Properties properties = System.getProperties();
            properties.put("mail.smtp.host", this.emailPosluzitelj);

            Session session =
                    Session.getInstance(properties, null);

            // Construct the message
            MimeMessage message = new MimeMessage(session);
            // Set the from address
            Address fromAddress = new InternetAddress(this.saljePoruku);
            message.setFrom(fromAddress);
            // Parse and set the recipient addresses
            Address[] toAddresses = InternetAddress.parse(this.primaPoruku);
            message.setRecipients(Message.RecipientType.TO, toAddresses);
            // Set the subject and text
            message.setSubject(this.predmetPoruke);
            message.setText(this.sadrzajPoruke);
            message.setSentDate(new Date());

            Transport.send(message);
            this.uspjesnoPoslano = true;
            this.primaPoruku = null;
            this.predmetPoruke = null;
            this.sadrzajPoruke = null;
            System.out.println("Slanje poruke uspješno");

        } catch (AddressException e) {
            e.printStackTrace();
        } catch (SendFailedException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();

        }
        return null;
    }
}
