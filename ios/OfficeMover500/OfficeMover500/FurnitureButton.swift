//
//  FurnitureButton.swift
//  OfficeMover500
//
//  Created by Katherine Fang on 10/28/14.
//  Copyright (c) 2014 Firebase. All rights reserved.
//

import Foundation
import UIKit
import QuartzCore

enum DragState {
    case None, Maybe, Dragging
}

class FurnitureButton : UIButton, UIAlertViewDelegate {
    
    // -- Model state handlers
    var moveHandler: ((Int, Int) -> ())?
    var rotateHandler: ((Int, Int, Int) -> ())?
    var deleteHandler: (() -> ())?
    var editHandler: ((String) -> ())?
    
    // Calculated propeties
    var top:Int {
        get {
            return Int(frame.origin.y)
        }
        set (newValue) {
            if newValue < 0 {
                frame.origin.y = 0
            } else if newValue > RoomHeight - Int(frame.size.height) {
                frame.origin.y = CGFloat(RoomHeight) - frame.size.height
            } else {
                frame.origin.y = CGFloat(newValue)
            }
        }
    }
    
    var left:Int {
        get {
            return Int(frame.origin.x)
        }
        set (newValue) {
            if newValue < 0 {
                frame.origin.x = 0
            } else if newValue > RoomWidth - Int(frame.size.width) {
                frame.origin.x = CGFloat(RoomWidth) - frame.size.width
            } else {
                frame.origin.x = CGFloat(newValue)
            }
        }
    }
    
    var rotation:Int {
        get {
            let radians = atan2f(Float(transform.b), Float(transform.a))
            let degrees = radians * Float(180/M_PI)
            switch (degrees) {
            case -90:
                return 90
            case -180, 180:
                return 180
            case 90, -270:
                return 270
            default:
                return 0
            }
        }
        set (newValue) {
            transform = CGAffineTransformMakeRotation(CGFloat(newValue / 90) * CGFloat(M_PI / -2))
        }
    }
    
    var name:String? {
        get {
            return currentTitle
        }
        set (newValue) {
            setTitle(newValue, forState:.Normal)
        }
    }
    
    // --- Handling UI state
    var dragging = DragState.None
    var menuShowing = false
    let type: String
    var startDown: CGPoint?
    private var menuListener: AnyObject?
    
    required init(coder aDecoder: NSCoder) {
        type = "desk"
        super.init(coder: aDecoder)
    }
    
    init(furniture: Furniture) {
        type = furniture.type == "plant" ? "plant1" : furniture.type // hack because we have 2 plant images
        super.init(frame: CGRectMake((CGFloat(RoomWidth)-100)/2, (CGFloat(RoomHeight)-100)/2, 10, 10))

        // Setup image and size
        let image = UIImage(named:"\(type).png")
        setBackgroundImage(image, forState:.Normal)
        frame.size = image!.size
        
        // Setup other properties
        name = furniture.name
        rotation = furniture.rotation
        top = furniture.top
        left = furniture.left
        
        // Add dragability
        addTarget(self, action:Selector("dragged:withEvent:"), forControlEvents:.TouchDragInside | .TouchDragOutside)
        
        // Add tap menu
        addTarget(self, action:Selector("touchDown:withEvent:"), forControlEvents:.TouchDown)
        addTarget(self, action:Selector("touchUp:withEvent:"), forControlEvents:.TouchUpInside)
    }
    
    // -- Methods for updating the view
    func delete() {
        if menuShowing {
            let menuController = UIMenuController.sharedMenuController()
            menuController.setMenuVisible(false, animated: true)
            menuShowing = false
        }
        if superview != nil {
            removeFromSuperview()
        }
    }
    
    
    // --- Methods for dragging
    func dragged(button: UIButton, withEvent event: UIEvent) {
        temporarilyHideMenu() // Hide the menu while dragging
        
        // Get the touch in view, bound it to the room, and move the button there
        if let touch = event.touchesForView(button)?.anyObject() as? UITouch {
            let touchLoc = touch.locationInView(self.superview)
            if abs(startDown!.x - touchLoc.x) > 10 || abs(startDown!.y - touchLoc.y) > 10 {
                dragging = DragState.Dragging // To avoid triggering tap functionality
            }
            center = boundCenterLocToRoom(touchLoc)
            if let handler = moveHandler {
                handler(top, left)
            }
        }
    }
    
    func boundCenterLocToRoom(centerLoc: CGPoint) -> CGPoint {
        var pt = CGPointMake(centerLoc.x, centerLoc.y)
        
        // Bound x inside of width
        if centerLoc.x < frame.size.width / 2 {
            pt.x = frame.size.width / 2
        } else if centerLoc.x > CGFloat(RoomWidth) - frame.size.width / 2 {
            pt.x = CGFloat(RoomWidth) - frame.size.width / 2
        }
        
        // Bound y inside of height
        if centerLoc.y < frame.size.height / 2 {
            pt.y = frame.size.height / 2
        } else if centerLoc.y > CGFloat(RoomHeight) - frame.size.height / 2 {
            pt.y = CGFloat(RoomHeight) - frame.size.height / 2
        }
        
        return pt
    }
    
    // --- Methods for popping the menu up
    func touchDown(button: UIButton, withEvent event: UIEvent) {
        startDown = center
        dragging = .Maybe
        superview?.bringSubviewToFront(self)
    }
    
    func touchUp(button: UIButton, withEvent event: UIEvent) {
        startDown = nil
        if dragging == .Dragging {
            dragging = .None // This always ends drag events
            if !menuShowing {
                // Don't show menu at the end of dragging if there wasn't a menu to begin with
                return
            }
        }
        
        showMenu()
    }
    
    // --- Edit buttons were clicked
    func triggerRotate(sender: AnyObject) {
        transform = CGAffineTransformRotate(transform, CGFloat(M_PI / -2))
        if let handler = rotateHandler {
            handler(top, left, rotation)
        }
    }
    
    func triggerDelete(sender: AnyObject) {
        if let handler = deleteHandler {
            handler()
        }
    }
    
    func triggerEdit(sender:AnyObject) {
        let alert = UIAlertView(title: "Who sits here?", message: "Enter name below", delegate: self, cancelButtonTitle: "Cancel", otherButtonTitles: "OK")
        alert.alertViewStyle = UIAlertViewStyle.PlainTextInput;
        alert.textFieldAtIndex(0)?.text = name
        alert.textFieldAtIndex(0)?.placeholder = "Name"
        alert.show()
    }
    
    func alertView(alertView: UIAlertView, clickedButtonAtIndex buttonIndex: Int) {
        if buttonIndex == 1 { // This is the ok button, and not the cancel button
            if let newName = alertView.textFieldAtIndex(0)?.text {
                name = newName
                if let handler = editHandler {
                    handler(newName)
                }
            }
        }
    }
    
    // --- Menu helper methods
    
    func showMenu() {
        menuShowing = true
        let menuController = UIMenuController.sharedMenuController()
        
        // Set new menu location
        if superview != nil {
            menuController.setTargetRect(frame, inView:superview!)
        }
        
        // Set menu items
        menuController.menuItems = [
            UIMenuItem(title: "Rotate", action:Selector("triggerRotate:")),
            UIMenuItem(title: "Delete", action:Selector("triggerDelete:"))
        ]
        if type == "desk" {
            menuController.menuItems?.insert(UIMenuItem(title: "Edit", action:Selector("triggerEdit:")), atIndex:0)
        }
        
        // Handle displaying and disappearing the menu
        becomeFirstResponder()
        menuController.setMenuVisible(true, animated: true)
        watchForMenuExited()
    }
    
    // Temporarily
    func temporarilyHideMenu() {
        let menuController = UIMenuController.sharedMenuController()
        menuController.setMenuVisible(false, animated:false)
    }
    
    // Watch for menu exited - handles the menuShowing state for cancels and such
    func watchForMenuExited() {
        if menuListener != nil {
            NSNotificationCenter.defaultCenter().removeObserver(menuListener!)
        }
        
        menuListener = NSNotificationCenter.defaultCenter().addObserverForName(UIMenuControllerWillHideMenuNotification, object:nil, queue: nil, usingBlock: {
            notification in
            if self.dragging == .Dragging {
                self.menuShowing = false
            }
            NSNotificationCenter.defaultCenter().removeObserver(self.menuListener!)
        })
    }
    
    // UIResponder override methods
    override func canBecomeFirstResponder() -> Bool {
        return true
    }
    
    override func canPerformAction(action: Selector, withSender sender: AnyObject?) -> Bool {
        return action == Selector("triggerRotate:") || action == Selector("triggerDelete:") || action == Selector("triggerEdit:")
    }
}

extension FurnitureButton {
    func parentViewController() -> UIViewController? {
        var parentResponder: UIResponder? = self
        while true {
            if parentResponder == nil {
                return nil
            }
            parentResponder = parentResponder!.nextResponder()
            if parentResponder is UIViewController {
                return (parentResponder as UIViewController)
            }
        }
    }
}
