/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.ztintor.konfiguracije.bp;

import java.util.Enumeration;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.ztintor.konfiguracije.Konfiguracija;
import org.foi.nwtis.ztintor.konfiguracije.KonfiguracijaApstraktna;
import org.foi.nwtis.ztintor.konfiguracije.NemaKonfiguracije;

/**
 *
 * @author nwtis_4
 */
public class BP_Konfiguracija implements BP_sucelje {

    private String datoteka;
    private Konfiguracija konfig;
    private String admin_database;
    private String admin_password;
    private String admin_username;
    private String user_database;
    private String user_password;
    private String user_username;
    private String driver_database;
    private String server_database;
    private Properties drivers_database;

    public BP_Konfiguracija(String datoteka) {
        this.datoteka = datoteka;
        try {
            konfig = KonfiguracijaApstraktna.preuzmiKonfiguraciju(datoteka);

            admin_database = konfig.dajPostavku("admin.database");
            admin_password = konfig.dajPostavku("admin.password");
            admin_username = konfig.dajPostavku("admin.username");

            user_database = konfig.dajPostavku("user.database");
            user_password = konfig.dajPostavku("user.password");
            user_username = konfig.dajPostavku("user.username");
            
            server_database = konfig.dajPostavku("server.database");
            driver_database = getDriver_database(server_database);

        } catch (NemaKonfiguracije ex) {
            Logger.getLogger(BP_Konfiguracija.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public String getAdmin_database() {//admin_database
        return admin_database;
    }

    @Override
    public String getAdmin_password() {
        return admin_password;
    }

    @Override
    public String getAdmin_username() {
        return admin_username;
    }

    @Override
    public String getDriver_database() {
        return driver_database;
    }

    @Override
    public String getDriver_database(String bp_url) {
        String trazeniDriver = null;
        String[] podaci = bp_url.split(":");
        String trazeni = podaci[1];
        Properties p = getDrivers_database();
        for (Enumeration e = p.keys(); e.hasMoreElements();) {
            String kljuc = (String) e.nextElement();
            String[] nesto = kljuc.split("\\.");
            if (nesto[2].compareTo(trazeni) == 0) {
                trazeniDriver = p.getProperty(kljuc);
            }
        }
        return trazeniDriver;
    }

    @Override
    public Properties getDrivers_database() {
        drivers_database = new Properties();
        for (Enumeration<String> e = konfig.dajPostavke(); e.hasMoreElements();) {
            String kljuc = e.nextElement();
            if (kljuc.startsWith("driver.database.")) {
                drivers_database.setProperty(kljuc, konfig.dajPostavku(kljuc));
            }
        }

        return drivers_database;
    }

    @Override
    public String getServer_database() {
        return server_database;
    }

    @Override
    public String getUser_database() {
        return user_database;
    }

    @Override
    public String getUser_password() {
        return user_password;
    }

    @Override
    public String getUser_username() {
        return user_username;
    }
}
