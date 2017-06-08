import Controller.HexController;
import Model.HexModel;
import View.HexView;

/**
 * Abomnes Gauthier
 * Bretheau Yann
 * S3C
 */

public class Main {

    public Main(){
        //Creation du model
        HexModel model = new HexModel();
        //Creation de la vue
        HexView view = new HexView(model,"HexGame - Abomnes - Bretheau - S3C");
        //Creation du controller
        HexController controller = new HexController(model,view);

    }

    public static void main(String[] args) {
        new Main();
    }
}
