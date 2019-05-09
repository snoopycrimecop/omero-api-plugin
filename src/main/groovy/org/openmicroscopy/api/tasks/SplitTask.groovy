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
package org.openmicroscopy.api.tasks

import groovy.transform.CompileStatic
import org.gradle.api.GradleException
import org.gradle.api.file.CopySpec
import org.gradle.api.file.Directory
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.FileTree
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.SourceTask
import org.gradle.api.tasks.TaskAction
import org.openmicroscopy.api.types.Language
import org.openmicroscopy.api.types.Prefix
import org.openmicroscopy.api.utils.ApiNamer

@CompileStatic
class SplitTask extends SourceTask {

    /**
     * Directory to spit out source files
     */
    private final DirectoryProperty outputDir = project.objects.directoryProperty()

    /**
     * Optional rename params (from, to) that support regex
     */
    private final Property<ApiNamer> namer = project.objects.property(ApiNamer)

    /**
     * List of the languages we want to split from .combinedFiles files
     */
    private final Property<Language> language = project.objects.property(Language)


    private static final Logger Log = Logging.getLogger(SplitTask)

    @TaskAction
    void action() {
        language.get().prefixes.each { Prefix prefix ->
            // Transform prefix enum to lower case for naming
            String prefixName = prefix.name().toLowerCase()

            // Assign default to rename
            ApiNamer apiNamer = namer.getOrElse(new ApiNamer())

            project.sync { CopySpec c ->
                c.into outputDir.get()
                c.from getSource()
                c.rename apiNamer.getRenamer(prefix)
                c.filter { String line -> filerLine(line, prefixName) }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @PathSensitive(PathSensitivity.RELATIVE)
    FileTree getSource() {
        return super.getSource()
    }

    @OutputDirectory
    DirectoryProperty getOutputDir() {
        return this.outputDir
    }

    @Input
    Property<Language> getLanguage() {
        return this.language
    }

    @Input
    @Optional
    Property<ApiNamer> getNamer() {
        return this.namer
    }

    void setOutputDir(File dir) {
        this.outputDir.set(dir)
    }

    void setOutputDir(Directory dir) {
        this.outputDir.set(dir)
    }

    void setOutputDir(Provider<Directory> provider) {
        this.outputDir.set(provider)
    }

    void setLanguage(String language) {
        Language lang = Language.find(language)
        if (lang == null) {
            throw new GradleException("Unsupported language : ${language}")
        }
        setLanguage(lang)
    }

    void setLanguage(Language lang) {
        this.language.set(lang)
    }

    void setLanguage(Property<? extends Language> lang) {
        this.language.set(lang)
    }

    void setNamer(Provider<? extends ApiNamer> provider) {
        this.namer.set(provider)
    }

    void setNamer(ApiNamer apiNamer) {
        this.namer.set(apiNamer)
    }

    private static def filerLine(String line, String prefix) {
        return line.matches("^\\[all](.*)|^\\[${prefix}](.*)") ?
                line.replaceAll("^\\[all]\\s?|^\\[${prefix}]\\s?", "") :
                null
    }

}
