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

    public static final String TASK_PREFIX_GENERATE = "generate"

    private static final def Log = Logging.getLogger(ApiPluginBase)

    @Override
    void apply(Project project) {
        ApiExtension api = createBaseExtension(project)

        api.language.whenObjectAdded { SplitExtension split ->
            registerSplitTask(project, api, split)
        }
    }

    @SuppressWarnings("GrMethodMayBeStatic")
    ApiExtension createBaseExtension(Project project) {
        def language = project.container(SplitExtension, new SplitFactory(project))
        return project.extensions.create(EXTENSION_NAME_API, ApiExtension, project, language)
    }

    static void registerSplitTask(Project project, ApiExtension api, SplitExtension split) {
        String taskName = TASK_PREFIX_GENERATE + split.name.capitalize()
        project.tasks.register(taskName, SplitTask, new Action<SplitTask>() {
            @Override
            void execute(SplitTask t) {
                t.with {
                    group = GROUP
                    setDescription("Splits ${split.language} from .combined files")
                    setOutputDir(split.outputDir.flatMap { File f -> api.outputDir.dir(f.toString()) })
                    setLanguage(split.language)
                    setNamer(split.renamer)
                    source api.combinedFiles + split.combinedFiles
                    include "**/*.combined"
                }
            }
        })
    }
}
