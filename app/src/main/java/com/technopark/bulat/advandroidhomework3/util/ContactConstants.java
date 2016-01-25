package com.technopark.bulat.advandroidhomework3.util;

import android.net.Uri;
import android.provider.ContactsContract;

/**
 * Created by bulat on 25.01.16.
 */
public interface ContactConstants {
    Uri CONTACT_CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
    String CONTACT_ID = ContactsContract.Contacts._ID;
    String CONTACT_DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
    String CONTACT_HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;

    Uri PHONE_CONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
    String PHONE_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
    String PHONE_NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;

    Uri EMAIL_CONTENT_URI =  ContactsContract.CommonDataKinds.Email.CONTENT_URI;
    String EMAIL_CONTACT_ID = ContactsContract.CommonDataKinds.Email.CONTACT_ID;
    String EMAIL_DATA = ContactsContract.CommonDataKinds.Email.DATA;
}
