package jco3.mydemo;

import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoTable;

/**
 * 从sap中生成凭证
 * Created by NanCarp on 2017/5/5.
 */
public class CreateCoucherFromSAP {
    public static void main(String[] args) {
        JCoFunction function = null;
        JCoDestination destination = SAPConn.connect();
        String state="";//调用接口返回状态
        String message="";//调用接口返回信息
        String belnr="";//会计凭证号
        String buzei="";//会计凭证行项目
        try {
            // 调用ZRFC_GET_REMAIN_SUM函数
            function = destination.getRepository().getFunction("ZRFC_OA_ACC_DOCUMENT");
            // 获取传入表参数T_ACCDOCUMENT
            JCoTable T_ACCDOCUMENT = function.getTableParameterList().getTable("T_ACCDOCUMENT");
            T_ACCDOCUMENT.appendRow();// 增加一行
            // 给表参数中的字段赋值，此处测试，就随便传两个值进去
            T_ACCDOCUMENT.setValue("BUKRS", "1000");
            T_ACCDOCUMENT.setValue("BLART", "SA");
            function.execute(destination);
            state = function.getExportParameterList().getString("E_STATUS");// 调用接口返回状态
            message = function.getExportParameterList().getString("E_MESAGE");// 调用接口返回信息
            System.out.println("调用返回状态--->"+state+";调用返回信息--->"+message);
            T_ACCDOCUMENT.firstRow();// 获取第一行的对象(此处看sap端如何写的，如果返回的可能有多行，那得循环)
            belnr = T_ACCDOCUMENT.getString("BELNR");
            buzei = T_ACCDOCUMENT.getString("BUZEI");
            System.out.println("会计凭证号--->"+belnr+";会计凭证行项目--->"+buzei);
            T_ACCDOCUMENT.clear();// 清空本次条件，如果要继续传入值去或者还要循环，那得将之前的条件清空
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
