package org.openmicroscopy.api.extensions.specs

import groovy.transform.CompileStatic
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.FileCollection
import org.gradle.api.provider.Property
import org.openmicroscopy.api.extensions.SplitExtension

@CompileStatic
interface ApiSpecification {

    NamedDomainObjectContainer<SplitExtension> getLanguage()

    ConfigurableFileCollection getCombinedFiles()

    Property<File> getOutputDir()

}