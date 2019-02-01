package org.openmicroscopy.api.tasks

import groovy.transform.CompileStatic
import org.apache.commons.io.FilenameUtils
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.CopySpec
import org.gradle.api.file.FileCollection
import org.gradle.api.internal.file.copy.RegExpNameMapper
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

    public static final String DEFAULT_SOURCE_NAME = "(.*?)I[.]combinedFiles"

    public static final String DEFAULT_RESULT_NAME = "\$1I"

    /**
     * Collection of .combinedFiles files to process
     */
    @InputFiles
    FileCollection combinedFiles = project.files()

    /**
     * List of the languages we want to split from .combinedFiles files
     */
    @Input
    Language language

    /**
     * Directory to spit out source files
     */
    @OutputDirectory
    File outputDir

    /**
     * Optional rename params (from, to) that support
     * regex
     */
    @Optional
    @Input
    Tuple2<String, String> renameParams

    @TaskAction
    void action() {
        language.prefixes.each { Prefix prefix ->
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

    void setCombinedFiles(Object files) {
        this.combinedFiles = project.files(files)
    }

    void language(Language lang) {
        setLanguage(lang)
    }

    void language(String language) {
        setLanguage(language)
    }

    void setLanguage(Language lang) {
        this.language = lang
    }

    void setLanguage(String language) {
        Language lang = Language.find(language)
        if (lang == null) {
            throw new GradleException("Unsupported language : ${language}")
        }
        this.language = lang
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
        this.renameParams = new Tuple2<>(sourceRegEx, replaceWith)
    }

    void setReplaceWith(String replaceWith) {
        this.rename(DEFAULT_SOURCE_NAME, replaceWith)
    }

    private RegExpNameMapper tupleToNameTransformer(Prefix prefix) {
        def first = renameParams.getFirst()
        if (textIsNullOrEmpty(first)) {
            first = DEFAULT_SOURCE_NAME
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

}
