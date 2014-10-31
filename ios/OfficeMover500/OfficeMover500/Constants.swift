//
//  Constants.swift
//  OfficeMover500
//
//  Created by Katherine Fang on 10/30/14.
//  Copyright (c) 2014 Firebase. All rights reserved.
//

import UIKit

let RoomWidth = 600
let RoomHeight = 800

let ThresholdMoved = CGFloat(10)

let BorderBlue = UIColor(red: CGFloat(214.0/255.0), green: CGFloat(235.0/255.0), blue: CGFloat(249.0/255.0), alpha: 1.0)
let TopbarBlue = UIColor(red: CGFloat(22.0/255.0), green: CGFloat(148.0/255.0), blue: CGFloat(223.0/255.0), alpha: 1.0)

// Each icon on the add menu should have one of these. The second value is the name of the asset
let Items = [
    ("Android Toy", "android", "android"),
    ("Ball Pit Pool", "ballpit", "ballpit"),
    ("Office Desk", "desk", "desk"),
    ("Dog (Corgi)", "dog", "dog_corgi"),
    ("Dog (Retriever)", "dog", "dog_retriever"),
    ("Laptop", "computer", "laptop"),
    ("Nerfgun Pistol", "nerf", "nerfgun"),
    ("Pacman Arcade", "game", "pacman"),
    ("Ping Pong Table", "pingpong", "pingpong"),
    ("Plant (Shrub)", "plant", "plant1"),
    ("Plant (Succulent)", "plant", "plant2"),
    ("Red Stapler", "stapler", "redstapler")
]

let Floors = [
    ("Carpet", "carpet"),
    ("Grid", "grid"),
    ("Tile", "tile"),
    ("Wood", "wood"),
]