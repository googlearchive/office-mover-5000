//
//  ViewController.swift
//  OfficeMover500
//
//  Created by Katherine Fang on 10/28/14.
//  Copyright (c) 2014 Firebase. All rights reserved.
//

import UIKit

let RoomWidth = 600
let RoomHeight = 800

class ViewController: UIViewController {

    @IBOutlet weak var roomView: UIView!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        let ref = Firebase(url: "https://office-mover.firebaseio.com/")
        var sync = Sync(ref: ref)
        
        sync.onFurnitureAdded({ item in
            println(item.key)
            self.createFurnitureView()
        })
        
    }
    
    // This should take in a Furniture Model whatever that is.
    // This creates a view as a button, and makes it draggable.
    func createFurnitureView() -> UIButton {
        let furnitureButton = FurnitureButton()
        furnitureButton.onMoveHandler = { top, left in
            println("From here \(top), \(left)")
        }
        roomView.addSubview(furnitureButton)
        return furnitureButton
    }
}