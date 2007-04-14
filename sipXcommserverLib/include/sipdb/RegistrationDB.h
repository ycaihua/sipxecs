// 
// 
// Copyright (C) 2007 Pingtel Corp., certain elements licensed under a Contributor Agreement.  
// Contributors retain copyright to elements licensed under a Contributor Agreement.
// Licensed to the User under the LGPL license.
// 
// $$
//////////////////////////////////////////////////////////////////////////////
#ifndef REGISTRATIONDB_H
#define REGISTRATIONDB_H

// SYSTEM INCLUDES

// APPLICATION INCLUDES
#include "os/OsMutex.h"
#include "utl/UtlLongLongInt.h"
#include "utl/UtlString.h"
#include "utl/UtlHashBag.h"

// DEFINES

// The prefix for GRUUs generated by this system.
#define GRUU_PREFIX "gruu~"

// MACROS
// EXTERNAL FUNCTIONS
// EXTERNAL VARIABLES
// CONSTANTS
// STRUCTS
// TYPEDEFS
// FORWARD DECLARATIONS
template<class T> class dbCursor;
class dbDatabase;
class dbFieldDescriptor;
class dbQuery;
class RegistrationBinding;
class RegistrationRow;
class ResultSet;
class TiXmlNode;
class Url;
class UtlHashMap;
class UtlSList;

/// Database of all information acquired through REGISTER messages
class RegistrationDB
{
public:
    // Singleton Accessor
    static RegistrationDB* getInstance(
        const UtlString& name = "registration" );

    /// releaseInstance - cleans up the singleton (for use at exit)
    static void releaseInstance();

    /// Queries

    UtlBoolean isOutOfSequence( const Url& uri
                               ,const UtlString& callid
                               ,const int& cseq
                              ) const;

    /// Utility method for dumping all rows
    void getAllRows ( ResultSet& rResultSet ) const;

    /// Return the max update number for primaryRegistrar, or zero if there are no such updates
    Int64 getMaxUpdateNumberForRegistrar(const UtlString& primaryRegistrar) const;

    /// Return the next updateNumber for the primaryRegistrar after the specified updateNumber
    Int64 getNextUpdateNumberForRegistrar(const UtlString& primaryRegistrar,
                                          Int64            updateNumber) const;
    /**<
     * If there are no such updates, then return 0
     */
    
    /// Get the next update for primaryRegistrar with an update number > updateNumber.
    int getNextUpdateForRegistrar( const UtlString& primaryRegistrar
                                   ,Int64           updateNumber
                                   ,UtlSList&       bindings
                                   ) const;
    /**<
     * Fill in the bindings arg with the bindings, objects of type RegistrationBinding.
     * (A single update may include multiple bindings.)
     * Return the number of bindings (the length of the list).
     */

    /// Get all updates for primaryRegistrar with an update number > updateNumber.
    int getNewUpdatesForRegistrar( const UtlString& primaryRegistrar
                                   ,Int64           updateNumber
                                   ,UtlSList&       bindings
                                   ) const;
    /**<
     * Fill in the bindings arg with the bindings, objects of type RegistrationBinding.
     * Return the number of bindings (the length of the list).
     */

    /// Return all contacts for 'uri' whose expirations are >= 'timeNow'.
    //  Used to generate lookup responses.
    void getUnexpiredContacts ( const Url& uri
                               ,const int& timeNow
                               ,ResultSet& rResultSet
                               ) const;

    /// update the binding of uri to contact: does insert or update as needed
    void updateBinding(const RegistrationBinding&);

    /// update the binding of uri to contact: does insert or update as needed
    void updateBinding( const Url& uri
                       ,const UtlString& contact
                       ,const UtlString& qvalue
                       ,const UtlString& callid
                       ,const int& cseq
                       ,const int& expires
                       ,const UtlString& instance_id
                       ,const UtlString& gruu
                       ,const UtlString& primary
                       ,const Int64& update_number
                       );

    /// expireAllBindings for this URI as of 1 second before timeNow
    void expireAllBindings( const Url& uri
                           ,const UtlString& callid
                           ,const int& cseq
                           ,const int& timeNow
                           ,const UtlString& primary
                           ,const Int64& update_number
                           );

    /// expireOldBindings for this callid and older cseq values.
    void expireOldBindings( const Url& uri
                           ,const UtlString& callid
                           ,const int& cseq
                           ,const int& timeNow
                           ,const UtlString& primary
                           ,const Int64& update_number
                           );

    /// Get all bindings expiring before newerThanTime.
    void getAllOldBindings (/// Get bindings expiring before newerThanTime
                            int newerThanTime,
                            /// Return their AORs in rAors.
                            UtlHashBag& rAors) const;

    void removeAllRows ();

    /// Garbage collect and persist database
    ///
    /// Garbage collect - delete all rows older than the specified
    /// time, and then write all remaining entries to the persistent
    /// data store (xml file).
    OsStatus cleanAndPersist( int newerThanTime );

    // Added this to make load and store code identical
    // in all database implementations -- one step closer
    // to a template version of the code.
    void insertRow ( const UtlHashMap& nvPairs );

    // ResultSet column Keys ===================================================

    // The identity of this registration
    static UtlString gIdentityKey;

    // The AOR of this registration
    static UtlString gUriKey;

    // The Call-ID of the REGISTERs that establish/maintain this registration
    static UtlString gCallidKey;

    // The contact of this registration
    static UtlString gContactKey;

    // The q-value of this registration
    static UtlString gQvalueKey;

    // The +sip.instance value that was provided with the registration, or the null string
    static UtlString gInstanceIdKey;

    // The GRUU that was assigned to this registration, or the null string
    static UtlString gGruuKey;

    // The contact of this registration
    static UtlString gCseqKey;

    // Absolute expiration time of this registration, seconds since 1/1/1970
    static UtlString gExpiresKey;

    // The name of the Primary Registrar for this registration
    static UtlString gPrimaryKey;

    // The DbUpdateNumber of the last modification to this entry
    static UtlString gUpdateNumberKey;

    //==========================================================================

protected:
    // There is only one singleton in this design
    static RegistrationDB* spInstance;

    // Fast DB instance
    static dbDatabase* spDBInstance;

    // Singleton and Serialization mutex
    static OsMutex sLockMutex;

    // Fast DB instance
    dbDatabase* m_pFastDB;

    // The persistent filename for loading/saving
    UtlString mDatabaseName;

    // this is implicit now
    OsStatus load();

    // Singleton Constructor is private
    RegistrationDB(const UtlString& name);

    RegistrationBinding* copyRowToRegistrationBinding(dbCursor<RegistrationRow>& cursor) const;

    int getUpdatesForRegistrar(dbQuery&         query,
                               UtlSList&        bindings) const;

private:
    /// No destructor, no no no
    ~RegistrationDB();

    static UtlString nullString;
};

#endif //REGISTRATIONDB_H
