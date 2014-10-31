//
//  FurnitureAdapter.swift
//  OfficeMover500
//
//  Created by David on 10/28/14.
//  Copyright (c) 2014 Firebase. All rights reserved.
//

import Foundation

class RoomSync {
    
    var room : Room
    let ref : Firebase
    let furnitureRef: Firebase
    
    init (ref: Firebase) {
        
        room = Room(json: nil)
        self.ref = ref
        self.furnitureRef = ref.childByAppendingPath("furniture")
        
    }
    
    func onFurnitureAdded (onAdded : (item: Furniture) -> ()) {
        
        furnitureRef.observeEventType(.ChildAdded, withBlock: { snapshot in
            var furniture = Furniture(snap: snapshot)
            self.room.addFurniture(furniture)
            onAdded(item: furniture)
        })
        
    }
    
    func moveFurniture(key: String, top: Int, left: Int) {
        self.furnitureRef.childByAppendingPath(key).updateChildValues([
            "top": top,
            "left": left
        ])
    }
    
    func deleteFurniture(item: Furniture) {
        self.furnitureRef.childByAppendingPath(item.key).removeValue()
    }
    
}