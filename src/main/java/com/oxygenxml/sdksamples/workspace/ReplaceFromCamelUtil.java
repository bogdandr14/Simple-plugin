package com.oxygenxml.sdksamples.workspace;

import javax.swing.text.BadLocationException;

import ro.sync.ecss.extensions.api.AuthorDocumentController;
import ro.sync.ecss.extensions.api.content.TextContentIterator;
import ro.sync.ecss.extensions.api.content.TextContext;
import ro.sync.exml.workspace.api.editor.page.author.WSAuthorEditorPage;
import ro.sync.exml.workspace.api.editor.page.text.WSTextEditorPage;

public class ReplaceFromCamelUtil {
	
	static String fromCamelCase(String s, String add){
		   String[] parts = s.split("(?=[A-Z])");
		   String normalCaseString = "";
		   for (String part : parts){
			   if(normalCaseString.equals("")) {
				   normalCaseString = part;
				   continue;
			   }
			   if(add.equals("_"))
				   normalCaseString = normalCaseString + add + part.toLowerCase();
			   else
				   normalCaseString = normalCaseString + add + part;
		   }	
		   if(add.equals(" "))
			   normalCaseString = normalCaseString.substring(0,1).toUpperCase() + normalCaseString.substring(1);
		   return normalCaseString;
		}
	
	public static void replaceFromCamelOnAuthor(WSAuthorEditorPage authorPageAccess, String replaceWith) {
		AuthorDocumentController controller = authorPageAccess.getDocumentController();
	
		TextContentIterator textContentIterator = controller.getTextContentIterator(authorPageAccess.getSelectionStart(), authorPageAccess.getSelectionEnd());
		
		while (textContentIterator.hasNext()) {
			TextContext next = textContentIterator.next();
			CharSequence string = next.getText();
			String string2 = string.toString();
			next.replaceText(fromCamelCase(string2, replaceWith));
		}
			
	}
	
	public static void replaceFromCamelOnText(WSTextEditorPage textPage,String replaceWith) {
		
		String replaceAll = textPage.getSelectedText();
		textPage.deleteSelection();
		
		try {
			textPage.getDocument().insertString(textPage.getCaretOffset(), fromCamelCase(replaceAll, replaceWith), null);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}
}
