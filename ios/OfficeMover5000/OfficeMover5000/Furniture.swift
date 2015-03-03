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

    init(key: String, json: Dictionary<String, AnyObject>) {
        self.key = key
        self.name = json["name"] as? String ?? ""
        self.type = json["type"] as? String ?? "desk"
        self.zIndex = json["z-index"] as? Int ?? ++maxZIndex
        self.rotation = json["rotation"] as? Int ?? 0
        
        let defaultLoc = Furniture.defaultLocation(self.type)
        self.top = json["top"] as? Int ?? defaultLoc.top
        self.left = json["left"] as? Int ?? defaultLoc.left
        
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
        self.zIndex = ++maxZIndex
        
        let defaultLoc = Furniture.defaultLocation(self.type)
        self.top = defaultLoc.top
        self.left = defaultLoc.left
    }
    
    convenience init(snap: FDataSnapshot) {
        if let json = snap.value as? Dictionary<String, AnyObject> {
            self.init(key: snap.key, json: json)
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
        ];
    }
    
    class private func defaultLocation(type: String) -> (top: Int, left: Int) {
        if let image = UIImage(named:"\(type).png") {
            return (top: RoomHeight/2 - Int(image.size.height)/2, left: RoomWidth/2 - Int(image.size.width)/2)
        } else {
            return (top: RoomHeight/2, left: RoomWidth/2)
        }
    }
}