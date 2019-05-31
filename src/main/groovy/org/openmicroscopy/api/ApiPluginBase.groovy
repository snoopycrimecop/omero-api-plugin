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
package org.openmicroscopy.api

import groovy.transform.CompileStatic
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.logging.Logging
import org.openmicroscopy.api.extensions.ApiExtension
import org.openmicroscopy.api.extensions.SplitExtension
import org.openmicroscopy.api.factories.SplitFactory
import org.openmicroscopy.api.tasks.SplitTask


@CompileStatic
class ApiPluginBase implements Plugin<Project> {

    /**
     * Sets the group name for the DSLPlugin tasks to reside in.
     * i.e. In a terminal, call `./gradlew tasks` to list tasks in their groups in a terminal
     */
    public static final String GROUP = "omero-api"

    public static final String EXTENSION_NAME_API = "api"

    public static final String TASK_PREFIX_COMBINED_TO = "combinedTo"

    private static final def Log = Logging.getLogger(ApiPluginBase)

    @Override
    void apply(Project project) {
        ApiExtension api = createBaseExtension(project)

        api.language.configureEach { SplitExtension split ->
            registerSplitTask(project, api, split)
        }
    }

    @SuppressWarnings("GrMethodMayBeStatic")
    ApiExtension createBaseExtension(Project project) {
        def language = project.container(SplitExtension, new SplitFactory(project))
        return project.extensions.create(EXTENSION_NAME_API, ApiExtension, project, language)
    }

    static void registerSplitTask(Project project, ApiExtension api, SplitExtension split) {
        String taskName = api.createTaskName(split.name)

        project.tasks.register(taskName, SplitTask, new Action<SplitTask>() {
            @Override
            void execute(SplitTask task) {
                task.group = GROUP
                task.setDescription("Splits ${split.language} from .combined files")
                task.setOutputDir(split.outputDir.flatMap { File f -> api.outputDir.dir(f.toString()) })
                task.setLanguage(split.language)
                task.setNamer(split.renamer)
                task.source api.combinedFiles + split.combinedFiles
                task.include "**/*.combined"
            }
        })
    }
}
