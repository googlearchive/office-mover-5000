//
//  AddItemController.swift
//  OfficeMover500
//
//  Created by Katherine Fang on 10/30/14.
//  Copyright (c) 2014 Firebase. All rights reserved.
//
import UIKit

class AddItemController : UITableViewController {
 
    override func numberOfSectionsInTableView(tableView: UITableView) -> Int {
        return 1
    }
    
    override func tableView(tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return Items.count
    }
    
    override func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> UITableViewCell {
        var cell: UITableViewCell! = tableView.dequeueReusableCellWithIdentifier("addItemCell") as? UITableViewCell
        if cell == nil {
            cell = UITableViewCell(style: .Default, reuseIdentifier: "addItemCell")
        }
        
        // Definitely populated now
        cell.textLabel.text = Items[indexPath.row].0
        let imageName = Items[indexPath.row].1
        cell.imageView
        cell.imageView.image = UIImage(named: "icon_\(imageName).png")
        return cell
    }
}