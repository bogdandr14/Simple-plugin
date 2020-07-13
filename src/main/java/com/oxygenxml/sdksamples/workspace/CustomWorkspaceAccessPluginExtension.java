package com.oxygenxml.sdksamples.workspace;

import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;
import javax.swing.text.Segment;

import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.AuthorDocumentController;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.api.Content;
import ro.sync.ecss.extensions.api.content.RangeProcessor;
import ro.sync.ecss.extensions.api.content.TextContentIterator;
import ro.sync.ecss.extensions.api.content.TextContext;
import ro.sync.ecss.extensions.api.node.AuthorDocumentFragment;
import ro.sync.ecss.extensions.api.node.AuthorNode;
import ro.sync.ecss.extensions.api.node.ContentIterator;
import ro.sync.exml.editor.EditorPageConstants;
import ro.sync.exml.plugin.workspace.WorkspaceAccessPluginExtension;
import ro.sync.exml.workspace.api.PluginWorkspace;
import ro.sync.exml.workspace.api.PluginWorkspaceProvider;
import ro.sync.exml.workspace.api.editor.WSEditor;
import ro.sync.exml.workspace.api.editor.page.author.WSAuthorEditorPage;
import ro.sync.exml.workspace.api.editor.page.text.WSTextEditorPage;
import ro.sync.exml.workspace.api.listeners.WSEditorChangeListener;
import ro.sync.exml.workspace.api.standalone.MenuBarCustomizer;
import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;
import ro.sync.exml.workspace.api.standalone.ToolbarComponentsCustomizer;
import ro.sync.exml.workspace.api.standalone.ToolbarInfo;
import ro.sync.exml.workspace.api.standalone.ViewComponentCustomizer;
import ro.sync.exml.workspace.api.standalone.ViewInfo;
import ro.sync.exml.workspace.api.standalone.actions.MenusAndToolbarsContributorCustomizer;
import ro.sync.exml.workspace.api.standalone.ui.ToolbarButton;

/**
 * Plugin extension - workspace access extension.
 */
public class CustomWorkspaceAccessPluginExtension implements WorkspaceAccessPluginExtension {
	/**
	 * The custom messages area. A sample component added to your custom view.
	 */
	private JTextArea customMessagesArea;

	/**
	 * @see ro.sync.exml.plugin.workspace.WorkspaceAccessPluginExtension#applicationStarted(ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace)
	 */
	public void applicationStarted(final StandalonePluginWorkspace pluginWorkspaceAccess) {
		// You can set or read global options.
		// The "ro.sync.exml.options.APIAccessibleOptionTags" contains all accessible
		// keys.
		// pluginWorkspaceAccess.setGlobalObjectProperty("can.edit.read.only.files",
		// Boolean.FALSE);
		// Check In action

		// You can access the content inside each opened WSEditor depending on the
		// current editing page (Text/Grid or Author).
		// A sample action which will be mounted on the main menu, toolbar and
		// contextual menu.
		//final Action selectionSourceAction = transformSpaceAction(pluginWorkspaceAccess);
		final Action spaceToUnderscoreAction = transformSpaceAction(pluginWorkspaceAccess,
				" ", "_", "spaces to underscore");
		final Action underscoreToSpaceAction = transformSpaceAction(pluginWorkspaceAccess,
				"_", " ", "underscores to space");
		final Action underscoreToCamelAction = transformCamelAction(pluginWorkspaceAccess,
				false, "to camel case");

		final Action underscoreToPascalAction = transformCamelAction(pluginWorkspaceAccess,
				true, "to pascal case");

		// Mount the action on the contextual menus for the Text and Author modes.
		pluginWorkspaceAccess.addMenusAndToolbarsContributorCustomizer(new MenusAndToolbarsContributorCustomizer() {
			/**
			 * Customize the author popup menu.
			 */
			@Override
			public void customizeAuthorPopUpMenu(JPopupMenu popup, AuthorAccess authorAccess) {
				// Add our custom action
				JMenu transformMenu = new JMenu("Transform");
				transformMenu.add(spaceToUnderscoreAction);
				transformMenu.add(underscoreToSpaceAction);
				transformMenu.add(underscoreToCamelAction);
				transformMenu.add(underscoreToPascalAction);
				popup.add(transformMenu);
			}

			@Override
			public void customizeTextPopUpMenu(JPopupMenu popup, WSTextEditorPage textPage) {
				// Add our custom action
				JMenu transformMenu = new JMenu("Transform");
				transformMenu.add(spaceToUnderscoreAction);
				transformMenu.add(underscoreToSpaceAction);
				transformMenu.add(underscoreToCamelAction);
				transformMenu.add(underscoreToPascalAction);
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
				JMenu myMenu = new JMenu("Transform");
				JMenuItem toUnderscoreItem = new JMenuItem(spaceToUnderscoreAction);
				JMenuItem toSpaceItem = new JMenuItem(underscoreToSpaceAction);
				JMenuItem toCamelItem = new JMenuItem(underscoreToCamelAction);
				JMenuItem toPascalItem = new JMenuItem(underscoreToPascalAction);

				myMenu.add(toUnderscoreItem);
				myMenu.add(toSpaceItem);
				myMenu.add(toCamelItem);
				myMenu.add(toPascalItem);

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
					spaceToUnderscoreAction
							.setEnabled(EditorPageConstants.PAGE_AUTHOR.equals(editorAccess.getCurrentPageID())
									|| EditorPageConstants.PAGE_TEXT.equals(editorAccess.getCurrentPageID()));
					underscoreToSpaceAction.setEnabled(EditorPageConstants.PAGE_AUTHOR.equals(editorAccess.getCurrentPageID())
							|| EditorPageConstants.PAGE_TEXT.equals(editorAccess.getCurrentPageID()));
					underscoreToCamelAction
					.setEnabled(EditorPageConstants.PAGE_AUTHOR.equals(editorAccess.getCurrentPageID())
							|| EditorPageConstants.PAGE_TEXT.equals(editorAccess.getCurrentPageID()));
					underscoreToPascalAction
					.setEnabled(EditorPageConstants.PAGE_AUTHOR.equals(editorAccess.getCurrentPageID())
							|| EditorPageConstants.PAGE_TEXT.equals(editorAccess.getCurrentPageID()));
				}
			}

			@Override
			public void editorClosed(URL editorLocation) {
				// An edited XML document has been closed.
			}

			/**
			 * @see ro.sync.exml.workspace.api.listeners.WSEditorChangeListener#editorAboutToBeClosed(java.net.URL)
			 */
			@Override
			public boolean editorAboutToBeClosed(URL editorLocation) {
				// You can veto the closing of an XML document.
				// Allow close
				return true;
			}

			/**
			 * The editor was relocated (Save as was called).
			 * 
			 * @see ro.sync.exml.workspace.api.listeners.WSEditorChangeListener#editorRelocated(java.net.URL,
			 *      java.net.URL)
			 */
			@Override
			public void editorRelocated(URL previousEditorLocation, URL newEditorLocation) {
				//
			}

			@Override
			public void editorPageChanged(URL editorLocation) {
				checkActionsStatus(editorLocation);
			}

			@Override
			public void editorSelected(URL editorLocation) {
				checkActionsStatus(editorLocation);
			}

			@Override
			public void editorActivated(URL editorLocation) {
				checkActionsStatus(editorLocation);
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
				if ("SampleWorkspaceAccessToolbarID".equals(toolbarInfo.getToolbarID())) {
					List<JComponent> comps = new ArrayList<JComponent>();
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
					
					toolbarInfo.setComponents(comps.toArray(new JComponent[3]));
				}
			}
		});

		pluginWorkspaceAccess.addViewComponentCustomizer(new ViewComponentCustomizer() {
			/**
			 * @see ro.sync.exml.workspace.api.standalone.ViewComponentCustomizer#customizeView(ro.sync.exml.workspace.api.standalone.ViewInfo)
			 */
			public void customizeView(ViewInfo viewInfo) {
				if (
				// The view ID defined in the "plugin.xml"
				"SampleWorkspaceAccessID".equals(viewInfo.getViewID())) {
					customMessagesArea = new JTextArea("Messages:");
					viewInfo.setComponent(new JScrollPane(customMessagesArea));
					viewInfo.setTitle("Custom Messages");
					// You can have images located inside the JAR library and use them...
					// viewInfo.setIcon(new
					// ImageIcon(getClass().getClassLoader().getResource("images/customMessage.png").toString()));
				}
			}
		});
	}

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
							ReplaceCamelUtil.replaceCamelOnAuthor(authorPageAccess,b);

						} else {
							// No selection
							pluginWorkspaceAccess.showInformationMessage("No selection available.");
						}
					} else if (EditorPageConstants.PAGE_TEXT.equals(editorAccess.getCurrentPageID())) {
						WSTextEditorPage textPage = (WSTextEditorPage) editorAccess.getCurrentPage();
						if (textPage.hasSelection()) {
							ReplaceCamelUtil.replaceCamelOnText(textPage, b);

						} else {
							// No selection
							pluginWorkspaceAccess.showInformationMessage("No selection available.");
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
	@SuppressWarnings("serial")
	private AbstractAction transformSpaceAction(final StandalonePluginWorkspace pluginWorkspaceAccess,
			String toReplace, String replaceWith, String replaceOperation) {
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
							ReplaceContentUtil.replaceOnAuthor(authorPageAccess,toReplace, replaceWith);

						} else {
							// No selection
							pluginWorkspaceAccess.showInformationMessage("No selection available.");
						}
					} else if (EditorPageConstants.PAGE_TEXT.equals(editorAccess.getCurrentPageID())) {
						WSTextEditorPage textPage = (WSTextEditorPage) editorAccess.getCurrentPage();
						if (textPage.hasSelection()) {
							ReplaceContentUtil.replaceOnText(textPage,toReplace, replaceWith);

						} else {
							// No selection
							pluginWorkspaceAccess.showInformationMessage("No selection available.");
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