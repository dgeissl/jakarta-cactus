/* 
 * ========================================================================
 * 
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * ========================================================================
 */
package org.apache.cactus;

import junit.framework.Test;
import org.apache.cactus.internal.AbstractCactusTestCase;
import org.apache.cactus.internal.CactusTestCase;
import org.apache.cactus.internal.client.connector.http.HttpProtocolHandler;
import org.apache.cactus.internal.configuration.DefaultServletConfiguration;
import org.apache.cactus.server.AbstractServletConfigWrapper;
import org.apache.cactus.spi.client.connector.ProtocolHandler;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Cactus test case to unit test Servlets. Test classes that need access to 
 * valid Servlet implicit objects (such as the HTTP request, the HTTP response,
 * the servlet config, ...) must subclass this class.
 *
 * @version $Id: ServletTestCase.java 238991 2004-05-22 11:34:50Z vmassol $
 */
public class ServletTestCase 
    extends AbstractCactusTestCase implements CactusTestCase
{
    /**
     * Valid <code>HttpServletRequest</code> object that you can access from
     * the <code>testXXX()</code>, <code>setUp</code> and
     * <code>tearDown()</code> methods. If you try to access it from either the
     * <code>beginXXX()</code> or <code>endXXX()</code> methods it will
     * have the <code>null</code> value.
     */
    public org.apache.cactus.server.AbstractHttpServletRequestWrapper request;

    /**
     * Valid <code>HttpServletResponse</code> object that you can access from
     * the <code>testXXX()</code>, <code>setUp</code> and
     * <code>tearDown()</code> methods. If you try to access it from either the
     * <code>beginXXX()</code> or <code>endXXX()</code> methods it will
     * have the <code>null</code> value.
     */
    public HttpServletResponse response;

    /**
     * Valid <code>HttpSession</code> object that you can access from
     * the <code>testXXX()</code>, <code>setUp</code> and
     * <code>tearDown()</code> methods. If you try to access it from either the
     * <code>beginXXX()</code> or <code>endXXX()</code> methods it will
     * have the <code>null</code> value.
     */
    public HttpSession session;

    /**
     * Valid <code>ServletConfig</code> object that you can access from
     * the <code>testXXX()</code>, <code>setUp</code> and
     * <code>tearDown()</code> methods. If you try to access it from either the
     * <code>beginXXX()</code> or <code>endXXX()</code> methods it will
     * have the <code>null</code> value.
     */
    public AbstractServletConfigWrapper config;

    /**
     * @see AbstractCactusTestCase#AbstractCactusTestCase()
     */
    public ServletTestCase()
    {
        super();
    }

    /**
     * {@inheritDoc}
     * @see AbstractCactusTestCase#AbstractCactusTestCase(String)
     */
    public ServletTestCase(String theName)
    {
        super(theName);
    }

    /**
     * {@inheritDoc}
     * @see AbstractCactusTestCase#AbstractCactusTestCase(String, Test)
     */
    public ServletTestCase(String theName, Test theTest)
    {
        super(theName, theTest);
    }

    /**
     * {@inheritDoc}
     * @see AbstractCactusTestCase#createProtocolHandler()
     */
    protected ProtocolHandler createProtocolHandler()
    {
        return new HttpProtocolHandler(new DefaultServletConfiguration());
    }
}
