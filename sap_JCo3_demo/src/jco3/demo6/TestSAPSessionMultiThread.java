package jco3.demo6;

import com.sap.conn.jco.JCoContext;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoException;
import jco3.demo3.DestinationProvider;

/**
 * Created by NanCarp on 2017/5/5.
 */
public class TestSAPSessionMultiThread {
    public static void main(String[] args) throws JCoException, InterruptedException {
        // get JCoDestination object instance
        JCoDestination destination = DestinationProvider.getDestination();

        // make sure the two functions will be executed in the same session
        JCoContext.begin(destination);

        // Before increment
        System.out.println("Before execution of ZINCREMENT_COUNTER:");
        System.out.println("Counter:" + RfcFunctions.runGetCounter(destination));

        // start a new Thread in which function ZINCREMENT_COUNTER will be executed for five times
        WorkingThread workingThread = new WorkingThread(destination, false);
        workingThread.start();

        // wait and switch thread
        Thread.sleep(1000);

        // After increment
        if (workingThread.hasDone() == true) {
            System.out.println("After execution of ZINCREMENT_COUNTER:");
            System.out.println("Counter:" + RfcFunctions.runGetCounter(destination));
        }

        // release the connection
        JCoContext.end(destination);
    }

}
