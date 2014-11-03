//
//  LoginViewController.swift
//  OfficeMover500
//
//  Created by David on 10/31/14.
//  Copyright (c) 2014 Firebase. All rights reserved.
//

import UIKit

class LoginViewController: UIViewController, GPPSignInDelegate {

    let ref = Firebase(url: OfficeMoverFirebaseUrl)
    var authData: FAuthData?
    var authHandler: UInt!
    
    @IBOutlet var btLogin: UIButton!
    
    override func viewWillAppear(animated: Bool) {
        super.viewWillAppear(animated)
        
        // Automatically log in when we are auth'd
        authHandler = ref.observeAuthEventWithBlock({
            [unowned self] authData in
            if authData != nil {
                self.ref.removeAuthEventObserverWithHandle(self.authHandler)
                self.performSegueWithIdentifier("LOGGED_IN", sender: self)
            }
        })
    }
    
    @IBAction func login(sender: AnyObject) {
        var signIn = GPPSignIn.sharedInstance()
        signIn.shouldFetchGooglePlusUser = true
        signIn.clientID = "311395164163-bhjoq6cb43hh1n92l7ntb8180uplbcll.apps.googleusercontent.com"
        signIn.scopes = [ kGTLAuthScopePlusLogin ]
        signIn.delegate = self
        // authenticate will do a callback to finishedWithAuth:error:
        signIn.authenticate()
    }
    
    func finishedWithAuth(auth: GTMOAuth2Authentication!, error: NSError!) {
        if error != nil {
            // There was an error obtaining the Google+ OAuth Token
        } else {
            // We successfully obtained an OAuth token, authenticate on Firebase with it
            ref.authWithOAuthProvider("google", token: auth.accessToken,
                withCompletionBlock: { error, authData in
                    if error != nil {
                        // Error authenticating with Firebase with OAuth token
                    } else {
                        // User is now logged in!
                        println("Successfully logged in! \(authData)")
                    }
            })
        }
    }
}