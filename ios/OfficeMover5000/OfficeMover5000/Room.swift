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
        
    }
    
    func addFurniture(item: Furniture) {
        self.furniture.append(item);
    }
    
    func toJson() -> String {
        return "";
    }
    
}