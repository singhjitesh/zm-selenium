/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2012, 2013, 2014, 2015, 2016 Synacor, Inc.
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software Foundation,
 * version 2 of the License.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see <https://www.gnu.org/licenses/>.
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.qa.selenium.projects.universal.tests.mail.folders.retention;

import org.testng.annotations.*;

import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.universal.core.PrefGroupMailByMessageTest;
import com.zimbra.qa.selenium.projects.universal.ui.mail.DialogEditFolder;

public class CreateRetention extends PrefGroupMailByMessageTest {

	public CreateRetention() {
		logger.info("New " + CreateRetention.class.getCanonicalName());
	}

	@Test( description = "Save a new basic retention on a folder (Context menu -> Edit -> Retention)", 
			groups = { "smoke", "L1" } )
	
	public void CreateRetention_01() throws HarnessException {

		//-- Data
		
		// Create the subfolder
		String foldername = "folder" + ConfigProperties.getUniqueString();

		app.zGetActiveAccount().soapSend(
					"<CreateFolderRequest xmlns='urn:zimbraMail'>"
				+		"<folder name='" + foldername + "' l='" +  FolderItem.importFromSOAP(app.zGetActiveAccount(), FolderItem.SystemFolder.Inbox).getId() + "'/>"
				+	"</CreateFolderRequest>");

		FolderItem folder = FolderItem.importFromSOAP(app.zGetActiveAccount(), foldername);
		ZAssert.assertNotNull(folder, "Verify the subfolder is available");

		
		//-- GUI
		
		// Click on Get Mail to refresh the folder list
		app.zPageMail.zToolbarPressButton(Button.B_REFRESH);

		// Rename the folder using context menu
		DialogEditFolder dialog = (DialogEditFolder) app.zTreeMail.zTreeItem(Action.A_RIGHTCLICK, Button.B_TREE_EDIT, folder);

		// Set to 2 years
		dialog.zRetentionEnable();
		dialog.zRetentionSetRange(
				DialogEditFolder.RetentionRangeType.Custom, 
				DialogEditFolder.RetentionRangeUnits.Years, 
				2);

		// Save
		dialog.zClickButton(Button.B_OK);
		
		
		//-- Verification
		
		// Verify the retention policy on the folder
		app.zGetActiveAccount().soapSend(
				"<GetFolderRequest xmlns='urn:zimbraMail'>"
			+		"<folder l='" + folder.getId() + "'/>"
			+	"</GetFolderRequest>");
		String lifetime = app.zGetActiveAccount().soapSelectValue("//mail:keep//mail:policy", "lifetime");
		String type = app.zGetActiveAccount().soapSelectValue("//mail:keep//mail:policy", "type");
		
		ZAssert.assertEquals(lifetime, "732d", "Verify the policy lifetime is set to 2 years");
		ZAssert.assertEquals(type, "user", "Verify the policy type is set to 'user'");
		
	}

	
	@DataProvider(name = "DataProviderRetentions")
	public Object[][] DataProviderRetentions() {
	  return new Object[][] {
			    new Object[] { DialogEditFolder.RetentionRangeUnits.Days, "2d" },
			    new Object[] { DialogEditFolder.RetentionRangeUnits.Weeks, "14d" },
			    new Object[] { DialogEditFolder.RetentionRangeUnits.Months, "62d" },
			    new Object[] { DialogEditFolder.RetentionRangeUnits.Years, "732d" },
	  };
	}
	
	@Test( description = "Create day, week, month, year retentions", 
			groups = { "functional", "L2" }, dataProvider = "DataProviderRetentions" )
	
	public void CreateRetention_02(DialogEditFolder.RetentionRangeUnits units, String expected) throws HarnessException {

		//-- Data
		
		// Create the subfolder
		String foldername = "folder" + ConfigProperties.getUniqueString();

		app.zGetActiveAccount().soapSend(
					"<CreateFolderRequest xmlns='urn:zimbraMail'>"
				+		"<folder name='" + foldername + "' l='" +  FolderItem.importFromSOAP(app.zGetActiveAccount(), FolderItem.SystemFolder.Inbox).getId() + "'/>"
				+	"</CreateFolderRequest>");

		FolderItem folder = FolderItem.importFromSOAP(app.zGetActiveAccount(), foldername);
		ZAssert.assertNotNull(folder, "Verify the subfolder is available");

		
		//-- GUI
		
		// Click on Get Mail to refresh the folder list
		app.zPageMail.zToolbarPressButton(Button.B_REFRESH);

		// Rename the folder using context menu
		DialogEditFolder dialog = (DialogEditFolder) app.zTreeMail.zTreeItem(Action.A_RIGHTCLICK, Button.B_TREE_EDIT, folder);

		// Set to 2 years
		dialog.zRetentionEnable();
		dialog.zRetentionSetRange(
				DialogEditFolder.RetentionRangeType.Custom, 
				units, 
				2);

		// Save
		dialog.zClickButton(Button.B_OK);
		
		
		//-- Verification
		
		// Verify the retention policy on the folder
		app.zGetActiveAccount().soapSend(
				"<GetFolderRequest xmlns='urn:zimbraMail'>"
			+		"<folder l='" + folder.getId() + "'/>"
			+	"</GetFolderRequest>");
		String lifetime = app.zGetActiveAccount().soapSelectValue("//mail:keep//mail:policy", "lifetime");
		
		ZAssert.assertEquals(lifetime, expected, "Verify the policy lifetime is set correctly");
		
	}

}
