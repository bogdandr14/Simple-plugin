package com.oxygenxml.sdksamples.workspace;

import javax.swing.text.BadLocationException;

import ro.sync.ecss.extensions.api.AuthorDocumentController;
import ro.sync.ecss.extensions.api.content.TextContentIterator;
import ro.sync.ecss.extensions.api.content.TextContext;
import ro.sync.exml.workspace.api.editor.page.author.WSAuthorEditorPage;
import ro.sync.exml.workspace.api.editor.page.text.WSTextEditorPage;

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
	
	public static String replaceOnText(	WSTextEditorPage textPage) {
		
		String replaceAll = textPage.getSelectedText().replaceAll(" ", "_");
		textPage.deleteSelection();
		
		return replaceAll;
	}
}
