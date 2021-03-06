/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2015, 2016 Synacor, Inc.
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
package com.zimbra.qa.selenium.projects.universal.tests.contacts.contactgroups;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.core.Bugs;
import com.zimbra.qa.selenium.framework.items.MailItem;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.universal.core.UniversalCommonTest;
import com.zimbra.qa.selenium.projects.universal.ui.mail.*;
import com.zimbra.qa.selenium.projects.universal.ui.mail.FormMailNew.Field;

public class SendMailToContactGroup extends UniversalCommonTest {
	public SendMailToContactGroup() {
		logger.info("New " + SendMailToContactGroup.class.getCanonicalName());

		// All tests start at the Address page
		super.startingPage = app.zPageContacts;

	}

	@Bugs(ids = "97157")
	@Test(description = "Right click then click New Email", 
	groups = { "smoke", "L0" })
	
	public void NewEmail_01() throws HarnessException {

		// -- Data

		// The message subject
		String subject = "subject" + ConfigProperties.getUniqueString();

		// Create a contact group
		String groupName = "group" + ConfigProperties.getUniqueString();
		app.zGetActiveAccount()
				.soapSend("<CreateContactRequest xmlns='urn:zimbraMail'>" + "<cn >" + "<a n='type'>group</a>"
						+ "<a n='nickname'>" + groupName + "</a>" + "<a n='fileAs'>8:" + groupName + "</a>"
						+ "<m type='I' value='" + ZimbraAccount.AccountA().EmailAddress + "' />" + "<m type='I' value='"
						+ ZimbraAccount.AccountB().EmailAddress + "' />" + "</cn>" + "</CreateContactRequest>");

		// -- GUI

		// Refresh
		app.zPageContacts.zToolbarPressButton(Button.B_REFRESH);

		// Right Click -> New Email
		FormMailNew formMailNew = (FormMailNew) app.zPageContacts.zListItem(Action.A_RIGHTCLICK, Button.B_NEW,
				groupName);

		formMailNew.zFillField(Field.Subject, subject);
		formMailNew.zFillField(Field.Body, "body" + ConfigProperties.getUniqueString());
		formMailNew.zSubmit();

		// -- Verification

		MailItem message1 = MailItem.importFromSOAP(ZimbraAccount.AccountA(), "subject:(" + subject + ")");
		ZAssert.assertNotNull(message1, "Verify the message is received by Account Bugs:97157");

		MailItem message2 = MailItem.importFromSOAP(ZimbraAccount.AccountA(), "subject:(" + subject + ")");
		ZAssert.assertNotNull(message2, "Verify the message is received by Account B");

	}

}
