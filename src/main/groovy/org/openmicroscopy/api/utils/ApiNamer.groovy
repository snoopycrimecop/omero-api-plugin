/*
 * -----------------------------------------------------------------------------
 *  Copyright (C) 2019 University of Dundee & Open Microscopy Environment.
 *  All rights reserved.
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * ------------------------------------------------------------------------------
 */
package org.openmicroscopy.api.utils

import groovy.transform.CompileStatic
import org.apache.commons.io.FilenameUtils
import org.gradle.api.Transformer
import org.gradle.api.internal.file.copy.RegExpNameMapper
import org.openmicroscopy.api.types.Prefix

@CompileStatic
class ApiNamer implements Serializable {

    public static final String DEFAULT_SOURCE_NAME = "(.*?)I[.]combined"

    public static final String DEFAULT_RESULT_NAME = "\$1I"

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
        return new RegExpNameMapper(sourceRegEx, selectExtension(prefix, replaceWith))
    }

    private String selectExtension(Prefix prefix, String replace) {
        final int index = FilenameUtils.indexOfExtension(replace)
        if (index == -1) {
            return "${replace}.${prefix.extension}"
        } else {
            return replace
        }
    }

}
