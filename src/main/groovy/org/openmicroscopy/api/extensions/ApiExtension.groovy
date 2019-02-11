package org.openmicroscopy.api.extensions

import org.gradle.api.Action
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.Directory
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Provider

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

        this.outputDir.convention(project.layout.projectDirectory.dir("src/generated"))
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
