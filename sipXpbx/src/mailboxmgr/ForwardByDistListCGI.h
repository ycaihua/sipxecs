// 
// 
// Copyright (C) 2007 Pingtel Corp., certain elements licensed under a Contributor Agreement.  
// Contributors retain copyright to elements licensed under a Contributor Agreement.
// Licensed to the User under the LGPL license.
// 
// $$
//////////////////////////////////////////////////////////////////////////////

#ifndef FORWARDBYDISTLIST_H
#define FORWARDBYDISTLIST_H

// SYSTEM INCLUDES
//#include <...>

// APPLICATION INCLUDES
#include "net/Url.h"
#include "mailboxmgr/CGICommand.h"
#include "mailboxmgr/MailboxManager.h"

// DEFINES
// MACROS
// EXTERNAL FUNCTIONS
// EXTERNAL VARIABLES
// CONSTANTS
// STRUCTS
// TYPEDEFS
// FORWARD DECLARATIONS

class ForwardByDistListCGI : public CGICommand
{
public:
    /**
     * Ctor
     */
    ForwardByDistListCGI( const char* comments,
                          const UtlString& commentsDuration,
                          const UtlString& commentsTimestamp,
                          const int  commentsSize,
                          const Url& fromMailboxIdentity,
                          const UtlString& fromFolder, 
                          const UtlString& messageIds,
                          const UtlString& toDistList ) ;
    /**
     * Virtual Destructor
     */
    virtual ~ForwardByDistListCGI();

    /** This does the work */
    virtual OsStatus execute (UtlString* out = NULL) ;

protected:

private:
    const UtlString m_commentsDuration;
    const UtlString m_commentsTimestamp;
    // Note that this is an identity and not a full URL
    // we need to look up the real Url from the IMDB
    const Url      m_fromMailboxIdentity;
    const UtlString m_fromFolder;
    const UtlString m_messageIds;
    const UtlString m_toDistList;
    char* m_comments;
    int   m_commentsSize;
};

#endif //FORWARDBYDISTLIST_H
