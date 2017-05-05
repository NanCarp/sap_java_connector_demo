import com.sap.conn.jco.*;
import com.sap.conn.jco.ext.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
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
    }

    static class MySessionReferenceProvider implements SessionReferenceProvider {
        public JCoSessionReference getCurrentSessionReference(String scopeType) {
            MySessionReference sesRef = WorkerThread.localSessionReference.get();
            if (sesRef != null) {
                return sesRef;
            }

            throw new RuntimeException("Unknown thread:" + Thread.currentThread().getId());
        }

        public boolean isSessionAlive(String sessionId) {
            Collection<MySessionReference> availableSessions = WorkerThread.sessions.values();
            for (MySessionReference ref : availableSessions) {
                if (ref.getID().equals(sessionId)) {
                    return true;
                }
            }
            return false;
        }

        public void jcoServerSessionContinued(String sessionID) throws SessionException {
        }
        public void jcoServerSessionFinished(String sessionID) {
        }
        public void jcoServerSessionPassivated(String sessionID) throws SessionException {
        }
        public JCoSessionReference jcoServerSessionStarted() throws SessionException {
            return null;
        }
    }

    static class MySessionReference implements JCoSessionReference {
        static AtomicInteger atomicInt = new AtomicInteger(0);
        private String id = "session-" + String.valueOf(atomicInt.addAndGet(1));

        public void contextFinished() {

        }

        public void contextStarted() {

        }

        public String getID() {
            return id;
        }
    }

    static class WorkerThread extends Thread {
        static Hashtable<MultiStepJob, MySessionReference> sessions = new Hashtable<MultiStepJob, MySessionReference>();
        static ThreadLocal<MySessionReference> localSessionReference = new ThreadLocal<MySessionReference>();

        private CountDownLatch doneSignal;

        WorkerThread(CountDownLatch doneSignal) {
            this.doneSignal = doneSignal;
        }

        @Override
        public void run() {
            try{
                for(;;) {
                    MultiStepJob job = queue.poll(10, TimeUnit.SECONDS);

                    //  stop if nothing to do
                    if (job == null) {
                        return;
                    }

                    MySessionReference sesRef = sessions.get(job);
                    if (sesRef == null) {
                        sesRef = new MySessionReference();
                        sessions.put(job, sesRef);
                    }
                    localSessionReference.set(sesRef);

                    System.out.println("Task " + job.getName() + " is started.");
                    try {
                        job.runNextStep();
                    } catch (Throwable th) {
                        th.printStackTrace();
                    }

                    if (job.isFinished()) {
                        System.out.println("Task " + job.getName() + " is finished.");
                        sessions.remove(job);
                        job.cleanUp();
                    } else {
                        System.out.println("Task " + job.getName() + " is passivated.");
                        queue.add(job);
                    }
                    localSessionReference.set(null);
                }
            } catch (InterruptedException e) {
                // just leave
            } finally {
                doneSignal.countDown();
            }
        }
    }

    private static BlockingQueue<MultiStepJob> queue = new LinkedBlockingQueue<MultiStepJob>();
    private static JCoFunctionTemplate incrementCounterTemplate, getCounterTemplate;

    static void runJobs(JCoDestination destination, int jobCount, int threadCount) {
        System.out.println(">>> Start");
        for(int i = 0; i < jobCount; i++) {
            queue.add(new StatelessMultiStepExample(destination, 10));
            queue.add(new StatefulMultiStepExample(destination, 10));
        }

        CountDownLatch doneSignal = new CountDownLatch(threadCount);
        for (int i = 0; i < threadCount; i++) {
            new WorkerThread(doneSignal).start();
        }

        System.out.print(">>> Wait ...");
        try {
            doneSignal.await();
        } catch (InterruptedException ie) {
            //
        }
        System.out.println(">>> Done");
    }

    public static void main(String[] argv) {
        Environment.registerSessionReferenceProvider(new MySessionReferenceProvider());
        try {
            JCoDestination destination = JCoDestinationManager.getDestination(DESTINATION_NAME2);
            incrementCounterTemplate = destination.getRepository().getFunctionTemplate("Z_INCREMENT_COUNTER");
            getCounterTemplate = destination.getRepository().getFunctionTemplate("Z_GET_COUNTER");
            if (incrementCounterTemplate == null || getCounterTemplate == null) {
                throw new RuntimeException("This example cannot run without Z_INCREMENT_COUNTER and Z_GET_COUNTER functions");
            }
            runJobs(destination, 5, 2);
        } catch (JCoException je) {
            je.printStackTrace();
        }
    }

}





















