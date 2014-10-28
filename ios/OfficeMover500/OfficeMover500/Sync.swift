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
    
    init (ref: Firebase) {
        
        room = Room(json: nil);
        
        
        ref.observeEventType(.ChildAdded, withBlock: { snapshot in
            
            var furniture = Furniture(snap: snapshot)
            
            println(furniture.key)
            
//            println(snapshot.value.objectForKey("author"))
//            println(snapshot.value.objectForKey("title"))
            
        })
        
    }
    
}