package org.openmicroscopy.api.extensions

import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.provider.Property
import org.openmicroscopy.api.types.Language

import java.util.regex.Pattern

class SplitExtension {

    final String name

    final Project project

    final ConfigurableFileCollection combinedFiles

    final Property<Language> language

    final Property<File> outputDir

    final Property<String> outputName

    SplitExtension(String name, Project project) {
        this.name = name
        this.project = project
        this.combinedFiles = project.files()
        this.language = project.objects.property(Language)
        this.outputDir = project.objects.property(File)
        this.outputName = project.objects.property(String)

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

    void rename(Pattern sourceRegEx, String replaceWith) {
        this.nameTransformer = new Tuple(
                sourceRegEx,
                replaceWith
        )
    }

    void rename(String sourceRegEx, String replaceWith) {
        this.nameTransformer = new Tuple(
                sourceRegEx,
                replaceWith
        )
    }

}