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
package org.apache.cactus.integration.ant.deployment.application;

import junit.framework.TestCase;
import org.codehaus.cargo.module.application.ApplicationXmlVersion;
import org.jdom.DocType;
import org.w3c.dom.DOMImplementation;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Unit tests for {@link ApplicationXmlVersion}.
 *
 * @version $Id: TestApplicationXmlVersion.java 239003 2004-05-31 20:05:27Z vmassol $
 */
public final class TestApplicationXmlVersion extends TestCase
{
    /**
     * The DOM implementation.
     */
    private DOMImplementation domImpl;

    /**
     * {@inheritDoc}
     * @see TestCase#setUp
     */
    public void setUp() throws ParserConfigurationException
    {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(false);
        factory.setNamespaceAware(false);

        this.domImpl = factory.newDocumentBuilder().getDOMImplementation();
    }

    /**
     * Verifies that comparing version 1.2 to version 1.2 yields zero.
     * 
     * @throws Exception If an unexpected error occurs
     */
    public void testCompare12To12() throws Exception
    {
        assertTrue(ApplicationXmlVersion.V1_2.compareTo(
            ApplicationXmlVersion.V1_2) == 0);
    }

    /**
     * Verifies that comparing version 1.2 to version 1.3 yields a negative
     * value.
     * 
     * @throws Exception If an unexpected error occurs
     */
    public void testCompare12To13() throws Exception
    {
        assertTrue(ApplicationXmlVersion.V1_2.compareTo(
            ApplicationXmlVersion.V1_3) < 0);
    }

    /**
     * Verifies that comparing version 1.3 to version 1.3 yields zero.
     * 
     * @throws Exception If an unexpected error occurs
     */
    public void testCompare13To13() throws Exception
    {
        assertTrue(ApplicationXmlVersion.V1_3.compareTo(
            ApplicationXmlVersion.V1_3) == 0);
    }

    /**
     * Verifies that comparing version 1.2 to version 1.3 yields a negative
     * value.
     * 
     * @throws Exception If an unexpected error occurs
     */
    public void testCompare13To12() throws Exception
    {
        assertTrue(ApplicationXmlVersion.V1_3.compareTo(
            ApplicationXmlVersion.V1_2) > 0);
    }

    /**
     * Verifies that calling ApplicationXmlVersion.valueOf(null) throws a
     * NullPointerException.
     * 
     * @throws Exception If an unexpected error occurs
     */
    public void testValueOfNull() throws Exception
    {
        try
        {
            ApplicationXmlVersion.valueOf((DocType) null);
            fail("Expected NullPointerException");
        }
        catch (NullPointerException expected)
        {
            // expected
        }
    }

    /**
     * Verifies that calling ApplicationXmlVersion.valueOf() with a unknown
     * document type returns null.
     * 
     * @throws Exception If an unexpected error occurs
     */
    public void testValueOfUnknownDocType() throws Exception
    {
        DocType docType = new DocType("application",
            "foo", "bar");
        assertNull(ApplicationXmlVersion.valueOf(docType));
    }

    /**
     * Verifies that calling ApplicationXmlVersion.valueOf() with a application
     * 1.2 document type returns the correct instance.
     * 
     * @throws Exception If an unexpected error occurs
     */
    public void testValueOfDocType12() throws Exception
    {
        DocType docType = new DocType("application",
            ApplicationXmlVersion.V1_2.getPublicId(),
                ApplicationXmlVersion.V1_2.getSystemId());
        assertEquals(ApplicationXmlVersion.V1_2,
            ApplicationXmlVersion.valueOf(docType));
    }

    /**
     * Verifies that calling ApplicationXmlVersion.valueOf() with a application
     * 1.3 document type returns the correct instance.
     * 
     * @throws Exception If an unexpected error occurs
     */
    public void testValueOfDocType13() throws Exception
    {
        DocType docType = new DocType("application",
            ApplicationXmlVersion.V1_3.getPublicId(),
                ApplicationXmlVersion.V1_3.getSystemId());
        assertEquals(ApplicationXmlVersion.V1_3,
            ApplicationXmlVersion.valueOf(docType));
    }

}
