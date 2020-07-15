package com.oxygenxml.sdksamples.workspace;

import java.util.regex.Pattern;

import javax.swing.text.BadLocationException;

import ro.sync.ecss.extensions.api.AuthorDocumentController;
import ro.sync.ecss.extensions.api.content.TextContentIterator;
import ro.sync.ecss.extensions.api.content.TextContext;
import ro.sync.exml.workspace.api.editor.page.author.WSAuthorEditorPage;
import ro.sync.exml.workspace.api.editor.page.text.WSTextEditorPage;
//replace camel/pascal case with underscore or space
public class ReplaceFromCamelUtil {
	
	static String fromCamelCase(CharSequence c, String add){
		//splits the string when it finds an upper case letter
		String[] parts = Pattern.compile("(?=[A-Z])").split(c, 0);
		String normalCaseString = "";
    for (String part : parts) {
      // if the string is empty, do not add underscore or space at the beginning of the string
      if (normalCaseString.equals("")) {
        normalCaseString = part;
      } else {
        // when replacing with underscore, make every letter to lower case
        // when replacing with space, just add the string
        if (add.equals("_")) {
          normalCaseString = normalCaseString + add + part.toLowerCase();
        } else {
          normalCaseString = normalCaseString + add + part;
        }
      }  
    }
    // when replacing with spaces, make the first letter upper case
    if (add.equals(" ")) {
      normalCaseString = normalCaseString.substring(0, 1).toUpperCase() + normalCaseString.substring(1);
    }
		return normalCaseString;
	}
	
	public static void replaceFromCamelOnAuthor(WSAuthorEditorPage authorPageAccess, String replaceWith) {
		AuthorDocumentController controller = authorPageAccess.getDocumentController();
	
		TextContentIterator textContentIterator = controller.getTextContentIterator(authorPageAccess.getSelectionStart(), authorPageAccess.getSelectionEnd());
		
		//take each content from an element and modify the string inside 
		while (textContentIterator.hasNext()) {
			TextContext next = textContentIterator.next();
			CharSequence string = next.getText();
			String string2 = string.toString();
			//replaces in string2 with replaceWith
			next.replaceText(fromCamelCase(string2, replaceWith));
		}
			
	}
	
	public static void replaceFromCamelOnText(WSTextEditorPage textPage,String replaceWith) {
		
		String replaceAll = textPage.getSelectedText();
		textPage.deleteSelection();
		//try to replace from camel/pascal case to underscore or space
		try {
			textPage.getDocument().insertString(textPage.getCaretOffset(), fromCamelCase(replaceAll, replaceWith), null);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}
}
