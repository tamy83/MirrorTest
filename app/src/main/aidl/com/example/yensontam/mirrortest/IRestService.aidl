// IRestService.aidl
package com.example.yensontam.mirrortest;

// Declare any non-default types here with import statements

interface IRestService {
/**
* POST  	/users
  	Creates new user, no jwt token required
  POST  	/auth
  	Returns jwt token
   GET   		/users/:id
  	Requires jwt token passed as header
  	"Authorization" "JWT <jwt token>"
  	So header of Authorization and value of "JWT" plus a space plus the jwt token returned from /auth
   PATCH 	/users/:id
  	Requires jwt token passed as header

*/
    void create(String username, String password);
    void login(String username, String password);

}
