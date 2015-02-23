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
 * Filename: DockerfileGeneratorTes.java
 *
 * Author: Jose San Leandro Armendariz.
 *
 * Description: Tests for DockerfileGenerator.
 */
package org.acmsl.dockerfile.maven;

/* 
 * Importing Maven Test Harness classes.
 */
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.apache.maven.plugin.testing.MojoRule;

/*
 * Importing JDK classes.
 */
import java.io.File;

/*
 * Importing NotNull annotations.
 */
import org.jetbrains.annotations.NotNull;

/*
 * Importing checkthread.org annotations.
 */
import org.checkthread.annotations.ThreadSafe;

/*
 * Importing JUnit classes.
 */
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.junit.Rule;
import org.junit.Test;

/**
 * Tests for {@link DockerfileGenerator}.
 * @author <a href="mailto:chous@acm-sl.org">Jose San Leandro Armendariz</a>
 * Created: 2014/12/05
 */
@ThreadSafe
//@RunWith(JUnit4.class)
public class DockerfileMojoTest
//    extends AbstractMojoTestCase
{
    /**
     * {@inheritDoc}
     */
//    @Override
    protected void setUp()
        throws Exception
    {
//        super.setUp();
    }

    /**
     * {@inheritDoc}
     */
//    @Override
    protected void tearDown()
        throws Exception
    {
//        super.tearDown();
    }

    @Rule
    public MojoRule rule =
        new MojoRule()
        {
            /**
             * {@inheritDoc}
             */
            @Override
            protected void before()
                throws Throwable 
            {
            }

            /**
             * {@inheritDoc}
             */
            @Override
            protected void after()
            {
            }
        };

    /**
     * Checks whether the target project is found.
     * @throws Exception if any.
     */
//    @Test
    public void test_target_pom_is_found()
        throws Exception
    {
//        @NotNull final File pom = getTestFile("src/test/resources/target.xml");
//        Assert.assertNotNull(pom);
//        Assert.assertTrue(pom.exists());

//        @NotNull final DockerfileMojo myMojo = (DockerfileMojo) lookupMojo("dockerfile-maven-plugin", pom);
//        Assert.assertNotNull( myMojo );
//        myMojo.execute();
    }
}
