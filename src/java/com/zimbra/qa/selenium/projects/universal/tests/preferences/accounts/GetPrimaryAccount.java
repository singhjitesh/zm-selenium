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
package com.zimbra.qa.selenium.projects.universal.tests.preferences.accounts;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.projects.universal.core.UniversalCommonTest;
import com.zimbra.qa.selenium.projects.universal.ui.preferences.TreePreferences.TreeItem;

public class GetPrimaryAccount extends UniversalCommonTest {

	public GetPrimaryAccount() {
		super.startingPage = app.zPagePreferences;
	}

	@Test(description = "View the accounts preference page shows the default primary account", groups = {
			"functional", "L3" })
	public void GetPrimaryAccount_01() throws HarnessException {

		// Navigate to preferences -> notifications
		app.zTreePreferences.zTreeItem(Action.A_LEFTCLICK, TreeItem.MailAccounts);

		String tableLocator = "css=div[id='zli__ACCT__" + app.zGetActiveAccount().ZimbraId + "']";
		ZAssert.assertTrue(app.zPagePreferences.sIsElementPresent(tableLocator),
				"Verify the zimbra default account is in the preferences list");

		String accountName = app.zPagePreferences.sGetText(tableLocator + " td[id$='__na']");
		String accountStatus = app.zPagePreferences.sGetText(tableLocator + " td[id$='__st']");
		String accountEmail = app.zPagePreferences.sGetText(tableLocator + " td[id$='__em']");
		String accountType = app.zPagePreferences.sGetText(tableLocator + " td[id$='__ty']");

		ZAssert.assertEquals(accountName, "Primary Account", "Verify the Primary account name");
		ZAssert.assertEquals(accountStatus, "OK", "Verify the Primary account status");
		ZAssert.assertEquals(accountEmail, app.zGetActiveAccount().EmailAddress, "Verify the Primary account email");
		ZAssert.assertEquals(accountType, "Primary", "Verify the Primary account type");

	}
}
