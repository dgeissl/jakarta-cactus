/*
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Cactus" and "Apache Software
 *    Foundation" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */
package org.apache.cactus.integration.ant.container.weblogic;

import java.io.File;
import java.io.IOException;

import org.apache.cactus.integration.ant.container.AbstractJavaContainer;
import org.apache.cactus.integration.ant.util.ResourceUtils;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.Jar;
import org.apache.tools.ant.taskdefs.Java;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.FilterChain;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.ZipFileSet;

/**
 * Special container support for the Bea WebLogic 7.x application server.
 * 
 * FIXME: this doesn't work for me on JDK 1.3.1 and WL 7.0 SP2
 * 
 * @author <a href="mailto:cmlenz@apache.org">Christopher Lenz</a>
 * 
 * @version $Id$
 */
public class WebLogic7xContainer extends AbstractJavaContainer
{

    // Instance Variables ------------------------------------------------------

    /**
     * The Bea home directory.
     */
    private File beaHome;

    /**
     * The WebLogic 7.x installation directory.
     */
    private File dir;

    /**
     * The port to which the container should be bound.
     */
    private int port = 8080;

    /**
     * The temporary directory from which the container will be started.
     */
    private transient File tmpDir;

    // Public Methods ----------------------------------------------------------

    /**
     * Sets the Bea home directory.
     * 
     * @param theBeaHome The BEA home directory
     */
    public final void setBeaHome(File theBeaHome)
    {
        this.beaHome = theBeaHome;
    }

    /**
     * Sets the WebLogic 7.x installation directory.
     * 
     * @param theDir The directory to set
     */
    public final void setDir(File theDir)
    {
        this.dir = theDir;
    }

    /**
     * Sets the port to which the container should listen.
     * 
     * @param thePort The port to set
     */
    public final void setPort(int thePort)
    {
        this.port = thePort;
    }

    // AbstractContainer Implementation ----------------------------------------

    /**
     * @see org.apache.cactus.integration.ant.container.Container#getName
     */
    public final String getName()
    {
        return "WebLogic 7.x";
    }

    /**
     * Returns the port to which the container should listen.
     * 
     * @return The port
     */
    public final int getPort()
    {
        return this.port;
    }

    /**
     * @see org.apache.cactus.integration.ant.container.Container#init
     */
    public final void init()
    {
        if (!this.dir.isDirectory())
        {
            throw new BuildException(this.dir + " is not a directory");
        }
    }

    /**
     * @see org.apache.cactus.integration.ant.container.Container#startUp
     */
    public final void startUp()
    {
        try
        {
            prepare("cactus/weblogic7x");
            
            Java java = createJavaForStartUp();
            java.setDir(new File(this.tmpDir, "testdomain"));
            
            java.createJvmarg().setValue("-hotspot");
            java.createJvmarg().setValue("-ms64m");
            java.createJvmarg().setValue("-mx64m");
            
            File serverDir = new File(this.dir, "server");
            
            Path libraryPath = new Path(getProject());
            libraryPath.createPathElement().setPath(
                getProject().getProperty("java.library.path"));
            libraryPath.createPathElement().setLocation(
                new File(serverDir, "bin"));
            java.addSysproperty(
                createSysProperty("java.library.path", libraryPath));
            java.addSysproperty(
                createSysProperty("weblogic.Name", "testserver"));
            java.addSysproperty(
                createSysProperty("bea.home", this.beaHome));
            java.addSysproperty(
                createSysProperty("weblogic.management.username", "system"));
            java.addSysproperty(
                createSysProperty("weblogic.management.password", "password"));
            java.addSysproperty(
                createSysProperty("java.security.policy",
                    "=./server/lib/weblogic.policy"));

            Path classpath = java.createClasspath();
            classpath.createPathElement().setLocation(
                new File(serverDir, "lib/weblogic_sp.jar"));
            classpath.createPathElement().setLocation(
                new File(serverDir, "lib/weblogic.jar"));

            java.setClassname("weblogic.Server");
            java.execute();
        }
        catch (IOException ioe)
        {
            throw new BuildException(ioe);
        }
    }

    /**
     * @see org.apache.cactus.integration.ant.container.Container#shutDown
     */
    public final void shutDown()
    {
        Java java = createJavaForShutDown();
            
        File serverDir = new File(this.dir, "server");

        Path classpath = java.createClasspath();
        classpath.createPathElement().setLocation(
            new File(serverDir, "lib/weblogic_sp.jar"));
        classpath.createPathElement().setLocation(
            new File(serverDir, "lib/weblogic.jar"));

        java.setClassname("weblogic.Admin");
        java.createArg().setValue("-url");
        java.createArg().setValue("t3://localhost:" + getPort());
        java.createArg().setValue("-username");
        java.createArg().setValue("system");
        java.createArg().setValue("-password");
        java.createArg().setValue("password");
        java.createArg().setValue("SHUTDOWN");
        java.execute();
    }

    // Private Methods ---------------------------------------------------------

    /**
     * Prepares a temporary installation of the container and deploys the 
     * web-application.
     * 
     * @param theDirName The name of the temporary container installation
     *        directory
     * @throws IOException If an I/O error occurs
     */
    private void prepare(String theDirName) throws IOException
    {
        FilterChain filterChain = createFilterChain();
        
        this.tmpDir = createTempDirectory(theDirName);

        File testDomainDir = createDirectory(this.tmpDir, "testdomain");
        ResourceUtils.copyResource(getProject(),
            RESOURCE_PATH + "weblogic7x/config.xml",
            new File(testDomainDir, "config.xml"),
            filterChain);
        ResourceUtils.copyResource(getProject(),
            RESOURCE_PATH + "weblogic7x/fileRealm.properties",
            new File(testDomainDir, "fileRealm.properties"),
            filterChain);
        ResourceUtils.copyResource(getProject(),
            RESOURCE_PATH + "weblogic7x/fileRealm.properties",
            new File(testDomainDir, "fileRealm.properties"),
            filterChain);
        ResourceUtils.copyResource(getProject(),
            RESOURCE_PATH + "weblogic7x/SerializedSystemIni.dat",
            new File(testDomainDir, "SerializedSystemIni.dat"),
            filterChain);
        ResourceUtils.copyResource(getProject(),
            RESOURCE_PATH + "weblogic7x/DefaultAuthenticatorInit.ldift",
            new File(testDomainDir, "DefaultAuthenticatorInit.ldift"),
            filterChain);

        File testServerDir = createDirectory(testDomainDir, "testserver");
        File ldapFilesDir =
            createDirectory(testServerDir, "ldap/ldapfiles");
        ResourceUtils.copyResource(getProject(), RESOURCE_PATH
            + "weblogic7x/testserver/ldap/ldapfiles/EmbeddedLDAP.data",
            new File(ldapFilesDir, "EmbeddedLDAP.data"),
            filterChain);
        ResourceUtils.copyResource(getProject(), RESOURCE_PATH
            + "weblogic7x/testserver/ldap/ldapfiles/EmbeddedLDAP.delete",
            new File(ldapFilesDir, "EmbeddedLDAP.delete"),
            filterChain);
        ResourceUtils.copyResource(getProject(), RESOURCE_PATH
            + "weblogic7x/testserver/ldap/ldapfiles/EmbeddedLDAP.index",
            new File(ldapFilesDir, "EmbeddedLDAP.index"),
            filterChain);
        ResourceUtils.copyResource(getProject(), RESOURCE_PATH
            + "weblogic7x/testserver/ldap/ldapfiles/EmbeddedLDAP.tran",
            new File(ldapFilesDir, "EmbeddedLDAP.tran"),
            filterChain);
        ResourceUtils.copyResource(getProject(), RESOURCE_PATH
            + "weblogic7x/testserver/ldap/ldapfiles/EmbeddedLDAP.trpos",
            new File(ldapFilesDir, "EmbeddedLDAP.trpos"),
            filterChain);
        ResourceUtils.copyResource(getProject(), RESOURCE_PATH
            + "weblogic7x/testserver/ldap/ldapfiles/EmbeddedLDAP.twpos",
            new File(ldapFilesDir, "EmbeddedLDAP.trwpos"),
            filterChain);

        // Extract the weblogic.xml descriptor
        File weblogicXml = new File(this.tmpDir, "weblogic.xml");
        ResourceUtils.copyResource(getProject(),
            RESOURCE_PATH + "weblogic7x/weblogic.xml",
            weblogicXml, filterChain);

        // deploy the web-app by copying the WAR file into the applications
        // directory, adding the weblogic.xml descriptor
        File applicationsDir =
            createDirectory(testDomainDir, "applications");
        Jar jar = (Jar) createAntTask("jar");
        jar.setDestFile(new File(applicationsDir, getWarFile().getName()));
        ZipFileSet zip = new ZipFileSet();
        zip.setSrc(getWarFile());
        jar.addZipfileset(zip);
        FileSet fileSet = new FileSet();
        fileSet.setDir(this.tmpDir);
        fileSet.createInclude().setName("weblogic.xml");
        jar.addFileset(fileSet);
        jar.execute();
    }

}
