/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011 VMware, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * 
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.qa.selenium.projects.desktop.tests.mail.folders;

import java.net.MalformedURLException;
import java.net.URL;

import org.testng.annotations.*;

import com.zimbra.qa.selenium.framework.items.DesktopAccountItem;
import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount.SOAP_DESTINATION_HOST_TYPE;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties.AppType;
import com.zimbra.qa.selenium.projects.desktop.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.desktop.ui.mail.DialogCreateFolder;

public class CreateFolder extends AjaxCommonTest {

	private boolean _folderIsCreated = false;
	private String _folderName = null;
	private SOAP_DESTINATION_HOST_TYPE _soapDestination = null;

	public CreateFolder() {
		logger.info("New " + CreateFolder.class.getCanonicalName());

		// All tests start at the login page
		super.startingPage = app.zPageMail;
		super.startingAccountPreferences = null;
	}

	@BeforeMethod(alwaysRun = true)
	public void setParameters() {
		_soapDestination = ZimbraSeleniumProperties.getAppType() == AppType.DESKTOP ? SOAP_DESTINATION_HOST_TYPE.CLIENT
				: SOAP_DESTINATION_HOST_TYPE.SERVER;
	}

	@Test(description = "Create a new folder using keyboard shortcuts", groups = { "functional" })
	public void CreateFolder_02() throws HarnessException {

		Shortcut shortcut = Shortcut.S_NEWFOLDER;

		// Set the new folder name
		_folderName = "folder" + ZimbraSeleniumProperties.getUniqueString();

		DialogCreateFolder dialog = (DialogCreateFolder) app.zPageMail
				.zKeyboardShortcut(shortcut);
		ZAssert.assertNotNull(dialog, "Verify the new dialog opened");

		// Fill out the form with the basic details
		// TODO: does a folder in the tree need to be selected?
		dialog.zEnterFolderName(_folderName);
		dialog.zClickButton(Button.B_OK);

	   // Force-sync
      GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());
      
      // Make sure the folder was created on the Desktop Server
      FolderItem desktopFolder = FolderItem.importFromSOAP(app
            .zGetActiveAccount(), _folderName,
            SOAP_DESTINATION_HOST_TYPE.CLIENT,
            app.zGetActiveAccount().EmailAddress);

      ZAssert.assertNotNull(desktopFolder, "Verify the new form opened");
      ZAssert.assertEquals(desktopFolder.getName(), _folderName,
      "Verify the server and client folder names match");

      // Make sure the folder was created on the ZCS server
		FolderItem folder = FolderItem.importFromSOAP(app.zGetActiveAccount(),
		      _folderName);
		ZAssert.assertNotNull(folder, "Verify the new folder was created");

		ZAssert.assertEquals(folder.getName(), _folderName,
				"Verify the server and client folder names match");
	}

	@Test(description = "Create a new folder using context menu from root folder", groups = { "functional" })
	public void CreateFolder_03() throws HarnessException {
		_folderName = "folder" + ZimbraSeleniumProperties.getUniqueString();

		FolderItem folderItem = FolderItem.importFromSOAP(app
				.zGetActiveAccount(), FolderItem.SystemFolder.UserRoot,
				_soapDestination, app.zGetActiveAccount().EmailAddress);
		DialogCreateFolder createFolderDialog = (DialogCreateFolder) app.zPageMail
				.zListItem(Action.A_RIGHTCLICK, Button.B_TREE_NEWFOLDER,
						folderItem);

		createFolderDialog.zEnterFolderName(_folderName);
		createFolderDialog.zClickButton(Button.B_OK);

		_folderIsCreated = true;

		// Force-sync
		GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());
		app.zPageMain.zWaitForDesktopLoadingSpinner(5000);
		
		// Make sure the folder was created on the Desktop Server
		FolderItem desktopFolder = FolderItem.importFromSOAP(app
		      .zGetActiveAccount(), _folderName,
		      SOAP_DESTINATION_HOST_TYPE.CLIENT,
		      app.zGetActiveAccount().EmailAddress);
		
		ZAssert.assertNotNull(desktopFolder, "Verify the new form opened");
		ZAssert.assertEquals(desktopFolder.getName(), _folderName,
		"Verify the server and client folder names match");

		// Make sure the folder was created on the ZCS server
		FolderItem folder = FolderItem.importFromSOAP(app.zGetActiveAccount(),
				_folderName);
		ZAssert.assertNotNull(folder, "Verify the new form opened");
		ZAssert.assertEquals(folder.getName(), _folderName,
				"Verify the server and client folder names match");
	}

	@Test(description = "Create a new folder using mail app New -> New Folder", groups = { "sanity" })
	public void CreateFolder_04() throws HarnessException {

		// Set the new folder name
	   _folderName = "folder" + ZimbraSeleniumProperties.getUniqueString();

		// Create a new folder in the inbox
		// using the context menu + New Folder
		DialogCreateFolder dialog = (DialogCreateFolder) app.zPageMail.zToolbarPressPulldown(Button.B_NEW, Button.O_NEW_FOLDER);
		ZAssert.assertNotNull(dialog, "Verify the new dialog opened");

		// Fill out the form with the basic details
		// TODO: does a folder in the tree need to be selected?
		dialog.zEnterFolderName(_folderName);
		dialog.zClickButton(Button.B_OK);

		_folderIsCreated = true;

      // Force-sync
      GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());
      
      // Make sure the folder was created on the Desktop Server
      FolderItem desktopFolder = FolderItem.importFromSOAP(app
            .zGetActiveAccount(), _folderName,
            SOAP_DESTINATION_HOST_TYPE.CLIENT,
            app.zGetActiveAccount().EmailAddress);
      
      ZAssert.assertNotNull(desktopFolder, "Verify the new form opened");
      ZAssert.assertEquals(desktopFolder.getName(), _folderName,
      "Verify the server and client folder names match");

      // Make sure the folder was created on the server
		FolderItem folder = FolderItem.importFromSOAP(app.zGetActiveAccount(),_folderName);
		ZAssert.assertNotNull(folder, "Verify the new folder was created");

		ZAssert.assertEquals(folder.getName(), _folderName,
				"Verify the server and client folder names match");

	}


	@Test(description = "Create a new folder for RSS/ATOM feeds", groups = { "functional" })
	public void CreateRSSFolder_05() throws HarnessException {

	   Shortcut shortcut = Shortcut.S_NEWFOLDER;
	   String rssurl="http://feeds.feedburner.com/zimbra";
	   // Set the new folder name
	   _folderName = "folderRSS" + ZimbraSeleniumProperties.getUniqueString();

	   DialogCreateFolder dialog = (DialogCreateFolder) app.zPageMail
	         .zKeyboardShortcut(shortcut);
	   ZAssert.assertNotNull(dialog, "Verify the new dialog opened");

	   // Fill out the form with the basic details
	   // TODO: does a folder in the tree need to be selected?
	   dialog.zEnterFolderName(_folderName);
	   dialog.zClickSubscribeFeed(true);
	   try{ 
	      dialog.zEnterFeedURL(new URL(rssurl));
	   } catch(MalformedURLException e) {
	      logger.info("Incorrect URL specified" +e);
	   }

	   dialog.zClickButton(Button.B_OK);

	   // Force-sync
	   GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());
	   app.zPageMail.zWaitForDesktopLoadingSpinner(5000);

	   // Make sure the folder was created on the Desktop Server
	   FolderItem desktopFolder = FolderItem.importFromSOAP(app.zGetActiveAccount(),
	         _folderName,
	         SOAP_DESTINATION_HOST_TYPE.CLIENT,
	         app.zGetActiveAccount().EmailAddress);

	   ZAssert.assertNotNull(desktopFolder, "Verify the new RSS folder got created");

	   // Make sure the folder was created on the ZCS server
	   FolderItem folder = FolderItem.importFromSOAP(app.zGetActiveAccount(), _folderName);
	   ZAssert.assertNotNull(folder, "Verify the new RSS folder was created");
	   ZAssert.assertEquals(folder.getName(), _folderName,"Verify the server and client RSS folder names match");
	   app.zGetActiveAccount().soapSend(
	         "<GetFolderRequest xmlns='urn:zimbraMail'>"
	         +		"<folder id='" + folder.getId() + "'/>"
				+	"</GetFolderRequest>");

	   String url = app.zGetActiveAccount().soapSelectValue("//mail:folder[@name='" + folder.getName() + "']", "url");

	   // Only RSS folder has url attribute , so if its equal , it asserts the RSS folder creation
	   ZAssert.assertEquals(url,rssurl, "Verify the url of the rss folder correct");
	}

	private void _nonZimbraAccountSetup(ZimbraAccount account) throws HarnessException {
	   account.authenticateToMailClientHost();
	   app.zPageLogin.zLogin(account);
      super.startingPage.zNavigateTo();

      app.zTreeMail.zExpandAll();
      app.zPageMain.zWaitForDesktopLoadingSpinner(5000);
	}

	@Test(description = "Create Inbox's subfolder for IMAP Zimbra Account through ZD", groups = { "functional" })
	public void CreateInboxSubfolderImapZimbraAccountThroughZD()
	throws HarnessException {
	   app.zPageLogin.zNavigateTo();
	   app.zPageLogin.zRemoveAccount();
	   ZimbraAccount zcsAccount = ZimbraAccount.AccountZWC();

	   DesktopAccountItem accountItem = app.zPageAddNewAccount.zAddZimbraImapAccountThruUI(
	         ZimbraAccount.AccountZWC().EmailAddress,
            ZimbraAccount.AccountZWC().Password,
            ZimbraSeleniumProperties.getStringProperty("server.host", "localhost"),
            true,
            "465");

	   ZimbraAccount account = new ZimbraAccount(accountItem.emailAddress,
	         accountItem.password);
	   _nonZimbraAccountSetup(account);

	   _folderName = "folder" + ZimbraSeleniumProperties.getUniqueString();

      FolderItem folderItem = FolderItem.importFromSOAP(app
            .zGetActiveAccount(), FolderItem.SystemFolder.Inbox,
            _soapDestination, app.zGetActiveAccount().EmailAddress);

      DialogCreateFolder createFolderDialog = (DialogCreateFolder)
            app.zPageMail.zListItem(
                  Action.A_RIGHTCLICK,
                  Button.B_TREE_NEWFOLDER,
                  folderItem);

      createFolderDialog.zEnterFolderName(_folderName);
      createFolderDialog.zClickButton(Button.B_OK);

      _folderIsCreated = true;

      // Force-sync
      GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());
      app.zPageMain.zWaitForDesktopLoadingSpinner(5000);

      // Make sure the folder was created on the Desktop Server
      FolderItem desktopFolder = FolderItem.importFromSOAP(app
            .zGetActiveAccount(), _folderName,
            SOAP_DESTINATION_HOST_TYPE.CLIENT,
            app.zGetActiveAccount().EmailAddress);

      ZAssert.assertNotNull(desktopFolder, "Verify the new form opened");
      ZAssert.assertEquals(desktopFolder.getName(), _folderName,
      "Verify the server and client folder names match");

      // Make sure the folder was created on the ZCS server
      FolderItem folder = FolderItem.importFromSOAP(zcsAccount,
            _folderName);

      ZAssert.assertNotNull(folder, "Verify the new form opened");
      ZAssert.assertEquals(folder.getName(), _folderName,
            "Verify the server and client folder names match");
	}

	@Test(description = "Create Inbox's subfolder for POP Zimbra Account through ZD", groups = { "functional" })
	public void CreateInboxSubfolderPopZimbraAccountThroughZD()
	throws HarnessException {
	   app.zPageLogin.zNavigateTo();
	   app.zPageLogin.zRemoveAccount();
	   ZimbraAccount zcsAccount = ZimbraAccount.AccountZWC();

	   DesktopAccountItem accountItem = app.zPageAddNewAccount.zAddZimbraPopAccountThruUI(
	         ZimbraAccount.AccountZWC().EmailAddress,
	         ZimbraAccount.AccountZWC().Password,
	         ZimbraSeleniumProperties.getStringProperty("server.host", "localhost"),
	         true,
	         "465");

	   ZimbraAccount account = new ZimbraAccount(accountItem.emailAddress,
	         accountItem.password);
	   _nonZimbraAccountSetup(account);

	   _folderName = "folder" + ZimbraSeleniumProperties.getUniqueString();

	   FolderItem folderItem = FolderItem.importFromSOAP(app
	         .zGetActiveAccount(), FolderItem.SystemFolder.Inbox,
	         _soapDestination, app.zGetActiveAccount().EmailAddress);

	   DialogCreateFolder createFolderDialog = (DialogCreateFolder)
	         app.zPageMail.zListItem(
	                  Action.A_RIGHTCLICK,
	                  Button.B_TREE_NEWFOLDER,
	                  folderItem);

	   createFolderDialog.zEnterFolderName(_folderName);
	   createFolderDialog.zClickButton(Button.B_OK);

	   _folderIsCreated = true;

	   // Force-sync
	   GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());
	   app.zPageMain.zWaitForDesktopLoadingSpinner(5000);

	   // Make sure the folder was created on the Desktop Server
	   FolderItem desktopFolder = FolderItem.importFromSOAP(app
	         .zGetActiveAccount(), _folderName,
	         SOAP_DESTINATION_HOST_TYPE.CLIENT,
	         app.zGetActiveAccount().EmailAddress);

	   ZAssert.assertNotNull(desktopFolder, "Verify the new form opened");
	   ZAssert.assertEquals(desktopFolder.getName(), _folderName,
	         "Verify the server and client folder names match");

	   // Make sure the folder was created on the ZCS server
	   FolderItem folder = null;

	   try {
	      folder = FolderItem.importFromSOAP(zcsAccount,
	            _folderName);
	   } catch (HarnessException e) {
	      // This is expected because the verification is to get no folder
	      // from import SOAP
	   }

	   ZAssert.assertNull(folder, "Verify the folder in ZCS server is not created");
	}

	@AfterMethod(groups = { "always" })
	public void createFolderTestCleanup() {
		if (_folderIsCreated) {
			try {

			   app.zPageMail.zNavigateTo();

				// Delete it from Email Server
				FolderItem.deleteUsingSOAP(app.zGetActiveAccount(),
				      _folderName);

			} catch (Exception e) {

			   logger.info("Failed while removing the folder.");
				e.printStackTrace();

			} finally {

			   _folderName = null;
				_folderIsCreated = false;

			}
		}
	}

}
