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
package org.apache.cactus.integration.ant.container;

import java.io.File;

import org.apache.cactus.integration.ant.util.AntTaskFactory;
import org.apache.commons.logging.Log;
import org.apache.tools.ant.types.Environment.Variable;

/**
 * Interface for classes that can be used as nested elements in the
 * <code>&lt;containers&gt;</code> element of the <code>&lt;cactus&gt;</code>
 * task.
 * 
 * @author <a href="mailto:cmlenz@apache.org">Christopher Lenz</a>
 * @author <a href="mailto:vmassol@apache.org">Vincent Massol</a>
 *
 * @version $Id$
 */
public interface Container
{
    // Public Methods ----------------------------------------------------------

    /**
     * Returns a displayable name of the container for logging purposes.
     * 
     * @return The container name
     */
    String getName();

    /**
     * Returns the port to which the container should listen.
     * 
     * @return The port
     */
    int getPort();

    /**
     * @return the time to wait after the container has been started up. 
     */
    long getStartUpWait();
    
    /**
     * Returns the value of the 'todir' attribute.
     * 
     * @return The output directory
     */
    File getToDir();

    /**
     * @return the list of system properties that will be set in the container 
     *         JVM
     */
    Variable[] getSystemProperties();
    
    /**
     * Subclasses should implement this method to perform any initialization
     * that may be necessary. This method is called before any of the accessors
     * or the methods {@link AbstractContainer#startUp} and
     * {@link AbstractContainer#shutDown} are called, but after all attributes
     * have been set.
     */
    void init();

    /**
     * Returns whether the container element is enabled, which is determined by
     * the evaluation of the if- and unless conditions
     * 
     * @return <code>true</code> if the container is enabled
     */
    boolean isEnabled();

    /**
     * Returns whether a specific test case is to be excluded from being run in
     * the container.
     * 
     * @param theTestName The fully qualified name of the test fixture class
     * @return <code>true</code> if the test should be excluded, otherwise
     *         <code>false</code>
     */
    boolean isExcluded(String theTestName);

    /**
     * Sets the factory to use for creating Ant tasks.
     * 
     * @param theFactory The factory to use for creating Ant tasks
     */
    void setAntTaskFactory(AntTaskFactory theFactory);

    /**
     * Sets the log which the implementation should use.
     *  
     * @param theLog The log to set
     */
    void setLog(Log theLog);

    /**
     * Sets the file that should be deployed to the container. This can be
     * either a WAR or an EAR file, depending on the capabilities of the
     * container.
     * 
     * @param theDeployableFile The file to deploy
     */
    void setDeployableFile(DeployableFile theDeployableFile);

    /**
     * Sets the system properties that will be passed to the JVM in which the
     * server will execute.
     * 
     * @param theProperties the list of system properties to set in the 
     *        container JVM
     */
    void setSystemProperties(Variable[] theProperties);

    /**
     * Subclasses must implement this method to perform the actual task of 
     * starting up the container.
     */
    void startUp();

    /**
     * Subclasses must implement this method to perform the actual task of 
     * shutting down the container.
     */
    void shutDown();
}
