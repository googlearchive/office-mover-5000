//
//  ChangeBackgroundController.swift
//  OfficeMover500
//
//  Created by Katherine Fang on 10/30/14.
//  Copyright (c) 2014 Firebase. All rights reserved.
//
import UIKit

@objc protocol ChangeBackgroundDelegate {
    func changeBackground(type: String)
    func dismissPopover()
}

class ChangeBackgroundController : UITableViewController {
    
    var delegate: ChangeBackgroundDelegate?
    
    override func numberOfSectionsInTableView(tableView: UITableView) -> Int {
        return 1
    }
    
    override func tableView(tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return Floors.count
    }
    
    override func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> UITableViewCell {
        var cell: PopoverMenuItemCell! = tableView.dequeueReusableCellWithIdentifier("menuItemCell") as? PopoverMenuItemCell
        if cell == nil {
            cell = PopoverMenuItemCell(style: .Default, reuseIdentifier: "menuItemCell")
        }
        
        // Definitely populated now
        cell.textLabel.text = Floors[indexPath.row].0
        cell.name = Floors[indexPath.row].1
        let imageName = Floors[indexPath.row].1
        cell.imageView.image = UIImage(named: "\(imageName)_unselected.png")
        
        return cell
    }
    
    override func tableView(tableView: UITableView, didSelectRowAtIndexPath indexPath: NSIndexPath) {
        var type = Floors[indexPath.row].1
        
        delegate?.changeBackground(type)
        delegate?.dismissPopover()
    }
}