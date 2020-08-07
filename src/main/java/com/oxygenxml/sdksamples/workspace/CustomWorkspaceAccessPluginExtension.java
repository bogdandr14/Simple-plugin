package com.oxygenxml.sdksamples.workspace;

import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.exml.editor.EditorPageConstants;
import ro.sync.exml.plugin.workspace.WorkspaceAccessPluginExtension;
import ro.sync.exml.workspace.api.editor.WSEditor;
import ro.sync.exml.workspace.api.editor.page.author.WSAuthorEditorPage;
import ro.sync.exml.workspace.api.editor.page.text.WSTextEditorPage;
import ro.sync.exml.workspace.api.listeners.WSEditorChangeListener;
import ro.sync.exml.workspace.api.standalone.MenuBarCustomizer;
import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;
import ro.sync.exml.workspace.api.standalone.ToolbarComponentsCustomizer;
import ro.sync.exml.workspace.api.standalone.ToolbarInfo;
import ro.sync.exml.workspace.api.standalone.actions.MenusAndToolbarsContributorCustomizer;
import ro.sync.exml.workspace.api.standalone.ui.ToolbarButton;

/**
 * Plugin extension - workspace access extension.
 */
public class CustomWorkspaceAccessPluginExtension implements WorkspaceAccessPluginExtension {
	/**
	 * The custom messages area. A sample component added to your custom view.
	 */
	private static final String MENU_NAME = "Replace";
	private static final String NO_SELECTION = "No selection available.";

	/**
	 * @see ro.sync.exml.plugin.workspace.WorkspaceAccessPluginExtension#applicationStarted(ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace)
	 */
	public void applicationStarted(final StandalonePluginWorkspace pluginWorkspaceAccess) {
		// You can set or read global options.
		// The "ro.sync.exml.options.APIAccessibleOptionTags" contains all accessible
		// keys.
		pluginWorkspaceAccess.setGlobalObjectProperty("can.edit.read.only.files", Boolean.FALSE);
		// Check In action
  
		// You can access the content inside each opened WSEditor depending on the
		// current editing page (Text/Grid or Author).
		// Actions which will be mounted on the main menu, toolbar and
		// contextual menu.
		
		final Action spaceToUnderscoreAction = transformSpaceAction(pluginWorkspaceAccess, " ", "_",
				"spaces to underscore");
		final Action underscoreToSpaceAction = transformSpaceAction(pluginWorkspaceAccess, "_", " ",
				"underscores to space");
		final Action underscoreToCamelAction = transformCamelAction(pluginWorkspaceAccess, false, "to camel case");

		final Action underscoreToPascalAction = transformCamelAction(pluginWorkspaceAccess, true, "to pascal case");

		final Action camelToUnderscoreAction = transformUnderscoreAction(pluginWorkspaceAccess, "_",
				"camel to underscore");

		final Action camelToSpaceAction = transformUnderscoreAction(pluginWorkspaceAccess, " ", "camel to space");
		// Mount the action on the contextual menus for the Text and Author modes.
		pluginWorkspaceAccess.addMenusAndToolbarsContributorCustomizer(new MenusAndToolbarsContributorCustomizer() {
			/**
			 * Customize the author popup menu.
			 */
			@Override
			public void customizeAuthorPopUpMenu(JPopupMenu popup, AuthorAccess authorAccess) {
				// Add our custom actions
				JMenu transformMenu = new JMenu(MENU_NAME);
				transformMenu.add(spaceToUnderscoreAction);
				transformMenu.add(underscoreToSpaceAction);
				transformMenu.add(underscoreToCamelAction);
				transformMenu.add(underscoreToPascalAction);
				transformMenu.add(camelToUnderscoreAction);
				transformMenu.add(camelToSpaceAction);
				popup.add(transformMenu);
			}

			@Override
			public void customizeTextPopUpMenu(JPopupMenu popup, WSTextEditorPage textPage) {
				// Add our custom actions
				JMenu transformMenu = new JMenu(MENU_NAME);
				transformMenu.add(spaceToUnderscoreAction);
				transformMenu.add(underscoreToSpaceAction);
				transformMenu.add(underscoreToCamelAction);
				transformMenu.add(underscoreToPascalAction);
				transformMenu.add(camelToUnderscoreAction);
				transformMenu.add(camelToSpaceAction);
				popup.add(transformMenu);

			}
		});

		// Create your own main menu and add it to Oxygen or remove one of Oxygen's
		// menus...
		pluginWorkspaceAccess.addMenuBarCustomizer(new MenuBarCustomizer() {
			/**
			 * @see ro.sync.exml.workspace.api.standalone.MenuBarCustomizer#customizeMainMenu(javax.swing.JMenuBar)
			 */
			public void customizeMainMenu(JMenuBar mainMenuBar) {
				JMenu myMenu = new JMenu(MENU_NAME);
			
				myMenu.add(spaceToUnderscoreAction);
				myMenu.add(underscoreToSpaceAction);
				myMenu.add(underscoreToCamelAction);
				myMenu.add(underscoreToPascalAction);
				myMenu.add(camelToUnderscoreAction);
				myMenu.add(camelToSpaceAction);
				// Add your menu before the Help menu
				mainMenuBar.add(myMenu, mainMenuBar.getMenuCount() - 1);
			}
		});

		pluginWorkspaceAccess.addEditorChangeListener(new WSEditorChangeListener() {
			@Override
			public boolean editorAboutToBeOpenedVeto(URL editorLocation) {
				// You can reject here the opening of an URL if you want
				return true;
			}

			@Override
			public void editorOpened(URL editorLocation) {
				checkActionsStatus(editorLocation);
			}

			// Check actions status
			private void checkActionsStatus(URL editorLocation) {
				WSEditor editorAccess = pluginWorkspaceAccess
						.getCurrentEditorAccess(StandalonePluginWorkspace.MAIN_EDITING_AREA);
				if (editorAccess != null) {
					boolean isOnEditor = EditorPageConstants.PAGE_AUTHOR.equals(editorAccess.getCurrentPageID())
          		|| EditorPageConstants.PAGE_TEXT.equals(editorAccess.getCurrentPageID());
          spaceToUnderscoreAction.setEnabled(isOnEditor);
					underscoreToSpaceAction.setEnabled(isOnEditor);
					underscoreToCamelAction.setEnabled(isOnEditor);
					underscoreToPascalAction.setEnabled(isOnEditor);
					camelToUnderscoreAction.setEnabled(isOnEditor);
					camelToSpaceAction.setEnabled(isOnEditor);
				}
			}
		}, StandalonePluginWorkspace.MAIN_EDITING_AREA);

		// You can use this callback to populate your custom toolbar (defined in the
		// plugin.xml) or to modify an existing Oxygen toolbar
		// (add components to it or remove them)
		pluginWorkspaceAccess.addToolbarComponentsCustomizer(new ToolbarComponentsCustomizer() {
			/**
			 * @see ro.sync.exml.workspace.api.standalone.ToolbarComponentsCustomizer#customizeToolbar(ro.sync.exml.workspace.api.standalone.ToolbarInfo)
			 */
			public void customizeToolbar(ToolbarInfo toolbarInfo) {
				// The toolbar ID is defined in the "plugin.xml"
				if ("ReplaceWorkspaceAccessToolbarID".equals(toolbarInfo.getToolbarID())) {
					List<JComponent> comps = new ArrayList<>();
					JComponent[] initialComponents = toolbarInfo.getComponents();
					boolean hasInitialComponents = initialComponents != null && initialComponents.length > 0;
					if (hasInitialComponents) {
						// Add initial toolbar components
						for (JComponent toolbarItem : initialComponents) {
							comps.add(toolbarItem);
						}
					}

					// Add your own toolbar button using our
					// "ro.sync.exml.workspace.api.standalone.ui.ToolbarButton" API component
					ToolbarButton customUnderscoreButton = new ToolbarButton(spaceToUnderscoreAction, true);
					comps.add(customUnderscoreButton);

					ToolbarButton customSpaceButton = new ToolbarButton(underscoreToSpaceAction, true);
					comps.add(customSpaceButton);

					ToolbarButton customCamelButton = new ToolbarButton(underscoreToCamelAction, true);
					comps.add(customCamelButton);

					ToolbarButton customPascalButton = new ToolbarButton(underscoreToPascalAction, true);
					comps.add(customPascalButton);

					ToolbarButton customToUnderscoreButton = new ToolbarButton(camelToUnderscoreAction, true);
					comps.add(customToUnderscoreButton);

					ToolbarButton customToSpaceButton = new ToolbarButton(camelToSpaceAction, true);
					comps.add(customToSpaceButton);

					toolbarInfo.setComponents(comps.toArray(new JComponent[5]));
				}
			}
		});
	}
	//action to transform from pascal/camel case to underscore or space 
	@SuppressWarnings("serial")
	private Action transformUnderscoreAction(StandalonePluginWorkspace pluginWorkspaceAccess, String replaceWith,
			String string) {
		return new AbstractAction(string) {
			public void actionPerformed(ActionEvent actionevent) {
				// Get the current opened XML document
				WSEditor editorAccess = pluginWorkspaceAccess
						.getCurrentEditorAccess(StandalonePluginWorkspace.MAIN_EDITING_AREA);
				// The action is available only in Author mode.
				if (editorAccess != null) {
					if (EditorPageConstants.PAGE_AUTHOR.equals(editorAccess.getCurrentPageID())) {
						WSAuthorEditorPage authorPageAccess = (WSAuthorEditorPage) editorAccess.getCurrentPage();
						if (authorPageAccess.hasSelection()) {
							ReplaceFromCamelUtil.replaceFromCamelOnAuthor(authorPageAccess, replaceWith);
						} else {
							// No selection
							pluginWorkspaceAccess.showInformationMessage(NO_SELECTION);
						}
					} else if (EditorPageConstants.PAGE_TEXT.equals(editorAccess.getCurrentPageID())) {
						WSTextEditorPage textPage = (WSTextEditorPage) editorAccess.getCurrentPage();
						if (textPage.hasSelection()) {
							ReplaceFromCamelUtil.replaceFromCamelOnText(textPage, replaceWith);
						} else {
							// No selection
							pluginWorkspaceAccess.showInformationMessage(NO_SELECTION);
						}
					}
				}
			}
		};
	}
	//action to transform from underscore or space to pascal/camel case
	@SuppressWarnings("serial")
	private Action transformCamelAction(StandalonePluginWorkspace pluginWorkspaceAccess, boolean b, String string) {
		return new AbstractAction(string) {
			public void actionPerformed(ActionEvent actionevent) {
				// Get the current opened XML document
				WSEditor editorAccess = pluginWorkspaceAccess
						.getCurrentEditorAccess(StandalonePluginWorkspace.MAIN_EDITING_AREA);
				// The action is available only in Author mode.
				if (editorAccess != null) {
					if (EditorPageConstants.PAGE_AUTHOR.equals(editorAccess.getCurrentPageID())) {
						WSAuthorEditorPage authorPageAccess = (WSAuthorEditorPage) editorAccess.getCurrentPage();
						if (authorPageAccess.hasSelection()) {
							ReplaceCamelUtil.replaceCamelOnAuthor(authorPageAccess, b);
						} else {
							// No selection
							pluginWorkspaceAccess.showInformationMessage(NO_SELECTION);
						}
					} else if (EditorPageConstants.PAGE_TEXT.equals(editorAccess.getCurrentPageID())) {
						WSTextEditorPage textPage = (WSTextEditorPage) editorAccess.getCurrentPage();
						if (textPage.hasSelection()) {
							ReplaceCamelUtil.replaceCamelOnText(textPage, b);
						} else {
							// No selection
							pluginWorkspaceAccess.showInformationMessage(NO_SELECTION);
						}
					}
				}
			}
		};
	}

	/**
	 * Create the Swing action which shows the current selection.
	 * 
	 * @param pluginWorkspaceAccess The plugin workspace access.
	 * @return The "Show Selection" action
	 */
	//action to transform from underscore to space or from space to underscore
	@SuppressWarnings("serial")
	private AbstractAction transformSpaceAction(final StandalonePluginWorkspace pluginWorkspaceAccess, String toReplace,
			String replaceWith, String replaceOperation) {
		return new AbstractAction(replaceOperation) {
			public void actionPerformed(ActionEvent actionevent) {
				// Get the current opened XML document
				WSEditor editorAccess = pluginWorkspaceAccess
						.getCurrentEditorAccess(StandalonePluginWorkspace.MAIN_EDITING_AREA);
				// The action is available only in Author mode.
				if (editorAccess != null) {
					if (EditorPageConstants.PAGE_AUTHOR.equals(editorAccess.getCurrentPageID())) {
						WSAuthorEditorPage authorPageAccess = (WSAuthorEditorPage) editorAccess.getCurrentPage();
						if (authorPageAccess.hasSelection()) {
							ReplaceContentUtil.replaceOnAuthor(authorPageAccess, toReplace, replaceWith);
						} else {
							// No selection
							pluginWorkspaceAccess.showInformationMessage(NO_SELECTION);
						}
					} else if (EditorPageConstants.PAGE_TEXT.equals(editorAccess.getCurrentPageID())) {
						WSTextEditorPage textPage = (WSTextEditorPage) editorAccess.getCurrentPage();
						if (textPage.hasSelection()) {
							ReplaceContentUtil.replaceOnText(textPage, toReplace, replaceWith);
						} else {
							// No selection
							pluginWorkspaceAccess.showInformationMessage(NO_SELECTION);
						}
					}
				}
			}
		};
	}

	/**
	 * @see ro.sync.exml.plugin.workspace.WorkspaceAccessPluginExtension#applicationClosing()
	 */
	public boolean applicationClosing() {
		// You can reject the application closing here
		return true;
	}
}
