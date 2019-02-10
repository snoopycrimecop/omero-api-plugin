package org.openmicroscopy.api.tasks

import groovy.transform.CompileStatic
import org.apache.commons.io.FilenameUtils
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.CopySpec
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.FileCollection
import org.gradle.api.internal.file.copy.RegExpNameMapper
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.util.PatternSet
import org.openmicroscopy.api.types.Language
import org.openmicroscopy.api.types.Prefix

import java.util.regex.Pattern

@CompileStatic
class SplitTask extends DefaultTask {


    /**
     * Collection of .combinedFiles files to process
     */
    @InputFiles
    final ConfigurableFileCollection combinedFiles = project.files()

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
     * Optional rename params (from, to) that support
     * regex
     */
    @Optional
    @Input
    final Property<ApiNamer> renameParams = project.objects.property(ApiNamer)

    @TaskAction
    void action() {
        language.get().prefixes.each { Prefix prefix ->
            // Transform prefix enum to lower case for naming
            String prefixName = prefix.name().toLowerCase()
            String extension = prefix.extension

            // Assign default to rename
            RegExpNameMapper nameTransformer
            if (!renameParams) {
                nameTransformer = new RegExpNameMapper(DEFAULT_SOURCE_NAME,
                        DEFAULT_RESULT_NAME + ".${extension}")
            } else {
                nameTransformer = tupleToNameTransformer(prefix)
            }

            project.sync { CopySpec c ->
                c.from _getFilesInCollection(combinedFiles, "**/*.combined")
                c.into outputDir
                c.rename nameTransformer
                c.filter { String line -> filerLine(line, prefixName) }
            }
        }
    }

    void combinedFiles(Object files) {
        setCombinedFiles(files)
    }

    void setCombinedFiles(Object... files) {
        this.combinedFiles.setFrom(files)
    }

    void language(Language lang) {
        setLanguage(lang)
    }

    void language(String language) {
        setLanguage(language)
    }

    void setLanguage(Language lang) {
        this.language.set(lang)
    }

    void setLanguage(String language) {
        Language lang = Language.find(language)
        if (lang == null) {
            throw new GradleException("Unsupported language : ${language}")
        }
        setLanguage(lang)
    }

    void outputDir(Object dir) {
        setOutputDir(dir)
    }

    void setOutputDir(Object dir) {
        this.outputDir = project.file(dir)
    }

    void rename(Pattern sourceRegEx, String replaceWith) {
        this.rename(sourceRegEx.pattern(), replaceWith)
    }

    void rename(String sourceRegEx, String replaceWith) {
        this.renameParams.set(new ApiNamer(sourceRegEx, replaceWith))
    }

    void setReplaceWith(String replaceWith) {
        this.rename(DEFAULT_SOURCE_NAME, replaceWith)
    }

    private RegExpNameMapper tupleToNameTransformer(Prefix prefix) {
        def first = renameParams.get().first
        if (renameParams.get().first) {
            renameParams.get().first = DEFAULT_SOURCE_NAME
        }
        def second = renameParams.getSecond()
        if (textIsNullOrEmpty(second)) {
            second = DEFAULT_RESULT_NAME + ".${prefix.extension}"
        } else {
            second = formatSecond(prefix, second)
        }

        println "Renaming from: ${first} \t to: ${second}"
        return new RegExpNameMapper(first, second)
    }

    private static String textIsNullOrEmpty(String text) {
        return !text?.trim()
    }

    private static String formatSecond(Prefix prefix, String second) {
        final int index = FilenameUtils.indexOfExtension(second)
        if (index == -1) {
            return "${second}.${prefix.extension}"
        } else {
            return second
        }
    }

    private static def filerLine(String line, String prefix) {
        return line.matches("^\\[all](.*)|^\\[${prefix}](.*)") ?
                line.replaceAll("^\\[all]\\s?|^\\[${prefix}]\\s?", "") :
                null
    }

    private static FileCollection _getFilesInCollection(FileCollection collection, String include) {
        PatternSet patternSet = new PatternSet().include(include)
        return collection.asFileTree.matching(patternSet)
    }

    static class ApiNamer {

        static final String DEFAULT_SOURCE_NAME = "(.*?)I[.]combinedFiles"

        static final String DEFAULT_RESULT_NAME = "\$1I"

        final String sourceRegEx

        final String replaceWith

        ApiNamer() {
            this(DEFAULT_SOURCE_NAME, DEFAULT_RESULT_NAME)
        }

        ApiNamer(String replaceWith) {
            this(DEFAULT_SOURCE_NAME, replaceWith)
        }

        ApiNamer(String sourceRegEx, String replaceWith) {
            this.sourceRegEx = sourceRegEx
            this.replaceWith = replaceWith
        }

    }

}
