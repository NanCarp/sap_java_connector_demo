package jco3.demo5;

import com.sap.conn.jco.*;

/**
 * Created by NanCarp on 2017/5/4.
 */
public class JCoTableAsImport {
    public JCoTable readTable() throws JCoException {
        //
        JCoDestination dest = JCoDestinationManager.getDestination("ECC");
        JCoFunction fm = dest.getRepository().getFunction("RFC_READ_TABLE");

        // table we want to query is USR04
        fm.getImportParameterList().setValue("QUERY_TABLE", "USR04");

        // output data will be delimited by comma
        fm.getImportParameterList().setValue("DELIMITER", ",");

        // processing table parameters
        JCoTable options = fm.getTableParameterList().getTable("OPTIONS");
        // modification date >= 2012.01.01 and <= 2015.12.31
        options.appendRow();
        options.setValue("TEXT", "MODDA GE '20120101' ");
        options.appendRow();
        options.setValue("TEXT", "AND MMODDA LE '20151231' ");

        /*String[] outputFields = new String[] {"BNAME", "MODDA"};
        JCoTable fields = fm.getTableParameterList().getTable("FIELDS");
        int count = outputFields.length;
        fields.appendRows(count);
        for (int i = 0; i < count; i++){
            fields.setRow(i);
            fields.setValue("FIELDNAME", outputFields[i]);
        }*/


        // We only care about fields of [user id] and [modification date]
        String[] outputFields = new String[]{"BNAME", "MODDA"};
        JCoTable fields = fm.getTableParameterList().getTable("FIELDS");
        int count = outputFields.length;
        fields.appendRows(count);
        for (int i =0 ; i < count; i++) {
            fields.setRow(i);
            fields.setValue("FIELDNAME", outputFields[i]);
        }

        fm.execute(dest);

        JCoTable data = fm.getTableParameterList().getTable("DATA");

        return data;
    }
}














