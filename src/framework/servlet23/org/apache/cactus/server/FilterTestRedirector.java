/*
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999 The Apache Software Foundation.  All rights
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

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.cactus.*;
import org.apache.cactus.util.log.*;

/**
 * Generic Filter redirector that calls a test method on the server side.
 *
 * @author <a href="mailto:vmassol@apache.org">Vincent Massol</a>
 *
 * @version $Id$
 * @see FilterTestCaller
 */
public class FilterTestRedirector implements Filter
{
    /**
     * Initialize the logging subsystem so that it can get it's configuration
     * details from the correct properties file. Initialization is done here
     * as this servlet is the first point of entry to the server code.
     */
    static {
        LogService.getInstance().init("/log_server.properties");
    }

    /**
     * The logger
     */
    private static Log logger =
        LogService.getInstance().getLog(FilterTestRedirector.class.getName());

    /**
     * The filter configuration object passed by the container when it calls
     * <code>init(FilterConfig)</code>
     */
    private FilterConfig config;

    /**
     * Handle the request. Extract from the HTTP request paramete the
     * Service to perform : call test method or return tests results.
     *
     * @param theRequest the incoming HTTP request which contains all needed
     *                   information on the test case and method to call
     * @param theResponse the response to send back to the client side
     * @param theFilterChain contains the chain of filters.
     */
    public void doFilter(ServletRequest theRequest,
        ServletResponse theResponse, FilterChain theFilterChain)
        throws IOException, ServletException
    {
        this.logger.entry("doFilter(...)");

        // Create implicit object holder
        FilterImplicitObjects objects = new FilterImplicitObjects();
        objects.setHttpServletRequest((HttpServletRequest)theRequest);
        objects.setHttpServletResponse((HttpServletResponse)theResponse);
        objects.setFilterConfig(this.config);
        objects.setServletContext(this.config.getServletContext());
        objects.setFilterChain(theFilterChain);

        FilterTestController controller = new FilterTestController();
        controller.handleRequest(objects);

        this.logger.exit("doFilter");
    }

    /**
     * Initialise this filter redirector. Called by the container.
     *
     * @param theConfig the filter config containing initialisation
     *                  parameters from web.xml
     */
    public void init(FilterConfig theConfig)
    {
        // Save the config to pass it to the test case later on
        this.config = theConfig;
    }

    /**
     * Provided so that it works with containers that do not support the
     * latest Filter spec yet ! (ex: Orion 1.5.2 !)
     */
    public void setFilterConfig(FilterConfig theConfig)
    {
        this.config = theConfig;
    }

    /**
     * Provided so that it works with containers that do not support the
     * latest Filter spec yet ! (ex: Orion 1.5.2 !)
     */
    public FilterConfig getFilterConfig()
    {
        return this.config;
    }

    /**
     * Destroy the filter. Called by the container.
     */
    public void destroy()
    {
    }

}