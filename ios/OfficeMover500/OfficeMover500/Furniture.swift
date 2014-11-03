//
//  FurnitureModel.swift
//  OfficeMover500
//
//  Created by David on 10/28/14.
//  Copyright (c) 2014 Firebase. All rights reserved.
//

import UIKit

var maxZIndex = 0

class Furniture {
    
    var key : String
    var name: String
    var top : Int
    var left : Int
    var zIndex: Int
    var rotation: Int
    var type : String
    var locked : Bool
    
    init(key: String, json: Dictionary<String, AnyObject>) {
        self.key = key
        self.name = json["name"] as String
        self.top = json["top"] as Int
        self.left = json["left"] as Int
        self.zIndex = json["z-index"] as Int
        self.rotation = json["rotation"] as Int
        self.type = json["type"] as String
        self.locked = json["locked"] as Bool
        
        if self.zIndex > maxZIndex {
            maxZIndex = self.zIndex
        }
    }
    
    //use this one if you want to hardcode some values.
    init(key: String, name: String, top: Int, left: Int, rotation: Int, type: String) {
        self.key = key
        self.name = name
        self.top = top
        self.left = left
        self.zIndex = maxZIndex + 1
        self.rotation = rotation
        self.type = type
        self.locked = false
        
        if self.zIndex > maxZIndex {
            maxZIndex = self.zIndex
        }
    }
    
    // Use this when adding one locally
    // You'll need to set top / left appropriately.
    init(key: String, type: String) {
        self.key = key
        self.name = ""
        self.rotation = 0
        self.type = type
        self.locked = false
        self.zIndex = ++maxZIndex
        
        // This is a huge hack to get the right top / left location of the object
        if let image = UIImage(named:"\(type).png") {
            self.top = RoomHeight/2 - Int(image.size.width)/2
            self.left = RoomWidth/2 - Int(image.size.width)/2
        } else {
            self.top = RoomHeight/2
            self.left = RoomHeight/2
        }
    }
    
    convenience init(snap: FDataSnapshot) {
        
        if let json = snap.value as? Dictionary<String, AnyObject> {
            self.init(key: snap.name, json: json)
        }
        else {
            fatalError("blah")
        }
        
    }
    
    func toJson() -> Dictionary<String, AnyObject> {
        return [
            "top" : self.top,
            "left" : self.left,
            "z-index" : self.zIndex,
            "name" : self.name,
            "rotation" : self.rotation,
            "type" : self.type,
            "locked" : self.locked
        ];
    }
}

let TheFurniture: [Furniture] = [
    Furniture(key: "-J_nWxmnt3Je0ceSDHUB", name: "", top: 667, left: 245, rotation: 0, type: "ballpit"),
    Furniture(key: "-J_ndeaMFZvHRGX7_yTl", name: "Andrew", top: 0, left: 0, rotation: 0, type: "desk"),
    Furniture(key: "-J_ndfk3cXdCP2y8hXmL", name: "Vikrum", top: 0, left: 484, rotation: 0, type: "desk"),
    Furniture(key: "-J_ndglOTQxqQdIxcgBT", name: "James", top: 195, left: 538, rotation: 270, type: "desk"),
    Furniture(key: "-J_ndio23J23Fa1tx3cw", name: "", top: 742, left: 546, rotation: 0, type: "plant1"),
    Furniture(key: "-J_ndklSKsTrUCZIAd-R", name: "", top: 292, left: 562, rotation: 0, type: "redstapler"),
    Furniture(key: "-J_ndltujoa6CWFIvCCu", name: "", top: 513, left: 0, rotation: 0, type: "plant2"),
    Furniture(key: "-J_ndnh4auwKcvwR5I7o", name: "", top: 168, left: 251, rotation: 90, type: "pingpong"),
    Furniture(key: "-J_ndyztgX58yc1b3VUF", name: "", top: 467, left: 534, rotation: 90, type: "pacman"),
    Furniture(key: "-J_ne2gXw0TQXsmO8AaJ", name: "", top: 289, left: 133, rotation: 0, type: "nerfgun"),
    Furniture(key: "-J_ne3Z2pkmWg5KXcvaK", name: "", top: 331, left: 499, rotation: 0, type: "dog_retriever"),
    Furniture(key: "-J_ne56hm2QjyEg77Ond", name: "Michael", top: 198, left: 0, rotation: 90, type: "desk"),
    Furniture(key: "-J_ne82h4lFQH0Y1xKFd", name: "", top: 63, left: 270, rotation: 0, type: "android"),
    Furniture(key: "-J_ne9mxULmvrVeQ7XXa", name: "", top: 0, left: 477, rotation: 0, type: "laptop"),
    Furniture(key: "-J_neAQmSvs3ckVH7Mha", name: "", top: 33, left: 74, rotation: 0, type: "laptop")
]