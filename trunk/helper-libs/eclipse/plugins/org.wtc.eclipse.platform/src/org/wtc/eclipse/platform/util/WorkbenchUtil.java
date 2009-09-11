package org.wtc.eclipse.platform.util;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import abbot.tester.swt.Robot;

/**
 * WorkbenchUtil - Workbench utility methods.
 */
public class WorkbenchUtil {

	/**
	 * Get the current workbench.
	 * 
	 * @return IWorkbench - the active workbench
	 */
	public static IWorkbench getWorkbench() {
		return PlatformUI.getWorkbench();
	}

	/**
	 * Get the current active workbench window.
	 * 
	 * @return IWorkbenchWindow - the active workbench window (or <code>null</code> if there is none).
	 */
	public static IWorkbenchWindow getWorkbenchWindow() {
		final IWorkbenchWindow window[] = new IWorkbenchWindow[1];
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				IWorkbench workbench = getWorkbench();
				if (workbench == null)
					return;
				window[0] = workbench.getActiveWorkbenchWindow();
			}
		});
		return window[0];
	}

	/**
	 * Bring the current workbench shell to front.
	 */
	public static void bringWorkbenchToFront() {
		IWorkbenchWindow window = getWorkbenchWindow();
		if (window == null)
			throw new IllegalStateException("workbench window is null"); //$NON-NLS-1$
		Shell workbenchShell = window.getShell();
		bringToFront(workbenchShell);
	}

	/**
	 * Bring the given shell to front.
	 * 
	 * @param shell - the shell to force front
	 */
	public static void bringToFront(final Shell shell) {
		if (shell == null)
			throw new IllegalArgumentException("shell is null"); //$NON-NLS-1$;
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				shell.forceActive();
				shell.setFocus();
			}
		});
		Robot.waitForIdle(shell.getDisplay());
	}

}
