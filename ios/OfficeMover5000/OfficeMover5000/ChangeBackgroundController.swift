//
//  ChangeBackgroundController.swift
//  OfficeMover500
//
//  Created by Katherine Fang on 10/30/14.
//  Copyright (c) 2014 Firebase. All rights reserved.
//
import UIKit

class ChangeBackgroundController : PopoverMenuController {
        
    override var numItems: Int { return Floors.count }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        // Adding a gutter to the menu
        tableView.contentInset = UIEdgeInsetsMake(15, 0, 15, 15);
        preferredContentSize.height += 30

    }
    
    // Set the cell with an image and the text
    override func populateCell(cell: PopoverMenuItemCell, row: Int) {
        cell.textLabel?.text = Floors[row].0
        cell.name = Floors[row].1
        let imageName = Floors[row].1
        if let image = UIImage(named: "\(imageName)_unselected.png") {
            cell.imageView?.image = image
        } else {
            // Create blank image ths size of wood
            if let woodImage = UIImage(named: "wood_unselected.png") {
                UIGraphicsBeginImageContextWithOptions(woodImage.size, false, 0.0)
                let blankImage = UIGraphicsGetImageFromCurrentImageContext()
                UIGraphicsEndImageContext()
                cell.imageView?.image = blankImage
            }
        }
    }
    
    // When selected, add an item using the delegate, and dismiss the popover
    override func tableView(tableView: UITableView, didSelectRowAtIndexPath indexPath: NSIndexPath) {
        var type = Floors[indexPath.row].1
        
        // Set background on Firebase
        delegate?.setBackground?(type)

        // Actually change background locally
        delegate?.setBackgroundLocally(type)
        dismissPopover(true)
    }
}