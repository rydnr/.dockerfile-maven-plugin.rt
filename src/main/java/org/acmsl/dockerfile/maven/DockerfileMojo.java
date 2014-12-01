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
package org.acmsl.queryj.tools.maven;

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

/*
 * Importing some Ant classes.
 */
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.Path;

/*
 * Importing NotNull annotations.
 */
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/*
 * Importing some JDK classes.
 */
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Set;

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
@Mojo( name = Literals.DOCKERFILE_L, defaultPhase = LifecyclePhase.GENERATE_SOURCES, threadSafe = true, executionStrategy = "once-per-session")
public class DockerfileMojo
    extends AbstractMojo
{
    /**
     * The location of pom.properties within the jar file.
     */
    protected static final String POM_PROPERTIES_LOCATION =
        "META-INF/maven/org.acmsl.queryj/queryj-maven/pom.properties";

    /**
     * The output directory.
     */
    @Parameter (name = Literals.OUTPUT_DIR, property = OUTPUT_DIR, required = false, defaultValue = "${project.build.dir}")
    private File m__OutputDir;

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
        final File result;

        final String aux = System.getProperty(OUTPUT_DIR);

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
        execute(log, retrieveVersion(retrievePomProperties(log)));
    }

    /**
     * Retrieves the version of Dockerfile Maven Plugin currently running.
     * @param properties the pom.properties information.
     * @return the version entry.
     */
    protected String retrieveVersion(@Nullable final Properties properties)
    {
        final String result;

        if (   (properties != null)
            && (properties.containsKey(VERSION_L)))
        {
            result = properties.getProperty(VERSION_LITERAL);
        }
        else
        {
            result = UNKNOWN_L;
        }

        return result;
    }

    /**
     * Executes Dockerfile Maven Plugin.
     * @param log the Maven log.
     * @param version the Dockerfile Maven Plugin version.
     * @throws MojoExecutionException if the process fails.
     */
    protected void execute(@NotNull final Log log, final String version)
        throws MojoExecutionException
    {
        boolean running = false;

        @Nullable final File outputDirPath = getOutputDir();

        @Nullable final QueryJTask task;

        if  (outputDirPath != null)
        {
            //initialize directories
            @NotNull final File outputDir = outputDirPath.getAbsoluteFile();

            if (   (!outputDir.exists())
                && (!outputDir.mkdirs()))
            {
                log.warn("Cannot create output folder: " + outputDir);
            }

            //execute task
            task = buildTask(version, log);

            log.info("Running QueryJ " + version);

            task.execute();

            running = true;
        }
        else
        {
            log.error("outputDir is null");
        }

        if (!running)
        {
            log.error("NOT running QueryJ " + version);
            throw new MojoExecutionException("QueryJ could not start");
        }
    }

    /**
     * Retrieves the pom.properties bundled within the QueryJ jar.
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

    /**
     * Instantiates a new task.
     * @param version the version.
     * @param log the log.
     * @return a new {@link org.acmsl.queryj.tools.ant.QueryJTask}.
     */
    @NotNull
    protected QueryJTask instantiateTask(
        @NotNull final String version, @NotNull final org.apache.commons.logging.Log log)
    {
        return new QueryJTask(log);
    }

    /**
     * Builds the QueryJ task.
     * @param version the version.
     * @param log the Maven log.
     * @return such info.
     */
    @NotNull
    protected QueryJTask buildTask(@NotNull final String version, @NotNull final Log log)
    {
        @NotNull final CommonsLoggingMavenLogAdapter t_Log = new CommonsLoggingMavenLogAdapter(log);

        @NotNull final QueryJTask result = instantiateTask(version, t_Log);

        initLogging(t_Log);

        @NotNull final Project project = new AntProjectAdapter(new Project(), log);

        result.setProject(project);

        @NotNull final Path path = new Path(project);
        result.setClasspath(path);

        result.setVersion(version);

        t_Log.debug("Catalog: " + getCatalog());
        result.setCatalog(getCatalog());

        t_Log.debug("Driver: " + getDriver());
        result.setDriver(getDriver());

        t_Log.debug("JNDI DataSource: " + getJndiDataSource());
        result.setJndiDataSource(getJndiDataSource());

        t_Log.debug("Output dir: " + getOutputDir());
        result.setOutputdir(getOutputDir());

        t_Log.debug("Package name: " + getPackageName());
        result.setPackage(getPackageName());

        t_Log.debug("Repository: " + getRepository());
        result.setRepository(getRepository());

        t_Log.debug("Schema: " + getSchema());
        result.setSchema(getSchema());

        t_Log.debug("Url: " + getUrl());
        result.setUrl(getUrl());

        t_Log.debug("Username: " + getUsername());
        result.setUsername(getUsername());

        t_Log.debug("Password specified: " + (getPassword() != null));
        result.setPassword(getPassword());

        t_Log.debug("SQL XML file: " + getSqlXmlFile());
        result.setSqlXmlFile(getSqlXmlFile());

        t_Log.debug("Header file: " + getHeaderFile());
        result.setHeaderfile(getHeaderFile());

        t_Log.debug(
            "Grammar bundle: " + getGrammarFolder() + File.separator
            + getGrammarName() + "(_" + Locale.US.getLanguage().toLowerCase(Locale.US)
            + ")" + getGrammarSuffix());

        result.setGrammarFolder(getGrammarFolder());
        result.setGrammarName(getGrammarName());
        result.setGrammarSuffix(getGrammarSuffix());

        buildTables(result);

        @Nullable final String encoding = getEncoding();

        if (encoding == null)
        {
            t_Log.warn("Using default (platform-dependent) encoding to generate QueryJ sources");
        }
        else
        {
            t_Log.info("Using encoding: \"" + encoding + "\" to generate QueryJ sources");
        }
        result.setEncoding(encoding);

        final boolean caching = true;

        final int threadCount = getRequestThreadCount();

        t_Log.info("Using " + threadCount + " threads");
        result.setThreadCount(threadCount);

        final boolean disableGenerationTimestamps = getDisableTimestamps();

        result.setDisableGenerationTimestamps(disableGenerationTimestamps);

        final boolean disableNotNullAnnotations = getDisableNotNullAnnotations();

        result.setDisableNotNullAnnotations(disableNotNullAnnotations);

        final boolean disableCheckthreadAnnotations = getDisableCheckthreadAnnotations();

        result.setDisableCheckthreadAnnotations(disableCheckthreadAnnotations);

        return result;
    }

    /**
     * Builds the table list.
     * @param task the task.
     */
    protected void buildTables(@NotNull final QueryJTask task)
    {
        @NotNull final Table[] array = getTables();
        Table table;
        @Nullable final AntTablesElement element;
        @Nullable AntTableElement tableElement;
        List<Field> fields;
        int fieldCount;
        Field field;
        @Nullable AntFieldElement fieldElement;

        final int count =  array.length;

        if  (count > 0)
        {
            element =
                (AntTablesElement) task.createDynamicElement(ParameterValidationHandler.TABLES);

            if (element != null)
            {
                for (@Nullable final Table anArray : array)
                {
                    table = anArray;

                    tableElement =
                        (AntTableElement) element.createDynamicElement(AntTablesElement.TABLE);

                    if (   (table != null)
                        && (tableElement != null))
                    {
                        @Nullable final String name = table.getName();

                        if (name != null)
                        {
                            tableElement.setDynamicAttribute("name", name);
                        }

                        fields = table.getFields();

                        fieldCount = (fields == null) ? 0 : fields.size();

                        if (fields != null)
                        {
                            for (int fieldIndex = 0; fieldIndex < fieldCount; fieldIndex++)
                            {
                                field = fields.get(fieldIndex);

                                if (field != null)
                                {
                                    fieldElement =
                                        (AntFieldElement) tableElement.createDynamicElement(
                                            AntExternallyManagedFieldsElement.FIELD_LITERAL);

                                    if (fieldElement != null)
                                    {
                                        fieldElement.setDynamicAttribute(
                                            "name", field.getName());
                                        fieldElement.setDynamicAttribute(
                                            "type", field.getType());
                                        @Nullable final String t_strPk = field.getPk();
                                        fieldElement.setDynamicAttribute(
                                            "pk", String.valueOf(Boolean.valueOf(t_strPk == null)));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Tries to get thread count if a Maven 3 build, using reflection as the plugin must not be maven3 api dependant
     *
     * @return number of thread for this build or 1 if not multi-thread build
     */
    protected int getRequestThreadCount()
    {
        int result = getRequestThreadCountFromSystemProperties();

        if (result < 1)
        {
            result = getRequestThreadCountFromMaven();
        }

        /*
        if (result < 1)
        {
            result = getRequestThreadCountFromRuntime();
        }
        */

        if (result < 1)
        {
            result = 1;
        }

        return result;
    }

    /**
     * Tries to get thread count if a Maven 3 build, using reflection as the plugin must not be maven3 api dependant
     *
     * @return number of thread for this build or 1 if not multi-thread build
     */
    protected int getRequestThreadCountFromMaven()
    {
        int result = 0;

        try
        {
            final Method getRequestMethod = this.session.getClass().getMethod("getRequest");
            final Object mavenExecutionRequest = getRequestMethod.invoke(this.session);
            final Method getThreadCountMethod = mavenExecutionRequest.getClass().getMethod("getThreadCount");
            final String threadCount = (String) getThreadCountMethod.invoke(mavenExecutionRequest);
            result  = Integer.valueOf(threadCount);
        }
        catch (@NotNull final Throwable unexpectedError)
        {
            getLog().debug( "unable to get thread count for the current build: " + unexpectedError.getMessage());
        }

//        if (   (result == 1)
//            && (isMultithreadEnabled()))
//        {
//            result = Runtime.getRuntime().availableProcessors();
//        }

        return result;
    }

    /**
     * Retrieves the thread count, from Runtime.
     * @return such information.
     */
    protected int getRequestThreadCountFromRuntime()
    {
        return Runtime.getRuntime().availableProcessors();
    }

    /**
     * Checks whether multi-threading is enabled.
     * @return {@code true} in such case.
     */
    @SuppressWarnings("unchecked")
    protected boolean isMultithreadEnabled()
    {
        @NotNull final Set<Map.Entry<?, ?>> entrySet = (Set<Map.Entry<?, ?>>) super.getPluginContext().entrySet();

        for (@NotNull final Map.Entry<?, ?> item : entrySet)
        {
            getLog().info(item.getKey() + " -> " + item.getValue());
        }

        return true;
    }

    /**
     * Retrieves the thread count from system properties (queryj.threadCount).
     * @return such value.
     */
    public int getRequestThreadCountFromSystemProperties()
    {
        int result = 0;

        @Nullable final String aux = System.getProperty("queryj.threadCount");

        if (aux != null)
        {
            result = Integer.parseInt(aux);
        }

        return result;
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    public String toString()
    {
        return
              "{ \"catalog\": \"" + catalog + '"'
            + ", \"driver\": \"" + m__strDriver + '"'
            + ", \"url\": \"" + m__strUrl + '"'
            + ", \"username\": \"" + m__strUsername + '"'
            + ", \"password\": \"" + m__strPassword + '"'
            + ", \"schema\": \"" + schema + '"'
            + ", \"repository\": \"" + m__strRepository + '"'
            + ", \"packageName\": \"" + m__strPackageName + '"'
            + ", \"outputDir\": \"" + m__OutputDir + '"'
            + ", \"jndiDataSource\": \"" + m__strJndiDataSource + '"'
            + ", \"sqlXmlFile\": \"" + m__SqlXmlFile + '"'
            + ", \"headerFile\": \"" + m__HeaderFile + '"'
            + ", \"grammarFolder\": \"" + m__GrammarFolder + '"'
            + ", \"grammarName\": \"" + m__strGrammarName + '"'
            + ", \"grammarSuffix\": \"" + m__strGrammarSuffix + '"'
            + ", \"tables\": " + Arrays.toString(m__aTables) + '"'
            + ", \"encoding\": \"" + m__strEncoding + '"'
            + ", \"disableGenerationTimestamps\": \"" + m__bDisableGenerationTimestamps + '"'
            + ", \"disableNotNullAnnotations\": \"" + m__bDisableNotNullAnnotations + '"'
            + ", \"disableCheckthreadAnnotations\": \"" + m__bDisableCheckthreadAnnotations + '"'
            + ", \"session\": \"" + session.hashCode() + '"'
            + ", \"class\": \"QueryJMojo\""
            + ", \"package\": \"org.acmsl.queryj.tools.maven\" }";
    }
}
