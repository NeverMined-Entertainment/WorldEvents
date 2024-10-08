package org.nevermined.worldevents.wrapper;

import me.wyne.wutils.i18n.language.replacement.TextReplacement;
import org.nevermined.worldevents.api.wrapper.TextReplacementWrapper;

import java.util.Arrays;

public class WrapperAdapter {

    public static TextReplacement[] adaptTextReplacement(TextReplacementWrapper[] textReplacementWrappers)
    {
        return Arrays.stream(textReplacementWrappers).map(textReplacementWrapper ->
                        (TextReplacement) textReplacementWrapper::replace)
                .toArray(TextReplacement[]::new);
    }

}
