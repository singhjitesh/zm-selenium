/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2016 Synacor, Inc.
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
package com.zimbra.qa.selenium.projects.admin.tests.backuprestore;

import org.testng.annotations.Test;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.ConfigProperties;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAdminAccount;
import com.zimbra.qa.selenium.projects.admin.core.AdminCommonTest;
import com.zimbra.qa.selenium.projects.admin.items.AccountItem;
import com.zimbra.qa.selenium.projects.admin.items.BackupItem;
import com.zimbra.qa.selenium.projects.admin.ui.WizardBackup;
import com.zimbra.qa.selenium.projects.admin.ui.WizardBackup.Locators;

public class BackupSelectedAccount extends AdminCommonTest {

	public BackupSelectedAccount() {
		logger.info("New "+ BackupSelectedAccount.class.getCanonicalName());

		// All tests start at the "backup" page
		super.startingPage = app.zPageManageBackups;
	}

	/**
	 * Testcase : Backup selected account
	 * Steps :
	 * 1. Navigate to "Home --> Tools and Migraton --> Backups"
	 * 2. Backup up selected account > verify backup is in running/completed status
	 * @throws HarnessException
	 */
	@Test( description = "Navigate to Backups",
			groups = { "functional", "L1","network" })
	public void BackupSelectedAccount_01() throws HarnessException {

		// Navigate to "Home --> Tools and Migraton --> Backups"
		app.zPageManageBackups.zNavigateTo();
		String hostname = ConfigProperties.getStringProperty("server.host");
		String backupStatus ="Completed";

		// Create an account
		AccountItem account = new AccountItem("email" + ConfigProperties.getUniqueString(),ConfigProperties.getStringProperty("testdomain"));
		ZimbraAdminAccount.AdminConsoleAdmin().soapSend(
				"<CreateAccountRequest xmlns='urn:zimbraAdmin'>"
						+			"<name>" + account.getEmailAddress() + "</name>"
						+			"<password>test123</password>"
						+		"</CreateAccountRequest>");

		BackupItem backup = new BackupItem();
		String emailAddress =account.getEmailAddress();
		String criteria ="selected";
		backup.setEmailAddress(emailAddress);

		// Select backup option from gear menu
		WizardBackup wizard= (WizardBackup) app.zPageManageBackups.zToolbarPressPulldown(Button.B_GEAR_BOX,Button.B_BACKUP);

		// Select backup method full
		wizard.setBackupMethod(Locators.BACKUP_METHOD_FULL);

		// Search and add an account
		wizard.selectAccountsToBackup(criteria,emailAddress);

		// Fill out the wizard	
		wizard.zCompleteWizard(backup);

		// Select backup
		app.zPageManageBackups.zListItem(Action.A_LEFTCLICK, hostname);

		// Select view option from gear menu
		app.zPageManageBackups.zToolbarPressPulldown(Button.B_GEAR_BOX,Button.B_VIEW);

		// Verfiy backup is Running/completed
		ZAssert.assertTrue(app.zPageManageBackups.zVerifyBackupStatus(backupStatus), "Verfiy backup is Running/completed!");

	}

}