package org.nevermined.worldevents.wrapper;

import me.lucko.helper.promise.ThreadContext;
import me.wyne.wutils.i18n.language.replacement.TextReplacement;
import org.nevermined.worldevents.api.wrapper.TextReplacementWrapper;
import org.nevermined.worldevents.api.wrapper.ThreadContextWrapper;

import java.util.Arrays;

public class WrapperAdapter {

    public static TextReplacement[] adaptTextReplacement(TextReplacementWrapper[] textReplacementWrappers)
    {
        return Arrays.stream(textReplacementWrappers).map(textReplacementWrapper ->
                        (TextReplacement) textReplacementWrapper::replace)
                .toArray(TextReplacement[]::new);
    }

    public static ThreadContext adaptThreadContext(ThreadContextWrapper threadContextWrapper)
    {
        if (threadContextWrapper == ThreadContextWrapper.SYNC)
            return ThreadContext.SYNC;
        else
            return ThreadContext.ASYNC;
    }

}
