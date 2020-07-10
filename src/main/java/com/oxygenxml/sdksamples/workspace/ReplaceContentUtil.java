package com.oxygenxml.sdksamples.workspace;

import javax.swing.text.BadLocationException;

import ro.sync.ecss.extensions.api.AuthorDocumentController;
import ro.sync.ecss.extensions.api.content.TextContentIterator;
import ro.sync.ecss.extensions.api.content.TextContext;
import ro.sync.exml.workspace.api.editor.page.author.WSAuthorEditorPage;
import ro.sync.exml.workspace.api.editor.page.text.WSTextEditorPage;
import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;

public class ReplaceContentUtil {
	public static void replaceOnAuthor(WSAuthorEditorPage authorPageAccess) {
		
		AuthorDocumentController controller = authorPageAccess.getDocumentController();

		TextContentIterator textContentIterator = controller.getTextContentIterator(authorPageAccess.getSelectionStart(), authorPageAccess.getSelectionEnd());
		
		while (textContentIterator.hasNext()) {
			TextContext next = textContentIterator.next();
			CharSequence string = next.getText();
			String string2 = string.toString();
			String replace = string2.replaceAll(" ", "_");
			next.replaceText(replace);
		}
	}
	
	public static void replaceOnText(WSTextEditorPage textPage) {
		
		String replaceAll = textPage.getSelectedText().replaceAll(" ", "_");
		textPage.deleteSelection();
		
		try {
			textPage.getDocument().insertString(textPage.getCaretOffset(), replaceAll, null);
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
