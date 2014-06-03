/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.ztintor.konfiguracije;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 *
 * @author nwtis_4
 */
public class KonfiguracijaTxt extends KonfiguracijaApstraktna {

    public KonfiguracijaTxt(String datoteka) {
        super(datoteka);
    }

    @Override
    public void ucitajKonfiguraciju() throws NemaKonfiguracije {
        if (this.datoteka == null || this.datoteka.trim().length() == 0) {
            throw new NemaKonfiguracije("Neispravno ime datoteke");
        }
        try {
            postavke.load(new FileInputStream(this.datoteka));
        } catch (IOException ex) {
            throw new NemaKonfiguracije(ex.getMessage());
        }
    }

    @Override
    public void ucitajKonfiguraciju(String datoteka) throws NemaKonfiguracije {
        if (datoteka == null || datoteka.trim().length() == 0) {
            throw new NemaKonfiguracije("Neispravno ime datoteke");
        }
        try {
            postavke.load(new FileInputStream(datoteka));
        } catch (IOException ex) {
            throw new NemaKonfiguracije(ex.getMessage());
        }
    }

    @Override
    public void spremiKonfiguraciju() throws NeispravnaKonfiguracija {
        if (this.datoteka == null || this.datoteka.trim().length() == 0) {
            throw new NeispravnaKonfiguracija("Neispravna konfiguracija");
        }
        try {
            postavke.store(new FileOutputStream(this.datoteka), "NWTIS sacuvaj");
        } catch (IOException ex) {
            throw new NeispravnaKonfiguracija(ex.getMessage());
        }
    }

    @Override
    public void spremiKonfiguraciju(String datoteka) throws NeispravnaKonfiguracija {
        if (datoteka == null || datoteka.trim().length() == 0) {
            throw new NeispravnaKonfiguracija("Neispravna konfiguracija");
        }
        try {
            postavke.store(new FileOutputStream(datoteka), "NWTIS sacuvaj");
        } catch (IOException ex) {
            throw new NeispravnaKonfiguracija(ex.getMessage());
        }
    }
}
