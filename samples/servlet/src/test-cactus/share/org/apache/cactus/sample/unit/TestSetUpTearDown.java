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
package org.apache.cactus.sample.unit;

import org.apache.cactus.ServletTestCase;
import org.apache.cactus.WebResponse;

/**
 * Test that <code>setUp()</code> and <code>tearDown()</code> methods are 
 * called and can access implicit objects in <code>ServletTestCase</code>.
 *
 * @author <a href="mailto:vmassol@apache.org">Vincent Massol</a>
 *
 * @version $Id$
 */
public class TestSetUpTearDown extends ServletTestCase
{
    /**
     * Defines the testcase name for JUnit.
     *
     * @param theName the testcase's name.
     */
    public TestSetUpTearDown(String theName)
    {
        super(theName);
    }

    //-------------------------------------------------------------------------

    /**
     * Put a value in the session to verify that this method is called prior
     * to the test, and that it can access servlet implicit objects.
     */
    protected void setUp()
    {
        session.setAttribute("setUpFlag", "a setUp test flag");
    }

    /**
     * Verify that <code>setUp()</code> has been called and that it put a
     * value in the session object.
     */
    public void testSetUp()
    {
        assertEquals("a setUp test flag", session.getAttribute("setUpFlag"));
    }

    //-------------------------------------------------------------------------

    /**
     * Set an HTTP response header to verify that this method is called after
     * the test, and that it can access servlet implicit objects.
     */
    protected void tearDown()
    {
        response.setHeader("Teardownheader", "tear down header");
    }

    /**
     * Verify that <code>tearDown()</code> has been called and that it created
     * an HTTP reponse header.
     */
    public void testTearDown()
    {
    }

    /**
     * Verify that <code>tearDown()</code> has been called and that it created
     * an HTTP reponse header.
     *
     * @param theResponse the HTTP connection that was used to call the
     *                    server redirector. It contains the returned HTTP
     *                    response.
     */
    public void endTearDown(WebResponse theResponse)
    {
        assertEquals("tear down header", 
            theResponse.getConnection().getHeaderField("Teardownheader"));
    }
}