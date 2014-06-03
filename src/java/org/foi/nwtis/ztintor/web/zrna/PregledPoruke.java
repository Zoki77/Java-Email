/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.ztintor.web.zrna;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import org.foi.nwtis.ztintor.web.kontrole.Poruka;

/**
 *Bean za pregled jedne poruke
 * @author zoran
 */
@ManagedBean
@RequestScoped
public class PregledPoruke {
    private Poruka poruka;

    /**
     * Creates a new instance of PregledPoruke
     */
    public PregledPoruke() {
    }

    public Poruka getPoruka() {
        PregledSvihPoruka psp = (PregledSvihPoruka)FacesContext
                .getCurrentInstance().getExternalContext()
                .getSessionMap().get("pregledSvihPoruka");
        poruka = psp.getOdabranaPoruka();

        return poruka;
    }

    public void setPoruka(Poruka poruka) {
        this.poruka = poruka;
    }
    
}
