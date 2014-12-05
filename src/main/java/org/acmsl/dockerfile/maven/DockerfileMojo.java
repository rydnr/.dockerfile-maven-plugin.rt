/*
                        Dockerfile Maven Plugin

    Copyright (C) 2014-today  Jose San Leandro Armendariz
                              chous@acm-sl.org

    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the License, or any later version.

    This library is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    General Public License for more details.

    You should have received a copy of the GNU General Public
    License along with this library; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

    Thanks to ACM S.L. for distributing this library under the GPL license.
    Contact info: jose.sanleandro@acm-sl.com

 ******************************************************************************
 *
 * Filename: DockerfileMojo.java
 *
 * Author: Jose San Leandro Armendariz.
 *
 * Description: Executes Dockerfile plugin.
 */
package org.acmsl.dockerfile.maven;

/*
 * Importing some ACM-SL Java Commons classes.
 */
import org.acmsl.commons.logging.UniqueLogFactory;

/*
 * Importing some Maven classes.
 */
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

/*
 * Importing NotNull annotations.
 */
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/*
 * Importing some JDK classes.
 */
import java.util.Properties;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/*
 * Importing checkthread.org annotations.
 */
import org.checkthread.annotations.ThreadSafe;

/**
 * Executes Dockerfile plugin.
 * @author <a href="mailto:chous@acm-sl.org">Jose San Leandro Armendariz</a>
 * Created: 2014/12/01
 */
@SuppressWarnings("unused")
@ThreadSafe
@Mojo(name = Literals.DOCKERFILE_L, defaultPhase = LifecyclePhase.GENERATE_SOURCES, threadSafe = true, executionStrategy = "once-per-session")
public class DockerfileMojo
    extends AbstractMojo
{
    /**
     * The location of pom.properties within the jar file.
     */
    protected static final String POM_PROPERTIES_LOCATION =
        "META-INF/maven/org.acmsl/dockerfile-maven-plugin/pom.properties";

    /**
     * The output directory.
     */
    @Parameter (name = Literals.OUTPUT_DIR_CC, property = Literals.OUTPUT_DIR_CC, required = false, defaultValue = "${project.build.dir}/META-INF/")
    private File m__OutputDir;

    /**
     * The output directory.
     */
    @Parameter (name = Literals.TEMPLATE_L, property = Literals.TEMPLATE_L, required = true)
    private File m__Template;

    /**
     * The current build session instance. This is used for toolchain manager API calls.
     * @readonly
     */
    @Parameter (defaultValue = "${session}", required = true, readonly = true)
    private MavenSession session;

    /**
     * Specifies the output directory.
     * @param outputDir such directory.
     */
    protected final void immutableSetOutputDir(@NotNull final File outputDir)
    {
        m__OutputDir = outputDir;
    }

    /**
     * Specifies the output directory.
     * @param outputDir such directory.
     */
    public void setOutputDir(@NotNull final File outputDir)
    {
        immutableSetOutputDir(outputDir);
    }

    /**
     * Returns the output directory.
     * @return such directory.
     */
    @Nullable
    protected final File immutableGetOutputDir()
    {
        return m__OutputDir;
    }

    /**
     * Returns the output directory.
     * @return such directory.
     */
    @Nullable
    public File getOutputDir()
    {
        @Nullable final File result;

        @Nullable final String aux = System.getProperty(Literals.DOCKERFILE_OUTPUT_DIR);

        if (aux == null)
        {
            result = immutableGetOutputDir();
        }
        else
        {
            result = new File(aux);
        }

        return result;
    }

    /**
     * Executes Dockerfile Maven plugin.
     * @throws org.apache.maven.plugin.MojoExecutionException if the process fails.
     */
    @Override
    public void execute()
        throws MojoExecutionException
    {
        execute(getLog());
    }

    /**
     * Executes Dockerfile Maven plugin.
     * @param log the Maven log.
     * @throws MojoExecutionException if the process fails.
     */
    protected void execute(@NotNull final Log log)
        throws MojoExecutionException
    {
        execute(
            log,
            retrieveOwnVersion(retrievePomProperties(log)),
            retrieveTargetName(retrieveTargetProject()),
            retrieveTargetVersion(retrieveTargetProject()));
    }

    /**
     * Retrieves the version of Dockerfile Maven Plugin currently running.
     * @param properties the pom.properties information.
     * @return the version entry.
     */
    @NotNull
    protected String retrieveOwnVersion(@Nullable final Properties properties)
    {
        @NotNull final String result;

        if (   (properties != null)
            && (properties.containsKey(Literals.VERSION_L)))
        {
            result = properties.getProperty(Literals.VERSION_L);
        }
        else
        {
            result = Literals.UNKNOWN_L;
        }

        return result;
    }

    /**
     * Retrieves the target project.
     * @return such version.
     */
    @NotNull
    protected MavenProject retrieveTargetProject()
    {
        return this.session.getCurrentProject();
    }

    /**
     * Retrieves the target version.
     * @param project the target project.
     * @return such version.
     */
    @NotNull
    protected String retrieveTargetVersion(@NotNull final MavenProject project)
    {
        return project.getVersion();
    }

    /**
     * Retrieves the target name.
     * @param project the target project.
     * @return such name.
     */
    @NotNull
    protected String retrieveTargetName(@NotNull final MavenProject project)
    {
        return project.getName();
    }

    /**
     * Executes Dockerfile Maven Plugin.
     * @param log the Maven log.
     * @param ownVersion the Dockerfile Maven Plugin version.
     * @param targetName the target name.
     * @param targetVersion the target version.
     * @throws MojoExecutionException if the process fails.
     */
    protected void execute(
        @NotNull final Log log,
        @NotNull final String ownVersion,
        @NotNull final String targetName,
        @NotNull final String targetVersion)
      throws MojoExecutionException
    {
        boolean running = false;

        @Nullable final File outputDirPath = getOutputDir();

        if  (outputDirPath != null)
        {
            //initialize directories
            @NotNull final File outputDir = outputDirPath.getAbsoluteFile();

            if (   (!outputDir.exists())
                && (!outputDir.mkdirs()))
            {
                log.warn("Cannot create output folder: " + outputDir);
            }

            log.info("Running Dockerfile Maven Plugin " + ownVersion + " on " + targetName + " " + targetVersion);

            running = true;
        }
        else
        {
            log.error("outputDir is null");
        }

        if (!running)
        {
            log.error("NOT running Dockerfile Maven Plugin " + ownVersion);
            throw new MojoExecutionException("Dockerfile Maven Plugin could not start");
        }
    }

    /**
     * Retrieves the pom.properties bundled within the Dockerfile Maven Plugin jar.
     * @param log the Maven log.
     * @return such information.
     */
    @Nullable
    protected Properties retrievePomProperties(@NotNull final Log log)
    {
        @Nullable Properties result = null;

        try
        {
            @Nullable final InputStream pomProperties =
                getClass().getClassLoader().getResourceAsStream(POM_PROPERTIES_LOCATION);

            if (pomProperties != null)
            {
                result = new Properties();

                result.load(pomProperties);
            }
        }
        catch (@NotNull final IOException ioException)
        {
            log.warn(
                Literals.CANNOT_READ_MY_OWN_POM + POM_PROPERTIES_LOCATION,
                ioException);
        }

        return result;
    }

    /**
     * Initializes the logging.
     * @param commonsLoggingLog such log.
     */
    protected void initLogging(@NotNull final org.apache.commons.logging.Log commonsLoggingLog)
    {
        UniqueLogFactory.initializeInstance(commonsLoggingLog);
    }
}
