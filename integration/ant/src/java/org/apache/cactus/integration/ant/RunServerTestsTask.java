/*
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
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
package org.apache.cactus.integration.ant;

import java.net.URL;

import org.apache.cactus.integration.ant.container.ContainerRunner;
import org.apache.cactus.integration.ant.container.GenericContainer;
import org.apache.cactus.integration.ant.util.AntLog;
import org.apache.cactus.integration.ant.util.AntTaskFactory;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

/**
 * Task to automate running in-container unit test. It has the following
 * syntax when used in Ant :
 * <code><pre>
 *   &lt;runservertests testURL="&t;url&gt;"
 *          starttarget="&lt;start target name&gt;"
 *          stoptarget="&lt;stop target name&gt;"
 *          testtarget="&lt;test target name&gt;"/>
 * </pre></code>
 * where <code>&lt;url&gt;</code> is the URL that is used by this task to
 * ensure that the server is running. Indeed, the algorithm is as follow :
 * <ul>
 *  <li>Checks if server is running by trying to open an HTTP connection to
 *  the URL,</li>
 *  <li>If it fails, call the start target and loop until the HTTP connection
 *  the URL can be established,</li>
 *  <li>Call the test target. This target is supposed to start the test,
 *  usually by running the junit Ant task,</li>
 *  <li>When the tests are finished, call the stop target to stop the server.
 *  Note: The stop target is called only if the server was not already running
 *  when this task was executed.</li>
 * </ul>
 *
 * @author <a href="mailto:vmassol@apache.org">Vincent Massol</a>
 * @author <a href="mailto:cmlenz@apache.org">Christopher Lenz</a>
 * 
 * @since Cactus 1.5
 * @version $Id$
 */
public class RunServerTestsTask extends Task
{

    // Instance Variables ------------------------------------------------------

    /**
     * The generic container.
     */
    private GenericContainer container = new GenericContainer();

    /**
     * The hook that is called when the tests should be run.
     */
    private GenericContainer.Hook testHook;

    /**
     * The URL that is continuously pinged to verify if the server is running.
     */
    private URL testUrl;

    /**
     * Timeout after which we stop trying to connect to the test URL (in ms).
     */
    private long timeout = 180000;
    
    /**
     * The factory for creating ant tasks that is passed to the containers.
     */
    private transient AntTaskFactory antTaskFactory = new AntTaskFactory()
    {
        public Task createTask(String theName)
        {
            Task retVal = getProject().createTask(theName);
            if (retVal != null)
            {
                retVal.setTaskName(getTaskName());
                retVal.setLocation(getLocation());
                retVal.setOwningTarget(getOwningTarget());
            }
            return retVal;
        }
    };

    // Task Implementation -----------------------------------------------------

    /**
     * @see Task#execute()
     */
    public void execute() throws BuildException
    {
        if (!this.container.isStartUpSet())
        {
            throw new BuildException("You must specify either a nested [start] "
                + "element or the [starttarget] attribute");
        }
        
        if (!this.container.isShutDownSet())
        {
            throw new BuildException("You must specify either a nested [stop] "
                + "element or the [stoptarget] attribute");
        }

        if (this.testHook == null)
        {
            throw new BuildException("You must specify either a nested [test] "
                + "element or the [testtarget] attribute");
        }

        // Verify that a test URL has been specified
        if (this.testUrl == null)
        {
            throw new BuildException(
                "The [testurl] attribute must be specified");
        }

        this.container.setAntTaskFactory(antTaskFactory);

        ContainerRunner runner = new ContainerRunner(this.container);
        runner.setLog(new AntLog(this));
        runner.setUrl(this.testUrl);
        runner.setTimeout(this.timeout);
        runner.startUpContainer();
        try
        {
            this.testHook.execute();
        }
        finally
        {
            runner.shutDownContainer();
        }
    }

    // Public Methods ----------------------------------------------------------

    /**
     * Creates a nested start element.
     * 
     * @return The start element
     */
    public GenericContainer.Hook createStart()
    {
        if (this.container.isStartUpSet())
        {
            throw new BuildException(
                "This task supports only one nested [start] element");
        }
        return this.container.createStartUp();
    }

    /**
     * Sets the target to call to start the server.
     *
     * @param theStartTarget the Ant target to call
     */
    public void setStartTarget(String theStartTarget)
    {
        if (this.container.isStartUpSet())
        {
            throw new BuildException("Either specify the [starttarget] "
                + "attribute or the nested [start] element, but not both");
        }
        this.container.setStartUpTarget(theStartTarget);
    }

    /**
     * Creates a nested stop element.
     * 
     * @return The stop element
     */
    public GenericContainer.Hook createStop()
    {
        if (this.container.isShutDownSet())
        {
            throw new BuildException(
                "This task supports only one nested [stop] element");
        }
        return this.container.createShutDown();
    }

    /**
     * Sets the target to call to stop the server.
     *
     * @param theStopTarget the Ant target to call
     */
    public void setStopTarget(String theStopTarget)
    {
        if (this.container.isShutDownSet())
        {
            throw new BuildException("Either specify the [stoptarget] "
                + "attribute or the nested [stop] element, but not both");
        }
        this.container.setShutDownTarget(theStopTarget);
    }

    /**
     * Creates a nested test element.
     * 
     * @return The test element
     */
    public GenericContainer.Hook createTest()
    {
        if (this.testHook != null)
        {
            throw new BuildException(
                "This task supports only one nested [test] element");
        }
        this.testHook = container.new Hook();
        return this.testHook;
    }

    /**
     * Sets the target to call to run the tests.
     *
     * @param theTestTarget the Ant target to call
     */
    public void setTestTarget(String theTestTarget)
    {
        if (this.testHook != null)
        {
            throw new BuildException("Either specify the [testtarget] "
                + "attribute or the nested [test] element, but not both");
        }
        this.testHook = container.new Hook();
        this.testHook.setTarget(theTestTarget);
    }

    /**
     * Sets the URL to call for testing if the server is running.
     *
     * @param theTestUrl the test URL to ping
     */
    public void setTestUrl(URL theTestUrl)
    {
        this.testUrl = theTestUrl;
    }

    /**
     * @param theTimeout the timeout after which we stop trying to call the test
     *        URL.
     */
    public void setTimeout(long theTimeout)
    {
        this.timeout = theTimeout;
    }

}
