package jco3.session;

import com.sap.conn.jco.ext.JCoSessionReference;
import com.sap.conn.jco.ext.SessionException;
import com.sap.conn.jco.ext.SessionReferenceProvider;

/**
 * Created by NanCarp on 2017/5/5.
 */
public class SessionReferenceProviderImpl implements SessionReferenceProvider {


    @Override
    public JCoSessionReference getCurrentSessionReference(String s) {
        JCoSessionReferenceImpl  sessionRef = new JCoSessionReferenceImpl();
        return sessionRef;
    }

    @Override
    public boolean isSessionAlive(String s) {
        return false;
    }

    @Override
    public JCoSessionReference jcoServerSessionStarted() throws SessionException {
        return null;
    }

    @Override
    public void jcoServerSessionContinued(String s) throws SessionException {

    }

    @Override
    public void jcoServerSessionPassivated(String s) throws SessionException {

    }

    @Override
    public void jcoServerSessionFinished(String s) throws SessionException {

    }
}
