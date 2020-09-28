package com.oxygenxml.sdksamples.workspace;

import java.util.regex.Pattern;

import javax.swing.text.BadLocationException;

import ro.sync.ecss.extensions.api.AuthorDocumentController;
import ro.sync.ecss.extensions.api.content.TextContentIterator;
import ro.sync.ecss.extensions.api.content.TextContext;
import ro.sync.exml.workspace.api.editor.page.author.WSAuthorEditorPage;
import ro.sync.exml.workspace.api.editor.page.text.WSTextEditorPage;

/**
 * This class is used to replace from a text selected, the underscore or space to camel/pascal case.
 * 
 * @author Bogdan Draghici
 */
public class ReplaceCamelUtil extends ReplaceContentUtil{
	
	//set the first character of the word to upper case
	static String toProperCase(String s) {
	    return s.substring(0, 1).toUpperCase() + s.substring(1);
	}

	static String toCamelCase(CharSequence c, boolean isPascal){
		//splits the string when finding underscore or space
		String[] parts = Pattern.compile("[\\_ ]").split(c, 0);
		StringBuilder camelCaseString = new StringBuilder();
		for (String part : parts){
			//adds to the string the new format for each word
			camelCaseString.append(toProperCase(part));
		}	
		//when creating the camel case, set the first letter to lower case
		if(!isPascal) {
			camelCaseString.replace(0, 1, camelCaseString.subSequence(0, 1).toString().toLowerCase());
		}
		return camelCaseString.toString();
	}
	
	
	
	
	public static void replaceCamelOnAuthor(WSAuthorEditorPage authorPageAccess, boolean isPascal) {
		AuthorDocumentController controller = authorPageAccess.getDocumentController();
	
		TextContentIterator textContentIterator = controller.getTextContentIterator(authorPageAccess.getSelectionStart(), authorPageAccess.getSelectionEnd());
		
		//take each content from an element and modify the string inside 
		while (textContentIterator.hasNext()) {
			TextContext next = textContentIterator.next();
			CharSequence string = next.getText();
			//changes in current content the string format to pascal/camel case
			next.replaceText(toCamelCase(string, isPascal));
		}
	}
	
	public static void replaceCamelOnText(WSTextEditorPage textPage,boolean isPascal) {
		
		String replaceAll = textPage.getSelectedText();
		textPage.deleteSelection();
		//try to replace from underscore or space to camel/pascal case 
		try {
			
			textPage.getDocument().insertString(textPage.getCaretOffset(), toCamelCase(replaceAll, isPascal), null);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}
}
