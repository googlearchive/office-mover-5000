//
//  RoomModel.swift
//  OfficeMover500
//
//  Created by David on 10/28/14.
//  Copyright (c) 2014 Firebase. All rights reserved.
//

import Foundation

class Room {
    
    var furniture = Array<Furniture>()
    
    init(json: Dictionary<String, AnyObject>?) {
//        var items = snap.childSnapshotForPath("furniture").children.allObjects
//        
//        for item in items {
//            
//            // initialize Furniture
//            if let data = item as? Dictionary<String, AnyObject> {
//                var furnitureItem = Furniture(key: item, json: <#Dictionary<String, AnyObject>#>)
//                furniture.append(
//            }
//            
//            furniture.append(item)
//        }
        
    }
    
    func addFurniture(item: Furniture) {
        self.furniture.append(item);
    }
    
    func toJson() -> String {
        return "";
    }
    
}