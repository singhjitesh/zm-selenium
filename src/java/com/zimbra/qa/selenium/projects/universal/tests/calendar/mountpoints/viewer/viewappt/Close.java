/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2013, 2014, 2015, 2016 Synacor, Inc.
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
package com.zimbra.qa.selenium.projects.universal.tests.calendar.mountpoints.viewer.viewappt;

import java.util.Calendar;
import org.testng.annotations.Test;
import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.universal.core.CalendarWorkWeekTest;

public class Close extends CalendarWorkWeekTest {

	public Close() {
		logger.info("New "+ Close.class.getCanonicalName());
		super.startingPage = app.zPageCalendar;
	}
	
	@Test( description = "Grantee opens appointment from grantor's calendar and close it without making any changes",
			groups = { "functional", "L2" })
			
	public void Close_01() throws HarnessException {
		
		String apptSubject = ConfigProperties.getUniqueString();
		String apptContent = ConfigProperties.getUniqueString();
		String foldername = "folder" + ConfigProperties.getUniqueString();
		String mountpointname = "mountpoint" + ConfigProperties.getUniqueString();
		
		Calendar now = this.calendarWeekDayUTC;
		ZDate startUTC = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 12, 0, 0);
		ZDate endUTC   = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 13, 0, 0);
		
		FolderItem calendarFolder = FolderItem.importFromSOAP(ZimbraAccount.Account6(), FolderItem.SystemFolder.Calendar);
		
		// Create a folder to share
		ZimbraAccount.Account6().soapSend(
					"<CreateFolderRequest xmlns='urn:zimbraMail'>"
				+		"<folder name='" + foldername + "' l='" + calendarFolder.getId() + "' view='appointment'/>"
				+	"</CreateFolderRequest>");
		
		FolderItem folder = FolderItem.importFromSOAP(ZimbraAccount.Account6(), foldername);
		
		// Share it
		ZimbraAccount.Account6().soapSend(
					"<FolderActionRequest xmlns='urn:zimbraMail'>"
				+		"<action id='"+ folder.getId() +"' op='grant'>"
				+			"<grant d='"+ app.zGetActiveAccount().EmailAddress +"' gt='usr' perm='r' view='appointment'/>"
				+		"</action>"
				+	"</FolderActionRequest>");
		
		// Mount it
		app.zGetActiveAccount().soapSend(
					"<CreateMountpointRequest xmlns='urn:zimbraMail'>"
				+		"<link l='1' name='"+ mountpointname +"'  rid='"+ folder.getId() +"' zid='"+ ZimbraAccount.Account6().ZimbraId +"' view='appointment' color='5'/>"
				+	"</CreateMountpointRequest>");
		
		// Create appointment
		ZimbraAccount.Account6().soapSend(
				"<CreateAppointmentRequest xmlns='urn:zimbraMail'>"
				+		"<m l='"+ folder.getId() +"' >"
				+			"<inv method='REQUEST' type='event' status='CONF' draft='0' class='PUB' fb='B' transp='O' allDay='0' name='"+ apptSubject +"'>"
				+				"<s d='"+ startUTC.toTimeZone(ZTimeZone.TimeZoneEST.getID()).toYYYYMMDDTHHMMSS() +"' tz='"+ ZTimeZone.TimeZoneEST.getID() +"'/>"
				+				"<e d='"+ endUTC.toTimeZone(ZTimeZone.TimeZoneEST.getID()).toYYYYMMDDTHHMMSS() +"' tz='"+ ZTimeZone.TimeZoneEST.getID() +"'/>"
				+				"<or a='"+ ZimbraAccount.Account6().EmailAddress +"'/>"
				+				"<at role='REQ' ptst='NE' rsvp='1' a='" + app.zGetActiveAccount().EmailAddress + "'/>"
				+			"</inv>"
				+			"<e a='"+ app.zGetActiveAccount().EmailAddress +"' t='t'/>"
				+			"<su>"+ apptSubject +"</su>"
				+			"<mp content-type='text/plain'>"
				+				"<content>" + apptContent + "</content>"
				+			"</mp>"
				+		"</m>"
				+	"</CreateAppointmentRequest>");
		
		// Verify appointment exists in current view
        ZAssert.assertTrue(app.zPageCalendar.zVerifyAppointmentExists(apptSubject), "Verify appointment displayed in current view");
		
		// Mark ON to mounted calendar folder and select the appointment
		app.zTreeCalendar.zMarkOnOffCalendarFolder("Calendar");
		app.zTreeCalendar.zMarkOnOffCalendarFolder(mountpointname);
		
		// Open appointment
		app.zPageCalendar.zListItem(Action.A_RIGHTCLICK, Button.O_OPEN_MENU, apptSubject);
		app.zPageCalendar.zToolbarPressButton(Button.B_CLOSE);
		
		// Make sure there is no warning or any other dialog (below code should fail if any)
		app.zPageCalendar.zListItem(Action.A_RIGHTCLICK, Button.O_OPEN_MENU, apptSubject);
		app.zPageCalendar.zToolbarPressButton(Button.B_CLOSE);
        
	}

}
