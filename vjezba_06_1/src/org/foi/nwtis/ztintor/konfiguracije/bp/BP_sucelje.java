/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.ztintor.konfiguracije.bp;

import java.util.Properties;

/**
 *
 * @author mjurisic
 */
public interface BP_sucelje {
    String getAdmin_database();
    String getAdmin_password();
    String getAdmin_username();
    
    String getDriver_database();
    String getDriver_database(String bp_url);
    Properties getDrivers_database();
    String getServer_database();
    
    String getUser_database();
    String getUser_password();
    String getUser_username();    
}
