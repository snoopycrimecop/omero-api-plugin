package org.openmicroscopy.api.utils

import org.apache.commons.io.FilenameUtils
import org.gradle.api.Transformer
import org.gradle.api.internal.file.copy.RegExpNameMapper
import org.openmicroscopy.api.types.Prefix

class ApiNamer implements Serializable {

    static final String DEFAULT_SOURCE_NAME = "(.*?)I[.]combined"

    static final String DEFAULT_RESULT_NAME = "\$1I"

    private final String sourceRegEx
    private final String replaceWith
    private final Transformer<String, String> rename

    ApiNamer() {
        this(DEFAULT_SOURCE_NAME, DEFAULT_RESULT_NAME)
    }

    ApiNamer(String sourceRegEx, String replaceWith) {
        this.sourceRegEx = sourceRegEx ?: DEFAULT_SOURCE_NAME
        this.replaceWith = replaceWith ?: DEFAULT_RESULT_NAME
    }

    ApiNamer(Transformer<String, String> namer) {
        this.rename = namer
    }

    Transformer<String, String> getRenamer(Prefix prefix) {
        if (rename) {
            return rename
        }
        return new RegExpNameMapper(sourceRegEx, handleExtension(prefix, replaceWith))
    }

    private String handleExtension(Prefix prefix, String replace) {
        final int index = FilenameUtils.indexOfExtension(replace)
        if (index == -1) {
            return "${replace}.${prefix.extension}"
        } else {
            return replace
        }
    }

}
