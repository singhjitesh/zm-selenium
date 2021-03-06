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
package com.zimbra.qa.selenium.projects.universal.ui.calendar;

import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.SleepUtil;
import com.zimbra.qa.selenium.projects.universal.ui.DialogWarning;

public class DialogFindEquipment extends DialogWarning {

	// The ID for the main Dialog DIV
	public static final String LocatorDivID = "SEND_UPDATES_DIALOG";
		
	public DialogFindEquipment(AbsApplication application, AbsTab page) {
		super(new DialogWarningID(LocatorDivID), application, page);
				
		logger.info("new " + DialogFindEquipment.class.getCanonicalName());
	}
	public static class Locators {
		public static final String EquipmentPickerSerach="css=div[class='DwtDialog'] td[id$='_title']:contains('Search')";
		public static final String SelectEquipmentFromPicker="css=div[class='DwtDialog'] td[id$='DwtChooserButton_1_title']:contains('Add')";
		public static final String AddEquipmentFromPicker="css=div[class='DwtDialog'] td[id='ZmAttendeePicker_EQUIPMENT_button2_title']";
	}

	@Override
	public AbsPage zClickButton(Button button) throws HarnessException {
		logger.info(myPageName() + " zClickButton(" + button + ")");

		tracer.trace("Click dialog button " + button);
		if ( button == null )
			throw new HarnessException("button cannot be null");
	
		String locator = null;
		AbsPage page = null;

		if (button == Button.B_SEARCH_EQUIPMENT) {

			locator = Locators.EquipmentPickerSerach;
			page = null;

		} else if (button == Button.B_SELECT_EQUIPMENT) {

			locator = Locators.SelectEquipmentFromPicker;
			page = null;
		
		} else if (button == Button.B_OK) {

			locator= Locators.AddEquipmentFromPicker;
			page = null;
		
		} else if (button == Button.B_CANCEL) {

			locator = "css=div[class='DwtDialog'] td[id$='_button1_title']";
			page = null;
			                              
		} else {
			
			return ( super.zClickButton(button) );
		}
		
		// Make sure the locator was set
		if (locator == null) {
			throw new HarnessException("Button " + button + " not implemented");
		}

		// Make sure the locator exists
		if (!this.sIsElementPresent(locator)) {
			throw new HarnessException("Button " + button + " locator "	+ locator + " not present!");
		}
		this.sFocus(locator);
		this.sClickAt(locator, "10,10");
		this.zWaitForBusyOverlay();
		SleepUtil.sleepMedium();

		return (page);
	}
}