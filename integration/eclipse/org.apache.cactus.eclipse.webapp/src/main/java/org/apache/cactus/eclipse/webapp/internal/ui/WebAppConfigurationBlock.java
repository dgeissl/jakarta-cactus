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
package org.apache.cactus.eclipse.webapp.internal.ui;

import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.util.PixelConverter;
import org.eclipse.jdt.internal.ui.wizards.buildpaths.CPListElement;
import org.eclipse.jdt.internal.ui.wizards.buildpaths.LibrariesWorkbookPage;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.CheckedListDialogField;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.DialogField;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.IDialogFieldListener;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.IStringButtonAdapter;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.LayoutUtil;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.StringButtonDialogField;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.StringDialogField;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * UI block which shows a list of jar entries.
 * 
 * @version $Id: WebAppConfigurationBlock.java 238816 2004-02-29 16:36:46Z vmassol $
 */
public class WebAppConfigurationBlock
{
    /**
     * Number of chars visible in the output war location field 
     */
    private static final int NB_VISIBLE_CHARS = 25;

    /**
     * Field for the output war location. 
     */
    private StringButtonDialogField outputField;

    /**
     * Field for the webapp location. 
     */
    private StringDialogField webappDirField;

    /**
     * UI block that shows a list of jar entries. 
     */
    private LibrariesWorkbookPage libraryPage;

    /**
     * List of entries displayed by the libraryPage. 
     */
    private CheckedListDialogField classPathList;

    /**
     * Java project needed for the libraryPage initialization. 
     */
    private IJavaProject javaProject;

    /**
     * The shell used by dialog popups (directory and file choosers). 
     */
    private Shell shell;

    /**
     * Constructor.
     * @param theShell The shell used by dialog popups
     *     (directory and file choosers)
     * @param theJavaProject Java project needed for libraryPage initialization
     * @param theOutput initial output field value
     * @param theDir initial webapp directory value
     * @param theEntries initial list of entries
     */
    public WebAppConfigurationBlock(final Shell theShell,
        final IJavaProject theJavaProject, final String theOutput,
        final String theDir, final IClasspathEntry[] theEntries)
    {
        shell = theShell;
        javaProject = theJavaProject;
        BuildPathAdapter adapter = new BuildPathAdapter();

        classPathList =
            new CheckedListDialogField(null, null, new LabelProvider());
        classPathList.setDialogFieldListener(adapter);
        //TODO tets if everything works as expected
        IWorkbenchPreferenceContainer root = (IWorkbenchPreferenceContainer)JavaPlugin.getWorkspace().getRoot().getParent();
        //SWTUtil.createPreferenceLink((IWorkbenchPreferenceContainer));
        libraryPage = new LibrariesWorkbookPage( classPathList, root);
        outputField = new StringButtonDialogField(adapter);
        outputField.setDialogFieldListener(adapter);
        outputField.setLabelText(
            WebappMessages.getString(
                "WebAppConfigurationBlock.outputfield.label"));
        outputField.setButtonLabel(
            WebappMessages.getString(
                "WebAppConfigurationBlock.outputfield.button.label"));

        webappDirField = new StringDialogField();
        webappDirField.setDialogFieldListener(adapter);
        webappDirField.setLabelText(
            WebappMessages.getString(
                "WebAppConfigurationBlock.webappdirfield.label"));
        update(theOutput, theDir, theEntries);
    }

    /**
     * Adapter that displatches control events.
     */
    private class BuildPathAdapter
        implements IStringButtonAdapter, IDialogFieldListener
    {

        /**
         * @see org.eclipse.jdt.internal.ui.wizards.dialogfields.
         *     IStringButtonAdapter#changeControlPressed(
         *     org.eclipse.jdt.internal.ui.wizards.dialogfields.DialogField)
         */
        public final void changeControlPressed(final DialogField theField)
        {
            webappChangeControlPressed(theField);
        }

        /**
         * @see org.eclipse.jdt.internal.ui.wizards.dialogfields.
         *     IDialogFieldListener#dialogFieldChanged(
         *     org.eclipse.jdt.internal.ui.wizards.dialogfields.DialogField)
         */
        public final void dialogFieldChanged(final DialogField theField)
        {
            webappDialogFieldChanged(theField);
        }
    }

    /**
     * Adapter that dispatches events from Dialog fields.
     * Possible use : validation of an entry in a dialog field.
     * @param theField field that triggered an event.
     */
    private void webappDialogFieldChanged(final DialogField theField)
    {
        // TODO: validate entries in dialogs
        // Do nothing.
    }

    /**
     * Adapter that dispatches events from StringButtonDialog fields.
     * @param theField field that triggered an event.
     */
    private void webappChangeControlPressed(final DialogField theField)
    {
            if (theField == outputField)
            {
                File output = chooseOutput();
                if (output != null)
                {
                    outputField.setText(output.getAbsolutePath());
                }
            }
    }

    /**
     * Displays a file chooser dialog and returns the chosen file.
     * @return File the chosen file
     */
    private File chooseOutput()
    {
        File output = new File(outputField.getText());
        String initPath = "";
        String initFileName = "webapp.war";

        if (output != null)
        {
            if (!output.isDirectory())
            {
                File dir = output.getParentFile();
                if (dir != null)
                {
                    initPath = dir.getPath();
                }
                initFileName = output.getName();
            }
            else
            {
                initPath = output.getPath();
            }
        }
        FileDialog dialog = new FileDialog(shell);
        dialog.setText("");
           // PreferencesMessages.getString(
             //   WebappMessages.getString(
               //     "WebAppConfigurationBlock.outputchooser.label")));
        dialog.setFileName(initFileName);
        dialog.setFilterExtensions(new String[] {"*.war"});
        dialog.setFilterPath(initPath);
        String res = dialog.open();
        if (res != null)
        {
            return (new File(res));
        }
        return null;
    }


    /**
     * Returns the UI control for this block.
     * @param theParent the parent control.
     * @return Control the created control
     */
    public final Control createContents(final Composite theParent)
    {
        Composite topComp = new Composite(theParent, SWT.NONE);

        GridLayout topLayout = new GridLayout();
        topLayout.numColumns = 3;
        topLayout.marginWidth = 0;
        topLayout.marginHeight = 0;
        topComp.setLayout(topLayout);

        outputField.doFillIntoGrid(topComp, 3);
        webappDirField.doFillIntoGrid(topComp, 3);

        PixelConverter converter = new PixelConverter(topComp);
        LayoutUtil.setWidthHint(
            outputField.getTextControl(null),
            converter.convertWidthInCharsToPixels(NB_VISIBLE_CHARS));
        LayoutUtil.setHorizontalGrabbing(outputField.getTextControl(null));

        Control libraryPageControl = libraryPage.getControl(topComp);
        GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        gd.horizontalSpan = 3;
        libraryPageControl.setLayoutData(gd);
        libraryPage.init(javaProject);
        return topComp;
    }

    /**
     * Returns the text entered in the output field.
     * @return String the text entered
     */
    public final String getOutput()
    {
        return outputField.getText();
    }

    /**
     * Returns the text entered in the webapp field.
     * @return String the text entered
     */
    public final String getWebappDir()
    {
        return webappDirField.getText();
    }

    /**
     * Returns the array of jar entries selected in the libraryPage.
     * @return IClasspathEntry[] the array of jar entries selected
     */
    public final IClasspathEntry[] getWebappClasspath()
    {
        Vector result = new Vector();
        List cplist = classPathList.getElements();
        for (int i = 0; i < cplist.size(); i++)
        {
            CPListElement elem = (CPListElement) cplist.get(i);
            if (elem.getEntryKind() == IClasspathEntry.CPE_LIBRARY)
            {
                result.add(elem.getClasspathEntry());
            }
        }
        return (IClasspathEntry[]) result.toArray(
            new IClasspathEntry[result.size()]);
    }

    /**
     * Returns a list of jar entries contained in an array of entries.
     * @param theClasspathEntries array of classpath entries 
     * @return ArrayList list containing the jar entries
     */
    private ArrayList getExistingEntries(
        final IClasspathEntry[] theClasspathEntries)
    {
        ArrayList newClassPath = new ArrayList();
        for (int i = 0; i < theClasspathEntries.length; i++)
        {
            IClasspathEntry curr = theClasspathEntries[i];
            if (curr.getEntryKind() == IClasspathEntry.CPE_LIBRARY)
            {
                try
                {
                    newClassPath.add(
                        CPListElement.createFromExisting(curr, javaProject));
                }
                catch (NullPointerException e)
                {
                    // an error occured when parsing the entry
                    // (possibly invalid entry)
                    // We don't add it
                }
            }
        }
        return newClassPath;

    }

    /**
     * Refreshes the control with the given values.
     * @param theOutput webapp output war location 
     * @param theDir webapp directory
     * @param theEntries jar entries for the webapp
     */
    public final void update(final String theOutput,
        final String theDir, final IClasspathEntry[] theEntries)
    {
        outputField.setText(theOutput);
        webappDirField.setText(theDir);
        classPathList.setElements(getExistingEntries(theEntries));
    }

    /**
     * Refreshes the control.
     */
    public final void refresh()
    {
        libraryPage.init(javaProject);
    }
}
