package org.openmicroscopy.api.utils

import groovy.transform.CompileStatic
import org.gradle.api.internal.file.copy.RegExpNameMapper

@CompileStatic
class CombinedNameMapper extends RegExpNameMapper {

    public static final String COMBINED_SOURCE_REGEX = "(.*?)I[.]combined"

    public static final String DEFAULT_RESULT_NAME = "\$1I"

    CombinedNameMapper(String replaceWith) {
        super(COMBINED_SOURCE_REGEX, replaceWith)
    }

    CombinedNameMapper() {
        super(COMBINED_SOURCE_REGEX, DEFAULT_RESULT_NAME)
    }

}
