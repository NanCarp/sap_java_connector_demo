package jco3.demo1;

import com.sap.conn.jco.JCo;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoException;
import org.junit.Test;

/**
 * Created by Administrator on 2017/5/3.
 */
public class JCoDestinationDemo {
    public JCoDestination getDestination() throws JCoException {
        JCoDestination dest = JCoDestinationManager.getDestination("ECC");
        return dest;
    }

    @Test
    public void pingDestination() throws JCoException {
        JCoDestination dest = this.getDestination();
        dest.ping();
    }
}


