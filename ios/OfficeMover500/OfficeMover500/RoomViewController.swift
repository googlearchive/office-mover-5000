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
    @IBOutlet weak var addItemButton: UIBarButtonItem!
    @IBOutlet weak var backgroundButton: UIBarButtonItem!
    
    var closePopover: (() -> ())?
    var popoverController: UIPopoverController?
    
    override func viewWillAppear(animated: Bool) {
        super.viewWillAppear(animated)
        
        roomView.layer.borderColor = BorderBlue.CGColor
        roomView.layer.borderWidth = 4
        
        var nav = self.navigationController?.navigationBar
        nav?.barTintColor = TopbarBlue
        nav?.barStyle = UIBarStyle.Default
        nav?.tintColor = UIColor.whiteColor()
        var font: UIFont = UIFont(name: "Proxima Nova", size: 20)!
        nav?.titleTextAttributes = [NSForegroundColorAttributeName: UIColor.whiteColor(), NSFontAttributeName:font]
        
        navigationItem.leftBarButtonItems = [addItemButton, backgroundButton]
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
            roomView.backgroundColor = UIColor(patternImage: image)
        }
    }
}