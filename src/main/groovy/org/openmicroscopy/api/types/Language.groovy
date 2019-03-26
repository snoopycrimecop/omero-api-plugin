/*
 * -----------------------------------------------------------------------------
 *  Copyright (C) 2019 University of Dundee. All rights reserved.
 *
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
package org.openmicroscopy.api.types

import groovy.transform.CompileStatic

/**
 * List of supported languages by blitz code generation
 */
@CompileStatic
enum Language {
    CPP(Prefix.HDR, Prefix.CPP),
    JAVA(Prefix.JAV),
    PYTHON(Prefix.PYC),
    ICE(Prefix.ICE)

    static Language find(String language) {
        String lang = language.trim().toUpperCase()
        for (Language sl : values()) {
            if (sl.name() == lang) {
                return sl
            }
        }
        return null
    }

    Language(Prefix... prefixes) {
        this.prefixes = prefixes
    }

    Prefix[] getPrefixes() {
        return Arrays.copyOf(prefixes, prefixes.size());
    }

    private final Prefix[] prefixes
}