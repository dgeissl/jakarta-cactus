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
 * 4. The names "The Jakarta Project", "Ant", and "Apache Software
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
package org.apache.commons.cactus.client;

import java.util.*;
import java.net.*;
import java.io.*;

import junit.framework.*;
import org.apache.commons.cactus.*;

/**
 * Abstract class that all HTTP clients to the server redirector must extend.
 * It provides a common abstraction of the <code>doTest</code> method and
 * provides the URL to the server redirector servlet or JSP. It also makes some
 * configuration checks to verify if the runtime configuration is right (see
 * the <code>ClientConfigurationChecker</code> class for details).
 *
 * @author <a href="mailto:vmassol@apache.org">Vincent Massol</a>
 *
 * @version $Id$
 */
public abstract class AbstractHttpClient
{
    /**
     * Name of the Cactus configuration file
     */
    public final static String CONFIG_NAME = "cactus";

    /**
     * Properties file holding configuration data for Cactus.
     */
    public final static ResourceBundle CONFIG =
        PropertyResourceBundle.getBundle(CONFIG_NAME);

    /**
     * Check client configuration parameters.
     */
    static {
        ClientConfigurationChecker.checkConfigProperties();
    }

    /**
     * Calls the test method indirectly by calling the Redirector servlet and
     * then open a second HTTP connection to retrieve the test results.
     *
     * @param theRequest the request containing all data to pass to the
     *                   redirector servlet.
     *
     * @return the <code>HttpURLConnection</code> that contains the HTTP
     *         response when the test was called.
     *
     * @exception Throwable if an error occured in the test method or in the
     *                      redirector servlet.
     */
    public abstract HttpURLConnection doTest(ServletTestRequest theRequest)
        throws Throwable;

}
