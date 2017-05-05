package jco3.session;

import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.ext.Environment;
import com.sap.conn.jco.ext.SessionReferenceProvider;

/**
 * Created by NanCarp on 2017/5/5.
 */
public class DestinationProvider {
    public static JCoDestination getDestination() throws JCoException {
        // create an instance of SessionReferenceProvider and register in environment
        SessionReferenceProvider provider = new SessionReferenceProviderImpl();
        Environment.registerSessionReferenceProvider(provider);

        JCoDestination destination = JCoDestinationManager.getDestination("ECC");

        return destination;
    }
}
