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
    ("Android Stuffed Animal", "android"),
    ("Ball Pit", "ballpit"),
    ("Laptop", "laptop"),
    ("Desk", "desk"),
    ("Dog", "dog"),
    ("Arcade Game", "game"),
    ("Nerf Gun", "nerfgun"),
    ("Ping Pong Table", "pingpong"),
    ("Indoor Plant", "plant"),
    ("Red Stapler", "stapler")
]

// Some items have multiple item types or names that don't match the add-icon name, so they're listed here
let ItemTypes = [
    "dog": ["dog_retriever", "dog_corgi"],
    "game": ["pacman"],
    "plant": ["plant1", "plant2"],
    "stapler": ["redstapler"]
]