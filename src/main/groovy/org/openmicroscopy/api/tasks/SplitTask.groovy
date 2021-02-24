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

import com.github.javaparser.StaticJavaParser
import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.PackageDeclaration
import groovy.transform.CompileStatic
import org.gradle.api.GradleException
import org.gradle.api.Transformer
import org.gradle.api.file.CopySpec
import org.gradle.api.file.Directory
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.FileCollection
import org.gradle.api.file.FileTree
import org.gradle.api.internal.file.copy.ClosureBackedTransformer
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.SourceTask
import org.gradle.api.tasks.TaskAction
import org.openmicroscopy.api.types.Language
import org.openmicroscopy.api.types.Prefix
import org.openmicroscopy.api.utils.ExtensionTransformer

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

@CompileStatic
class SplitTask extends SourceTask {

    /**
     * Directory to spit out source files
     */
    private final DirectoryProperty outputDir = project.objects.directoryProperty()

    /**
     * List of the languages we want to split from .combinedFiles files
     */
    private final Property<Language> language = project.objects.property(Language)

    /**
     * Optional file name output transformer
     */
    private Transformer<String, String> nameTransformer = noOpTransformer()

    @TaskAction
    void createSources() {
        language.get().prefixes.each { Prefix prefix ->
            // Transform prefix enum to lower case for naming
            String prefixName = prefix.name().toLowerCase()

            project.copy { CopySpec c ->
                c.into outputDir.get()
                c.from getSource()
                c.rename createTransformer(prefix)
                c.filter { String line ->
                    filerLine(line, prefixName)
                }
            }

            if (prefix == Prefix.JAV) {
                moveJavaFilesToPackage()
            }
        }
    }

    void moveJavaFilesToPackage() {
        File outputDirFile = outputDir.asFile.get()

        FileCollection javaSrc = project.fileTree(outputDirFile).matching {
            include("**/*.java")
        }

        javaSrc.files.each { File javaFile ->
            CompilationUnit cu = StaticJavaParser.parse(javaFile)

            java.util.Optional<PackageDeclaration> packageDeclaration = cu.getPackageDeclaration()
            if (packageDeclaration.present) {
                String packageName = packageDeclaration.get().name

                // Convert package to path, relative to outputDir
                Path packagePath = Paths.get(outputDirFile.getPath(), packageName.replace(".", "/"))
                if (!Files.exists(packagePath)) {
                    Files.createDirectories(packagePath)
                }

                // Move each file to package location
                Files.move(javaFile.toPath(), packagePath.resolve(javaFile.name), StandardCopyOption.REPLACE_EXISTING)

                // Log change
                logger.info("Moving $javaFile.name from $outputDirFile to $packagePath")
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

    void setLanguage(Provider<? extends Language> lang) {
        this.language.set(lang)
    }

    void rename(Closure closure) {
        if (closure) {
            this.nameTransformer = new ClosureBackedTransformer(closure)
        }
    }

    void rename(Transformer<String, String> transformer) {
        if (transformer) {
            this.nameTransformer = transformer
        }
    }

    @Nested
    private Transformer<String, String> getNameTransformer() {
        return this.nameTransformer
    }

    private Transformer<String, String> createTransformer(Prefix prefix) {
        return new ExtensionTransformer(prefix, this.nameTransformer)
    }

    private static String filerLine(String line, String prefix) {
        return line.matches("^\\[all](.*)|^\\[${prefix}](.*)") ?
                line.replaceAll("^\\[all]\\s?|^\\[${prefix}]\\s?", "") :
                null
    }

    private static <T> Transformer<T, T> noOpTransformer() {
        return new Transformer<T, T>() {
            T transform(T original) {
                return original
            }
        }
    }

}
