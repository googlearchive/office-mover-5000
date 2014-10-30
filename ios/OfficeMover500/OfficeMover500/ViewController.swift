//
//  ViewController.swift
//  OfficeMover500
//
//  Created by Katherine Fang on 10/28/14.
//  Copyright (c) 2014 Firebase. All rights reserved.
//

import UIKit

class ViewController: RoomViewController {
    
    let ref = Firebase(url: "https://office-mover.firebaseio.com/")
    let furnitureRef = Firebase(url: "https://office-mover.firebaseio.com/furniture")
    var room = Room(json: nil)
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        // load the furniture items from Firebase
        furnitureRef.observeEventType(.ChildAdded, withBlock: { snapshot in
            var furniture = Furniture(snap: snapshot)
            self.room.addFurniture(furniture)
            self.createFurnitureView(furniture)
        })
    }
    
    // This should take in a Furniture Model whatever that is.
    // This creates a view as a button, and makes it draggable.
    func createFurnitureView(furniture: Furniture) {
        let view = FurnitureView(furniture: furniture)
        
        let currentFurnitureRef = furnitureRef.childByAppendingPath(furniture.key)

        // move the view from a remote update
        currentFurnitureRef.observeEventType(.Value, withBlock: { snapshot in
            
            // check if snapshot.value does not equal NSNull
            if snapshot.value as? NSNull != NSNull() {
                var furniture = Furniture(snap: snapshot)
                view.top = furniture.top
                view.left = furniture.left
                view.rotation = furniture.rotation
                view.name = furniture.name
            }
        })
        
        // delete the view from remote update
        currentFurnitureRef.observeEventType(.ChildRemoved, withBlock: { snapshot in
            view.delete()
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
        
        roomView.addSubview(view)
    }
}