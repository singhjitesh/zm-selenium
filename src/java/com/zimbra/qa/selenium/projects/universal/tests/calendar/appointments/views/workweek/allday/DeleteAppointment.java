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
package com.zimbra.qa.selenium.projects.universal.tests.calendar.appointments.views.workweek.allday;

import java.awt.event.KeyEvent;
import java.util.Calendar;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import com.zimbra.common.soap.Element;
import com.zimbra.qa.selenium.framework.core.Bugs;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.universal.core.CalendarWorkWeekTest;
import com.zimbra.qa.selenium.projects.universal.ui.calendar.*;

public class DeleteAppointment extends CalendarWorkWeekTest {
	
	public DeleteAppointment() {
		logger.info("New "+ DeleteAppointment.class.getCanonicalName());
		super.startingPage = app.zPageCalendar;

	}
	
	@Bugs(ids = "69132")
	@Test( description = "Delete all-day appointment using Delete toolbar button",
			groups = { "smoke", "L1" })
	public void DeleteAllDayAppointment_01() throws HarnessException {
		
		//-- Data Setup
		// Creating objects for appointment data
		String tz, apptSubject, apptBody;
		tz = ZTimeZone.TimeZoneEST.getID();
		apptSubject = ConfigProperties.getUniqueString();
		apptBody = "body" + ConfigProperties.getUniqueString();
		
		// Absolute dates in UTC zone
		Calendar now = this.calendarWeekDayUTC;
		ZDate startUTC = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 12, 0, 0);
		ZDate endUTC   = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 14, 0, 0);
		
        app.zGetActiveAccount().soapSend(
                          "<CreateAppointmentRequest xmlns='urn:zimbraMail'>" +
                               "<m>"+
                               "<inv method='REQUEST' type='event' fb='B' transp='O' allDay='1' name='"+ apptSubject +"'>"+
                               "<s d='"+ startUTC.toTimeZone(tz).toYYYYMMDDTHHMMSS() +"' tz='"+ tz +"'/>" +
                               "<e d='"+ endUTC.toTimeZone(tz).toYYYYMMDDTHHMMSS() +"' tz='"+ tz +"'/>" +
                               "<or a='"+ app.zGetActiveAccount().EmailAddress +"'/>" +
                               "</inv>" +
                               "<mp content-type='text/plain'>" +
                               "<content>"+ apptBody +"</content>" +
                               "</mp>" +
                               "<su>"+ apptSubject +"</su>" +
                               "</m>" +
                         "</CreateAppointmentRequest>");
        
        // Verify appointment exists in current view
        ZAssert.assertTrue(app.zPageCalendar.zVerifyAppointmentExists(apptSubject), "Verify appointment displayed in current view");
        
        // Select the appointment
        app.zPageCalendar.zListItem(Action.A_LEFTCLICK, apptSubject);
        
        // Press the delete button
        DialogConfirmDeleteAppointment dlgConfirm = (DialogConfirmDeleteAppointment)app.zPageCalendar.zToolbarPressButton(Button.B_DELETE);
		dlgConfirm.zClickButton(Button.B_YES);
		dlgConfirm.zWaitForClose();
		
		
		//-- Verification
		ZAssert.assertEquals(app.zPageCalendar.sIsElementPresent(app.zPageCalendar.zGetAllDayApptLocator(apptSubject)), false, "Verify all-day appointment is deleted");
			
	}
	
	@Bugs(ids = "69132")
	@Test( description = "Delete all-day appointment using context menu",
			groups = { "smoke", "L1" })
	public void DeleteAllDayAppointment_02() throws HarnessException {
		
		
		//-- Data Setup
		
		// Creating objects for appointment data
		String tz, apptSubject, apptBody;
		tz = ZTimeZone.TimeZoneEST.getID();
		apptSubject = ConfigProperties.getUniqueString();
		apptBody = ConfigProperties.getUniqueString();
		
		// Absolute dates in UTC zone
		Calendar now = this.calendarWeekDayUTC;
		ZDate startUTC = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 12, 0, 0);
		ZDate endUTC   = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 14, 0, 0);
		
        app.zGetActiveAccount().soapSend(
                          "<CreateAppointmentRequest xmlns='urn:zimbraMail'>" +
                               "<m>"+
                               "<inv method='REQUEST' type='event' fb='B' transp='O' allDay='1' name='"+ apptSubject +"'>"+
                               "<s d='"+ startUTC.toTimeZone(tz).toYYYYMMDDTHHMMSS() +"' tz='"+ tz +"'/>" +
                               "<e d='"+ endUTC.toTimeZone(tz).toYYYYMMDDTHHMMSS() +"' tz='"+ tz +"'/>" +
                               "<or a='"+ app.zGetActiveAccount().EmailAddress +"'/>" +
                               "</inv>" +
                               "<mp content-type='text/plain'>" +
                               "<content>"+ apptBody +"</content>" +
                               "</mp>" +
                               "<su>"+ apptSubject +"</su>" +
                               "</m>" +
                         "</CreateAppointmentRequest>");

        // Verify appointment exists in current view
        ZAssert.assertTrue(app.zPageCalendar.zVerifyAppointmentExists(apptSubject), "Verify appointment displayed in current view");
        
        // Right click -> Delete
        DialogConfirmDeleteAppointment dlgConfirm = (DialogConfirmDeleteAppointment)app.zPageCalendar.zListItem(Action.A_RIGHTCLICK, Button.O_DELETE, apptSubject);
		dlgConfirm.zClickButton(Button.B_YES);
		dlgConfirm.zWaitForClose();
		
		
		//-- Verification
		ZAssert.assertEquals(app.zPageCalendar.sIsElementPresent(app.zPageCalendar.zGetAllDayApptLocator(apptSubject)), false, "Verify all-day appointment is deleted");
			
	}
	
	@DataProvider(name = "DataProviderShortcutKeys")
	public Object[][] DataProviderShortcutKeys() {
		return new Object[][] {
				new Object[] { "VK_DELETE", KeyEvent.VK_DELETE },
				new Object[] { "VK_BACK_SPACE", KeyEvent.VK_BACK_SPACE },
		};
	}

	@Bugs(ids = "69132")
	@Test( description = "Delete all-day appointment using keyboard shortcuts (Del & Backspace)",
			groups = { "functional", "L2" },
			dataProvider = "DataProviderShortcutKeys")
	public void DeleteAllDayAppointment_03(String name, int keyEvent) throws HarnessException {
		
		//-- Data Setup
		
		// Creating objects for appointment data
		String tz, apptSubject, apptBody;
		tz = ZTimeZone.TimeZoneEST.getID();
		apptSubject = ConfigProperties.getUniqueString();
		apptBody = ConfigProperties.getUniqueString();
		
		// Absolute dates in UTC zone
		Calendar now = this.calendarWeekDayUTC;
		ZDate startUTC = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 12, 0, 0);
		ZDate endUTC   = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 14, 0, 0);
		
        app.zGetActiveAccount().soapSend(
                          "<CreateAppointmentRequest xmlns='urn:zimbraMail'>" +
                               "<m>"+
                               "<inv method='REQUEST' type='event' fb='B' transp='O' allDay='1' name='"+ apptSubject +"'>"+
                               "<s d='"+ startUTC.toTimeZone(tz).toYYYYMMDDTHHMMSS() +"' tz='"+ tz +"'/>" +
                               "<e d='"+ endUTC.toTimeZone(tz).toYYYYMMDDTHHMMSS() +"' tz='"+ tz +"'/>" +
                               "<or a='"+ app.zGetActiveAccount().EmailAddress +"'/>" +
                               "</inv>" +
                               "<mp content-type='text/plain'>" +
                               "<content>"+ apptBody +"</content>" +
                               "</mp>" +
                               "<su>"+ apptSubject +"</su>" +
                               "</m>" +
                         "</CreateAppointmentRequest>");

        // Verify appointment exists in current view
        ZAssert.assertTrue(app.zPageCalendar.zVerifyAppointmentExists(apptSubject), "Verify appointment displayed in current view");
        
        // Select the appointment
        app.zPageCalendar.zListItem(Action.A_LEFTCLICK, apptSubject);
        
        // Delete appointment using keyboard Del and Backspace key
        DialogConfirmDeleteAppointment dlgConfirm = (DialogConfirmDeleteAppointment)app.zPageCalendar.zKeyboardKeyEvent(keyEvent);
		dlgConfirm.zClickButton(Button.B_YES);
		
		
		//-- Verification
		app.zGetActiveAccount().soapSend(
					"<SearchRequest xmlns='urn:zimbraMail' types='appointment' calExpandInstStart='"+ startUTC.addDays(-7).toMillis() +"' calExpandInstEnd='"+ startUTC.addDays(7).toMillis() +"'>"
				+	"<query>subject:("+ apptSubject +")</query>"
				+	"</SearchRequest>");

		Element[] nodes = app.zGetActiveAccount().soapSelectNodes("//mail:appt");
		ZAssert.assertEquals(nodes.length, 0, "Verify appointment is deleted");
	}
}
