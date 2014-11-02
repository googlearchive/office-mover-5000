//
//  ChangeBackgroundController.swift
//  OfficeMover500
//
//  Created by Katherine Fang on 10/30/14.
//  Copyright (c) 2014 Firebase. All rights reserved.
//
import UIKit

@objc protocol ChangeBackgroundDelegate : PopoverMenuDelegate {
    func setBackgroundLocally(type: String)
    optional func setBackground(type: String)
}

class ChangeBackgroundController : PopoverMenuController {
    
    var delegate: ChangeBackgroundDelegate?
    
    override var numItems: Int { return Floors.count }
    
    override func dismissIOS7Popover() {
        delegate?.dismissPopover()
    }
    
    override func populateCell(cell: PopoverMenuItemCell, row: Int) {
        cell.textLabel.text = Floors[row].0
        cell.name = Floors[row].1
        let imageName = Floors[row].1
        cell.imageView.image = UIImage(named: "\(imageName)_unselected.png")
    }
    
    override func tableView(tableView: UITableView, didSelectRowAtIndexPath indexPath: NSIndexPath) {
        var type = Floors[indexPath.row].1
        
        // Set background on Firebase
        delegate?.setBackground?(type)

        // Actually change background locally
        delegate?.setBackgroundLocally(type)
        
        dismissPopover()
    }
}