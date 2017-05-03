import com.sap.conn.jco.*;
import com.sap.conn.jco.ext.DestinationDataProvider;
import com.sap.conn.jco.ext.JCoSessionReference;
import com.sap.conn.jco.ext.SessionReferenceProvider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by NanCarp on 2017/5/3.
 */
public class MultiThreadedExample {
    static String DESTINATION_NAME1 = "ABAP_AS_WITHOUT_POOL";
    static String DESTINATION_NAME2 = "ABAP_AS_WITH_POOL";

    static {
        Properties connectProperties = new Properties();
        connectProperties.setProperty(DestinationDataProvider.JCO_ASHOST, "binmain");
        connectProperties.setProperty(DestinationDataProvider.JCO_SYSNR, "53");
        connectProperties.setProperty(DestinationDataProvider.JCO_CLIENT, "000");
        connectProperties.setProperty(DestinationDataProvider.JCO_USER, "JCOTEST");
        connectProperties.setProperty(DestinationDataProvider.JCO_PASSWD, "JCOTEST");
        connectProperties.setProperty(DestinationDataProvider.JCO_LANG, "en");
        createDataFile(DESTINATION_NAME1, "jcoDestination", connectProperties);
        connectProperties.setProperty(DestinationDataProvider.JCO_POOL_CAPACITY, "3");
        connectProperties.setProperty(DestinationDataProvider.JCO_PEAK_LIMIT, "10");
        createDataFile(DESTINATION_NAME2, "jcoDestination", connectProperties);
    }

    static void createDataFile(String name, String suffix, Properties properties) {
        File cfg = new File(name + "." + suffix);
        if (!cfg.exists()) {
            try {
                FileOutputStream fos = new FileOutputStream(cfg, false);
                properties.store(fos, "for tests only !");
                fos.close();
            } catch (Exception e) {
                throw new RuntimeException("Unable to create the destination file" + cfg.getName(), e);
            }
        }
    }

    static void createDestinationDataFile(String destinationName, Properties connectProperties) {
        File destCfg = new File(destinationName + ".jcoDestination");
        try {
            FileOutputStream fos = new FileOutputStream(destCfg, false);
            connectProperties.store(fos, "for tests only !");
            fos.close();
        } catch (Exception e) {
            throw new RuntimeException("Unable to create the destination files", e);
        }
    }

    interface MultiStepJob {
        boolean isFinished();
        public void runNextStep();
        String getName();
        public void cleanUp();
    }

    static class StatelessMultiStepExample implements MultiStepJob {
        static AtomicInteger JOB_COUNT = new AtomicInteger(0);
        int jobID = JOB_COUNT.addAndGet(1);
        int calls;
        JCoDestination destination;

        int executedCalls = 0;
        Exception ex = null;
        int remoteCounter;

        StatelessMultiStepExample(JCoDestination destination, int calls) {
            this.calls = calls;
            this.destination = destination;
        }

        public boolean isFinished() {
            return executedCalls == calls || ex != null;
        }

        public void runNextStep() {
            try {
                JCoFunction incrementCounter = incrementCounterTemplate.getFunction();
                incrementCounter.execute(destination);
                JCoFunction getCounter = getCounterTemplate.getFunction();
                executedCalls++;

                if (isFinished()) {
                    getCounter.execute(destination);
                    remoteCounter = getCounter.getExportParameterList().getInt("GET_VALUE");
                }
            } catch (JCoException je) {
                ex = je;
            } catch (RuntimeException re) {
                ex = re;
            }
        }

        public String getName() {
            return "stateless Job-" + jobID;
        }

        public void cleanUp() {
            StringBuilder sb = new StringBuilder("Task").append(getName()).append(" is finished ");
            if (ex != null) {
                sb.append("with exception ").append(ex.toString());
            } else {
                sb.append("successful. Counter is ").append(remoteCounter);
            }
            System.out.println(sb.toString());
        }
    }

    static class StatefulMultiStepExample extends StatelessMultiStepExample {
        StatefulMultiStepExample(JCoDestination destination, int calls) {
            super(destination, calls);
        }

        @Override
        public void runNextStep() {
            if (executedCalls == 0) {
                JCoContext.begin(destination);
            }
            super.runNextStep();
        }

        @Override
        public String getName() {
            return "stateful Job-" + jobID;
        }

        @Override
        public void cleanUp() {
            try {
                JCoContext.end(destination);
            } catch (JCoException je) {
                ex = je;
            }
            super.cleanUp();
        }

        /*static class MySessionReferenceProvider implements SessionReferenceProvider {
            public JCoSessionReference getCurrentSessionReference(String scopeType) {
                MySessionReference
            }
        }

        static class MySessionReference implements JCoSessionReference {

        }*/


    }

    private static JCoFunctionTemplate incrementCounterTemplate, getCounterTemplate;
}


















