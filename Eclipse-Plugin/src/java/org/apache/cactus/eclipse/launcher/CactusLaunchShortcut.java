package org.apache.cactus.eclipse.launcher;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.cactus.eclipse.ui.CactusPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.IDebugModelPresentation;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.debug.ui.ILaunchShortcut;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.debug.ui.JavaUISourceLocator;
import org.eclipse.jdt.internal.junit.launcher.JUnitBaseLaunchConfiguration;
import org.eclipse.jdt.internal.junit.ui.JUnitMessages;
import org.eclipse.jdt.internal.junit.util.TestSearchEngine;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

/*
 * (c) Copyright IBM Corp. 2000, 2001.
 * All Rights Reserved.
 */
public class CactusLaunchShortcut implements ILaunchShortcut {

	protected void searchAndLaunch(Object[] search, String mode) {
		IType[] types= null;

		if (search != null) {
			try {
				types= findLaunchTypes(search);
			} catch (InterruptedException e) {
				CactusPlugin.log(e);
				return;
			} catch (InvocationTargetException e) {
				CactusPlugin.log(e);
				return;
			}
			IType type= null;
			if (types.length == 0) {
				MessageDialog.openInformation(getShell(), JUnitMessages.getString("LaunchTestAction.dialog.title"), JUnitMessages.getString("LaunchTestAction.message.notests")); //$NON-NLS-1$ //$NON-NLS-2$
			} else if (types.length > 1) {
				type= chooseType(types, mode);
			} else {
				type= types[0];
			}
			if (type != null) {
				launch(type, mode);
			}
		}
	}

	private IType[] findLaunchTypes(Object[] search) throws InvocationTargetException, InterruptedException {
		return TestSearchEngine.findTests(new ProgressMonitorDialog(getShell()), search);
	}
	
	/**
	 * @see ILaunchShortcut#launch(IEditorPart, String)
	 */
	public void launch(IEditorPart editor, String mode) {
		IEditorInput input = editor.getEditorInput();
		IJavaElement je = (IJavaElement) input.getAdapter(IJavaElement.class);
		if (je != null) {
			searchAndLaunch(new Object[] {je}, mode);
		} 
	}

	/**
	 * @see ILaunchShortcut#launch(ISelection, String)
	 */
	public void launch(ISelection selection, String mode) {
		if (selection instanceof IStructuredSelection) {
			searchAndLaunch(((IStructuredSelection)selection).toArray(), mode);
		} 
	}
	
	/**
	 * Prompts the user to select a type
	 * 
	 * @return the selected type or <code>null</code> if none.
	 */
	protected IType chooseType(IType[] types, String mode) {
		ElementListSelectionDialog dialog= new ElementListSelectionDialog(getShell(), new JavaElementLabelProvider());
		dialog.setElements(types);
		dialog.setTitle(JUnitMessages.getString("LaunchTestAction.dialog.title2")); //$NON-NLS-1$
		if (mode.equals(ILaunchManager.DEBUG_MODE)) {
			dialog.setMessage(JUnitMessages.getString("LaunchTestAction.message.selectTestToRun"));  //$NON-NLS-1$
		} else {
			dialog.setMessage(JUnitMessages.getString("LaunchTestAction.message.selectTestToDebug")); //$NON-NLS-1$
		}
		dialog.setMultipleSelection(false);
		if (dialog.open() == dialog.OK) {
			return (IType)dialog.getFirstResult();
		}
		return null;
	}
	
	/**
	 * Launches a configuration for the given type
	 */
	protected void launch(IType type, String mode) {
		try { 
			ILaunchConfiguration config = findLaunchConfiguration(type, mode);
			if (config != null) {
				config.launch(mode, null);
			}			
		} catch (CoreException e) {
			ErrorDialog.openError(getShell(), JUnitMessages.getString("LaunchTestAction.message.launchFailed"), e.getMessage(), e.getStatus());  //$NON-NLS-1$
		}
	}
	
	/**
	 * Locate a configuration to relaunch for the given type.  If one cannot be found, create one.
	 * 
	 * @return a re-useable config or <code>null</code> if none
	 */
	protected ILaunchConfiguration findLaunchConfiguration(IType type, String mode) {
		ILaunchConfigurationType configType= getCactusLaunchConfigType();
		List candidateConfigs= Collections.EMPTY_LIST;
		try {
			ILaunchConfiguration[] configs= getLaunchManager().getLaunchConfigurations(configType);
			candidateConfigs= new ArrayList(configs.length);
			for (int i= 0; i < configs.length; i++) {
				ILaunchConfiguration config= configs[i];
				if (config.getAttribute(IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME, "").equals(type.getFullyQualifiedName())) { //$NON-NLS-1$
					if (config.getAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, "").equals(type.getJavaProject().getElementName())) { //$NON-NLS-1$
						candidateConfigs.add(config);
					}
				}
			}
		} catch (CoreException e) {
			CactusPlugin.log(e);
		}
		
		// If there are no existing configs associated with the IType, create one.
		// If there is exactly one config associated with the IType, return it.
		// Otherwise, if there is more than one config associated with the IType, prompt the
		// user to choose one.
		int candidateCount= candidateConfigs.size();
		if (candidateCount < 1) {
			return createConfiguration(type);
		} else if (candidateCount == 1) {
			return (ILaunchConfiguration) candidateConfigs.get(0);
		} else {
			// Prompt the user to choose a config.  A null result means the user
			// cancelled the dialog, in which case this method returns null,
			// since cancelling the dialog should also cancel launching anything.
			ILaunchConfiguration config= chooseConfiguration(candidateConfigs, mode);
			if (config != null) {
				return config;
			}
		}
		return null;
	}
	
	/**
	 * Show a selection dialog that allows the user to choose one of the specified
	 * launch configurations.  Return the chosen config, or <code>null</code> if the
	 * user cancelled the dialog.
	 */
	protected ILaunchConfiguration chooseConfiguration(List configList, String mode) {
		IDebugModelPresentation labelProvider = DebugUITools.newDebugModelPresentation();
		ElementListSelectionDialog dialog= new ElementListSelectionDialog(getShell(), labelProvider);
		dialog.setElements(configList.toArray());
		dialog.setTitle(JUnitMessages.getString("LaunchTestAction.message.selectConfiguration")); //$NON-NLS-1$
		if (mode.equals(ILaunchManager.DEBUG_MODE)) {
			dialog.setMessage(JUnitMessages.getString("LaunchTestAction.message.selectDebugConfiguration")); //$NON-NLS-1$
		} else {
			dialog.setMessage(JUnitMessages.getString("LaunchTestAction.message.selectRunConfiguration")); //$NON-NLS-1$
		}
		dialog.setMultipleSelection(false);
		int result= dialog.open();
		labelProvider.dispose();
		if (result == dialog.OK) {
			return (ILaunchConfiguration)dialog.getFirstResult();
		}
		return null;		
	}
	
	/**
	 * Create & return a new configuration based on the specified <code>IType</code>.
	 */
	protected ILaunchConfiguration createConfiguration(IType type) {
		ILaunchConfiguration config = null;
		try {
			ILaunchConfigurationType configType= getCactusLaunchConfigType();
			ILaunchConfigurationWorkingCopy wc = configType.newInstance(null, getLaunchManager().generateUniqueLaunchConfigurationNameFrom(type.getElementName())); 
			wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME, type.getFullyQualifiedName());
			wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, type.getJavaProject().getElementName());
			wc.setAttribute(IDebugUIConstants.ATTR_TARGET_DEBUG_PERSPECTIVE, IDebugUIConstants.PERSPECTIVE_DEFAULT);
			wc.setAttribute(IDebugUIConstants.ATTR_TARGET_RUN_PERSPECTIVE, IDebugUIConstants.PERSPECTIVE_NONE);
			wc.setAttribute(ILaunchConfiguration.ATTR_SOURCE_LOCATOR_ID, JavaUISourceLocator.ID_PROMPTING_JAVA_SOURCE_LOCATOR);
			wc.setAttribute(JUnitBaseLaunchConfiguration.ATTR_KEEPRUNNING, false);
			config= wc.doSave();		
		} catch (CoreException ce) {
			CactusPlugin.log(ce);
		}
		return config;
	}
	
	/**
	 * Returns the local java launch config type
	 */
	protected ILaunchConfigurationType getCactusLaunchConfigType() {
		ILaunchManager lm= DebugPlugin.getDefault().getLaunchManager();
		return lm.getLaunchConfigurationType(CactusLaunchConfiguration.ID_CACTUS_APPLICATION);		
	}	
	
	protected ILaunchManager getLaunchManager() {
		return DebugPlugin.getDefault().getLaunchManager();
	}
	
	/**
	 * Convenience method to get the window that owns this action's Shell.
	 */
	protected Shell getShell() {
		return CactusPlugin.getActiveWorkbenchShell();
	}
}
