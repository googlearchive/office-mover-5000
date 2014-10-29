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
    
    var sync = RoomSync(ref: Firebase(url: "https://office-mover.firebaseio.com/"))
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        sync.onFurnitureAdded({ item in
            self.createFurnitureView(item)
        })
    }
    
    // This should take in a Furniture Model whatever that is.
    // This creates a view as a button, and makes it draggable.
    func createFurnitureView(furniture: Furniture) {
        let view = FurnitureButton(furniture: furniture)
        
        view.moveHandler = { top, left in
            self.sync.moveFurniture(furniture.key, top: top, left: left)
        }
        
        view.rotateHandler = { rotation in
            // TODO: rotate furniture
            println("[\(furniture.key)] should rotate \(rotation)")
            view.rotation = rotation
        }
        
        view.deleteHandler = {
            view.deleteView()
            self.sync.deleteFurniture(furniture)
        }
        
        roomView.addSubview(view)
    }
}