/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011, 2012, 2013, 2014, 2015, 2016 Synacor, Inc.
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
package com.zimbra.qa.selenium.projects.ajax.tests.preferences.zimlets;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.core.Bugs;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ConfigProperties;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.preferences.TreePreferences.TreeItem;


public class GetZimlets extends AjaxCommonTest {

	public GetZimlets() {
		
		super.startingPage = app.zPagePreferences;
		
	}


	@Test(
			description = "View the number of zimlets in the default list",
			groups = { "deprecated", "L4" }
			)
	public void GetZimlets_01() throws HarnessException {

		
		// Navigate to preferences -> notifications
		app.zTreePreferences.zTreeItem(Action.A_LEFTCLICK, TreeItem.Zimlets);

		// The locator to the table
		String tablelocator = "css=div[id='ZmPrefZimletListView']";
		
		// The locator to the rows (not including the header)
		String locator = tablelocator + " div[id$='__rows']";

		// Get how many rows there are
		int count = app.zPagePreferences.sGetCssCount(locator + ">div[id^='zli__']");
		
		// IronMaiden: 8 zimlets in NETWORK
		//	Cisco Click2Call
		//	Mitel Click2Call
		//	Phone
		//	SearchHighlighter
		//	Secure Email
		//	Voice Preferences
		//	WebEx
		//	Yahoo! Emoticons
		// IronMaiden: 4 zimlets in FOSS
		//	Phone
		//	SearchHighlighter
		//	WebEx
		//	Yahoo! Emoticons
		//
		// JudasPriest: 9 zimlets in NETWORK
		//	Archive
		//	Cisco Click2Call
		//  LinkedIn Image - http://bugzilla.zimbra.com/show_bug.cgi?id=81078
		//	Mitel Click2Call
		//	Phone
		//	SearchHighlighter
		//	Secure Email
		//	Voice Preferences
		//	WebEx
		//	Yahoo! Emoticons
		//
		// JudasPriest: 5 zimlets in FOSS
		//	Archive
		//  LinkedIn Image - http://bugzilla.zimbra.com/show_bug.cgi?id=81078
		//	Phone
		//	SearchHighlighter
		//	WebEx
		//	Yahoo! Emoticons
		//
		if (ConfigProperties.zimbraGetVersionString().contains("FOSS")) {
		    ZAssert.assertEquals(count, 6, "Verify 6 zimlets are shown in the preferences page");
		} else {
		    ZAssert.assertEquals(count, 10, "Verify 10 zimlets are shown in the preferences page");
		}
	}
	
	// IronMaiden: Bug 50123: 3 zimlets - Phone, Search Highlighter, Webex
	@Bugs(ids = "50123")
	@Test(
			description = "Verify the LinkedIn table text",
			groups = { "deprecated", "L4" }
			)
	public void GetZimlets_02() throws HarnessException {

		// Navigate to preferences -> notifications
		app.zTreePreferences.zTreeItem(Action.A_LEFTCLICK, TreeItem.Zimlets);
		
		// The locator to the rows (not including the header)
		String locator = "css=div[id='ZmPrefZimletListView'] div[id$='__rows']";

		String name = app.zPagePreferences.sGetText(locator + " td[id$='__com_zimbra_linkedin__na']");
		String description = app.zPagePreferences.sGetText(locator + " td[id$='__com_zimbra_linkedin__ds']");

		ZAssert.assertEquals(name, "LinkedIn", "Verify the LinkedIn entry name");
		ZAssert.assertEquals(description, "Hooks on to email Zimlet; shows LinkedIn search result for a given email.", "Verify the LinkedIn description");		
		
	}

	@Test(
			description = "Verify the Phone table text",
			groups = { "functional", "L3" }
			)
	public void GetZimlets_03() throws HarnessException {

		// Navigate to preferences -> notifications
		app.zTreePreferences.zTreeItem(Action.A_LEFTCLICK, TreeItem.Zimlets);

		// The locator to the table
		String locator = "css=div[id='ZmPrefZimletListView'] div[id$='__rows']";

		String name = app.zPagePreferences.sGetText(locator + " td[id$='__com_zimbra_phone__na']");
		String description = app.zPagePreferences.sGetText(locator + " td[id$='__com_zimbra_phone__ds']");
		
		ZAssert.assertEquals(name, "Phone", "Verify the Phone entry exists");
		ZAssert.assertEquals(description, "Highlights phone numbers to enable Skype calls.", "Verify the Phone description");
		
		
	}

	
	@Test(
			description = "Verify the Search Highlighter table text",
			groups = { "functional", "L3" }
			)
	public void GetZimlets_04() throws HarnessException {

		// Navigate to preferences -> notifications
		app.zTreePreferences.zTreeItem(Action.A_LEFTCLICK, TreeItem.Zimlets);

		// The locator to the table
		String locator = "css=div[id='ZmPrefZimletListView'] div[id$='__rows']";

		String name = app.zPagePreferences.sGetText(locator + " td[id$='__com_zimbra_srchhighlighter__na']");
		String description = app.zPagePreferences.sGetText(locator + " td[id$='__com_zimbra_srchhighlighter__ds']");
		
		ZAssert.assertEquals(name, "Search Highlighter", "Verify the Search Highlighter entry exists");
		ZAssert.assertEquals(description, "After a mail search, this Zimlet highlights Search terms with yellow color.", "Verify the Search Highlighter description");
		
		
	}


	@Test(
			description = "Verify the WebEx table text",
			groups = { "functional", "L2" }
			)
	public void GetZimlets_05() throws HarnessException {

		// Navigate to preferences -> notifications
		app.zTreePreferences.zTreeItem(Action.A_LEFTCLICK, TreeItem.Zimlets);

		// The locator to the table
		String locator = "css=div[id='ZmPrefZimletListView'] div[id$='__rows']";

		String name = app.zPagePreferences.sGetText(locator + " td[id$='__com_zimbra_webex__na']");
		String description = app.zPagePreferences.sGetText(locator + " td[id$='__com_zimbra_webex__ds']");
		
		ZAssert.assertEquals(name, "WebEx", "Verify the WebEx entry exists");
		ZAssert.assertEquals(description, "Easily schedule, start or join WebEx meetings", "Verify the WebEx description");
		
		
	}

	// IronMaiden: Bug 50123: 3 zimlets - Phone, Search Highlighter, Webex
	@Bugs(ids = "50123")
	@Test(
			description = "Verify the Zimbra Social table text",
			groups = { "deprecated", "L4" }
			)
	public void GetZimlets_06() throws HarnessException {

		// Navigate to preferences -> notifications
		app.zTreePreferences.zTreeItem(Action.A_LEFTCLICK, TreeItem.Zimlets);

		// The locator to the table
		String locator = "css=div[id='ZmPrefZimletListView'] div[id$='__rows']";

		String name = app.zPagePreferences.sGetText(locator + " td[id$='__com_zimbra_social__na']");
		String description = app.zPagePreferences.sGetText(locator + " td[id$='__com_zimbra_social__ds']");
		
		ZAssert.assertEquals(name, "Zimbra Social", "Verify the Zimbra Social entry exists");
		ZAssert.assertEquals(description, "Access social services like Twitter, Facebook, Digg and TweetMeme.", "Verify the Zimbra Social description");
		
		
	}

	@Test(
			description = "Verify the Y-Emoticons table text",
			groups = { "functional", "L2" }
			)
	public void GetZimlets_07() throws HarnessException {

		// Navigate to preferences -> notifications
		app.zTreePreferences.zTreeItem(Action.A_LEFTCLICK, TreeItem.Zimlets);

		// The locator to the table
		String locator = "css=div[id='ZmPrefZimletListView'] div[id$='__rows']";

		String name = app.zPagePreferences.sGetText(locator + " td[id$='__com_zimbra_ymemoticons__na']");
		String description = app.zPagePreferences.sGetText(locator + " td[id$='__com_zimbra_ymemoticons__ds']");
		
		ZAssert.assertEquals(name, "Yahoo! Emoticons", "Verify the Y Emoticons entry exists");
		ZAssert.assertEquals(description, "Displays Yahoo! Emoticons images in email messages.", "Verify the Y Emoticons description");
		
		
	}



}
