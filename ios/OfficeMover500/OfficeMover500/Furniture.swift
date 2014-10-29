//
//  FurnitureModel.swift
//  OfficeMover500
//
//  Created by David on 10/28/14.
//  Copyright (c) 2014 Firebase. All rights reserved.
//

import Foundation

class Furniture {
    
    var key : String
    var name: String
    var top : Int
    var left : Int
    //var zIndex: Int
    var rotation: Int
    var type : String
    var locked : Bool

    init(key: String, json: Dictionary<String, AnyObject>) {
     
        self.key = key
        self.name = json["name"] as String
        self.top = json["top"] as Int
        self.left = json["left"] as Int
        //self.zIndex = json["z-index"] as Int
        self.rotation = json["rotation"] as Int
        self.type = json["type"] as String
        self.locked = json["locked"] as Bool
        
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
            "key" : self.key,
            "top" : self.top,
            "left" : self.left,
            //"z-index" : self.zIndex,
            "rotation" : self.rotation,
            "type" : self.type,
            "locked" : self.locked
        ];
    }

    
}