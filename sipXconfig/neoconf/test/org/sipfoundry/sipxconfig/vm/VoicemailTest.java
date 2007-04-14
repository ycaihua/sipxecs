/*
 * 
 * 
 * Copyright (C) 2007 Pingtel Corp., certain elements licensed under a Contributor Agreement.  
 * Contributors retain copyright to elements licensed under a Contributor Agreement.
 * Licensed to the User under the LGPL license.
 * 
 * $
 */
package org.sipfoundry.sipxconfig.vm;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import junit.framework.TestCase;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.sipfoundry.sipxconfig.vm.Voicemail.MessageDescriptor;

public class VoicemailTest extends TestCase {
    
    public void testReadMessageDescriptor() throws IOException {
        InputStream in = getClass().getResourceAsStream("200/inbox/00000001-00.xml");
        MessageDescriptor md = Voicemail.readMessageDescriptor(in);
        assertNotNull(md);
        assertEquals("Voice Message 00000002", md.getSubject());
    }
    
    public void testGetDate() throws ParseException {
        DateFormat formatter = new SimpleDateFormat(MessageDescriptor.TIMESTAMP_FORMAT);
        Date date = formatter.parse("Tue, 9-Jan-2007 02:33:00 PM EST");
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("EST"), Locale.ENGLISH);
        cal.setTime(date);
        cal.toString();
        assertEquals(14, cal.get(Calendar.HOUR_OF_DAY));
    }
    
    /**
     * XCF-1519
     */
    public void testGetDateWithNoTimezone() throws ParseException {
        DateFormat formatter = new SimpleDateFormat(MessageDescriptor.TIMESTAMP_FORMAT_NO_ZONE);
        formatter.setLenient(true);
        formatter.parse("Sun, 11-Feb-2007 04:07:31 AM");        
    }
    
    public void testLoadDescriptor() {
        Voicemail vm = new Voicemail(MailboxManagerTest.READONLY_MAILSTORE, "200", "inbox", "00000001");
        MessageDescriptor desc = vm.getDescriptor();
        assertEquals("Voice Message 00000002", desc.getSubject());        
        assertEquals("\"Douglas+Hubler\"<sip:201@nuthatch.pingtel.com>;tag%3D53585A61-338ED896", desc.getFrom());        
    }
    
    public void testRealbasename() {
        Voicemail vm = new Voicemail(new File("."), "200", "inbox", "00000001");
        assertEquals("00000001", vm.getMessageId());        
    }
    
    public void testMove() throws IOException {
        File mailstore = MailboxManagerTest.createTestMailStore();
        MailboxManagerImpl mgr = new MailboxManagerImpl();
        mgr.setMailstoreDirectory(mailstore.getAbsolutePath());
        Mailbox mbox = mgr.getMailbox("200");
        Voicemail vmOrig = mbox.getVoicemail("inbox", "00000001");
        mgr.move(mbox, vmOrig, "deleted");
        Voicemail vmNew = mbox.getVoicemail("deleted", "00000001");
        assertEquals("Voice Message 00000002", vmNew.getDescriptor().getSubject());
        // nice, not critical
        FileUtils.deleteDirectory(mailstore);
    }
    
    public void testAllFiles() throws IOException {
        File mailstore = MailboxManagerTest.createTestMailStore();
        Voicemail vm = new Voicemail(mailstore, "200", "inbox", "00000001");
        File[] files = vm.getAllFiles();
        assertEquals(2, files.length);        
    }
    
    public void testDelete() throws IOException {
        File mailstore = MailboxManagerTest.createTestMailStore();
        MailboxManagerImpl mgr = new MailboxManagerImpl();
        mgr.setMailstoreDirectory(mailstore.getAbsolutePath());
        Mailbox mbox = mgr.getMailbox("200");
        List<Voicemail> vm = mgr.getVoicemail(mbox, "inbox"); 
        assertEquals(2, vm.size());

        mgr.delete(mbox, vm.get(0));

        assertEquals(1, mgr.getVoicemail(mbox, "inbox").size());

        // nice, not critical
        FileUtils.deleteDirectory(mailstore);        
    }
    
    public void testWriteDescriptor() throws IOException {
        ByteArrayOutputStream original = new ByteArrayOutputStream();
        InputStream in = getClass().getResourceAsStream("200/inbox/00000001-00.xml");
        IOUtils.copy(in, original);
        InputStream inMemory = new ByteArrayInputStream(original.toByteArray());
        MessageDescriptor md = Voicemail.readMessageDescriptor(inMemory);
        md.setSubject("testWriteDescriptor");
        ByteArrayOutputStream actual = new ByteArrayOutputStream();
        Voicemail.writeMessageDescriptor(md, actual);
        
        ByteArrayOutputStream expected = new ByteArrayOutputStream();
        InputStream inExpected = getClass().getResourceAsStream("write-expected.xml");
        IOUtils.copy(inExpected, expected);
        
        assertEquals(new String(expected.toByteArray()), new String(actual.toByteArray()));
    }
    
    public void testSave() throws IOException {
        File mailstore = MailboxManagerTest.createTestMailStore();
        Voicemail vm = new Voicemail(mailstore, "200", "inbox", "00000001");
        assertEquals("Voice Message 00000002", vm.getDescriptor().getSubject());
        vm.setSubject("new subject");
        vm.save();
        
        Voicemail saved = new Voicemail(mailstore, "200", "inbox", "00000001");
        assertEquals("new subject", saved.getDescriptor().getSubject());
    }
    
    public void testIsHeard() throws IOException {
        Voicemail vm1 = new Voicemail(MailboxManagerTest.READONLY_MAILSTORE, "200", "inbox", "00000001");
        assertTrue(vm1.isHeard());
        
        Voicemail vm2 = new Voicemail(MailboxManagerTest.READONLY_MAILSTORE, "200", "inbox", "00000018");
        assertFalse(vm2.isHeard());
    }
    
    public void testForwardedVoicemail() throws IOException {
        Voicemail vm = new Voicemail(MailboxManagerTest.READONLY_MAILSTORE, "200", "inbox", "00000018");
        assertTrue(vm.isForwarded());
        assertEquals("00000018-FW.wav", vm.getMediaFile().getName());
        assertFalse(vm.hasForwardComment());
        assertEquals(6, vm.getForwardedDescriptor().getDurationsecs());
    }
}
