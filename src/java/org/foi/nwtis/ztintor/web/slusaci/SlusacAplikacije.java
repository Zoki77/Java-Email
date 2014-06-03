/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.ztintor.web.slusaci;

import java.io.File;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import org.foi.nwtis.ztintor.konfiguracije.Konfiguracija;
import org.foi.nwtis.ztintor.konfiguracije.KonfiguracijaApstraktna;
import org.foi.nwtis.ztintor.konfiguracije.NemaKonfiguracije;
import org.foi.nwtis.ztintor.konfiguracije.bp.BP_Konfiguracija;
import org.foi.nwtis.ztintor.web.ObradaPoruka;

/**
 * Web application lifecycle listener.
 *
 * @author nwtis_4
 */
@WebListener()
public class SlusacAplikacije implements ServletContextListener {

    private ObradaPoruka obradaPoruka;
    private static Konfiguracija conf;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        String path = sce.getServletContext().getRealPath("WEB-INF");

        String datoteka = path + File.separator
                + sce.getServletContext().getInitParameter("konfiguracija");
        System.out.println("Datoteka konfiguracije: " + datoteka + "<br/>");
        BP_Konfiguracija konfig = new BP_Konfiguracija(datoteka);
        System.out.println("Konfiguracija ucitana");
        sce.getServletContext().setAttribute("BP_Konfiguracija", konfig);

        String konfiguracijaskaDatoteka = path + File.separator
                + sce.getServletContext().getInitParameter("conf");
        //Konfiguracija conf;



        obradaPoruka = new ObradaPoruka();
        obradaPoruka.setBpKonfig(konfig);
        try {
            conf = KonfiguracijaApstraktna.preuzmiKonfiguraciju(konfiguracijaskaDatoteka);
            obradaPoruka.setKonfig(conf);
        } catch (NemaKonfiguracije ex) {
            System.out.println("Nema obične konfiguracije");
        }
        obradaPoruka.start();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (obradaPoruka != null) {
            obradaPoruka.interrupt();
        }
    }

    public static Konfiguracija getConf() {
        return conf;
    }

    
}
