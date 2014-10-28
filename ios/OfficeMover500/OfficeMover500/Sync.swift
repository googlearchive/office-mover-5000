//
//  FurnitureAdapter.swift
//  OfficeMover500
//
//  Created by David on 10/28/14.
//  Copyright (c) 2014 Firebase. All rights reserved.
//

import Foundation

class Sync {
    
    var room : Room
    var ref : Firebase
    
    init (ref: Firebase) {
        
        room = Room(json: nil)
        self.ref = ref
        
    }
    
    func onFurnitureAdded (onAdded : (Furniture) -> ()) {
        
        let furnitureRef = ref.childByAppendingPath("furniture")
        
        furnitureRef.observeEventType(.ChildAdded, withBlock: { snapshot in
            
            var furniture = Furniture(snap: snapshot)
            self.room.addFurniture(furniture)
            onAdded(furniture)
            
        })
        
    }
    
}