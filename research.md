# breaking down the problem

## Create a token

* server side?
* signed?
*
*

## Keep track of the token client side

* cookies
* local storage
  * session: what if the user want's to display info in two windows/tabs in parallel?
* hidden form fields
*
*

## Exchange tokens between client/server

https only, just give an error in case of http, don't redirect

* url component
* hidden form fields (POST only as GET turns it into a url component, requires disciplined development)
* cookies
* headers; conventions/standards:
  * names:  "Authorization", "HTTP_AUTHORIZATION", "X-HTTP_AUTHORIZATION", "X_HTTP_AUTHORIZATION"
  * value:
    * basic (id,password)
    * bearer (token)
    *
*
*

## Verify the token server side

*
*
*

# articles

## https://en.wikipedia.org/wiki/Web_storage

* exclusively available for client side scripting (not by servers), greater storage capacity than cookies
  * local : per origin (the combination of protocol, hostname, and port number) 
  * session : per-origin-per-window-or-tab
* [where stored](https://stackoverflow.com/questions/8634058/where-the-sessionstorage-and-localstorage-stored)
  so on the file system
  
Cookie limits: 4kb, 15-20 [stackoverflow](https://stackoverflow.com/questions/17882647/can-user-disable-html5-sessionstorage)

## [a white-paper](http://technicalinfo.net/papers/WebBasedSessionManagement.html) discusses pro's and cons of

* URL-based session-id’s
* Hidden post fields (requires more skills for hijacking, accidental support of get breaks this, more complex pages)
* Cookies (can be switched off)

more on https://www.owasp.org/index.php/Session_Management_Cheat_Sheet

## critical articles

### https://hueniverse.com/oauth-bearer-tokens-are-a-terrible-idea-1a300fd12e13

* cookie or cash: owned by whoever has it in its pocket, source unsure
* “Bearer tokens have the same security properties of cookie authentication, as both use plaintext strings without secrets or signatures. However, ...”
* separate the ‘how to get a token’ part from the ‘how to use a token’ part

### http://cryto.net/~joepie91/blog/2016/06/13/stop-using-jwt-for-sessions/

* The correct comparisons are "sessions vs. JWT" and "cookies vs. Local Storage".
* signing in no way unique to JWT
* any kind of session implementation will be interceptable if you don't use TLS, including JWT
* Local Storage: not vulnerable to CSRF attacks ... requires JavaScript ... potentially worse class of vulnerabilities.
* stateless JWT tokens, where all the data is encoded directly into the token
* sessions - can be invalidated by the server ... individual stateless JWT tokens cannot be invalidated

* JWT usecases: single-use authorization token
  * The tokens are short-lived.
  * The token is only expected to be used once.
  * The application server still uses sessions.
* combine sessions and JWT tokens ... Just don't use JWT for persistent, long-lived data.

* [Footnote: microservice architectures](http://cryto.net/%7Ejoepie91/blog/2016/06/19/stop-using-jwt-for-sessions-part-2-why-your-solution-doesnt-work/)
  * Unlike Cookies, any javascript on the page can steal from local storage
  * ... reimplementing sessions ...
  * For a stateless service, there's no session at all, so you simply have the application server hand out short-lived, single-use tokens for each individual authorized operation.

### conclusion

To prevent authentication between fetching a draft dataset end submitting again, uses sessions.
The other servlet can still remain stateless.
But why does the example still store something in a cookie?


## https://stormpath.com/blog/secure-your-rest-api-right-way

* I'm getting confused: is OAuth a protocol or delegation like using the institution account for the current easy-web-ui?
*
*

## scentry = scalatra + sentry

*  https://cartalyst.com/manual/sentry/2.1
  * framework agnostic
  * ldap like functionality but also throttling
* succeeded by https://github.com/cartalyst/sentinel
  * php
   


# sample code / libraries

http://scalatra.org/guides/2.6/http/authentication.html
https://www.eclipse.org/jetty/documentation/9.3.x/session-management.html


## committed code

* mimics a subset of https://github.com/scalatra/scalatra-website-examples/tree/master/2.6/http/authentication-demo
* not yet: remember-me / expiration
* applies sessions, so service not stateless (but it is just one servlet that takes care of the session)
* learnt to apply scentry strategies

## org.scalatra.BasicAuthStrategy [class](https://github.com/scalatra/scalatra/blob/2.3.x/auth/src/main/scala/org/scalatra/auth/strategy/BasicAuthStrategy.scala)

* AUTHORIZATION_KEYS = List("Authorization", "HTTP_AUTHORIZATION", "X-HTTP_AUTHORIZATION", "X_HTTP_AUTHORIZATION")
* these KEYS are headers of the request
* part before first space should be "basic" rest should be <username>:<password>
* refers to https://gist.github.com/casualjim/732347
   (also applies from/to-session as in the example used for the committed example, which handle session cookies)

## http://lollyrock.com/articles/scalatra-bearer-authentication/

* authentication service issuing tokens
* other services ask the authentication service to verify the token
* token stored in (session) cookie ?
* comparing with BasicAuthStrategy: "basic" is replaced with bearer

## react/redux https://www.youtube.com/watch?v=tIajENrOJ0o

redux store = local storage 

15) Authentication: Login Form
   commit [Sign up form and its state](https://github.com/Remchi/reddice/commit/37f548c43316dd7d3b7ed4b75b87580748deb31a)
16) Authentication: Verify User Credentials
  client side handling of server response on authentication (stores username + token ?)
  [commit](https://github.com/Remchi/reddice/commit/4962a3de593dedb0f06e3a9fd9928850ea1e6347)
  looks to me like how to handle service response on client side (or was it 17?)
17) Authentication: Use JWT on Client
  [Use jwt on client and set current user in redux store](https://github.com/Remchi/reddice/commit/4babaf22bef65e779603b60d4f7e09f97949533e)
18) Authentication: Login and Logout on Client
  So far used local session storage to store token and created a tool bar with a logout link for users, login/sign-up for guests
  [Login and logout on client ](https://github.com/Remchi/reddice/commit/260f377b6eeae80a0216cdd54b95015ab7962874) authAction logout + navbar
19) Authentication: Verify JWT on Server with Express Middleware
  * at about 4:00 starts the equivalent of the before/strategy of committed code
  * at 8:25 json web-tokens are introduced
  * at 10:30 [jwt.verfy](https://github.com/Remchi/reddice/blob/5bcde44a753fdea31be552a52affff099e3d268b/server/middlewares/authenticate.js#L14)? Where went the configured secret in earlier sessions?
  scala: [jwt-core](http://pauldijou.fr/jwt-scala/samples/jwt-core/)
  where to get a private key
  [Authenticate express middleware](https://github.com/Remchi/reddice/commit/5bcde44a753fdea31be552a52affff099e3d268b)
20) Protect Client-Side Routes with Higher Order Component
  listened until 6:50 is equivalent of the implemented before


