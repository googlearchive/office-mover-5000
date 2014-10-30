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

let BorderBlue = UIColor(red: CGFloat(214.0/255.0), green: CGFloat(235.0/255.0), blue: CGFloat(249.0/255.0), alpha: 1.0)
let TopbarBlue = UIColor(red: CGFloat(22.0/255.0), green: CGFloat(148.0/255.0), blue: CGFloat(223.0/255.0), alpha: 1.0)


class RoomViewController: UIViewController {
    
    @IBOutlet weak var roomView: UIView!
    @IBOutlet weak var addItemButton: UIBarButtonItem!
    @IBOutlet weak var backgroundButton: UIBarButtonItem!
    
    
    override func viewWillAppear(animated: Bool) {
        super.viewWillAppear(animated)
        
        roomView.layer.borderColor = BorderBlue.CGColor
        roomView.layer.borderWidth = 4
        
        var nav = self.navigationController?.navigationBar
        nav?.barTintColor = TopbarBlue
        nav?.barStyle = UIBarStyle.Default
        nav?.tintColor = UIColor.whiteColor()
        var font: UIFont = UIFont(name: "Proxima Nova", size: 20)!
        nav?.titleTextAttributes = [NSForegroundColorAttributeName: UIColor.whiteColor(), NSFontAttributeName:font]
        
        let addItemButton = UIBarButtonItem(barButtonSystemItem: .Add, target: nil, action: nil)
        let backgroundButton = UIBarButtonItem(barButtonSystemItem: .Action, target: nil, action: nil)
        
        //navigationItem.leftBarButtonItems = [addItemButton, backgroundButton]
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // uncomment this when we want to show the buttons to add things and such
        // navigationItem.leftBarButtonItems = [addItemButton, backgroundButton]
    }
}