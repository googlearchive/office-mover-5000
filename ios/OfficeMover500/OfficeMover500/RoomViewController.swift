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

class RoomViewController: UIViewController {
    
    @IBOutlet weak var roomView: UIView!
    
    override func viewWillAppear(animated: Bool) {
        super.viewWillAppear(animated)
        
        roomView.layer.borderColor = UIColor(red: CGFloat(214.0/255.0), green: CGFloat(235.0/255.0), blue: CGFloat(249.0/255.0), alpha: 1.0).CGColor
        roomView.layer.borderWidth = 4
        
        var nav = self.navigationController?.navigationBar
        nav?.barTintColor = UIColor(red: CGFloat(22.0/255.0), green: CGFloat(148.0/255.0), blue: CGFloat(223.0/255.0), alpha: 1.0)
        nav?.barStyle = UIBarStyle.Default
        nav?.tintColor = UIColor.whiteColor()
        nav?.titleTextAttributes = [NSForegroundColorAttributeName: UIColor.whiteColor()]
        
        let addItemButton = UIBarButtonItem(barButtonSystemItem: .Add, target: nil, action: nil)
        let backgroundButton = UIBarButtonItem(barButtonSystemItem: .Action, target: nil, action: nil)
        
        navigationItem.leftBarButtonItems = [addItemButton, backgroundButton]
    }
}