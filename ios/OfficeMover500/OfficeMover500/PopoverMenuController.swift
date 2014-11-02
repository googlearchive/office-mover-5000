//
//  PopoverMenuController.swift
//  OfficeMover500
//
//  Created by Katherine Fang on 10/30/14.
//  Copyright (c) 2014 Firebase. All rights reserved.
//
import UIKit

@objc protocol PopoverMenuDelegate {
    func dismissPopover()
}

class PopoverMenuController : UITableViewController {
    
    
    override func viewWillAppear(animated: Bool) {
        super.viewWillAppear(animated)
        view.superview?.layer.cornerRadius = 0
        view.backgroundColor = UIColor.clearColor()
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        preferredContentSize.height = 70 * CGFloat(numItems)
    }
    
    override func tableView(tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return numItems
    }
    
    override func numberOfSectionsInTableView(tableView: UITableView) -> Int {
        return 1
    }
    
    override func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> UITableViewCell {
        var cell: PopoverMenuItemCell! = tableView.dequeueReusableCellWithIdentifier("menuItemCell") as? PopoverMenuItemCell
        if cell == nil {
            cell = PopoverMenuItemCell(style: .Default, reuseIdentifier: "menuItemCell")
        }
        
        // Definitely exists now
        populateCell(cell, row: indexPath.row)
        return cell
    }
    
    override func tableView(tableView: UITableView, heightForRowAtIndexPath indexPath: NSIndexPath) -> CGFloat {
        return 70
    }
    
    // Animated
    func dismissPopover() {
        dismissIOS7Popover()
        dismissViewControllerAnimated(true, nil) // iOS 8
    }
    
    func closePopover() {
        dismissViewControllerAnimated(false, nil)
    }
    
    // Override this in subclass
    var numItems: Int { return 0 }
    
    // Override this in subclass
    func dismissIOS7Popover() {}
    
    // Override this in subclass
    func populateCell(cell: PopoverMenuItemCell, row: Int) {}
}