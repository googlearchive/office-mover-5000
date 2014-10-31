//
//  AddItemController.swift
//  OfficeMover500
//
//  Created by Katherine Fang on 10/30/14.
//  Copyright (c) 2014 Firebase. All rights reserved.
//
import UIKit

@objc protocol AddNewItemDelegate {
    optional func addNewItem(type: String)
}

class AddItemController : UITableViewController {
    
    var delegate: AddNewItemDelegate?
 
    override func numberOfSectionsInTableView(tableView: UITableView) -> Int {
        return 1
    }
    
    override func tableView(tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return Items.count
    }
    
    override func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> UITableViewCell {
        var cell: PopoverMenuItemCell! = tableView.dequeueReusableCellWithIdentifier("menuItemCell") as? PopoverMenuItemCell
        if cell == nil {
            cell = PopoverMenuItemCell(style: .Default, reuseIdentifier: "menuItemCell")
        }
        
        // Definitely populated now
        cell.textLabel.text = Items[indexPath.row].0
        cell.name = Items[indexPath.row].1
        let imageName = Items[indexPath.row].1
        cell.imageView.image = UIImage(named: "\(imageName)_unselected.png")
        
        return cell
    }

    override func tableView(tableView: UITableView, didSelectRowAtIndexPath indexPath: NSIndexPath) {
        var type = Items[indexPath.row].1
        if let multiType = ItemTypes[type] {
            type = multiType[Int(arc4random_uniform(UInt32(multiType.count)))]
        }
        
        delegate?.addNewItem?(type)
        dismissViewControllerAnimated(true, completion: nil)
    }
}