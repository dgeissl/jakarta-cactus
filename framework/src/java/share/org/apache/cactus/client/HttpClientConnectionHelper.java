/*
 * ====================================================================
 *
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
package org.apache.cactus.client;

import java.io.IOException;

import java.net.HttpURLConnection;
import java.net.URL;

import java.util.Enumeration;

import org.apache.cactus.WebRequest;
import org.apache.cactus.client.authentication.AbstractAuthentication;
import org.apache.cactus.util.UrlUtil;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;

/**
 * Implementation of <code>ConnectionHelper</code> using Jakarta Commons
 * HttpClient.
 *
 * @author <a href="mailto:vmassol@apache.org">Vincent Massol</a>
 *
 * @version $Id$
 */
public class HttpClientConnectionHelper extends AbstractConnectionHelper
{
    /**
     * The <code>HttpMethod</code> used to connect to the HTTP server. It is
     * either a <code>GetMethod</code> or a <code>PostMethod</code>.
     */
    private GetMethod method;

    /**
     * The URL that will be used for the HTTP connection.
     */
    private String url;

    /**
     * @param theURL the URL that will be used for the HTTP connection.
     */
    public HttpClientConnectionHelper(String theURL)
    {
        this.url = theURL;
    }

    /**
     * @see ConnectionHelper#connect(WebRequest)
     */
    public HttpURLConnection connect(WebRequest theRequest) throws Throwable
    {
        URL url = new URL(this.url);

        // Add Authentication headers, if necessary. This is the first
        // step to allow authentication to add extra headers, HTTP parameters,
        // etc.
        AbstractAuthentication authentication = theRequest.getAuthentication();

        if (authentication != null)
        {
            authentication.configure(theRequest);
        }

        // Add the parameters that need to be passed as part of the URL
        url = addParametersGet(theRequest, url);

        // Choose the method that we will use to post data :
        // - If at least one parameter is to be sent in the request body, then
        //   we are doing a POST.
        // - If user data has been specified, then we are doing a POST
        if (theRequest.getParameterNamesPost().hasMoreElements()
            || (theRequest.getUserData() != null))
        {
            this.method = new PostMethod();
        }
        else
        {
            this.method = new GetMethod();
        }

        this.method.setUseDisk(false);
        this.method.setFollowRedirects(false);
        this.method.setPath(UrlUtil.getPath(url));
        this.method.setQueryString(UrlUtil.getQuery(url));

        // Sets the content type
        this.method.setRequestHeader("Content-type", 
            theRequest.getContentType());

        // Add the other header fields
        addHeaders(theRequest);

        // Add the cookies
        String cookieString = getCookieString(theRequest, url);

        if (cookieString != null)
        {
            this.method.addRequestHeader("Cookie", cookieString);
        }

        // Add the POST parameters if no user data has been specified (user data
        // overried post parameters)
        if (theRequest.getUserData() != null)
        {
            addUserData(theRequest);
        }
        else
        {
            addParametersPost(theRequest);
        }

        // Open the connection and get the result
        HttpClient client = new HttpClient();

        client.startSession(url.getHost(), url.getPort());
        client.executeMethod(this.method);

        // Wrap the HttpClient method in a java.net.HttpURLConnection object
        return new org.apache.cactus.util.HttpURLConnection(this.method, url);
    }

    /**
     * Add the HTTP parameters that need to be passed in the request body.
     *
     * @param theRequest the request containing all data to pass to the server
     *        redirector.
     */
    private void addParametersPost(WebRequest theRequest)
    {
        // If no parameters, then exit
        if (!theRequest.getParameterNamesPost().hasMoreElements())
        {
            return;
        }

        Enumeration keys = theRequest.getParameterNamesPost();
        while (keys.hasMoreElements())
        {
            String key = (String) keys.nextElement();
            String[] values = theRequest.getParameterValuesPost(key);

            for (int i = 0; i < values.length; i++)
            {
                ((PostMethod) this.method).addParameter(key, values[i]);
            }
        }
    }

    /**
     * Add the Headers to the request.
     *
     * @param theRequest the request containing all data to pass to the server
     *        redirector.
     */
    private void addHeaders(WebRequest theRequest)
    {
        Enumeration keys = theRequest.getHeaderNames();

        while (keys.hasMoreElements())
        {
            String key = (String) keys.nextElement();
            String[] values = theRequest.getHeaderValues(key);

            StringBuffer fullHeaderValue = new StringBuffer(values[0]);

            for (int i = 1; i < values.length; i++)
            {
                fullHeaderValue.append("," + values[i]);
            }

            this.method.addRequestHeader(key, fullHeaderValue.toString());
        }
    }

    /**
     * Add user data in the request body.
     *
     * @param theRequest the request containing all data to pass to the server
     *        redirector.
     * @exception IOException if we fail to read the user data
     */
    private void addUserData(WebRequest theRequest) throws IOException
    {
        // If no user data, then exit
        if (theRequest.getUserData() == null)
        {
            return;
        }

        ((PostMethod) this.method).setRequestBody(theRequest.getUserData());
    }
}