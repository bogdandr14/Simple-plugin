package com.oxygenxml.sdksamples.workspace;


import javax.swing.text.BadLocationException;

import ro.sync.ecss.extensions.api.AuthorDocumentController;
import ro.sync.ecss.extensions.api.content.TextContentIterator;
import ro.sync.ecss.extensions.api.content.TextContext;
import ro.sync.exml.workspace.api.editor.page.author.WSAuthorEditorPage;
import ro.sync.exml.workspace.api.editor.page.text.WSTextEditorPage;

public class ReplaceCamelUtil extends ReplaceContentUtil{
	
	static String toProperCase(String s) {
	    return s.substring(0, 1).toUpperCase() +
	               s.substring(1);
	}

	static String toCamelCase(String s, boolean isPascal){
	   String[] parts = s.split("_");
	   String camelCaseString = "";
	   for (String part : parts){
	      camelCaseString = camelCaseString + toProperCase(part);
	   }
	   if(!isPascal) {
		   camelCaseString = camelCaseString.substring(0,1).toLowerCase() + camelCaseString.substring(1);
	   }
	   return camelCaseString;
	}
	
	public static void replaceCamelOnAuthor(WSAuthorEditorPage authorPageAccess, boolean isPascal) {
		AuthorDocumentController controller = authorPageAccess.getDocumentController();
	
		TextContentIterator textContentIterator = controller.getTextContentIterator(authorPageAccess.getSelectionStart(), authorPageAccess.getSelectionEnd());
		
		while (textContentIterator.hasNext()) {
			TextContext next = textContentIterator.next();
			CharSequence string = next.getText();
			String string2 = string.toString();
			
			next.replaceText(toCamelCase(string2, isPascal));
		}
			
	}
	
	public static void replaceCamelOnText(WSTextEditorPage textPage,boolean isPascal) {
		
		String replaceAll = textPage.getSelectedText();
		textPage.deleteSelection();
		
		try {
			textPage.getDocument().insertString(textPage.getCaretOffset(), toCamelCase(replaceAll, isPascal), null);
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
