package com.oxygenxml.sdksamples.workspace;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;

import javax.swing.text.BadLocationException;
import javax.xml.transform.stream.StreamSource;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.w3c.css.sac.InputSource;
import org.xml.sax.SAXException;

import junit.framework.TestCase;
import ro.sync.ecss.component.CSSInputSource;
import ro.sync.ecss.css.csstopdf.facade.AuthorDocumentFacade;
import ro.sync.ecss.css.csstopdf.facade.AuthorDocumentFacadeFactory;
import ro.sync.ecss.extensions.api.AuthorDocumentController;
import ro.sync.ecss.extensions.api.node.AuthorDocument;
import ro.sync.ecss.extensions.api.node.AuthorDocumentFragment;
import ro.sync.exml.workspace.api.editor.page.author.WSAuthorEditorPage;

public class SampleTest extends TestCase{
	public static CustomWorkspaceAccessPluginExtension test;
	
	@BeforeClass
	public static void openWorkspace() {
		test = new CustomWorkspaceAccessPluginExtension();
	}
	
	@Test
	public void test() throws Exception {
		//facade authorDocument
		String inputXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" + 
				"<smartphones>\r\n" + 
				"    <smartphone>\r\n" + 
				"        <name>Samsung Galaxy S10 Plus</name>\r\n" + 
				"        <screenSize measuringUnit = \"inch\">6.4</screenSize>\r\n" + 
				"        <memory>\r\n" + 
				"            <storage measuringUnit = \"gb\">128</storage>\r\n" + 
				"            <RAM measuringUnit = \"gb\">8</RAM>\r\n" + 
				"        </memory>\r\n" + 
				"        <processor>Octa Core</processor>\r\n" + 
				"        <camera measuringUnit =\"MP\">16</camera>\r\n" + 
				"    </smartphone>\r\n" +
				"</smartphones>";
		
		String outputXML = "<smartphones><smartphone><name>Samsung_Galaxy_S10_Plus</name><screenSize measuringUnit=\"inch\">6.4</screenSize><memory><storage measuringUnit=\"gb\">128</storage><RAM measuringUnit=\"gb\">8</RAM></memory><processor>Octa Core</processor><camera measuringUnit=\"MP\">16</camera></smartphone></smartphones>";
		
		AuthorDocumentFacadeFactory facadeFactory = new AuthorDocumentFacadeFactory();
		InputSource inputSource = new InputSource(new StringReader("* {display: block;}"));
		CSSInputSource[] cssInputSources =  new CSSInputSource[] { new CSSInputSource(inputSource, (byte)2) };

		StringReader reader = new StringReader(inputXML);
		AuthorDocumentFacade facade = facadeFactory.createFacade(new StreamSource(reader), cssInputSources, null,
				new File("."));
		AuthorDocumentController controller = facade.getController();
		
		
		WSAuthorEditorPage wsAuthorEditorPage = Mockito.mock(WSAuthorEditorPage.class);
		Mockito.when(wsAuthorEditorPage.hasSelection()).thenReturn(true);
		//when-return selection
		
		Mockito.when(wsAuthorEditorPage.getSelectionStart()).thenReturn(7);
		Mockito.when(wsAuthorEditorPage.getSelectionEnd()).thenReturn(25);
		
		//when-return getController
		Mockito.when(wsAuthorEditorPage.getDocumentController()).thenReturn(controller);
	//	Mockito.when(controller.getTextContentIterator(s,e)).thenReturn()
		
		ReplaceContentUtil.replaceOnAuthor(wsAuthorEditorPage);
		
		
		AuthorDocument documentNode = controller.getAuthorDocumentNode();
		AuthorDocumentFragment fragment = controller.createDocumentFragment(documentNode, true);
		String toXML = controller.serializeFragmentToXML(fragment);
				
		//CustomWorkspaceAccessPluginExtension result;
		assertEquals("Wrong output", outputXML, toXML);
	}
	
	@AfterClass
	public static void closeWorkspace() {
		test.applicationClosing();
	}
}
