import UIKit

class ViewController: RoomViewController {
    
    let furnitureListRef = Firebase(url: "https://gcp-office-demo.firebaseio.com/furniture")

    override func viewDidLoad() {
        super.viewDidLoad()
        
        furnitureListRef.observeEventType(.ChildAdded, withBlock: { [unowned self] snapshot in
            let furniture = Furniture(snap: snapshot)
            self.createFurnitureView(furniture)
        })
    }

    func createFurnitureView(furniture: Furniture) {
        let view = FurnitureView(furniture: furniture)
        
        let furnitureRef = furnitureListRef.childByAppendingPath(furniture.key)

        furnitureRef.observeEventType(.Value, withBlock: { snapshot in
            if snapshot.value as? NSNull != NSNull() {
                let furniture = Furniture(snap: snapshot)
                view.setViewState(furniture)
            }
        })
        
        view.moveHandler = { top, left in
            furnitureRef.updateChildValues([
                "top": top,
                "left": left
            ])
        }
        
        view.rotateHandler = { top, left, rotation in
            furnitureRef.updateChildValues([
                "top": top,
                "left": left,
                "rotation": rotation
            ])
        }
        
        view.editHandler = { name in
            furnitureRef.updateChildValues([
                "name": name
            ])
        }
        self.roomView.addSubview(view)
    }
}