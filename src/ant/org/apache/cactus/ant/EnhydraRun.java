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
package org.apache.commons.cactus.ant;

import java.net.*;
import java.io.*;
import java.util.*;
import java.lang.reflect.*;

/**
 * Starts/stop Enhydra by setting up a listener socket.
 *
 * @author <a href="mailto:digital@ix.net.au">Robert Leftwich</a>
 * @author <a href="mailto:vmassol@apache.org">Vincent Massol</a>
 *
 * @version $Id$
 * @see AbstractServerRun
 */
public class EnhydraRun extends AbstractServerRun
{
    /**
     * The started Enhydra server class. We use <code>Object</code> instead of
     * the Enhydra class so that we don't need the Resin jars in the classpath
     * to compile this class.
     */
    private Object enhydraServer;

    /**
     * Entry point to start/stop the Enhydra server.
     *
     * @param theArgs the command line arguments
     */
	public static void main(String[] theArgs)
	{
        EnhydraRun enhydra = new EnhydraRun(theArgs);
        enhydra.doRun();
	}

    /**
     * @param theArgs the command line arguments
     */
    public EnhydraRun(String[] args)
    {
        super(args);
    }

    /**
     * Start the Enhydra server. We use reflection so that the Enhydra jars do
     * not need to be in the classpath to compile this class.
     */
	protected void doStartServer()
    {
        try {
            Class enhydraClass =
                Class.forName("com.lutris.multiServer.MultiServer");
            Method initMethod = enhydraClass.getMethod("main",
                new Class[] {this.args.getClass()});
            initMethod.invoke(null, new Object[] {this.args});
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Cannot create instance of MultiServer");
        }
	}

    /**
     * Stops the Enhydra server. We use reflection so that the Enhydra jars do
     * not need to be in the classpath to compile this class.
     */
	protected void doStopServer()
    {
        try {
            Class enhydraClass =
                Class.forName("com.lutris.multiServer.MultiServer");
            Method shutDownMethod = enhydraClass.getMethod("shutdown", null);
            shutDownMethod.invoke(null, null );
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Cannot stop running instance of " +
                "MultiServer");
        }
	}

}