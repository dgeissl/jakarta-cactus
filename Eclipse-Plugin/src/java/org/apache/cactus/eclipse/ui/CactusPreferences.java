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
package org.apache.cactus.eclipse.ui;

import org.eclipse.jface.preference.IPreferenceStore;

/**
 * Central class for managing the Cactus preferences.
 * 
 * @version $Id:$
 * @author <a href="mailto:cmlenz@apache.org">Christopher Lenz</a>
 * @author <a href="mailto:jruaux@octo.com">Julien Ruaux</a>
 */
public class CactusPreferences
{
    /**
     * The protocol scheme component of the context URL (either 'http' or 
     * 'https') preference.
     */
    public static final String CONTEXT_URL_SCHEME = "contextURL_Scheme";

    /**
     * The host component of the context URL preference. 
     */
    public static final String CONTEXT_URL_HOST = "contextURL_Host";

    /**
     * The port component of the context URL preference.
     */
    public static final String CONTEXT_URL_PORT = "contextURL_Port";

    /**
     * The path component of the context URL preference.
     */
    public static final String CONTEXT_URL_PATH = "contextURL_Path";
    
    /**
     * The directory of the jars needed by Cactus.
     */
    public static final String JARS_DIR = "jars_Dir";

    /**
     * The temp directory used by the plugin to set up containers.
     */
    public static final String TEMP_DIR = "temp_Dir";

    /**
     * Returns the context URL that should be used by the client, as 
     * configured in the plug-in preferences.
     * 
     * @return the context URL
     */
    public static String getContextURL()
    {
        IPreferenceStore store = CactusPlugin.getDefault().getPreferenceStore();
        StringBuffer buf =
            new StringBuffer()
                .append(store.getString(CONTEXT_URL_SCHEME))
                .append("://")
                .append(store.getString(CONTEXT_URL_HOST))
                .append(":")
                .append(store.getInt(CONTEXT_URL_PORT))
                .append("/")
                .append(store.getString(CONTEXT_URL_PATH));
        return buf.toString();
    }

    /**
     * Returns the context URL port that should be used by the client, as 
     * configured in the plug-in preferences.
     * 
     * @return the context port
     */
    public static int getContextURLPort()
    {
        IPreferenceStore store = CactusPlugin.getDefault().getPreferenceStore();
        return store.getInt(CONTEXT_URL_PORT);
    }
    
    /**
     * Returns the context URL path that should be used by the client, as 
     * configured in the plug-in preferences.
     * 
     * @return the context path
     */
    public static String getContextURLPath()
    {
        IPreferenceStore store = CactusPlugin.getDefault().getPreferenceStore();
        return store.getString(CONTEXT_URL_PATH);
    }

    /**
     * Returns the directory containing the jars needed by cactus, as 
     * configured in the plug-in preferences.
     * 
     * @return the context path
     */
    public static String getJarsDir()
    {
        IPreferenceStore store = CactusPlugin.getDefault().getPreferenceStore();
        return store.getString(JARS_DIR);
    }

    /**
     * Returns the temp directory used by cactus, as 
     * configured in the plug-in preferences.
     * 
     * @return the context path
     */
    public static String getTempDir()
    {
        IPreferenceStore store = CactusPlugin.getDefault().getPreferenceStore();
        return store.getString(TEMP_DIR);
    }    
}
