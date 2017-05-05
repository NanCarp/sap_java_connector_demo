package jco3.demo6;

import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;

/**
 * Created by NanCarp on 2017/5/5.
 */
public class RfcFunctions {
    public static int runGetCounter(JCoDestination dest) throws JCoException {
        JCoFunction couterFM = dest.getRepository().getFunction("ZGET_COUNTER");
        couterFM.execute(dest);
        int counter = (int) couterFM.getExportParameterList().getValue("GET_VALUE");
        return counter;
    }

    public static void runIncrement(JCoDestination dest) throws JCoException {
        JCoFunction increment = dest.getRepository().getFunction("ZINCREMENT_COUNTER");
        increment.execute(dest);
    }
}
