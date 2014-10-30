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
        let view = FurnitureButton(furniture: furniture)

        // move the view from a remote update
        furnitureRef.childByAppendingPath(furniture.key).observeEventType(.Value, withBlock: { snapshot in
            
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
        furnitureRef.childByAppendingPath(furniture.key).observeEventType(.ChildRemoved, withBlock: { snapshot in
            view.delete()
        })
        
        view.moveHandler = { top, left in
            self.moveFurniture(furniture.key, top: top, left: left)
        }
        
        view.rotateHandler = { rotation in
            view.rotation = rotation
            self.rotateFurniture(furniture.key, rotation: rotation, top: view.top, left: view.left)
        }
        
        view.deleteHandler = {
            view.delete()
            self.deleteFurniture(furniture)
        }
        
        view.editHandler = { name in
            self.editFurnitureName(furniture.key, name: name)
        }
        
        roomView.addSubview(view)
    }
    
    // update the top and left to Firebase
    func moveFurniture(key: String, top: Int, left: Int) {
        self.furnitureRef.childByAppendingPath(key).updateChildValues([
            "top": top,
            "left": left
        ])
    }
    
    func rotateFurniture(key: String, rotation: Int, top: Int, left: Int) {
        self.furnitureRef.childByAppendingPath(key).updateChildValues([
            "rotation": rotation,
            "top": top,
            "left": left
        ])
    }
    
    // remove the furniture item in Firebase
    func deleteFurniture(item: Furniture) {
        self.furnitureRef.childByAppendingPath(item.key).removeValue()
    }
    
    func editFurnitureName(key: String, name: String) {
        self.furnitureRef.childByAppendingPath(key).updateChildValues([
            "name": name
        ])
    }
}