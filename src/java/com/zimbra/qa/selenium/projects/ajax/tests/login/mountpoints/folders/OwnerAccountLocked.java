/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011, 2012, 2013 Zimbra Software, LLC.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.4 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.qa.selenium.projects.ajax.tests.login.mountpoints.folders;

import org.testng.annotations.*;

import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;



public class OwnerAccountLocked extends AjaxCommonTest {
	
	protected ZimbraAccount Owner = null;
	
	public OwnerAccountLocked() {
		logger.info("New "+ OwnerAccountLocked.class.getCanonicalName());
		
		// Test starts in the mail app
		super.startingPage = app.zPageMail;
		super.startingAccountPreferences = null;
		
	}
	
	@BeforeMethod(	description = "Make sure the Owner account exists",
					groups = { "always" } )
	public void CreateOwner() throws HarnessException {
		Owner = new ZimbraAccount();
		Owner.provision();
		Owner.authenticate();
	}
	
	
	@Test(	description = "Login to the Ajax Client - with a mountpoint to a 'locked' account",
			groups = { "functional" })
	public void OwnerAccountLocked01() throws HarnessException {
		
		// Data setup
		
		String subject = "subject" + ZimbraSeleniumProperties.getUniqueString();
		String foldername = "folder" + ZimbraSeleniumProperties.getUniqueString();
		String mountpointname = "mountpoint" + ZimbraSeleniumProperties.getUniqueString();
		
		FolderItem inbox = FolderItem.importFromSOAP(Owner, FolderItem.SystemFolder.Inbox);
		
		// Create a folder to share
		Owner.soapSend(
					"<CreateFolderRequest xmlns='urn:zimbraMail'>"
				+		"<folder name='" + foldername + "' l='" + inbox.getId() + "'/>"
				+	"</CreateFolderRequest>");
		
		FolderItem folder = FolderItem.importFromSOAP(Owner, foldername);
		
		Owner.soapSend(
				"<AddMsgRequest xmlns='urn:zimbraMail'>"
    		+		"<m l='"+ folder.getId() +"' f='u'>"
        	+			"<content>From: foo@foo.com\n"
        	+				"To: foo@foo.com \n"
        	+				"Subject: "+ subject +"\n"
        	+				"MIME-Version: 1.0 \n"
        	+				"Content-Type: text/plain; charset=utf-8 \n"
        	+				"Content-Transfer-Encoding: 7bit\n"
        	+				"\n"
        	+				"simple text string in the body\n"
        	+			"</content>"
        	+		"</m>"
			+	"</AddMsgRequest>");

		// Share it
		Owner.soapSend(
					"<FolderActionRequest xmlns='urn:zimbraMail'>"
				+		"<action id='"+ folder.getId() +"' op='grant'>"
				+			"<grant d='"+ app.zGetActiveAccount().EmailAddress +"' gt='usr' perm='rwidxa'/>"
				+		"</action>"
				+	"</FolderActionRequest>");
		
		
		// Mount it
		app.zGetActiveAccount().soapSend(
					"<CreateMountpointRequest xmlns='urn:zimbraMail'>"
				+		"<link l='1' name='"+ mountpointname +"'  rid='"+ folder.getId() +"' zid='"+ Owner.ZimbraId +"'/>"
				+	"</CreateMountpointRequest>");
		
		FolderMountpointItem mountpoint = FolderMountpointItem.importFromSOAP(app.zGetActiveAccount(), mountpointname);
		ZAssert.assertNotNull(mountpoint, "Verify the mountpoint was created");
		
		
		
		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		// View the folder
		app.zTreeMail.zTreeItem(Action.A_LEFTCLICK, mountpoint);
		
		// Logout
		ZimbraAccount account = app.zGetActiveAccount();
		app.zPageMain.zLogout();
		
		// Make the owner account in maintenance
		ZimbraAdminAccount.GlobalAdmin().soapSend(
					"<ModifyAccountRequest xmlns='urn:zimbraAdmin'>"
				+		"<id>"+ Owner.ZimbraId + "</id>"
				+		"<a n='zimbraAccountStatus'>locked</a>"
				+	"</ModifyAccountRequest>");
		
		
		// Login
		app.zPageLogin.zLogin(account);
		
		
		// View the folder
		app.zTreeMail.zTreeItem(Action.A_LEFTCLICK, mountpoint);
		

	}


}
