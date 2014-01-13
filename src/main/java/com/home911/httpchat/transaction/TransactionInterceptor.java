package com.home911.httpchat.transaction;

import com.googlecode.objectify.TxnType;
import com.googlecode.objectify.Work;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import static com.googlecode.objectify.ObjectifyService.ofy;

public class TransactionInterceptor implements MethodInterceptor {

    /** Work around java's annoying checked exceptions */
    private static class ExceptionWrapper extends RuntimeException {
        private static final long serialVersionUID = 1L;

        public ExceptionWrapper(Throwable cause) {
            super(cause);
        }

        /** This makes the cost of using the ExceptionWrapper negligible */
        @Override
        public synchronized Throwable fillInStackTrace() {
            return this;
        }
    }

    /** The only trick here is that we need to wrap & unwrap checked exceptions that go through the Work interface */
    public Object invoke(final MethodInvocation inv) throws Throwable {
        Transaction attr = inv.getStaticPart().getAnnotation(Transaction.class);
        TxnType type = attr.value();

        try {
            return ofy().execute(type, new Work<Object>() {
                public Object run() {
                    try {
                        return inv.proceed();
                    }
                    catch (RuntimeException ex) { throw ex; }
                    catch (Throwable th) { throw new ExceptionWrapper(th); }
                }
            });
        } catch (ExceptionWrapper ex) { throw ex.getCause(); }
    }
}
