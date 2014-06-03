/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.ztintor.web.zrna;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

/**
 *
 * @author zoran
 */
@ManagedBean(name = "lokalizacija")
@SessionScoped
public class Lokalizacija {

    private Map<String, Object> jezici;
    private String odabraniJezik;
    private Locale odabraniLocale;

    /**
     * Creates a new instance of Lokalizacija
     */
    public Lokalizacija() {
        odabraniLocale = FacesContext.getCurrentInstance()
                .getViewRoot().getLocale();
        jezici = new HashMap<String, Object>();
        jezici.put("English", Locale.ENGLISH);
        jezici.put("Deutsch", Locale.GERMAN);
        jezici.put("Hrvatski", new Locale("hr"));
    }

    /**
 * Funkcija koja se poziva pritiskom na određeni gumb 
 * i preusmjerava na temelju povratne vrijednosti na željenu stranicu.
 * Postavlja jezik. 
 * @return 
 */
    public Object odaberiJezik() {
        for (Map.Entry<String, Object> entry : jezici.entrySet()) {
            if (entry.getValue().toString().equals(odabraniJezik)) {
                FacesContext.getCurrentInstance()
                        .getViewRoot().setLocale((Locale) entry.getValue());
                odabraniLocale = (Locale) entry.getValue();
            }
        }
        if (odabraniJezik == null) {
            return "NOT_OK";
        } else {
            return "OK";
        }


    }

    public Map<String, Object> getJezici() {
        return jezici;
    }

    public void setJezici(Map<String, Object> jezici) {
        this.jezici = jezici;
    }

    public String getOdabraniJezik() {
        return odabraniJezik;
    }

    public void setOdabraniJezik(String odabraniJezik) {
        this.odabraniJezik = odabraniJezik;
    }

    public Locale getOdabraniLocale() {
        return odabraniLocale;
    }

    public void setOdabraniLocale(Locale odabraniLocale) {
        this.odabraniLocale = odabraniLocale;
    }
}
