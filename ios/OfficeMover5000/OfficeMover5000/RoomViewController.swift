//
//  ViewController.swift
//  OfficeMover500
//
//  Created by Katherine Fang on 10/28/14.
//  Copyright (c) 2014 Firebase. All rights reserved.
//

import UIKit

class RoomViewController: UIViewController, UIPopoverControllerDelegate, AddNewItemDelegate, ChangeBackgroundDelegate {
    
    @IBOutlet weak var roomView: UIView!
    @IBOutlet weak var layoutView: UIView!
    @IBOutlet weak var backgroundView: UIView!
    @IBOutlet weak var addItemButton: UIBarButtonItem!
    @IBOutlet weak var backgroundButton: UIBarButtonItem!
    @IBOutlet weak var logoutButton: UIBarButtonItem!
    
    @IBAction func logout(sender: AnyObject) {
        let ref = Firebase(url: OfficeMoverFirebaseUrl)
        ref.unauth()
        self.performSegueWithIdentifier("LOGGED_OUT", sender: self)
    }
    
    var closePopover: (() -> ())?
    var popoverController: UIPopoverController?
    
    override func viewWillAppear(animated: Bool) {
        super.viewWillAppear(animated)
        
        layoutView.backgroundColor = UIColor(patternImage: UIImage(named: "office.png")!)
        
        var nav = self.navigationController?.navigationBar
        nav?.barTintColor = TopbarBlue
        nav?.barStyle = UIBarStyle.Default
        nav?.tintColor = UIColor.whiteColor()
        var font: UIFont = UIFont(name: "ProximaNova-Light", size: 20)!
        nav?.titleTextAttributes = [NSForegroundColorAttributeName: UIColor.whiteColor(), NSFontAttributeName:font]
        
        navigationItem.leftBarButtonItems = [addItemButton, backgroundButton]
        navigationItem.setHidesBackButton(true, animated: false)
        
        logoutButton.setTitleTextAttributes([NSFontAttributeName:font], forState: UIControlState.Normal)
        
    }
    
    override func prepareForSegue(segue: UIStoryboardSegue, sender: AnyObject?) {
        // If opening a new popover, preemtively close the old one
        popoverController?.dismissPopoverAnimated(false) // iOS 7
        closePopover?() // iOS 8
        
        // This is to support closing popovers in iOS 7 and to set frosted menu
        if let popoverSegue = segue as? UIStoryboardPopoverSegue {
            self.popoverController = popoverSegue.popoverController
            self.popoverController?.backgroundColor = UIColor.whiteColor().colorWithAlphaComponent(0.8)
        }
        
        if let controller = segue.destinationViewController as? AddItemController {
            controller.delegate = self
            closePopover = controller.closePopover // to support iOS 8 preemptive closing
        } else if let controller = segue.destinationViewController as? ChangeBackgroundController {
            controller.delegate = self
            closePopover = controller.closePopover // to support iOS 8 preemptive closing
        }
    }
    
    func dismissPopover() {
        // Close popover in iOS 8
        popoverController?.dismissPopoverAnimated(true)
        popoverController = nil
    }
    
    func setBackgroundLocally(type: String) {
        if let image = UIImage(named:"\(type).png") {
            backgroundView.backgroundColor = UIColor(patternImage: image)
        }
    }
}