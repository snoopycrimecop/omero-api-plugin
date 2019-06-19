package org.openmicroscopy.api.utils

import groovy.transform.CompileStatic
import org.gradle.api.Transformer

import java.util.regex.Matcher
import java.util.regex.Pattern

@CompileStatic
class CombinedNameMapper implements Transformer<String, String> {

    public static final String COMBINED_SOURCE_REGEX = "(.*?).combined"

    public static final String DEFAULT_RESULT_NAME = "\$1"

    private Matcher matcher
    private String replacement

    CombinedNameMapper() {
        this(DEFAULT_RESULT_NAME)
    }

    CombinedNameMapper(String replaceWith) {
        matcher = Pattern.compile(COMBINED_SOURCE_REGEX).matcher("")
        replacement = replaceWith
    }

    @Override
    String transform(String source) {
        matcher.reset(source)
        if (matcher.find()) {
            return matcher.replaceFirst(replacement)
        }
        return null
    }

}
