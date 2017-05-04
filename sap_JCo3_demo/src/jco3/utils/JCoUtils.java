package jco3.utils;

import com.sap.conn.jco.JCoField;
import com.sap.conn.jco.JCoRecordMetaData;
import com.sap.conn.jco.JCoTable;

/**
 * Created by NanCarp on 2017/5/4.
 */
public class JCoUtils {
    public static void printJCoTable(JCoTable jCoTable) {
        // header
        JCoRecordMetaData tableMeta = jCoTable.getRecordMetaData();
        for(int i = 0; i < tableMeta.getFieldCount(); i++) {
            System.out.print(String.format("%s%t", tableMeta.getName(i)));
        }
        System.out.println();

        // line items

        for(int i = 0; i < jCoTable.getNumRows(); i++) {
            // Sets the row pointer to the specified positon(beginning from zero)
            jCoTable.setRow(i);

            // Each line is of type JCoStructure
            for (JCoField fld : jCoTable) {
                System.out.print(String.format("%s\t", fld.getValue()));
            }
            System.out.println();
        }
    }
}
