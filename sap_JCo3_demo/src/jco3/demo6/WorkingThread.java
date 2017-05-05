package jco3.demo6;

import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoException;

/**
 * Created by NanCarp on 2017/5/5.
 */
public class WorkingThread extends Thread {
    private boolean doneSignal;
    private JCoDestination destination;

    // constructor
    public WorkingThread(JCoDestination destination, boolean doneSignal) {
        this.destination = destination;
        this.doneSignal = doneSignal;
    }

    public boolean hasDone() {
        return doneSignal;
    }

    @Override
    public void run() {
        // run method of runIncrement for five time
        for (int i = 0; i < 5; i++) {
            try {
                RfcFunctions.runIncrement(this.destination);
                System.out.println("Run " + (i+1) + " times.");
            } catch (JCoException e) {
                e.printStackTrace();
            }
        }

        this.doneSignal = true;
    }
}
