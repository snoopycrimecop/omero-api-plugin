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
package org.openmicroscopy.api.extensions

import org.gradle.api.Action
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.Directory
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Provider
import org.openmicroscopy.api.ApiPluginBase

import java.util.concurrent.Callable

class ApiExtension {

    private final Project project

    final NamedDomainObjectContainer<SplitExtension> language

    final ConfigurableFileCollection combinedFiles

    final DirectoryProperty outputDir

    ApiExtension(Project project, NamedDomainObjectContainer<SplitExtension> language) {
        this.project = project
        this.language = language
        this.combinedFiles = project.files()
        this.outputDir = project.objects.directoryProperty()

        // Set conventions
        this.outputDir.convention(project.layout.projectDirectory.dir("src/api"))
    }

    Provider<String> createTaskName(String name) {
        project.providers.provider(new Callable<String>() {
            @Override
            String call() throws Exception {
                return ApiPluginBase.TASK_PREFIX_COMBINED_TO + name.capitalize()
            }
        })
    }

    void language(Action<? super NamedDomainObjectContainer<SplitExtension>> action) {
        action.execute(language)
    }

    void setOutputDir(String dir) {
        setOutputDir(new File(dir))
    }

    void setOutputDir(Directory dir) {
        this.outputDir.set(dir)
    }

    void setOutputDir(File dir) {
        this.outputDir.set(dir)
    }

    void setOutputDir(Provider<? extends Directory> dir) {
        this.outputDir.set(dir)
    }

}
