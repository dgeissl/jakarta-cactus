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
package org.apache.cactus.sample.unit;

import java.util.*;
import java.text.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.net.*;
import java.io.*;

import junit.framework.*;

import org.apache.cactus.*;
import org.apache.cactus.server.*;
import org.apache.cactus.util.*;

/**
 * Some Cactus unit tests for testing <code>ServletTestCase</code>.
 *
 * These tests should not really be part of the sample application functional
 * tests as they are unit tests for Cactus. However, they are unit tests that
 * need a servlet environment running for their execution, so they have been
 * package here for convenience. They can also be read by end-users to
 * understand how Cactus work.
 *
 * @author <a href="mailto:vmassol@apache.org">Vincent Massol</a>
 *
 * @version $Id$
 */
public class TestServletTestCase2 extends ServletTestCase
{
    /**
     * Defines the testcase name for JUnit.
     *
     * @param theName the testcase's name.
     */
    public TestServletTestCase2(String theName)
    {
        super(theName);
    }

    /**
     * Start the tests.
     *
     * @param theArgs the arguments. Not used
     */
    public static void main(String[] theArgs)
    {
        junit.ui.TestRunner.main(new String[] {
            TestServletTestCase2.class.getName()});
    }

    /**
     * @return a test suite (<code>TestSuite</code>) that includes all methods
     *         starting with "test"
     */
    public static Test suite()
    {
        // All methods starting with "test" will be executed in the test suite.
        return new TestSuite(TestServletTestCase2.class);
    }

    //-------------------------------------------------------------------------

    /**
     * Verify that it is possible to ask for no automatic session creation in
     * the <code>beginXXX()</code> method.
     *
     * @param theRequest the request object that serves to initialize the
     *                   HTTP connection to the server redirector.
     */
    public void beginNoAutomaticSessionCreation(WebRequest theRequest)
    {
        theRequest.setAutomaticSession(false);
    }

    /**
     * Verify that it is possible to ask for no automatic session creation in
     * the <code>beginXXX()</code> method.
     */
    public void testNoAutomaticSessionCreation()
    {
        assert("A valid session has been found when no session should exist",
            session == null);
    }

    //-------------------------------------------------------------------------

    /**
     * Verify that multi value parameters can be sent in the
     * <code>beingXXX()</code> method to the server redirector.
     *
     * @param theRequest the request object that serves to initialize the
     *                   HTTP connection to the server redirector.
     */
    public void beginMultiValueParameters(WebRequest theRequest)
    {
        theRequest.addParameter("multivalue", "value 1");
        theRequest.addParameter("multivalue", "value 2");
    }

    /**
     * Verify that multi value parameters can be sent in the
     * <code>beingXXX()</code> method to the server redirector.
     */
    public void testMultiValueParameters()
    {
        String[] values = request.getParameterValues("multivalue");
        if (values[0].equals("value 1")) {
            assertEquals("value 2", values[1]);
        } else if (values[0].equals("value 2")) {
            assertEquals("value 1", values[1]);
        } else {
            fail("Shoud have returned a vector with the " +
                "values \"value 1\" and \"value 2\"");
        }
    }

    //-------------------------------------------------------------------------

    /**
     * Verify that it is possible to write to the servlet output stream.
     */
    public void testWriteOutputStream() throws IOException
    {
        PrintWriter pw = response.getWriter();
        pw.println("should not result in an error");
    }

    /**
     * Verify that it is possible to write to the servlet output stream.
     *
     * @param theResponse the response from the server side.
     */
    public void endWriteOutputStream(WebResponse theResponse) throws IOException
    {
        DataInputStream dis = new DataInputStream(
            theResponse.getConnection().getInputStream());
        assertEquals("should not result in an error", dis.readLine());
    }

    //-------------------------------------------------------------------------

    /**
     * Verify that we can add parameters to the config list of parameters
     * programatically, without having to define them in <code>web.xml</code>.
     */
    public void testSetConfigParameter()
    {
        config.setInitParameter("testparam", "test value");

        assertEquals("test value", config.getInitParameter("testparam"));

        boolean found = false;
        Enumeration enum = config.getInitParameterNames();
        while(enum.hasMoreElements()) {
            String name = (String)enum.nextElement();
            if (name.equals("testparam")) {
                found = true;
                break;
            }
        }

        assert("[testparam] not found in parameter names", found);
    }

    //-------------------------------------------------------------------------

    /**
     * Verify that we can override the
     * <code>ServletConfig.getServletName()</code> method.
     */
    public void testGetServletName()
    {
        config.setServletName("MyServlet");
        assertEquals("MyServlet", config.getServletName());
    }

    //-------------------------------------------------------------------------

    /**
     * Verify that we can simulate several HTTP header values with the same
     * header name.
     *
     * @param theRequest the request object that serves to initialize the
     *                   HTTP connection to the server redirector.
     */
    public void beginSendMultivaluedHeader(WebRequest theRequest)
    {
        theRequest.addHeader("testheader", "value1");
        theRequest.addHeader("testheader", "value2");
    }

    /**
     * Verify that we can simulate several HTTP header values with the same
     * header name.
     */
    public void testSendMultivaluedHeader()
    {
        // Note: I am not sure how to retrieve multi valued headers. The
        // problem is that I use
        // URLConnection.setRequestProperty("testheader", "value1,value2") in
        // HttpClientHelper to send the headers but request.getHeaders() does
        // not seem to separate the different header values.

        // The RFC 2616 says :

        // message-header = field-name ":" [ field-value ]
        // field-name     = token
        // field-value    = *( field-content | LWS )
        // field-content  = <the OCTETs making up the field-value
        //                  and consisting of either *TEXT or combinations
        //                  of token, separators, and quoted-string>
        // [...]
        // Multiple message-header fields with the same field-name MAY be
        // present in a message if and only if the entire field-value for that
        // header field is defined as a comma-separated list [i.e., #(values)].
        // It MUST be possible to combine the multiple header fields into one
        // "field-name: field-value" pair, without changing the semantics of
        // the message, by appending each subsequent field-value to the first,
        // each separated by a comma. The order in which header fields with the
        // same field-name are received is therefore significant to the
        // interpretation of the combined field value, and thus a proxy MUST
        // NOT change the order of these field values when a message is
        // forwarded.

        // ... so it should be ok ...

        assertEquals("value1,value2", request.getHeader("testheader"));

        // Here is commented out what I would have thought I should have
        // written to verify this test but it does not seem to work this way ...

        /*
        Enumeration values = request.getHeaders("testheader");
        int count = 0;
        while (values.hasMoreElements()) {
            String value = (String)values.nextElement();
            if (!(value.equals("value1") || value.equals("value2"))) {
                fail("unknown value [" + value + "] for header [testheader]");
            }
            count++;
        }
        assertEquals("Should have received 2 values for header [testheader]",
            2, count);
        */
    }

    //-------------------------------------------------------------------------

    /**
     * Verify that the <code>AsertUtils.getResponseAsString()</code> method
     * works with output text sent on multiple lines.
     */
    public void testGetResponseAsStringMultiLines() throws IOException
    {
        PrintWriter pw = response.getWriter();
        response.setContentType("text/html");
        pw.println("<html><head/>");
        pw.println("<body>A GET request</body>");
        pw.println("</html>");
    }

    /**
     * Verify that the <code>AsertUtils.getResponseAsString()</code> method
     * works with output text sent on multiple lines.
     *
     * @param theResponse the response from the server side.
     */
    public void endGetResponseAsStringMultiLines(WebResponse theResponse)
        throws IOException
    {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        pw.println("<html><head/>");
        pw.println("<body>A GET request</body>");
        pw.println("</html>");

        String result = theResponse.getText();
        assertEquals(sw.toString(), result);

        pw.close();
    }

    //-------------------------------------------------------------------------

    /**
     * Verify that the <code>getTestAsArray()</code> method
     * works with output text sent on multiple lines. We also verify that
     * we can call it several times with the same result.
     */
    public void testGetResponseAsStringArrayMultiLines() throws IOException
    {
        PrintWriter pw = response.getWriter();
        response.setContentType("text/html");
        pw.println("<html><head/>");
        pw.println("<body>A GET request</body>");
        pw.println("</html>");
    }

    /**
     * Verify that the <code>getTestAsArray()</code> method
     * works with output text sent on multiple lines. We also verify that
     * we can call it several times with the same result.
     *
     * @param theResponse the response from the server side.
     */
    public void endGetResponseAsStringArrayMultiLines(WebResponse theResponse)
        throws IOException
    {
        String[] results1 = theResponse.getTextAsArray();
        String[] results2 = theResponse.getTextAsArray();

        assert("Should have returned 3 lines of text but returned [" +
            results1.length + "]", results1.length == 3);
        assertEquals("<html><head/>", results1[0]);
        assertEquals("<body>A GET request</body>", results1[1]);
        assertEquals("</html>", results1[2]);

        assert("Should have returned 3 lines of text but returned [" +
            results2.length + "]", results2.length == 3);
        assertEquals("<html><head/>", results2[0]);
        assertEquals("<body>A GET request</body>", results2[1]);
        assertEquals("</html>", results2[2]);
    }

    //-------------------------------------------------------------------------

    /**
     * Verify that special characters in cookies are not URL encoded
     *
     * @param theRequest the request object that serves to initialize the
     *                   HTTP connection to the server redirector.
     */
    public void beginCookieEncoding(WebRequest theRequest)
    {
        // Note: the pipe ('&') character is a special character regarding
        // URL encoding
        theRequest.addCookie("testcookie", "user&pwd");
    }

    /**
     * Verify that special characters in cookies are not encoded
     */
    public void testCookieEncoding()
    {
        javax.servlet.http.Cookie[] cookies = request.getCookies();
        for (int i = 0; i < cookies.length; i++) {
            if (cookies[i].getName().equals("testcookie")) {
                assertEquals("user&pwd", cookies[i].getValue());
                return;
            }
        }
        fail("No cookie named 'testcookie' found");
    }

    //-------------------------------------------------------------------------

    /**
     * Verify that request.getRequestDispatcher() works properly with an
     * absolute path
     */
    public void testGetRequestDispatcherFromRequest1()
        throws ServletException, IOException
    {
        RequestDispatcher rd = request.getRequestDispatcher("/test/test.jsp");
        rd.include(request, response);
    }

    /**
     * Verify that request.getRequestDispatcher() works properly with an
     * absolute path
     *
     * @param theResponse the response from the server side.
     */
    public void endGetRequestDispatcherFromRequest1(WebResponse theResponse)
        throws IOException
    {
        String result = theResponse.getText();
        assert("Page not found, got [" + result + "]",
            result.indexOf("Hello !") > 0);
    }

    //-------------------------------------------------------------------------

    /**
     * Verify that request.getRequestDispatcher() works properly with a
     * relative path.
     *
     * @param theRequest the request object that serves to initialize the
     *                   HTTP connection to the server redirector.
     */
    public void beginGetRequestDispatcherFromRequest2(
        WebRequest theRequest)
    {
        theRequest.setURL(null, "/test", "/anything.jsp", null, null);
    }

    /**
     * Verify that request.getRequestDispatcher() works properly with a
     * relative path.
     */
    public void testGetRequestDispatcherFromRequest2()
        throws ServletException, IOException
    {
        RequestDispatcher rd = request.getRequestDispatcher("test/test.jsp");
        rd.include(request, response);
    }

    /**
     * Verify that request.getRequestDispatcher() works properly with a
     * relative path.
     *
     * @param theResponse the response from the server side.
     */
    public void endGetRequestDispatcherFromRequest2(WebResponse theResponse)
        throws IOException
    {
        String result = theResponse.getText();
        assert("Page not found, got [" + result + "]",
            result.indexOf("Hello !") > 0);
    }

    //-------------------------------------------------------------------------

    /**
     * Verify that calls to <code>ServletContext.log()</code> methods can
     * be retrieved and asserted.
     */
    public void testGetLogs()
    {
        String message = "some test log";
        ServletContext context = config.getServletContext();

        context.log(message);

        Vector logs = ((ServletContextWrapper)context).getLogs();
        assertEquals("Found more than one log message", logs.size(), 1);
        assert("Cannot find expected log message : [" + message + "]",
            logs.contains("some test log"));
    }

    //-------------------------------------------------------------------------

    /**
     * Verify that we can assert HTTP status code when it is an error
     * status code (> 400).
     *
     * Note: HttpURLConnection will return a FileNotFoundException if the
     * status code is > 400 and the request does not end with a "/" ! In
     * order to prevent, this ensure the Servlet Redirector URL in
     * cactus.properties does end with a "/" and modify the servlet mapping
     * in web.xml to include the trailing slash.
     */
    public void testStatusCode()
    {
// FIXME: use HttpClient for http connection
//        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    /**
     * Verify that we can assert HTTP status code when it is an error
     * status code (> 400).
     *
     * Note: HttpURLConnection will return a FileNotFoundException if the
     * status code is > 400 and the request does not end with a "/" ! In
     * order to prevent, this ensure the Servlet Redirector URL in
     * cactus.properties does end with a "/" and modify the servlet mapping
     * in web.xml to include the trailing slash.
     *
     * @param theResponse the response from the server side.
     */
    public void endStatusCode(WebResponse theResponse)
        throws IOException
    {
// FIXME: use HttpClient for http connection
//        assertEquals(HttpServletResponse.SC_UNAUTHORIZED,
//            theResponse.getConnection().getResponseCode());
    }

    //-------------------------------------------------------------------------

    /**
     * Verify that we can assert HTTP status code when it is a redirect and
     * that the client side of Cactus does not follow the redirect.
     */
    public void testRedirect() throws IOException
    {
        response.sendRedirect("http://jakarta.apache.org");
    }

    /**
     * Verify that we can assert HTTP status code when it is a redirect and
     * that the client side of Cactus does not follow the redirect.
     *
     * @param theResponse the response from the server side.
     */
    public void endRedirect(WebResponse theResponse)
        throws IOException
    {
        assertEquals(HttpServletResponse.SC_MOVED_TEMPORARILY,
            theResponse.getConnection().getResponseCode());
    }

    //-------------------------------------------------------------------------

    /**
     * Verify that <code>HttpServletRequestWrapper.getPathTranslated()</code>
     * takes into account the simulated URL (if any).
     *
     * @param theRequest the request object that serves to initialize the
     *                   HTTP connection to the server redirector.
     */
    public void beginGetPathTranslated(WebRequest theRequest)
    {
        theRequest.setURL("jakarta.apache.org", "/mywebapp", "/myservlet",
            "/test1/test2", "PARAM1=value1");
    }

    /**
     * Verify that <code>HttpServletRequestWrapper.getPathTranslated()</code>
     * takes into account the simulated URL (if any).
     */
    public void testGetPathTranslated()
    {
        String nativePathInfo = File.separator + "test1" + File.separator +
            "test2";

        String pathTranslated = request.getPathTranslated();

        assertNotNull("Should not be null", pathTranslated);
        assert("Should end with [" + nativePathInfo + "] but got [" +
            pathTranslated + "] instead",
            pathTranslated.endsWith(nativePathInfo));
    }

}