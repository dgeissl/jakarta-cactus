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
package org.apache.cactus.internal.client;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import junit.framework.Assert;
import junit.framework.Test;

import org.apache.cactus.Request;
import org.apache.cactus.client.ClientException;
import org.apache.cactus.client.ResponseObjectFactory;
import org.apache.cactus.configuration.Configuration;
import org.apache.cactus.util.JUnitVersionHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Delegate class that provides useful methods for the Cactus  
 * <code>XXXTestCase</code> classes. All the methods provided are independent
 * of any communication protocol between client side and server side (HTTP, 
 * JMS, etc). Subclasses will define additional behaviour that depends on the 
 * protocol.
 *  
 * It provides the ability to run common code before and after each test on the
 * client side.
 *
 * In addition it provides the ability to execute some one time (per-JVM)
 * initialisation code (a pity this is not provided in JUnit). It can be 
 * useful to start an embedded server for example. Note: In the future this
 * should be refatored and provided using a custom JUnit TestSuite.
 *
 * @author <a href="mailto:vmassol@apache.org">Vincent Massol</a>
 *
 * @version $Id$
 */
public abstract class AbstractClientTestCaseDelegate 
    extends Assert implements ClientTestCaseDelegate
{
    /**
     * The prefix of a test method.
     */
    protected static final String TEST_METHOD_PREFIX = "test";

    /**
     * The prefix of a begin test method.
     */
    protected static final String BEGIN_METHOD_PREFIX = "begin";

    /**
     * The prefix of an end test method.
     */
    protected static final String END_METHOD_PREFIX = "end";

    /**
     * The name of the method that is called before each test on the client
     * side (if it exists).
     */
    protected static final String CLIENT_GLOBAL_BEGIN_METHOD = "begin";

    /**
     * The name of the method that is called after each test on the client
     * side (if it exists).
     */
    protected static final String CLIENT_GLOBAL_END_METHOD = "end";

    /**
     * The logger (only used on the client side).
     */
    private Log logger;

    /**
     * The Cactus configuration.
     */
    private Configuration configuration;

    /**
     * Pure JUnit Test Case that we are wrapping (if any)
     */
    private Test wrappedTest;

    /**
     * The test we are delegating for.
     */
    private Test delegatedTest;   
    
    /**
     * @param theDelegatedTest the test we are delegating for
     * @param theWrappedTest the test being wrapped by this delegate (or null 
     *        if none)
     * @param theConfiguration the configuration to use 
     */
    public AbstractClientTestCaseDelegate(Test theDelegatedTest, 
        Test theWrappedTest, Configuration theConfiguration)
    {        
        if (theDelegatedTest == null)
        {
            throw new IllegalStateException(
                "The test object passed must not be null");
        }

        setDelegatedTest(theDelegatedTest); 
        setWrappedTest(theWrappedTest);
        setConfiguration(theConfiguration);               
    }

    /**
     * @param theWrappedTest the pure JUnit test that we need to wrap 
     */
    public void setWrappedTest(Test theWrappedTest)
    {
        this.wrappedTest = theWrappedTest;
    }

    /**
     * @param theDelegatedTest the test we are delegating for
     */
    public void setDelegatedTest(Test theDelegatedTest)
    {
        this.delegatedTest = theDelegatedTest;
    }

    /**
     * @return the wrapped JUnit test
     */
    public Test getWrappedTest()
    {
        return this.wrappedTest;
    }

    /**
     * @return the test we are delegating for
     */
    public Test getDelegatedTest()
    {
        return this.delegatedTest;
    }

    /**
     * @return the test on which we will operate. If there is a wrapped
     *         test then the returned test is the wrapped test. Otherwise we
     *         return the delegated test.
     */
    public Test getTest()
    {
        Test activeTest;
        if (getWrappedTest() != null)
        {
            activeTest = getWrappedTest();
        }
        else
        {
            activeTest = getDelegatedTest();
        }
        return activeTest;
    }

    
    /**
     * @return The logger used by the <code>TestCase</code> class and
     *         subclasses to perform logging.
     */
    public final Log getLogger()
    {
        return this.logger;
    }

    /**
     * @param theLogger the logger to use 
     */
    protected void setLogger(Log theLogger)
    {
        this.logger = theLogger;
    }
    
    /**
     * @return the Cactus configuration
     */
    public Configuration getConfiguration()
    {
        return this.configuration;
    }

    /**
     * Sets the Cactus configuration
     * 
     * @param theConfiguration the Cactus configuration
     */
    public void setConfiguration(Configuration theConfiguration)
    {
        this.configuration = theConfiguration;
    }
   
    /**
     * @return the name of the test method to call without the
     *         TEST_METHOD_PREFIX prefix
     */
    private String getBaseMethodName()
    {
        // Sanity check
        if (!getCurrentTestName().startsWith(TEST_METHOD_PREFIX))
        {
            throw new RuntimeException("bad name ["
                + getCurrentTestName()
                + "]. It should start with ["
                + TEST_METHOD_PREFIX + "].");
        }

        return getCurrentTestName().substring(
            TEST_METHOD_PREFIX.length());
    }

    /**
     * @return the name of the test begin method to call that initialize the
     *         test by initializing the <code>WebRequest</code> object
     *         for the test case.
     */
    protected String getBeginMethodName()
    {
        return BEGIN_METHOD_PREFIX + getBaseMethodName();
    }

    /**
     * @return the name of the test end method to call when the test has been
     *         run on the server. It can be used to verify returned headers,
     *         cookies, ...
     */
    protected String getEndMethodName()
    {
        return END_METHOD_PREFIX + getBaseMethodName();
    }

    /**
     * Perform client side initializations before each test, such as
     * re-initializating the logger and printing some logging information.
     */
    public void runBareInit()
    {
        // We make sure we reinitialize The logger with the name of the
        // current extending class so that log statements will contain the
        // actual class name (that's why the logged instance is not static).
        this.logger = LogFactory.getLog(this.getClass());

        // Mark beginning of test on client side
        getLogger().debug("------------- Test: " 
            + this.getCurrentTestName());        
    }

    /**
     * Call a begin method which takes Cactus WebRequest as parameter
     *
     * @param theRequest the request object which will contain data that will
     *        be used to connect to the Cactus server side redirectors.
     * @param theMethodName the name of the begin method to call
     * @exception Throwable any error that occurred when calling the method
     */
    private void callGenericBeginMethod(Request theRequest, 
        String theMethodName) throws Throwable
    {
        // First, verify if a begin method exist. If one is found, verify if
        // it has the correct signature. If not, send a warning.
        Method[] methods = getTest().getClass().getMethods();

        for (int i = 0; i < methods.length; i++)
        {
            if (methods[i].getName().equals(theMethodName))
            {
                // Check return type
                if (!methods[i].getReturnType().getName().equals("void"))
                {
                    fail("The method [" + methods[i].getName()
                        + "] should return void and not ["
                        + methods[i].getReturnType().getName() + "]");
                }

                // Check if method is public
                if (!Modifier.isPublic(methods[i].getModifiers()))
                {
                    fail("Method [" + methods[i].getName()
                        + "] should be declared public");
                }

                // Check parameters
                Class[] parameters = methods[i].getParameterTypes();

                if (parameters.length != 1)
                {
                    fail("The method [" + methods[i].getName()
                        + "] must accept a single parameter implementing "
                        + "interface [" + Request.class.getName() + "], "
                        + "but " + parameters.length
                        + " parameters were found");
                }
                else if (!Request.class.isAssignableFrom(parameters[0]))
                {
                    fail("The method [" + methods[i].getName()
                        + "] must accept a single parameter implementing "
                        + "interface [" + Request.class.getName() + "], "
                        + "but found a [" + parameters[0].getName() + "] "
                        + "parameter instead");
                }

                try
                {
                    methods[i].invoke(getTest(), new Object[] {theRequest});

                    break;
                }
                catch (InvocationTargetException e)
                {
                    e.fillInStackTrace();
                    throw e.getTargetException();
                }
                catch (IllegalAccessException e)
                {
                    e.fillInStackTrace();
                    throw e;
                }
            }
        }
    }
    
    /**
     * Call the global begin method. This is the method that is called before
     * each test if it exists. It is called on the client side only.
     *
     * @param theRequest the request object which will contain data that will
     *        be used to connect to the Cactus server side redirectors.
     * @exception Throwable any error that occurred when calling the method
     */
    protected void callGlobalBeginMethod(Request theRequest) throws Throwable
    {
        callGenericBeginMethod(theRequest, CLIENT_GLOBAL_BEGIN_METHOD);
    }

    /**
     * Call the test case begin method.
     *
     * @param theRequest the request object to pass to the begin method.
     * @exception Throwable any error that occurred when calling the begin
     *            method for the current test case.
     */
    public void callBeginMethod(Request theRequest) throws Throwable
    {
        callGenericBeginMethod(theRequest, getBeginMethodName());
    }

    /**
     * Call the global end method. This is the method that is called after
     * each test if it exists. It is called on the client side only.
     *
     * @param theRequest the request data that were used to open the
     *        connection.
     * @param theResponseFactory the factory to use to return response objects.
     * @param theMethodName the name of the end method to call
     * @param theResponse the Response object if it exists. Can be null in
     *        which case it is created from the response object factory
     * @return the created Reponse object
     * @exception Throwable any error that occurred when calling the end method
     *            for the current test case.
     */
    private Object callGenericEndMethod(Request theRequest,
        ResponseObjectFactory theResponseFactory, String theMethodName, 
        Object theResponse) throws Throwable
    {
        Method methodToCall = null;
        Object paramObject = null;

        Method[] methods = getTest().getClass().getMethods();

        for (int i = 0; i < methods.length; i++)
        {
            if (methods[i].getName().equals(theMethodName))
            {
                // Check return type
                if (!methods[i].getReturnType().getName().equals("void"))
                {
                    fail("The method [" + methods[i].getName()
                       + "] should return void and not ["
                       + methods[i].getReturnType().getName() + "]");
                }

                // Check if method is public
                if (!Modifier.isPublic(methods[i].getModifiers()))
                {
                    fail("Method [" + methods[i].getName()
                       + "] should be declared public");
                }

                // Check parameters
                Class[] parameters = methods[i].getParameterTypes();

                // Verify only one parameter is defined
                if (parameters.length != 1)
                {
                    fail("The method [" + methods[i].getName()
                       + "] must only have a single parameter");
                }

                paramObject = theResponse;

                if (paramObject == null)
                {
                    try
                    {
                        paramObject = theResponseFactory.getResponseObject(
                            parameters[0].getName(), theRequest);
                    }
                    catch (ClientException e)
                    {
                        throw new ClientException("The method ["
                            + methods[i].getName() 
                            + "] has a bad parameter of type ["
                            + parameters[0].getName() + "]", e);
                    }
                }

                // Has a method to call already been found ?
                if (methodToCall != null)
                {
                    fail("There can only be one method ["
                       + methods[i].getName() + "] per test case. "
                       + "Test case [" + this.getCurrentTestName()
                       + "] has two at least !");
                }

                methodToCall = methods[i];
            }
        }

        if (methodToCall != null)
        {
            try
            {
                methodToCall.invoke(getTest(), new Object[] {paramObject});
            }
            catch (InvocationTargetException e)
            {
                e.fillInStackTrace();
                throw e.getTargetException();
            }
            catch (IllegalAccessException e)
            {
                e.fillInStackTrace();
                throw e;
            }
        }

        return paramObject;
    }

    /**
     * Call the client tear down up method if it exists.
     *
     * @param theRequest the request data that were used to open the
     *                   connection.
     * @param theResponseFactory the factory to use to return response objects.
     * @param theResponse the Response object if it exists. Can be null in
     *        which case it is created from the response object factory
     * @exception Throwable any error that occurred when calling the method
     */
    protected void callGlobalEndMethod(Request theRequest, 
        ResponseObjectFactory theResponseFactory, Object theResponse) 
        throws Throwable
    {
        callGenericEndMethod(theRequest, theResponseFactory,
            CLIENT_GLOBAL_END_METHOD, theResponse);
    }

    /**
     * Call the test case end method
     *
     * @param theRequest the request data that were used to open the
     *                   connection.
     * @param theResponseFactory the factory to use to return response objects.
     * @return the created Reponse object
     * @exception Throwable any error that occurred when calling the end method
     *         for the current test case.
     */
    public Object callEndMethod(Request theRequest, 
        ResponseObjectFactory theResponseFactory) throws Throwable
    {
        return callGenericEndMethod(theRequest, theResponseFactory,
            getEndMethodName(), null);
    }
    
    /**
     * @see #getCurrentTestName()
     * @deprecated Use {@link #getCurrentTestName()} instead
     */
    protected String getCurrentTestMethod()
    {
        return getCurrentTestName();
    }

    /**
     * @return the name of the current test case being executed (it corresponds
     *         to the name of the test method with the "test" prefix removed.
     *         For example, for "testSomeTestOk" would return "someTestOk".
     */
    protected String getCurrentTestName()
    {
        return JUnitVersionHelper.getTestCaseName(getDelegatedTest());        
    }

    /**
     * @return The wrapped test name, if any (null otherwise).
     */
    public String getWrappedTestName()
    {
        if (isWrappingATest())
        {
            return getWrappedTest().getClass().getName();
        }
        return null;
    }

    /**
     * @return whether this test case wraps another
     */
    public boolean isWrappingATest()
    {
        return (getWrappedTest() != null);
    }
}
