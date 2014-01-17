import httplib
import base64
import json

HOST="myhttprestchat.appspot.com"
PORT=80
#HOST="localhost"
#PORT=8080
IS_HTTPS="false";

class HttpChatClient(object):
    def __init__(self):
        object.__init__(self)

    def getConnection(self):
        if (IS_HTTPS == "true"):
            return httplib.HTTPSConnection(HOST, PORT)
        else:
            return httplib.HTTPConnection(HOST, PORT)

    def registerA(self):
        request = '{"username":"bhoule","password":"qwerty","email":"benoit.houle@gmail.com"}'
        conn = self.getConnection() 
        print 'Sending register A'
        conn.request("POST", "/rest/register",
                        request,
                        {
                            "Content-type" : "application/json"
                        }
                    ); 

        response = conn.getresponse()
        body = response.read()
        print response.status, response.reason, response.getheaders(), body;

    def registerB(self):
        request = '{"username":"gringo","password":"qazwsx","email":"gringo.hull@here.com"}'
        conn = self.getConnection() 
        print 'Sending register B'
        conn.request("POST", "/rest/register",
                        request,
                        {
                            "Content-type" : "application/json"
                        }
                    ); 

        response = conn.getresponse()
        body = response.read()
        print response.status, response.reason, response.getheaders(), body;

    def loginA(self):
        request = '{"username":"bhoule","password":"qwerty"}'
        conn = self.getConnection() 
        print 'Sending login A'
        conn.request("POST", "/rest/login",
                        request,
                        {
                            "Content-type" : "application/json"
                        }
                    ); 

        response = conn.getresponse()
        body = response.read()
        print response.status, response.reason, response.getheaders(), body;
        self.currentTokenA = response.getheader('x-httpchat-token')
        self.currentUserIdA = response.getheader('x-httpchat-userid')
        print 'generated token is:' + self.currentTokenA
        print 'generated userid is:' + self.currentUserIdA

    def loginB(self):
        request = '{"username":"gringo","password":"qazwsx"}'
        conn = self.getConnection() 
        print 'Sending login B'
        conn.request("POST", "/rest/login",
                        request,
                        {
                            "Content-type" : "application/json"
                        }
                    ); 

        response = conn.getresponse()
        body = response.read()
        print response.status, response.reason, response.getheaders(), body;
        self.currentTokenB = response.getheader('x-httpchat-token')
        self.currentUserIdB = response.getheader('x-httpchat-userid')
        print 'generated token is:' + self.currentTokenB
        print 'generated userid is:' + self.currentUserIdB

    def saveProfileA(self):
        request = '{"fullname":"Benoit Houle"}'
        conn = self.getConnection()
        print 'Sending saveProfile A'
        conn.request("PUT", "/rest/secure/profile",
                        request,
                        {
                            "Content-type" : "application/json",
							"Authorization" : base64.b64encode("%s:%s" % (self.currentUserIdA, self.currentTokenA))
                        }
                    ); 

        response = conn.getresponse()
        body = response.read()
        print response.status, response.reason, response.getheaders(), body;

    def getMyProfileA(self):
        request = ''
        conn = self.getConnection()
        print 'Sending getMyProfile A'
        conn.request("GET", "/rest/secure/profile?filterType=FULL",
                        request,
                        {
							"Authorization" : base64.b64encode("%s:%s" % (self.currentUserIdA, self.currentTokenA))
                        }
                    ); 

        response = conn.getresponse()
        body = response.read()
        print response.status, response.reason, response.getheaders(), body;

    def saveProfileB(self):
        request = '{"fullname":"Gringo Hull"}'
        conn = self.getConnection()
        print 'Sending saveProfile B'
        conn.request("PUT", "/rest/secure/profile?filterType=FULL",
                        request,
                        {
                            "Content-type" : "application/json",
							"Authorization" : base64.b64encode("%s:%s" % (self.currentUserIdB, self.currentTokenB))
                        }
                    ); 

        response = conn.getresponse()
        body = response.read()
        print response.status, response.reason, response.getheaders(), body;

    def getMyProfileB(self):
        request = ''
        conn = self.getConnection()
        print 'Sending getMyProfile B'
        conn.request("GET", "/rest/secure/profile?filterType=FULL",
                        request,
                        {
							"Authorization" : base64.b64encode("%s:%s" % (self.currentUserIdB, self.currentTokenB))
                        }
                    ); 

        response = conn.getresponse()
        body = response.read()
        print response.status, response.reason, response.getheaders(), body;

    def getProfileA(self):
        request = ''
        conn = self.getConnection()
        print 'Sending getProfile A->B'
        conn.request("GET", "/rest/secure/profile/%s?filterType=FULL" % (self.currentUserIdB),
                        request,
                        {
							"Authorization" : base64.b64encode("%s:%s" % (self.currentUserIdA, self.currentTokenA))
                        }
                    ); 

        response = conn.getresponse()
        body = response.read()
        print response.status, response.reason, response.getheaders(), body;

    def getProfileB(self):
        request = ''
        conn = self.getConnection()
        print 'Sending getProfile B->A'
        conn.request("GET", "/rest/secure/profile/%s?filterType=FULL" % (self.currentUserIdA),
                        request,
                        {
							"Authorization" : base64.b64encode("%s:%s" % (self.currentUserIdB, self.currentTokenB))
                        }
                    ); 

        response = conn.getresponse()
        body = response.read()
        print response.status, response.reason, response.getheaders(), body;
    def searchContactsA(self):
        request = ''
        conn = self.getConnection()
        print 'Sending searchContacts A'
        conn.request("GET", "/rest/secure/contacts/s?filterTypes=USERNAME&filterValue=grin&limit=10",
                        request,
                        {
							"Authorization" : base64.b64encode("%s:%s" % (self.currentUserIdA, self.currentTokenA))
                        }
                    ); 

        response = conn.getresponse()
        body = response.read()
        print response.status, response.reason, response.getheaders(), body;
        jsonData = json.loads(body)
        self.invitedContactIdA = jsonData["contacts"][0]["id"]
        print 'contact id to invite:' + str(self.invitedContactIdA)

    def searchContactsB(self):
        request = ''
        conn = self.getConnection()
        print 'Sending searchContacts B'
        conn.request("GET", "/rest/secure/contacts/s?filterTypes=USERNAME&filterValue=bh&limit=10",
                        request,
                        {
							"Authorization" : base64.b64encode("%s:%s" % (self.currentUserIdB, self.currentTokenB))
                        }
                    ); 

        response = conn.getresponse()
        body = response.read()
        print response.status, response.reason, response.getheaders(), body;
        jsonData = json.loads(body)
        self.invitedContactIdB = jsonData["contacts"][0]["id"]
        print 'contact id to invite:' + str(self.invitedContactIdB)

    def inviteContactA(self):
        request = ''
        conn = self.getConnection()
        print 'Sending inviteContact A->B'
        conn.request("POST", "/rest/secure/contact/%s/invite" % (str(self.invitedContactIdA)),
                        request,
                        {
							"Authorization" : base64.b64encode("%s:%s" % (self.currentUserIdA, self.currentTokenA))
                        }
                    ); 

        response = conn.getresponse()
        body = response.read()
        print response.status, response.reason, response.getheaders(), body;

    def pollA(self):
        request = ''
        conn = self.getConnection()
        print 'Sending poll A'
        conn.request("GET", "/rest/secure/alerts",
                        request,
                        {
							"Authorization" : base64.b64encode("%s:%s" % (self.currentUserIdA, self.currentTokenA))
                        }
                    ); 

        response = conn.getresponse()
        body = response.read()
        print response.status, response.reason, response.getheaders(), body;
        self.alertIdA = ""
        jsonData = json.loads(body)
        if "alerts" in jsonData:
            self.alertIdA = jsonData["alerts"][0]["id"]
        print 'contact invite id to accept/deny:' + str(self.alertIdA)

    def pollB(self):
        request = ''
        conn = self.getConnection()
        print 'Sending poll B'
        conn.request("GET", "/rest/secure/alerts",
                        request,
                        {
							"Authorization" : base64.b64encode("%s:%s" % (self.currentUserIdB, self.currentTokenB))
                        }
                    ); 

        response = conn.getresponse()
        body = response.read()
        print response.status, response.reason, response.getheaders(), body;
        self.alertIdB = ""
        jsonData = json.loads(body)
        if "alerts" in jsonData:
            self.alertIdB = jsonData["alerts"][0]["id"]
        print 'contact invite id to accept/deny:' + str(self.alertIdB)

    def acceptContactInviteB(self):
        request = ''
        conn = self.getConnection()
        print 'Sending acceptContactInvite B->A'
        conn.request("POST", "/rest/secure/contact/invite/%s/accept" % (str(self.alertIdB)),
                        request,
                        {
							"Authorization" : base64.b64encode("%s:%s" % (self.currentUserIdB, self.currentTokenB))
                        }
                    ); 

        response = conn.getresponse()
        body = response.read()
        print response.status, response.reason, response.getheaders(), body;

    def getContactsA(self):
        request = ''
        conn = self.getConnection()
        print 'Sending getContacts A'
        conn.request("GET", "/rest/secure/contacts",
                        request,
                        {
							"Authorization" : base64.b64encode("%s:%s" % (self.currentUserIdA, self.currentTokenA))
                        }
                    ); 

        response = conn.getresponse()
        body = response.read()
        print response.status, response.reason, response.getheaders(), body;

    def getContactsB(self):
        request = ''
        conn = self.getConnection()
        print 'Sending getContacts B'
        conn.request("GET", "/rest/secure/contacts",
                        request,
                        {
							"Authorization" : base64.b64encode("%s:%s" % (self.currentUserIdB, self.currentTokenB))
                        }
                    ); 

        response = conn.getresponse()
        body = response.read()
        print response.status, response.reason, response.getheaders(), body;

    def sendMessageA(self):
        request = '{"to":"%s","text":"HelloWorld!"}' % (self.currentUserIdB)
        conn = self.getConnection()
        print 'Sending sendMessage A->B'
        conn.request("PUT", "/rest/secure/message",
                        request,
                        {
                            "Content-type" : "application/json",
							"Authorization" : base64.b64encode("%s:%s" % (self.currentUserIdA, self.currentTokenA))
                        }
                    ); 

        response = conn.getresponse()
        body = response.read()
        print response.status, response.reason, response.getheaders(), body;

    def sendMessageB(self):
        request = '{"to":"%s","text":"HelloWorld!!!!"}' % (self.currentUserIdA)
        conn = self.getConnection()
        print 'Sending sendMessage B->A'
        conn.request("PUT", "/rest/secure/message",
                        request,
                        {
                            "Content-type" : "application/json",
							"Authorization" : base64.b64encode("%s:%s" % (self.currentUserIdB, self.currentTokenB))
                        }
                    ); 

        response = conn.getresponse()
        body = response.read()
        print response.status, response.reason, response.getheaders(), body;

    def removeContactA(self):
        request = ''
        conn = self.getConnection()
        print 'Sending removeContact A->B'
        conn.request("DELETE", "/rest/secure/contact/%s" % (str(self.currentUserIdB)),
                        request,
                        {
							"Authorization" : base64.b64encode("%s:%s" % (self.currentUserIdA, self.currentTokenA))
                        }
                    ); 

        response = conn.getresponse()
        body = response.read()
        print response.status, response.reason, response.getheaders(), body;

    def logoutA(self):
        request = ''
        conn = self.getConnection() 
        print 'Sending logout A'
        conn.request("POST", "/rest/secure/logout",
                        request,
                        {
							"Authorization" : base64.b64encode("%s:%s" % (self.currentUserIdA, self.currentTokenA))
                        }
                    ); 

        response = conn.getresponse()
        body = response.read()
        print response.status, response.reason, response.getheaders(), body;

    def logoutB(self):
        request = ''
        conn = self.getConnection() 
        print 'Sending logout B'
        conn.request("POST", "/rest/secure/logout",
                        request,
                        {
							"Authorization" : base64.b64encode("%s:%s" % (self.currentUserIdB, self.currentTokenB))
                        }
                    ); 

        response = conn.getresponse()
        body = response.read()
        print response.status, response.reason, response.getheaders(), body;

client = HttpChatClient()
client.registerA()
client.registerB()
client.loginA()
client.loginB()
client.saveProfileA()
client.getMyProfileA()
client.saveProfileB()
client.getMyProfileB()
client.getProfileA()
client.getProfileB()
client.searchContactsA()
client.searchContactsB()
client.inviteContactA()
client.pollB()
client.acceptContactInviteB()
client.getContactsA()
client.getContactsB()
client.sendMessageA()
client.sendMessageB()
client.pollA()
client.pollB()
client.removeContactA()
client.getContactsA()
client.pollB()
client.getContactsB()
client.searchContactsA()
client.inviteContactA()
client.pollB()
client.acceptContactInviteB()
client.getContactsA()
client.getContactsB()
client.logoutA()
client.logoutB()