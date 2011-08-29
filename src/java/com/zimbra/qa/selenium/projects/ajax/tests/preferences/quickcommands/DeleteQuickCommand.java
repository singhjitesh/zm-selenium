package com.zimbra.qa.selenium.projects.ajax.tests.preferences.quickcommands;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.preferences.TreePreferences.TreeItem;


public class DeleteQuickCommand extends AjaxCommonTest {

	public DeleteQuickCommand() {
		
		super.startingPage = app.zPagePreferences;
		super.startingAccountPreferences = null;
	}
	


	@Test(
			description = "Delete a Quick Command",
			groups = { "functional" }
			)
	public void DeleteQuickCommand_01() throws HarnessException {
		
		// Create a quick command
//		app.zGetActiveAccount().soapSend(
//					"<ModifyPrefsRequest xmlns='urn:zimbraAccount'>"
//				+		"<pref name='zimbraPrefQuickCommand'>{'id':1,'itemTypeId':'MSG','name':'asdf','description':'asdf','isActive':true,'actions':[{'id':1,'typeId':'actionFlag','value':'flagged','isActive':true}]}</pref>"
//				+	"</ModifyPrefsRequest>");
		
		// Navigate to preferences -> notifications
		app.zTreePreferences.zTreeItem(Action.A_LEFTCLICK, TreeItem.QuickCommands);

		
		// Click "Delete"
		// TODO:
		
		// Get the quick commands from the server.  Verify the quick command is not there.
		
		
	}
}
