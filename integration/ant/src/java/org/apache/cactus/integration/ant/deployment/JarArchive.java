/*
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 The Apache Software Foundation.  All rights
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
package org.apache.cactus.integration.ant.deployment;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarInputStream;
import java.util.zip.ZipEntry;

/**
 * Abstract base class for classes that provide convenient access to the
 * contents of a J2EE deployment archive (EAR or WAR, for example).
 * 
 * @author <a href="mailto:cmlenz@apache.org">Christopher Lenz</a>
 *
 * @since Cactus 1.5
 * @version $Id$
 */
public abstract class JarArchive
{

    // Instance Variables ------------------------------------------------------

    /**
     * The content of the archive as an input stream.
     */
    private InputStream content;

    // Constructors ------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param theFile The web application archive
     * @throws IOException If there was a problem reading the WAR
     */
    public JarArchive(File theFile)
        throws IOException
    {
        this(new FileInputStream(theFile));
    }

    /**
     * Constructor.
     * 
     * @param theInputStream The input stream for the web application archive
     * @throws IOException If there was a problem reading the WAR
     */
    public JarArchive(InputStream theInputStream)
        throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[2048];
        int bytesRead = -1;
        while ((bytesRead = theInputStream.read(buffer)) != -1)
        {
            baos.write(buffer, 0, bytesRead);
        }
        this.content = new ByteArrayInputStream(baos.toByteArray());
    }

    // Public Methods ----------------------------------------------------------

    /**
     * Returns the full path of a named resource in the archive.
     * 
     * @param theName The name of the resource
     * @return The full path to the resource inside the archive
     * @throws IOException If an I/O error occurred reading the archive
     */
    public String findResource(String theName)
        throws IOException
    {
        JarInputStream in = null;
        try
        {
            in = new JarInputStream(this.content);
            ZipEntry entry = null;
            while ((entry = in.getNextEntry()) != null)
            {
                String entryName = entry.getName();
                int lastSlashIndex = entryName.lastIndexOf('/');
                if (lastSlashIndex >= 0)
                {
                    entryName = entryName.substring(lastSlashIndex + 1);
                }
                if (entryName.equals(theName))
                {
                    return entry.getName();
                }
            }
        }
        finally
        {
            if (in != null)
            {
                in.close();
            }
        }
        return null;
    }

    // Protected Methods -------------------------------------------------------

    /**
     * Returns the content of the archive as <code>JarInputStream</code>.
     * 
     * @return The input stream
     * @throws IOException If an exception occurred reading the archive
     */
    protected final JarInputStream getContentAsStream()
        throws IOException
    {
        return new JarInputStream(this.content);
    }

}
