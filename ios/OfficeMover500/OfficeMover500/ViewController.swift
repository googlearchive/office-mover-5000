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
        let furnitureNameRef = furnitureRef.childByAppendingPath("name")

        view.editHandler = { name in
            furnitureNameRef.setValue(name)
        }
        
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
        
        furnitureRef.observeEventType(.Value, withBlock: { snapshot in
            let furniture = Furniture(snap: snapshot)
            view.setViewState(furniture)
        })
        
        self.roomView.addSubview(view)
    }
}