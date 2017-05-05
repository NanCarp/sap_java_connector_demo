package jco3.session;

import com.sap.conn.jco.ext.JCoSessionReference;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by NanCarp on 2017/5/5.
 */
public class JCoSessionReferenceImpl implements JCoSessionReference {

    private AtomicInteger atomInt = new AtomicInteger(0);
    private String id = "session" + String.valueOf(atomInt.addAndGet(1));

    @Override
    public String getID() {
        return id;
    }

    @Override
    public void contextStarted() {

    }

    @Override
    public void contextFinished() {

    }
}
