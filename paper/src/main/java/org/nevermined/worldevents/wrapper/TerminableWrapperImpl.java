package org.nevermined.worldevents.wrapper;

import me.lucko.helper.terminable.Terminable;
import org.jetbrains.annotations.Nullable;
import org.nevermined.worldevents.api.wrapper.TerminableWrapper;

public class TerminableWrapperImpl implements TerminableWrapper {

    private final Terminable terminable;

    public TerminableWrapperImpl(Terminable terminable) {
        this.terminable = terminable;
    }

    @Override
    public void close() throws Exception {
        terminable.close();
    }

    @Override
    public boolean isClosed() {
        return terminable.isClosed();
    }

    @Override
    public @Nullable Exception closeSilently() {
        return terminable.closeSilently();
    }

    @Override
    public void closeAndReportException() {
        terminable.closeAndReportException();
    }
}
