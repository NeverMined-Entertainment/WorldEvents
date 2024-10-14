package org.nevermined.worldevents.api.wrapper;

import org.jetbrains.annotations.Nullable;

public interface TerminableWrapper extends AutoCloseable {

    void close() throws Exception;
    boolean isClosed();
    @Nullable
    Exception closeSilently();
    void closeAndReportException();
    Object getTerminable();

}
