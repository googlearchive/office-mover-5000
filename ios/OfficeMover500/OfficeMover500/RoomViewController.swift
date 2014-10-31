//
//  ViewController.swift
//  OfficeMover500
//
//  Created by Katherine Fang on 10/28/14.
//  Copyright (c) 2014 Firebase. All rights reserved.
//

import UIKit

class RoomViewController: UIViewController, UIPopoverControllerDelegate, AddNewItemDelegate {
    
    @IBOutlet weak var roomView: UIView!
    @IBOutlet weak var addItemButton: UIBarButtonItem!
    @IBOutlet weak var backgroundButton: UIBarButtonItem!
    
    override func viewWillAppear(animated: Bool) {
        super.viewWillAppear(animated)
        
        roomView.layer.borderColor = BorderBlue.CGColor
        roomView.layer.borderWidth = 4
        
        var nav = self.navigationController?.navigationBar
        nav?.barTintColor = TopbarBlue
        nav?.barStyle = UIBarStyle.Default
        nav?.tintColor = UIColor.whiteColor()
        var font: UIFont = UIFont(name: "ProximaNova-Light", size: 20)!
        nav?.titleTextAttributes = [NSForegroundColorAttributeName: UIColor.whiteColor(), NSFontAttributeName:font]
        
        navigationItem.leftBarButtonItems = [addItemButton, backgroundButton]
    }
    
    override func prepareForSegue(segue: UIStoryboardSegue, sender: AnyObject?) {
        if segue.identifier == "addItemPopoverSegue" {
            if let popoverController = segue.destinationViewController as? AddItemController {
                popoverController.delegate = self
            }
        }
    }
}