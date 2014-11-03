import UIKit

class ViewController: RoomViewController {

    override func viewDidLoad() {
        super.viewDidLoad()
        
        for furniture in TheFurniture {
            self.createFurnitureView(furniture)
        }
    }

    func createFurnitureView(furniture: Furniture) {
        let view = FurnitureView(furniture: furniture)
        self.roomView.addSubview(view)
    }
}