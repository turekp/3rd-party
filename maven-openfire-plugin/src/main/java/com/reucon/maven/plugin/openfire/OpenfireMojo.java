package com.reucon.maven.plugin.openfire;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.apache.maven.archiver.MavenArchiver;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProjectHelper;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.jar.ManifestException;

import java.io.File;
import java.io.IOException;

/**
 * Build an Openfire Plugin jar.
 *
 * @author <a href="evenisse@apache.org">Emmanuel Venisse</a>
 * @version $Id: OpenfireMojo.java 1035 2008-04-08 17:21:16Z srt $
 * @goal openfire-plugin
 * @phase package
 * @requiresDependencyResolution runtime
 */
public class OpenfireMojo extends AbstractOpenfireMojo
{
    /**
     * The directory for the generated WAR.
     *
     * @parameter expression="${project.build.directory}"
     * @required
     */
    private String outputDirectory;

    /**
     * The name of the generated WAR.
     *
     * @parameter expression="${project.build.finalName}"
     * @required
     */
    private String warName;

    /**
     * Classifier to add to the artifact generated. If given, the artifact will be an attachment instead.
     *
     * @parameter
     */
    private String classifier;

    /**
     * @component
     */
    private MavenProjectHelper projectHelper;

    /**
     * Whether this is the main artifact being built. Set to <code>false</code> if you don't want to install or
     * deploy it to the local repository instead of the default one in an execution.
     *
     * @parameter expression="${primaryArtifact}" default-value="true"
     */
    private boolean primaryArtifact;

    // ----------------------------------------------------------------------
    // Implementation
    // ----------------------------------------------------------------------

    /**
     * Overload this to produce a test-war, for example.
     */
    protected String getClassifier()
    {
        return classifier;
    }

    protected static File getWarFile(File basedir, String finalName, String classifier)
    {
        if (classifier == null)
        {
            classifier = "";
        }
        else if (classifier.trim().length() > 0 && !classifier.startsWith("-"))
        {
            classifier = "-" + classifier;
        }

        return new File(basedir, finalName + classifier + ".jar");
    }

    /**
     * Executes the OpenfireMojo on the current project.
     *
     * @throws MojoExecutionException if an error occured while building the webapp
     */
    public void execute() throws MojoExecutionException, MojoFailureException
    {
        File warFile = getWarFile(new File(outputDirectory), warName, classifier);

        try
        {
            performPackaging(warFile);
        }
        catch (DependencyResolutionRequiredException e)
        {
            throw new MojoExecutionException("Error assembling Openfire Plugin: " + e.getMessage(), e);
        }
        catch (ManifestException e)
        {
            throw new MojoExecutionException("Error assembling Openfire Plugin", e);
        }
        catch (IOException e)
        {
            throw new MojoExecutionException("Error assembling Openfire Plugin", e);
        }
        catch (ArchiverException e)
        {
            throw new MojoExecutionException("Error assembling Openfire Plugin: " + e.getMessage(), e);
        }
    }

    /**
     * Generates the webapp according to the <tt>mode</tt> attribute.
     *
     * @param warFile the target war file
     * @throws IOException
     * @throws ArchiverException
     * @throws ManifestException
     * @throws DependencyResolutionRequiredException
     *
     */
    private void performPackaging(File warFile)
            throws IOException, ArchiverException, ManifestException, DependencyResolutionRequiredException,
            MojoExecutionException, MojoFailureException
    {
        buildExplodedOpenfirePlugin(getOpenfirePluginDirectory());

        //generate war file
        getLog().info("Generating Openfire Plugin " + warFile.getAbsolutePath());

        MavenArchiver archiver = new MavenArchiver();

        archiver.setArchiver(jarArchiver);

        archiver.setOutputFile(warFile);

        jarArchiver.addDirectory(getOpenfirePluginDirectory(), getIncludes(), getExcludes());

        //openfireArchiver.setWebxml(new File(getOpenfirePluginDirectory(), "web/WEB-INF/web.xml"));

        // create archive
        archiver.createArchive(getProject(), archive);

        String classifier = this.classifier;
        if (classifier != null)
        {
            projectHelper.attachArtifact(getProject(), "jar", classifier, warFile);
        }
        else
        {
            Artifact artifact = getProject().getArtifact();
            if (primaryArtifact)
            {
                artifact.setFile(warFile);
            }
            else if (artifact.getFile() == null || artifact.getFile().isDirectory())
            {
                artifact.setFile(warFile);
            }
        }
    }
}
