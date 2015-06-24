package com.zimbra.qa.selenium.projects.touch.tests.mail.mail.conversation.messageaction;

	import java.awt.AWTException;

import org.testng.annotations.*;

import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.touch.core.PrefGroupMailByConversationTest;

	public class MoveMessage extends PrefGroupMailByConversationTest{

		public MoveMessage() {
			logger.info("New "+ MoveMessage.class.getCanonicalName());
		}
			@Test(	description = "Move a mail by 'move conversation' button",
					groups = { "sanity" })
			
			
			public void MoveMail_01() throws HarnessException, AWTException {

				String subject = "subject"+ ZimbraSeleniumProperties.getUniqueString();
				FolderItem userRoot = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.UserRoot);
				String folderName = "folder" + ZimbraSeleniumProperties.getUniqueString();
				app.zGetActiveAccount().soapSend(
						"<CreateFolderRequest xmlns='urn:zimbraMail'>" +
		                	"<folder name='"+ folderName +"' l='"+ userRoot.getId() +"'/>" +
		                "</CreateFolderRequest>");
				FolderItem pagemail = FolderItem.importFromSOAP(app.zGetActiveAccount(), folderName);

				// Send a message to the account
				ZimbraAccount.AccountA().soapSend(
							"<SendMsgRequest xmlns='urn:zimbraMail'>" +
								"<m>" +
									"<e t='t' a='"+ app.zGetActiveAccount().EmailAddress +"'/>" +
									"<su>"+ subject +"</su>" +
									"<mp ct='text/plain'>" +
										"<content>content"+ ZimbraSeleniumProperties.getUniqueString() +"</content>" +
									"</mp>" +
								"</m>" +
							"</SendMsgRequest>");

				// Get the mail item for the new message
				MailItem mail = MailItem.importFromSOAP(app.zGetActiveAccount(), "subject:("+ subject +")");
				

		app.zPageMail.zRefresh();	

		// Move message
		app.zPageMail.zConversationListItem(Button.B_CONVERSATION_ACTION_DROPDOWN, subject);
		app.zPageMail.zListItem(Button.B_MOVE_CONVERSATION);
		app.zTreeMail.zSelectFolder(folderName);
		
		// Verification
		app.zGetActiveAccount().soapSend(
				"<GetMsgRequest xmlns='urn:zimbraMail'>" +
					"<m id='" + mail.getId() +"'/>" +
				"</GetMsgRequest>");
		String folderId = app.zGetActiveAccount().soapSelectValue("//mail:m", "l");
		
		ZAssert.assertEquals(folderId, pagemail.getId(), "Verify the subfolder ID that the message was moved into");
		}	
	}