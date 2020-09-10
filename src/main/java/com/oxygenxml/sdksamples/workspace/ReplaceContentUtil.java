package com.oxygenxml.sdksamples.workspace;

import javax.swing.text.BadLocationException;

import ro.sync.ecss.extensions.api.AuthorDocumentController;
import ro.sync.ecss.extensions.api.content.TextContentIterator;
import ro.sync.ecss.extensions.api.content.TextContext;
import ro.sync.exml.workspace.api.editor.page.author.WSAuthorEditorPage;
import ro.sync.exml.workspace.api.editor.page.text.WSTextEditorPage;
/**
 * This class is used to replace the space with underline and vice versa in author and text editor.
 * 
 * @author Bogdan Draghici
 *
 */public class ReplaceContentUtil {
	
	public static void replaceOnAuthor(WSAuthorEditorPage authorPageAccess, String toReplace, String replaceWith) {
		AuthorDocumentController controller = authorPageAccess.getDocumentController();
	
		TextContentIterator textContentIterator = controller.getTextContentIterator(authorPageAccess.getSelectionStart(), authorPageAccess.getSelectionEnd());
		
		//take each content from an element and modify the string inside 
		while (textContentIterator.hasNext()) {
			TextContext next = textContentIterator.next();
			CharSequence string = next.getText();
			String string2 = string.toString();
			//replace spaces with underscores or vice versa in the current content
			next.replaceText(string2.replaceAll(toReplace, replaceWith));
		}
	}
	
	public static void replaceOnText(WSTextEditorPage textPage,String toReplace, String replaceWith) {
		//replaces all characters toReplace with the new characters replaceWith
		String replaceAll = textPage.getSelectedText().replaceAll(toReplace, replaceWith);
		textPage.deleteSelection();
		
		try {
			textPage.getDocument().insertString(textPage.getCaretOffset(), replaceAll, null);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}
}
