//
//  ViewController.swift
//  OfficeMover500
//
//  Created by Katherine Fang on 10/28/14.
//  Copyright (c) 2014 Firebase. All rights reserved.
//

import UIKit

let OfficeMoverFirebaseUrl = "https://<your-firebase>.firebaseio.com"

class ViewController: RoomViewController {
    
    let ref = Firebase(url: OfficeMoverFirebaseUrl)
    let furnitureRef = Firebase(url: "\(OfficeMoverFirebaseUrl)/furniture")
    let backgroundRef = Firebase(url: "\(OfficeMoverFirebaseUrl)/background")
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        // Load the furniture items from Firebase
        furnitureRef.observeEventType(.ChildAdded, withBlock: { [unowned self] snapshot in
            self.refLocations += ["furniture/\(snapshot.key)"]
            let furniture = Furniture(snap: snapshot)
            self.createFurnitureView(furniture)
        })
        
        // Observe bacakground changes
        backgroundRef.observeEventType(.Value, withBlock: { [unowned self] snapshot in
            if let background = snapshot.value as? String {
                self.setBackgroundLocally(background)
            }
        })
    }
    
    // This should take in a Furniture Model whatever that is.
    // This creates a view as a button, and makes it draggable.
    func createFurnitureView(furniture: Furniture) {
        let view = FurnitureView(furniture: furniture)
        
        let currentFurnitureRef = furnitureRef.childByAppendingPath(furniture.key)

        // move the view from a remote update
        currentFurnitureRef.observeEventType(.Value, withBlock: { snapshot in
            if snapshot.exists() {
                let furniture = Furniture(snap: snapshot)
                view.setViewState(furniture)
            } else {
                view.delete()
            }
        })
        
        
        // When the furniture moves, update the Firebase
        view.moveHandler = { top, left in
            currentFurnitureRef.updateChildValues([
                "top": top,
                "left": left
            ])
        }
        
        // When the furniture rotates, update the Firebase
        view.rotateHandler = { top, left, rotation in
            currentFurnitureRef.updateChildValues([
                "top": top,
                "left": left,
                "rotation": rotation
            ])
        }
        
        // When the furniture is deleted, update the Firebase
        view.deleteHandler = {
            view.delete()
            currentFurnitureRef.removeValue()
        }
        
        // For desks, when we edit the name on the desk, update the Firebase
        view.editHandler = { name in
            currentFurnitureRef.updateChildValues([
                "name": name
            ])
        }
        
        // When furniture is moved, it should jump to the top
        view.moveToTopHandler = {
            currentFurnitureRef.updateChildValues([
                "z-index": ++maxZIndex
            ])
        }
        
        roomView.addSubview(view)
    }
    
    // Handling adding a new item from the popover menu
    func addNewItem(type: String) {
        let itemRef = furnitureRef.childByAutoId()
        let furniture = Furniture(key: itemRef.key, type: type)
        itemRef.setValue(furniture.toJson())
    }

    // Handling changing the background from the popover menu
    func setBackground(type: String) {
        backgroundRef.setValue(type)
    }
}