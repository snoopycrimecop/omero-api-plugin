package org.openmicroscopy.api.extensions.specs

import groovy.transform.CompileStatic
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.file.FileCollection
import org.openmicroscopy.api.extensions.SplitExtension

@CompileStatic
interface ApiSpecification {

    NamedDomainObjectContainer<SplitExtension> getLanguage()

    FileCollection getCombinedFiles()

    File getOutputDir()

}