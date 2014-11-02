//
//  AddItemCell.swift
//  OfficeMover500
//
//  Created by Katherine Fang on 10/30/14.
//  Copyright (c) 2014 Firebase. All rights reserved.
//
import UIKit

class PopoverMenuItemCell : UITableViewCell {
    
    var name: String?
    
    override func setHighlighted(highlighted: Bool, animated: Bool) {
        if highlighted {
//            backgroundColor = BorderBlue.colorWithAlphaComponent(0.5)
        } else {
//            backgroundColor = UIColor.whiteColor().colorWithAlphaComponent(0.5)
        }
        backgroundColor = UIColor.clearColor()
        
        if let imageName = name {
            let selected = highlighted ? "_selected.png" : "_unselected.png"
            if let image = UIImage(named: "\(imageName)\(selected)") {
                imageView.image = image
            }
        }
    }
}