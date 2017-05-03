package jco3.demo3;

import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoException;
import org.junit.Test;

/**
 * Created by NanCarp on 2017/5/3.
 */
public class TestDestinationProvider {
    @Test
    public void pingSAPDestination() throws JCoException {
        JCoDestination dest = DestinationProvider.getDestination();
        dest.ping();
    }
}
