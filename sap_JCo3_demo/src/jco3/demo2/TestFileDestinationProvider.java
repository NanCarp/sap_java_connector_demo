package jco3.demo2;

import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoException;
import org.junit.Test;

/**
 * Created by Administrator on 2017/5/3.
 */
public class TestFileDestinationProvider {
    @Test
    public void pingSAPDestination() throws JCoException {
        JCoDestination dest = FileDestinationDataProvider.getDestination();
        dest.ping();
    }
}
