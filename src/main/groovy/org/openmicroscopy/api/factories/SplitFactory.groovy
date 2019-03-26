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
package org.openmicroscopy.api.factories


import org.gradle.api.NamedDomainObjectFactory
import org.gradle.api.Project
import org.openmicroscopy.api.extensions.SplitExtension

class SplitFactory implements NamedDomainObjectFactory<SplitExtension> {

    final Project project

    SplitFactory(Project project) {
        this.project = project
    }

    @Override
    SplitExtension create(String name) {
        return new SplitExtension(name, project)
    }

}
