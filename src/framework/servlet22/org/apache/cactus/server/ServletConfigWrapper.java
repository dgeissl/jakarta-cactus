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
package org.apache.commons.cactus.server;

import java.util.*;
import java.io.*;
import java.security.*;
import java.net.*;

import javax.servlet.*;
import javax.servlet.http.*;

/**
 * Wrapper around <code>ServletConfig</code> which overrides the
 * <code>getServletContext()</code> method to return our own wrapper around
 * <code>ServletContext</code>.
 *
 * @version @version@
 * @see ServletContext
 */
public class ServletConfigWrapper implements ServletConfig
{
    /**
     * The original servlet config object
     */
    private ServletConfig m_OriginalConfig;

    /**
     * List of parameters set using the <code>setInitParameter()</code> method.
     */
    private Hashtable m_InitParameters;

    /**
     * Simulated name of the servlet
     */
    private String m_ServletName;

    /**
     * @param theOriginalConfig the original servlet config object
     */
    public ServletConfigWrapper(ServletConfig theOriginalConfig)
    {
        m_OriginalConfig = theOriginalConfig;
        m_InitParameters = new Hashtable();
    }

    /**
     * Sets a parameter as if it were set in the <code>web.xml</code> file.
     *
     * @param theName the parameter's name
     * @param theValue the parameter's value
     */
    public void setInitParameter(String theName, String theValue)
    {
        m_InitParameters.put(theName, theValue);
    }

    /**
     * Sets the servlet name. That will be the value returned by the
     * <code>getServletName()</code> method.
     *
     * @param theServletName the servlet's name
     */
    public void setServletName(String theServletName)
    {
        m_ServletName = theServletName;
    }

    //--Overridden methods -----------------------------------------------------

    /**
     * @return our own wrapped servlet context object
     */
    public ServletContext getServletContext()
    {
        return new ServletContextWrapper(m_OriginalConfig.getServletContext());
    }

    /**
     * @param theName the name of the parameter's value to return
     * @return the value of the parameter, looking for it first in the list of
     *         parameters set using the <code>setInitParameter()</code> method
     *         and then in those set in <code>web.xml</code>.
     */
    public String getInitParameter(String theName)
    {
        // Look first in the list of parameters set using the
        // setInitParameter() method.
        String value = (String)m_InitParameters.get(theName);
        if (value == null) {
            value = m_OriginalConfig.getInitParameter(theName);
        }

        return value;
    }

    /**
     * @return the union of the parameters defined in the Redirector
     *         <code>web.xml</code> file and the one set using the
     *         <code>setInitParameter()</code> method.
     */
    public Enumeration getInitParameterNames()
    {
        Vector names = new Vector();

        Enumeration enum = m_InitParameters.keys();
        while (enum.hasMoreElements()) {
            String value = (String)enum.nextElement();
            names.add(value);
        }

        enum = m_OriginalConfig.getInitParameterNames();
        while (enum.hasMoreElements()) {
            String value = (String)enum.nextElement();
            names.add(value);
        }

        return names.elements();
    }

    /**
     * @return the simulated servlet's name if defined or the redirector
     *         servlet's name
     */
    public String getServletName()
    {
        if (m_ServletName != null) {
            return m_ServletName;
        }

        return m_OriginalConfig.getServletName();
    }

}
