//
//  AddItemController.swift
//  OfficeMover500
//
//  Created by Katherine Fang on 10/30/14.
//  Copyright (c) 2014 Firebase. All rights reserved.
//
import UIKit

@objc protocol AddNewItemDelegate : PopoverMenuDelegate {
    optional func addNewItem(type: String)
}

class AddItemController : PopoverMenuController {
    
    var delegate: AddNewItemDelegate?
    
    override var numItems: Int { return Items.count }
    
    override func dismissIOS7Popover() {
        delegate?.dismissPopover()
    }
    
    override func populateCell(cell: PopoverMenuItemCell, row: Int) {
        cell.textLabel.text = Items[row].0
        cell.name = Items[row].1
        let imageName = Items[row].1
        cell.imageView.image = UIImage(named: "\(imageName)_unselected.png")
    }
    
    override func tableView(tableView: UITableView, didSelectRowAtIndexPath indexPath: NSIndexPath) {
        var type = Items[indexPath.row].2
        delegate?.addNewItem?(type)
        dismissPopover()
    }
}