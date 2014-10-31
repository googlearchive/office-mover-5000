//
//  AddItemCell.swift
//  OfficeMover500
//
//  Created by Katherine Fang on 10/30/14.
//  Copyright (c) 2014 Firebase. All rights reserved.
//
import UIKit

class AddItemCell : UITableViewCell {
    
    var name: String?
    
    override func setHighlighted(highlighted: Bool, animated: Bool) {
        if highlighted {
            backgroundColor = BorderBlue
        } else {
            backgroundColor = UIColor.whiteColor()
        }
        
        if let imageName = name {
            let selected = highlighted ? "_selected.png" : "_unselected.png"
            imageView.image = UIImage(named: "\(imageName)\(selected)")
        }
    }
}
