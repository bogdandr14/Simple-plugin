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
	
  static String fromCamelCase(CharSequence c, String add) {
    // splits the string when it finds an upper case letter
    String[] parts = Pattern.compile("(?=[A-Z])").split(c, 0);
    StringBuilder normalCaseString = new StringBuilder();
    for (String part : parts) {
      // when replacing with underscore, also make every letter to lower case
      if (add.equals("_")) {
        normalCaseString.append(add + part.toLowerCase());
      } else {
        normalCaseString.append(add + part);
      }
    }
    //remove the underscore or space added at the beginning of the string
      normalCaseString.deleteCharAt(0);
    // when replacing with spaces, make the first letter upper case
    if (add.equals(" ")) {
      normalCaseString.replace(0, 1, normalCaseString.subSequence(0, 1).toString().toUpperCase());
    }
    return normalCaseString.toString();
  }
	
	public static void replaceFromCamelOnAuthor(WSAuthorEditorPage authorPageAccess, String replaceWith) {
		AuthorDocumentController controller = authorPageAccess.getDocumentController();
	
		TextContentIterator textContentIterator = controller.getTextContentIterator(authorPageAccess.getSelectionStart(), authorPageAccess.getSelectionEnd());
		
		//take each content from an element and modify the string inside 
		while (textContentIterator.hasNext()) {
			TextContext next = textContentIterator.next();
			CharSequence string = next.getText();
			//replaces in current content the camel/pascal case with replaceWith
			next.replaceText(fromCamelCase(string, replaceWith));
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
