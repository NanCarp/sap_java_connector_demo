package jco3.demo3;

import com.sap.conn.jco.JCo;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.ext.DestinationDataProvider;
import com.sap.conn.jco.ext.Environment;

import java.util.Properties;

/**
 * Created by NanCarp on 2017/5/3.
 */
public class DestinationProvider {
    private static Properties setProperties() {
        // logon parameter and other properties
        Properties connProps = new Properties();
        connProps.setProperty(DestinationDataProvider.JCO_ASHOST, "192.168.65.100");
        connProps.setProperty(DestinationDataProvider.JCO_SYSNR, "00");
        connProps.setProperty(DestinationDataProvider.JCO_USER, "STONE");
        connProps.setProperty(DestinationDataProvider.JCO_PASSWD, "xxxxxx");
        connProps.setProperty(DestinationDataProvider.JCO_CLIENT, "001");
        connProps.setProperty(DestinationDataProvider.JCO_LANG, "EN");

        return connProps;
    }

    public static JCoDestination getDestination() throws JCoException {
        String destName = "SAP_AS";

        Properties props = setProperties();
        DestinationDataProviderImp destDataProvider = new DestinationDataProviderImp();
        destDataProvider.addDestinationProperties(destName, props);
        Environment.registerDestinationDataProvider(destDataProvider);

        JCoDestination dest = JCoDestinationManager.getDestination(destName);
        return dest;
    }
}

















