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
package com.zimbra.qa.selenium.projects.universal.tests.calendar.meetings.organizer.singleday.resetmeetingstatus;

import java.util.Calendar;

import org.testng.annotations.*;

import com.zimbra.qa.selenium.framework.core.Bugs;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.universal.core.CalendarWorkWeekTest;
import com.zimbra.qa.selenium.projects.universal.ui.calendar.DialogSendUpdatetoAttendees;
import com.zimbra.qa.selenium.projects.universal.ui.calendar.FormApptNew;
import com.zimbra.qa.selenium.projects.universal.ui.calendar.FormApptNew.Field;

public class ResetStatusAfterUpdatingAttendee extends CalendarWorkWeekTest {	
	
	public ResetStatusAfterUpdatingAttendee() {
		logger.info("New "+ ResetStatusAfterUpdatingAttendee.class.getCanonicalName());
		super.startingPage = app.zPageCalendar;
	}
	
	@Bugs(ids = "49881")
	@Test( description = "Check reset status of meeting after Updating attendee",
			groups = { "functional", "L2"})
			
	public void ResetStatusAfterUpdatingAttendee_01() throws HarnessException {
		
		// Create a meeting			
		String tz = ZTimeZone.TimeZoneEST.getID();
		String apptSubject = ConfigProperties.getUniqueString();
		String apptAttendee1 = ZimbraAccount.Account1().EmailAddress;
		String apptAttendee2 = ZimbraAccount.Account2().EmailAddress;		
		String apptAttendee3 = ZimbraAccount.Account3().EmailAddress;
		
		// Absolute dates in UTC zone
		Calendar now = this.calendarWeekDayUTC;
		ZDate startUTC = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 13, 0, 0);
		ZDate endUTC   = new ZDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), 14, 0, 0);
		
		app.zGetActiveAccount().soapSend(
                "<CreateAppointmentRequest xmlns='urn:zimbraMail'>" +
                     "<m>"+
                     	"<inv method='REQUEST' type='event' status='CONF' draft='0' class='PUB' fb='B' transp='O' allDay='0' name='"+ apptSubject +"'>"+
                     		"<s d='"+ startUTC.toTimeZone(tz).toYYYYMMDDTHHMMSS() +"' tz='"+ tz +"'/>" +
                     		"<e d='"+ endUTC.toTimeZone(tz).toYYYYMMDDTHHMMSS() +"' tz='"+ tz +"'/>" +
                     		"<or a='"+ app.zGetActiveAccount().EmailAddress +"'/>" +
                     		"<at role='REQ' ptst='NE' rsvp='1' a='" + apptAttendee1 + "' d='2'/>" +
                     		"<at role='REQ' ptst='NE' rsvp='1' a='" + apptAttendee2 + "' d='2'/>" +
                     	"</inv>" +
                     	"<e a='"+ ZimbraAccount.Account1().EmailAddress +"' t='t'/>" +
                     	"<mp content-type='text/plain'>" +
                     		"<content>"+ ConfigProperties.getUniqueString() +"</content>" +
                     	"</mp>" +
                     "<su>"+ apptSubject +"</su>" +
                     "</m>" +
               "</CreateAppointmentRequest>");
        
		// Verify appointment exists in current view
        ZAssert.assertTrue(app.zPageCalendar.zVerifyAppointmentExists(apptSubject), "Verify appointment displayed in current view");
        
        //Login as attendee and accept the invite
        app.zPageMain.zLogout();
		app.zPageLogin.zLogin(ZimbraAccount.Account1());

		app.zPageCalendar.zNavigateTo();
		app.zPageCalendar.zListItem(Action.A_RIGHTCLICK, Button.O_ACCEPT_MENU, apptSubject);		
		app.zPageMain.zLogout();			
		app.zPageLogin.zLogin(ZimbraAccount.AccountZWC());   
        
        // Remove one attendee, add another and re-send the appointment
		app.zPageCalendar.zNavigateTo();
        FormApptNew apptForm = (FormApptNew)app.zPageCalendar.zListItem(Action.A_DOUBLECLICK, apptSubject);
        apptForm.zRemoveAttendee(apptAttendee2);
        apptForm.zFillField(Field.Attendees, apptAttendee3);
        apptForm.zSubmit();
        
        DialogSendUpdatetoAttendees sendUpdateDialog = (DialogSendUpdatetoAttendees) new DialogSendUpdatetoAttendees(app, app.zPageCalendar);
        sendUpdateDialog.zClickButton(Button.B_SEND_UPDATES_ONLY_TO_ADDED_OR_REMOVED_ATTENDEES);
        sendUpdateDialog.zClickButton(Button.B_OK);
        
		// --- Check that the organizer shows the attendee as "Accepted" ---
		app.zGetActiveAccount().soapSend(
				"<SearchRequest xmlns='urn:zimbraMail' types='appointment' calExpandInstStart='"+ startUTC.addDays(-10).toMillis() +"' calExpandInstEnd='"+ endUTC.addDays(10).toMillis() +"'>"
			+		"<query>"+ apptSubject +"</query>"
			+	"</SearchRequest>");
	
		String organizerInvId = app.zGetActiveAccount().soapSelectValue("//mail:appt", "invId");

			app.zGetActiveAccount().soapSend(
				"<GetAppointmentRequest  xmlns='urn:zimbraMail' id='"+ organizerInvId +"'/>");
	
		String attendeeStatus = app.zGetActiveAccount().soapSelectValue("//mail:at[@a='"+ ZimbraAccount.Account1().EmailAddress +"']", "ptst");

		// Verify attendee status shows as ptst=AC
		ZAssert.assertEquals(attendeeStatus, "AC", "Verify that the attendee shows as 'Accepted'");
		
		// --- Check that the attendee showing status as "Needs Action" ---
		ZimbraAccount.Account1().soapSend(
				"<SearchRequest xmlns='urn:zimbraMail' types='appointment' calExpandInstStart='"+ startUTC.addDays(-10).toMillis() +"' calExpandInstEnd='"+ endUTC.addDays(10).toMillis() +"'>"
			+		"<query>"+ apptSubject +"</query>"
			+	"</SearchRequest>");
	
		String attendeeInvId = ZimbraAccount.Account1().soapSelectValue("//mail:appt", "invId");

		ZimbraAccount.Account1().soapSend(
				"<GetAppointmentRequest  xmlns='urn:zimbraMail' id='"+ attendeeInvId +"'/>");
	
		String myStatus = ZimbraAccount.Account1().soapSelectValue("//mail:at[@a='"+ ZimbraAccount.Account1().EmailAddress +"']", "ptst");

		// Verify attendee status shows as ptst=AC
		ZAssert.assertEquals(myStatus, "AC", "Verify that the attendee shows as 'Accepted'");
		
	}
	
}
