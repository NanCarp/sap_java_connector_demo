package jco3.demo6;

import com.sap.conn.jco.JCoContext;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoException;

/**
 * Created by NanCarp on 2017/5/5.
 */
public class TestSessionSameThread {
    public static void main(String[] args) throws JCoException, InterruptedException {
        // get JCoDestination object instance
        JCoDestination destination = JCoDestinationManager.getDestination("ECC");

        // make sure the twi functions will be executed in the same session
        JCoContext.begin(destination);

        // Before increment
        System.out.println("Before execution of ZINCREMENT_COUNTER:");
        System.out.println("Counter:" + RfcFunctions.runGetCounter(destination));

        // Run incrementCouter five times
        for (int i = 0; i < 5; i++) {
            RfcFunctions.runIncrement(destination);
            System.out.println("Add:" + (i + 1));
        }

        // After increment
        System.out.println("After execution of ZINCREMENT_COUNTER:");
        System.out.println("Counter:" + RfcFunctions.runGetCounter(destination));

        // release the connection
        JCoContext.end(destination);
    }
}
