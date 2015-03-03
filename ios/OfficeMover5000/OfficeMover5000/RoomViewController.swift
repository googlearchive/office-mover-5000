//
//  ViewController.swift
//  OfficeMover500
//
//  Created by Katherine Fang on 10/28/14.
//  Copyright (c) 2014 Firebase. All rights reserved.
//

import UIKit

class RoomViewController: UIViewController, UIPopoverControllerDelegate, PopoverMenuDelegate {
    
    @IBOutlet weak var roomView: UIView!
    @IBOutlet weak var layoutView: UIView!
    @IBOutlet weak var backgroundView: UIView!
    @IBOutlet weak var addItemButton: UIBarButtonItem!
    @IBOutlet weak var backgroundButton: UIBarButtonItem!
    @IBOutlet weak var logoutButton: UIBarButtonItem!
    
    var refLocations = ["furniture", "background"] // Array of all locations that we'll need to remove observers for
    var popoverController: UIPopoverController? // For supporting iOS 7 popover close
    var popoverMenuController: PopoverMenuController? // For supporting iOS 8 popover close
    
    override func viewWillAppear(animated: Bool) {
        super.viewWillAppear(animated)
        
        // Set office blueprint
        layoutView.backgroundColor = UIColor(patternImage: UIImage(named: "office.png")!)
        
        // Set menu buttons on left
        navigationItem.leftBarButtonItems = [addItemButton, backgroundButton]
        navigationItem.setHidesBackButton(true, animated: false)
        
        // Set logout button on right
        logoutButton.setTitleTextAttributes([NSFontAttributeName:ProximaNovaLight20], forState: UIControlState.Normal)
        
        // Attach listener for logged out
        let ref = Firebase(url: OfficeMoverFirebaseUrl)
        ref.observeAuthEventWithBlock({ [unowned self]
            authData in
            if authData == nil {
                // Not logged in, so let's explicitly transition to logout
                self.logout()
            }
        })
    }
    
    // Set delegate to handle menu actions and make sure only one popover is open
    override func prepareForSegue(segue: UIStoryboardSegue, sender: AnyObject?) {
        // If opening a new popover, preemtively close the old one
        popoverMenuController?.dismissPopover(false)
        
        // This is to support popovers in iOS 7
        if let popoverSegue = segue as? UIStoryboardPopoverSegue {
            popoverController = popoverSegue.popoverController
            popoverController?.backgroundColor = UIColor.whiteColor().colorWithAlphaComponent(0.8)
        }
        
        // Set delegate to handle menu actions
        if let controller = segue.destinationViewController as? AddItemController {
            controller.delegate = self
            popoverMenuController = controller
        } else if let controller = segue.destinationViewController as? ChangeBackgroundController {
            controller.delegate = self
            popoverMenuController = controller
        }
    }
    
    // close popover in iOS 7
    func dismissPopover(animated: Bool) {
        popoverController?.dismissPopoverAnimated(animated)
        popoverController = nil
    }
    
    // Set the background
    func setBackgroundLocally(type: String) {
        if let image = UIImage(named:"\(type).png") {
            backgroundView.backgroundColor = UIColor(patternImage: image)
        } else {
            backgroundView.backgroundColor = UIColor.clearColor()
        }
    }
    
    // Logout from app
    @IBAction func logout(sender: AnyObject) {
        logout()
        
        // Also log out of Google
        GPPSignIn.sharedInstance().signOut()
    }
    
    func logout() {
        // Unauthenticate with Firebase
        let ref = Firebase(url: OfficeMoverFirebaseUrl)
        ref.unauth()
        
        // Remove observers
        for loc in refLocations {
            ref.childByAppendingPath(loc).removeAllObservers()
        }
        
        for view in roomView.subviews {
            if let furnitureView = view as? FurnitureView {
                furnitureView.handleLogout()
            }
        }
        
        // Perform segue
        self.performSegueWithIdentifier("LOGGED_OUT", sender: self)
    }
}