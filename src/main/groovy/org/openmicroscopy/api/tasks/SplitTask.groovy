package org.openmicroscopy.api.tasks

import groovy.transform.CompileStatic
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.CopySpec
import org.gradle.api.file.Directory
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.FileTree
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.util.PatternFilterable
import org.gradle.api.tasks.util.PatternSet
import org.gradle.internal.Factory
import org.openmicroscopy.api.types.Language
import org.openmicroscopy.api.types.Prefix
import org.openmicroscopy.api.utils.ApiNamer

import javax.inject.Inject

@CompileStatic
class SplitTask extends DefaultTask {

    /**
     * List of the languages we want to split from .combinedFiles files
     */
    @Input
    final Property<Language> language = project.objects.property(Language)

    /**
     * Directory to spit out source files
     */
    @OutputDirectory
    final DirectoryProperty outputDir = project.objects.directoryProperty()

    /**
     * Optional rename params (from, to) that support regex
     */
    @Optional
    @Input
    final Property<ApiNamer> namer = project.objects.property(ApiNamer)

    private final ConfigurableFileCollection combinedFiles = project.files()

    private final PatternFilterable combinedPattern

    SplitTask() {
        combinedPattern = getPatternSetFactory().create()
                .include("**/*.combined")
    }

    @Inject
    protected Factory<PatternSet> getPatternSetFactory() {
        throw new UnsupportedOperationException()
    }

    @TaskAction
    void action() {
        language.get().prefixes.each { Prefix prefix ->
            // Transform prefix enum to lower case for naming
            String prefixName = prefix.name().toLowerCase()

            // Assign default to rename
            def apiNamer = namer.getOrElse(new ApiNamer())

            project.sync { CopySpec c ->
                c.from combinedFiles
                c.into outputDir
                c.rename apiNamer.getRenamer(prefix)
                c.filter { String line -> filerLine(line, prefixName) }
            }
        }
    }

    @InputFiles
    @PathSensitive(PathSensitivity.RELATIVE)
    FileTree getCombinedFiles() {
        FileTree src = this.combinedFiles.asFileTree
        return src.matching(combinedPattern)
    }

    void combinedFiles(Iterable<?> paths) {
        this.combinedFiles.from(paths)
    }

    void combinedFiles(Object... paths) {
        this.combinedFiles.from(paths)
    }

    void setCombinedFiles(Iterable<?> paths) {
        this.combinedFiles.setFrom(paths)
    }

    void setCombinedFiles(Object... paths) {
        this.combinedFiles.setFrom(paths)
    }

    void language(Language lang) {
        setLanguage(lang)
    }

    void language(String lang) {
        setLanguage(lang)
    }

    void language(Property<? extends Language> lang) {
        setLanguage(lang)
    }

    void setLanguage(Language lang) {
        this.language.set(lang)
    }

    void setLanguage(Property<? extends Language> lang) {
        this.language.set(lang)
    }

    void setLanguage(String language) {
        Language lang = Language.find(language)
        if (lang == null) {
            throw new GradleException("Unsupported language : ${language}")
        }
        setLanguage(lang)
    }

    void outputDir(File dir) {
        setOutputDir(dir)
    }

    void setOutputDir(Provider<? extends Directory> provider) {
        this.outputDir.set(provider)
    }

    void setOutputDir(Directory dir) {
        this.outputDir.set(dir)
    }

    void setOutputDir(File dir) {
        this.outputDir.set(dir)
    }

    void namer(Provider<? extends ApiNamer> provider) {
        setNamer(provider)
    }

    void setNamer(Provider<? extends ApiNamer> provider) {
        this.namer.set(provider)
    }

    private static def filerLine(String line, String prefix) {
        return line.matches("^\\[all](.*)|^\\[${prefix}](.*)") ?
                line.replaceAll("^\\[all]\\s?|^\\[${prefix}]\\s?", "") :
                null
    }

}
