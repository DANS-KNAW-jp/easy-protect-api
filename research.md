# Wrapping up

## Our use case: 

Prevent the need to re-enter user-name and password between for example
fetching the metadata of draft dataset and submitting again.

## Approach:

Our JWT tokens contain only identification and are stored in cookies.
Reissue a token after each server interaction and expire after 15 minutes, [however](#2010).
N.B: form-data should not get lost when time to submit exceeds expiration.
Low priority (if at all) for additional server-side token validation:
use ldap to verify the user found in the token,
in case it got blocked since issuing the token.

## Not yet discussed

* Choice between scala's [jwt-core] (issues and PRs show more activity) and [java-jwt]
* How to create/configure the secret needed for the SWT
* [nonce] and [digest authentication]

[jwt-core]: http://pauldijou.fr/jwt-scala/samples/jwt-core/
[java-jwt]: https://github.com/auth0/java-jwt
[nonce]: https://en.wikipedia.org/wiki/Cryptographic_nonce
[digest authentication]: https://en.wikipedia.org/wiki/Digest_access_authentication

# Research Notes


## Breaking down the problem

### Create a token

* server side?
* signed?
*
*

### Keep track of the token client side

* cookies
* local storage
  * session: what if the user want's to display info in two windows/tabs in parallel?
* hidden form fields
*
*

### Exchange tokens between client/server

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

### Verify the token server side

*
*
*

## articles

### https://en.wikipedia.org/wiki/Web_storage

* exclusively available for client side scripting (not by servers), greater storage capacity than cookies
  * local : per origin (the combination of protocol, hostname, and port number) 
  * session : per-origin-per-window-or-tab
* [where stored](https://stackoverflow.com/questions/8634058/where-the-sessionstorage-and-localstorage-stored)
  so on the file system
  
Cookie limits: 4kb, 15-20 [stackoverflow](https://stackoverflow.com/questions/17882647/can-user-disable-html5-sessionstorage)

### [a white-paper](http://technicalinfo.net/papers/WebBasedSessionManagement.html) discusses pro's and cons of

* URL-based session-id’s
* Hidden post fields (requires more skills for hijacking, accidental support of get breaks this, more complex pages)
* Cookies (can be switched off)

more on https://www.owasp.org/index.php/Session_Management_Cheat_Sheet

### critical articles

#### <a name="2010"/>2010 https://hueniverse.com/oauth-bearer-tokens-are-a-terrible-idea-1a300fd12e13

* cookie or cash: owned by whoever has it in its pocket, source unsure
* bearer tokens rely solely on SSL/TLS for its security.
* SSL/TLS is Harder than You Think
  * man-in-the-middle: If anyone can get between your client and the server, they can capture the token and use it at will.
  * The server has no way of enforcing certificate verification
* Signatures ensure that mistakes don’t compromise the token... At most, an attacker can use the captured request once
* “Bearer tokens have the same security properties of cookie authentication, as both use plaintext strings without secrets or signatures. However, ...”
* separate the ‘how to get a token’ part from the ‘how to use a token’ part

#### <a name="2016"/>2016 http://cryto.net/~joepie91/blog/2016/06/13/stop-using-jwt-for-sessions/

* (invalid) claims of JWT
  *

* Drawbacks
  * take up space: we only need identification
  * less secure: in a cookie, it's no different from any other session identifier, elsewhere, you are now vulnerable to a new class of attacks
  * You cannot invalidate individual JWT tokens: we won't be able to analyse and block an attack with short-lived (15 minutes or so) tokens. If really needed verify the user and its state in ldap.
  * Data goes stale: our only data is identification, same as previous point
  * Implementations are less battle-tested or non-existent: 
  
  * Local Storage: not vulnerable to CSRF attacks ... requires JavaScript ... potentially worse class of vulnerabilities.
  * stateless JWT tokens, where all the data is encoded directly into the token
  * sessions - can be invalidated by the server ... individual stateless JWT tokens cannot be invalidated

* JWT usecases
  * Hello Server B, Server A told me that I could <claim goes here>, and here's the (cryptographic) proof.


### https://stormpath.com/blog/secure-your-rest-api-right-way

* I'm getting confused: is OAuth a protocol or delegation like using the institution account for the current easy-web-ui?
*
*

### scentry = scalatra + sentry

*  https://cartalyst.com/manual/sentry/2.1
  * framework agnostic
  * ldap like functionality but also throttling
* succeeded by https://github.com/cartalyst/sentinel
  * php
   


## sample code / libraries

http://scalatra.org/guides/2.6/http/authentication.html
https://www.eclipse.org/jetty/documentation/9.3.x/session-management.html


### committed code

* mimics a subset of https://github.com/scalatra/scalatra-website-examples/tree/master/2.6/http/authentication-demo
* not yet: remember-me / expiration
* applies sessions, so service not stateless (but it is just one servlet that takes care of the session)
* learnt to apply scentry strategies

### org.scalatra.BasicAuthStrategy [class](https://github.com/scalatra/scalatra/blob/2.3.x/auth/src/main/scala/org/scalatra/auth/strategy/BasicAuthStrategy.scala)

* AUTHORIZATION_KEYS = List("Authorization", "HTTP_AUTHORIZATION", "X-HTTP_AUTHORIZATION", "X_HTTP_AUTHORIZATION")
* these KEYS are headers of the request
* part before first space should be "basic" rest should be <username>:<password>
* refers to https://gist.github.com/casualjim/732347
   (also applies from/to-session as in the example used for the committed example, which handle session cookies)

### http://lollyrock.com/articles/scalatra-bearer-authentication/

* authentication service issuing tokens
* other services ask the authentication service to verify the token
* token stored in (session) cookie ?
* comparing with BasicAuthStrategy: "basic" is replaced with bearer

### react/redux https://www.youtube.com/watch?v=tIajENrOJ0o

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

  [Authenticate express middleware](https://github.com/Remchi/reddice/commit/5bcde44a753fdea31be552a52affff099e3d268b)
20) Protect Client-Side Routes with Higher Order Component
  listened until 6:50 is equivalent of the implemented before


