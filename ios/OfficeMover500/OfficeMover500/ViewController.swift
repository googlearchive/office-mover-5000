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
            self.createFurnitureView(item)
        })
    }
    
    // This should take in a Furniture Model whatever that is.
    // This creates a view as a button, and makes it draggable.
    func createFurnitureView(furniture: Furniture) -> UIButton {
        let view = FurnitureButton(furniture: furniture)
        
        view.moveHandler = { top, left in
            // TODO: update to view. Something like: sync.updateItem(furniture, top, left)
            println("[\(furniture.key)] Furniture at \(top), \(left)")
        }
        
        view.rotateHandler = { rotation in
            // TODO: rotate furniture
            println("[\(furniture.key)] should rotate \(rotation)")
            view.rotateView(rotation)
        }
        
        view.deleteHandler = {
            // TODO: delete furniture
            println("[\(furniture.key)] should delete")
            view.deleteView()
        }
        
        roomView.addSubview(view)
        return view
    }
}