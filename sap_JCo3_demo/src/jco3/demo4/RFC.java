package jco3.demo4;

import com.sap.conn.jco.*;
import org.junit.Test;

/**
 * Created by NanCarp on 2017/5/4.
 */
public class RFC {
    public void getCompanyCodeDetial(String cocd) throws JCoException {
        //  JCoException instance represents the backend SAP system
        JCoDestination dest = JCoDestinationManager.getDestination("ECC");

        //
        /*JCoRepository repository = dest.getRepository();
        JCoFunction fm = repository.getFunction("BAPI_COMPANYCODE_GETDETAIL");*/
        JCoFunctionTemplate fmTemplate = dest.getRepository().getFunctionTemplate("BAPI_COMPANYCODE_GETDETAIL");
        JCoFunction fm = fmTemplate.getFunction();

        if (fm == null) {
            throw new RuntimeException("Function does not exists in SAP system.");
        }

        // set import parameter(s)
        fm.getImportParameterList().setValue("COMPANYCODEID", cocd);

        // call function
        fm.execute(dest);

        //
        JCoStructure cocdDetail = fm.getExportParameterList().getStructure("COMPANY_DETAIL");
        this.printStructure(cocdDetail);
    }

    private void printStructure(JCoStructure jcoStructure) {
        for (JCoField field : jcoStructure) {
            System.out.println(String.format("%s\\t%s",
                    field.getName(),
                    field.getString()
            ));
        }
    }

    private void printStructure2(JCoStructure jcoStructure) {
        for (int i = 0; i < jcoStructure.getMetaData().getFieldCount(); i++) {
            System.out.println(String.format("%s\\t%s",
                    jcoStructure.getMetaData().getName(i),
                    jcoStructure.getString(i)
            ));
        }
    }

    @Test
    public void test() throws JCoException {
        this.getCompanyCodeDetial("Z900");
    }
}





















