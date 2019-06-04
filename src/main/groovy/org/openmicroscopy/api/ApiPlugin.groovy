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
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.tasks.SourceSet
import org.openmicroscopy.api.tasks.SplitTask
import org.openmicroscopy.api.types.Language

@CompileStatic
class ApiPlugin implements Plugin<Project> {

    private Project project

    @Override
    void apply(Project project) {
        this.project = project

        // Apply our base plugin
        project.plugins.apply(ApiPluginBase)

        project.plugins.withType(JavaPlugin) {
            configureForJavaPlugin()
        }
    }

    void configureForJavaPlugin() {
        project.afterEvaluate {
            project.tasks.withType(SplitTask).all { SplitTask splitTask ->
                if (splitTask.language.get() == Language.JAVA) {
                    // Set Java compileJava task to depend on the splitJava task
                    project.tasks.named(JavaPlugin.COMPILE_JAVA_TASK_NAME).configure {
                        it.dependsOn(splitTask)
                    }

                    // Set java source set to include output of combinedToJava task
                    JavaPluginConvention javaConvention =
                            project.convention.getPlugin(JavaPluginConvention)

                    SourceSet main =
                            javaConvention.sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME)

                    main.java.srcDirs(splitTask.outputDir)
                }
            }
        }
    }

}
