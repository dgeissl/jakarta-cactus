/*
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2002 The Apache Software Foundation.  All rights
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
 * 4. The names "The Jakarta Project", "Cactus", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
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
 */
package org.apache.cactus.server;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Vector;
import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

/**
 * Abstract wrapper around <code>ServletContext</code>. This class provides
 * a common implementation of the wrapper for the different servlet API. In
 * addition to implementing the <code>ServletContext</code> interface it
 * provides additional features helpful for writing unit tests. More
 * specifically the <code>getRequestDispatcher()</code> method is overrided
 * to return an request dispatcher wrapper. In addition logs generated by
 * calls to the <code>log()</code> methods can be retrieved and asserted by
 * calling the <code>getLogs()</code> method.
 *
 * @author <a href="mailto:vmassol@apache.org">Vincent Massol</a>
 *
 * @version $Id$
 */
public abstract class AbstractServletContextWrapper implements ServletContext
{
    /**
     * The original servlet context object
     */
    protected ServletContext originalContext;

    /**
     * The logs resulting from calling the <code>log()</code> methods
     */
    private Vector logs = new Vector();

    // New methods ---------------------------------------------------------

    /**
     * Returns all the text logs that have been generated using the
     * <code>log()</code> methods so that it is possible to easily assert the
     * content of the logs. This method does not return the exceptions or
     * throwable sent for logging; it only returns the messages.
     *
     * @return the logs as a vector of strings (each string contains the
     *         message that was sent for logging).
     */
    public Vector getLogs()
    {
        return this.logs;
    }

    // Interface methods ---------------------------------------------------

    /**
     * @param theOriginalContext the original servlet context object
     */
    public AbstractServletContextWrapper(ServletContext theOriginalContext)
    {
        this.originalContext = theOriginalContext;
    }

    public void setAttribute(String theName, Object theAttribute)
    {
        this.originalContext.setAttribute(theName, theAttribute);
    }

    public void removeAttribute(String theName)
    {
        this.originalContext.removeAttribute(theName);
    }

    /**
     * Intercept the log call and add the message to an internal vector of
     * log messages that can then later be retrieved and asserted by the
     * test case writer. Note that the throwable is not saved.
     *
     * @see #getLogs()
     */
    public void log(String theMessage, Throwable theCause)
    {
        if (theMessage != null) {
            this.logs.addElement(theMessage);
        }
        this.originalContext.log(theMessage, theCause);
    }

    /**
     * Intercept the log call and add the message to an internal vector of
     * log messages that can then later be retrieved and asserted by the
     * test case writer. Note that the throwable is not saved.
     *
     * @see #getLogs()
     */
    public void log(String theMessage)
    {
        if (theMessage != null) {
            this.logs.addElement(theMessage);
        }
        this.originalContext.log(theMessage);
    }

    /**
     * Intercept the log call and add the message to an internal vector of
     * log messages that can then later be retrieved and asserted by the
     * test case writer. Note that the throwable is not saved.
     *
     * @see #getLogs()
     */
    public void log(Exception theException, String theMessage)
    {
        if (theMessage != null) {
            this.logs.addElement(theMessage);
        }
        this.originalContext.log(theException, theMessage);
    }

    public Enumeration getServlets()
    {
        return this.originalContext.getServlets();
    }

    public Enumeration getServletNames()
    {
        return this.originalContext.getServletNames();
    }

    public Servlet getServlet(String theName) throws ServletException
    {
        return this.originalContext.getServlet(theName);
    }

    public String getServerInfo()
    {
        return this.originalContext.getServerInfo();
    }

    public InputStream getResourceAsStream(String thePath)
    {
        return this.originalContext.getResourceAsStream(thePath);
    }

    public URL getResource(String thePath) throws MalformedURLException
    {
        return this.originalContext.getResource(thePath);
    }

    /**
     * @return our request dispatcher wrapper
     */
    public RequestDispatcher getRequestDispatcher(String thePath)
    {
        RequestDispatcher dispatcher = new RequestDispatcherWrapper(
            this.originalContext.getRequestDispatcher(thePath));
        return dispatcher;
    }

    /**
     * @return our request dispatcher wrapper or null if the servlet cannot
     *         be found.
     * @see javax.servlet.ServletContext.getNamedDispatcher(String)
     */
    public RequestDispatcher getNamedDispatcher(String theName)
    {
        RequestDispatcher wrappedDispatcher = null;

        RequestDispatcher originalDispatcher =
            this.originalContext.getNamedDispatcher(theName);

        if (originalDispatcher != null) {
            wrappedDispatcher =
                new RequestDispatcherWrapper(originalDispatcher);
        }

        return wrappedDispatcher;
    }

    public String getRealPath(String thePath)
    {
        return this.originalContext.getRealPath(thePath);
    }

    public int getMinorVersion()
    {
        return this.originalContext.getMinorVersion();
    }

    public String getMimeType(String theFilename)
    {
        return this.originalContext.getMimeType(theFilename);
    }

    public int getMajorVersion()
    {
        return this.originalContext.getMajorVersion();
    }

    public Enumeration getInitParameterNames()
    {
        return this.originalContext.getInitParameterNames();
    }

    public String getInitParameter(String theName)
    {
        return this.originalContext.getInitParameter(theName);
    }

    /**
     * @return our servlet context wrapper
     */
    public ServletContext getContext(String theUripath)
    {
        ServletContext context = new ServletContextWrapper(
            this.originalContext.getContext(theUripath));
        return context;
    }

    public Enumeration getAttributeNames()
    {
        return this.originalContext.getAttributeNames();
    }

    public Object getAttribute(String theName)
    {
        return this.originalContext.getAttribute(theName);
    }

}
