//
//  FurnitureButton.swift
//  OfficeMover500
//
//  Created by Katherine Fang on 10/28/14.
//  Copyright (c) 2014 Firebase. All rights reserved.
//

import Foundation
import UIKit

class FurnitureButton : UIButton {
    
    required init(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
    }
    
    override init() {
        super.init(frame: CGRectMake((CGFloat(RoomWidth)-100)/2, (CGFloat(RoomHeight)-100)/2, 100, 100))
        backgroundColor = UIColor.redColor()
        self.addTarget(self, action:Selector("dragged:withEvent:"), forControlEvents:.TouchDragInside | .TouchDragOutside)
    }

    func dragged(button: UIButton, withEvent event: UIEvent) {
        // Get the Touch
        if let touch = event.touchesForView(button)?.anyObject() as? UITouch {
            let touchLoc = touch.locationInView(self.superview) // CGPoint
            button.center = boundLocToRoom(touchLoc)
        }
        // Get delta?
    }
    
    func boundLocToRoom(loc: CGPoint) -> CGPoint {
        var pt = CGPointMake(loc.x, loc.y)
        
        // Bound x inside of width
        if loc.x < frame.size.width / 2 {
            pt.x = frame.size.width / 2
        } else if loc.x > CGFloat(RoomWidth) - frame.size.width / 2 {
            pt.x = CGFloat(RoomWidth) - frame.size.width / 2
        }

        // Bound y inside of height
        if loc.y < frame.size.height / 2 {
            pt.y = frame.size.height / 2
        } else if loc.y > CGFloat(RoomHeight) - frame.size.height / 2 {
            pt.y = CGFloat(RoomHeight) - frame.size.height / 2
        }
        
        return pt
    }
}