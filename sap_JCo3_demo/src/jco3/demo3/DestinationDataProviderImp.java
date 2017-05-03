package jco3.demo3;

import com.sap.conn.jco.ext.DestinationDataEventListener;
import com.sap.conn.jco.ext.DestinationDataProvider;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by NanCarp on 2017/5/3.
 */
public class DestinationDataProviderImp implements DestinationDataProvider {
    /**
     * DestinationDataProvider is an interface
     * We define DestinationDataProviderImp class to implements this interface
     * so that we can define the logon parameters more flexibly
     * not just in xxx.jcodestionation file.
     *
     * The key point is that we override getDestinationProperties() method
     * Afterwards, instance of DestinationDataProvider should be registered
     * using Environment.registerDestinationDataProvider() method to take effect
     */

    private Map provider = new HashMap();

    public void addDestinationProperties(String destName, Properties props) {
        provider.put(destName, props);
    }

    @Override
    public Properties getDestinationProperties(String destName) {
        if (destName == null) {
            throw new NullPointerException("Destination name is empty.");
        }

        if (provider.size() == 0) {
            throw new IllegalStateException("Data provider is empty.");
        }
        return (Properties) provider.get(destName);
    }

    @Override
    public boolean supportsEvents() {
        return false;
    }

    @Override
    public void setDestinationDataEventListener(DestinationDataEventListener destinationDataEventListener) {
        throw new UnsupportedOperationException();
    }
}
