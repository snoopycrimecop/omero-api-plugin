package org.openmicroscopy.api

import groovy.transform.CompileStatic
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.openmicroscopy.api.extensions.ApiExtension

@CompileStatic
class ApiPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        // Apply our base plugin
        project.plugins.apply(ApiPluginBase)

        // Get the extension
        project.extensions.getByType(ApiExtension)
    }

}
