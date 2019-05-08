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

import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.Transformer
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.openmicroscopy.api.types.Language
import org.openmicroscopy.api.utils.ApiNamer

class SplitExtension {

    final String name

    final Project project

    final ConfigurableFileCollection combinedFiles

    final Property<Language> language

    final Property<File> outputDir

    final Property<ApiNamer> renamer

    SplitExtension(String name, Project project) {
        this.name = name
        this.project = project
        this.combinedFiles = project.files()
        this.language = project.objects.property(Language)
        this.outputDir = project.objects.property(File)
        this.renamer = project.objects.property(ApiNamer)

        // Optionally set language based on name of extension
        Language lang = Language.values().find { lang ->
            name.toUpperCase().contains(lang.name())
        }
        if (lang) {
            this.language.convention(lang)
        }
    }

    void combinedFiles(Iterable<?> files) {
        this.combinedFiles.from files
    }

    void combinedFiles(Object... files) {
        this.combinedFiles.from files
    }

    void setCombinedFiles(Iterable<?> files) {
        this.combinedFiles.setFrom(files)
    }

    void setCombinedFiles(Object... files) {
        this.combinedFiles.setFrom(files)
    }

    void language(String language) {
        setLanguage(language)
    }

    void language(Language lang) {
        setLanguage(lang)
    }

    void setLanguage(Language lang) {
        this.language.set(lang)
    }

    void setLanguage(String languageString) {
        Language lang = Language.find(languageString)
        if (lang == null) {
            throw new GradleException("Unsupported language: ${languageString}")
        }
        this.language.set(lang)
    }

    void outputDir(File dir) {
        setOutputDir(dir)
    }

    void outputDir(String dir) {
        setOutputDir(dir)
    }

    void setOutputDir(String dir) {
        setOutputDir(new File(dir))
    }

    void setOutputDir(File dir) {
        this.outputDir.set(dir)
    }

    void setOutputDir(Provider<? extends File> provider) {
        this.outputDir.set(provider)
    }

    void rename(Transformer<? extends String, ? extends String> renamer) {
        this.renamer.set(new ApiNamer(renamer))
    }

    void rename(String sourceRegEx, String replaceWith) {
        this.renamer.set(new ApiNamer(sourceRegEx, replaceWith))
    }

    void rename(String replaceWith) {
        this.renamer.set(new ApiNamer(null, replaceWith))
    }

}