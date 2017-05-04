package jco3.demo5;

import com.sap.conn.jco.*;
import jco3.utils.JCoUtils;

/**
 * Created by NanCarp on 2017/5/4.
 */
public class JCoTableDemo {
    public JCoTable getCocdList() throws JCoException {
        /**
         * Get company code list in SAP using
         *
         *
         */

        JCoDestination dest = JCoDestinationManager.getDestination("ECC");
        JCoFunction fm = dest.getRepository().getFunction("BAPI_COMPANYCODE_GETLIST");
        fm.execute(dest);

        JCoTable companies = fm.getTableParameterList().getTable("COMPANYCODE_LIST");

        return companies;
    }

    public void printCompanies() throws JCoException {
        JCoTable companies = this.getCocdList();
        JCoUtils.printJCoTable(companies);
    }
}
