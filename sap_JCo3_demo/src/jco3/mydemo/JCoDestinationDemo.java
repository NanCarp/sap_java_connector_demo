package jco3.mydemo;

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

    public void main (String[] args) throws JCoException {
        JCoDestinationDemo demo = new JCoDestinationDemo();
        JCoDestination dest = null;
        dest = demo.getDestination();
        dest.ping();
    }
}


