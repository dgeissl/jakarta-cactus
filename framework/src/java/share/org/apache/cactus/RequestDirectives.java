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
package org.apache.cactus;


/**
 * Encapsulates the Cactus-specific URL parameters added to the WebRequest.
 *
 * @author <a href="mailto:ndlesiecki@apache.org>Nicholas Lesiecki</a>
 *
 * @version $Id$
 */
public class RequestDirectives
{

    /**
     * The WebRequest that the directives modifies.
     */
    private WebRequest underlyingRequest;

    /**
     * @param theRequest The WebRequest to read directives from or
     *                   apply directives to.
     */
    public RequestDirectives(WebRequest theRequest)
    {
        this.underlyingRequest = theRequest;
    }

    /**
     * Adds a cactus-specific command to the URL of the WebRequest
     * The URL is used to allow the user to send whatever he wants
     * in the request body. For example a file, ...
     * 
     * @param theCommandName The name of the command to add--must start with 
     *                       "Cactus_"
     * @param theCommandValue Value of the command
     */
    public void addCactusCommand(String theCommandName, String theCommandValue)
    {
        if (!theCommandName.startsWith(HttpServiceDefinition.COMMAND_PREFIX))
        {
            throw new IllegalArgumentException(
                "Cactus commands must begin"
                    + " with" + HttpServiceDefinition.COMMAND_PREFIX
                    + ". The offending command was [" + theCommandName
                    + "]");
        }
        underlyingRequest.addParameter(
            theCommandName,
            theCommandValue,
            WebRequest.GET_METHOD);
    }

    /**
     * @param theId new id for the test case associated
     *        with this request
     */
    public void setId(String theId)
    {
        if (getId() != null)
        {
            throw new IllegalStateException("uniqueId already set!");
        }
        addCactusCommand(HttpServiceDefinition.TEST_ID_PARAM, theId);
    }

    /**
     * @return Gets the unique id of the test case
     */
    public String getId()
    {
        return underlyingRequest.getParameterGet(
            HttpServiceDefinition.TEST_ID_PARAM);
    }

    /**
     * @param theName name of the test class.
     */
    public void setClassName(String theName)
    {
        addCactusCommand(
            HttpServiceDefinition.CLASS_NAME_PARAM,
            theName);
    }


    /**
     * @param theName The name of the wrapped test.
     */
    public void setWrappedTestName(String theName)
    {
        addCactusCommand(
            HttpServiceDefinition.WRAPPED_CLASS_NAME_PARAM,
            theName);

    }

    /**
     * @param theMethodName name of the test method to execute.
     */
    public void setMethodName(String theMethodName)
    {
            addCactusCommand(
                HttpServiceDefinition.METHOD_NAME_PARAM,
                theMethodName);
    }



    /**
     * @param theService The service to request of the redirector.
     */
    public void setService(ServiceEnumeration theService)
    {
        addCactusCommand(HttpServiceDefinition.SERVICE_NAME_PARAM,
                         theService.toString());
        
    }

    /**
     * @param hasAutoSession A "boolean string" indicating
     *                       whether or not to use the 
     *                       autoSession option.
     */
    public void setAutoSession(String hasAutoSession)
    {
        addCactusCommand(
            HttpServiceDefinition.AUTOSESSION_NAME_PARAM,
            hasAutoSession);
        
    }

}
