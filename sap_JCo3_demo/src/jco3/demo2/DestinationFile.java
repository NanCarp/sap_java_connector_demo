package jco3.demo2;

import com.sap.conn.jco.ext.DestinationDataProvider;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by NanCarp on 2017/5/3.
 */
public class DestinationFile {
    private Properties setProperties() {
        // logon parameters and other properties
        Properties connProps = new Properties();
        connProps.setProperty(DestinationDataProvider.JCO_ASHOST, "192.168.65.100");
        connProps.setProperty(DestinationDataProvider.JCO_SYSNR, "00");
        connProps.setProperty(DestinationDataProvider.JCO_USER, "STONE");
        connProps.setProperty(DestinationDataProvider.JCO_PASSWD, "xxxxxx");
        connProps.setProperty(DestinationDataProvider.JCO_CLIENT, "001");
        connProps.setProperty(DestinationDataProvider.JCO_LANG, "EN");

        return connProps;
    }

    private void doCreateFile(String fName, String suffix, Properties props) throws IOException {
        /**
         * Write contents of properties into a text file
         * which was named [fName+suffix.jcodestination]
         */

        File cfg = new File(fName + "." + suffix);
        if (!cfg.exists()) {// file not exists
            // Create file output stream, not using append mode
            FileOutputStream fos = new FileOutputStream(cfg, false);

            // store the properties in file output stream
            // and also add comments
            props.store(fos, "SAP logon parameters:");

            fos.close();
        } else {
            throw new RuntimeException("File already existes.");
        }
    }

    @Test
    public void createConfigFile() throws IOException {
        Properties props = this.setProperties();
        String fileName = "SAP_AS";// sap application server

        // jcodestination suffix is required by JCoDestinationManager
        this.doCreateFile(fileName, "jcodestination", props);
    }
}
