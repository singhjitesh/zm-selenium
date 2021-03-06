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
package com.zimbra.qa.selenium.projects.universal.tests.calendar.meetings.attendee.singleday.viewappt;

import java.util.Calendar;
import org.testng.annotations.Test;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.universal.core.CalendarWorkWeekTest;
import com.zimbra.qa.selenium.projects.universal.ui.SeparateWindow;

public class ShowOriginal extends CalendarWorkWeekTest {

	public ShowOriginal() {
		logger.info("New "+ ShowOriginal.class.getCanonicalName());
		super.startingPage = app.zPageCalendar;
		
	}
	
	@Test( description = "View meeting invite by opening it and view meeting show original", 
			groups = { "functional", "L2" })
			
	public void MeetingShowOriginal_01() throws HarnessException {
		
		String apptSubject = ConfigProperties.getUniqueString();
		String apptBody = ConfigProperties.getUniqueString();

		Calendar now = this.calendarWeekDayUTC;
		ZDate startUTC = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 14, 0, 0);
		ZDate endUTC   = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 15, 0, 0);

		ZimbraAccount.AccountA().soapSend(
				"<CreateAppointmentRequest xmlns='urn:zimbraMail'>"
				+		"<m>"
				+			"<inv method='REQUEST' type='event' status='CONF' draft='0' class='PUB' fb='B' transp='O' allDay='0' name='"+ apptSubject +"'>"
				+				"<s d='"+ startUTC.toTimeZone(ZTimeZone.TimeZoneEST.getID()).toYYYYMMDDTHHMMSS() +"' tz='"+ ZTimeZone.TimeZoneEST.getID() +"'/>"
				+				"<e d='"+ endUTC.toTimeZone(ZTimeZone.TimeZoneEST.getID()).toYYYYMMDDTHHMMSS() +"' tz='"+ ZTimeZone.TimeZoneEST.getID() +"'/>"
				+				"<or a='"+ ZimbraAccount.AccountA().EmailAddress +"'/>"
				+				"<at role='REQ' ptst='NE' rsvp='1' a='" + app.zGetActiveAccount().EmailAddress + "'/>"
				+			"</inv>"
				+			"<e a='"+ app.zGetActiveAccount().EmailAddress +"' t='t'/>"
				+			"<su>"+ apptSubject +"</su>"
				+			"<mp content-type='text/plain'>"
				+				"<content>" + apptBody + "</content>"
				+			"</mp>"
				+		"</m>"
				+	"</CreateAppointmentRequest>");     
		
		// Verify appointment exists in current view
        ZAssert.assertTrue(app.zPageCalendar.zVerifyAppointmentExists(apptSubject), "Verify appointment displayed in current view");
		
		// Select Actions -> Show original
		SeparateWindow window = (SeparateWindow)app.zPageCalendar.zListItem(Action.A_DOUBLECLICK, Button.O_SHOW_ORIGINAL_MENU, apptSubject);
		
		try {
			window.zWaitForActive();
			SleepUtil.sleepMedium();

			String body = window.sGetBodyText();
			ZAssert.assertStringContains(body, apptSubject,	"Verify subject in show original");
			ZAssert.assertStringContains(body, apptBody, "Verify content in show original");
			ZAssert.assertStringContains(body, "BEGIN:VCALENDAR", "Verify 'BEGIN' header in show original");
			ZAssert.assertStringContains(body, "END:VCALENDAR", "Verify 'END' header in show original");
			ZAssert.assertStringContains(body, "ORGANIZER:mailto:" + ZimbraAccount.AccountA().EmailAddress,	"Verify organizer email address in show original");
			
		} finally {
			app.zPageMain.zCloseWindow(window, app);
		}

		
		app.zPageCalendar.zToolbarPressButton(Button.B_CLOSE);
	}
	
}
